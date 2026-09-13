package com.expensemanager.security;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.expensemanager.dto.AuthLoginRequest;
import com.expensemanager.dto.AuthTokenResponse;
import com.expensemanager.dto.RefreshTokenRequest;
import com.expensemanager.entity.AppUser;
import com.expensemanager.entity.AppUserRole;
import com.expensemanager.entity.Member;
import com.expensemanager.entity.RefreshToken;
import com.expensemanager.repository.AppUserRepository;
import com.expensemanager.repository.MemberRepository;
import com.expensemanager.repository.RefreshTokenRepository;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    public AuthenticationService(AuthenticationManager authenticationManager,
            AppUserRepository appUserRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public ResponseEntity<AuthTokenResponse> login(AuthLoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            AppUser appUser = appUserRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
            if (!appUser.isEnabled()) {
                throw new IllegalArgumentException("Invalid username or password");
            }
            appUser.setLastLoginAt(Instant.now());
            appUserRepository.save(appUser);
            String accessToken = jwtService.generateAccessToken(appUser);
            String refreshToken = jwtService.generateRefreshToken(appUser);
            storeRefreshToken(appUser, refreshToken);
            AuthTokenResponse response = new AuthTokenResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtProperties.getAccessTokenExpiration() / 1000);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    @Transactional
    public ResponseEntity<AuthTokenResponse> refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        var claims = jwtService.parseClaims(token);
        String username = claims.getSubject();
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        String tokenHash = hashToken(token);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(appUser);
        String newRefreshToken = jwtService.generateRefreshToken(appUser);
        storeRefreshToken(appUser, newRefreshToken);

        return ResponseEntity.ok(new AuthTokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtProperties.getAccessTokenExpiration() / 1000));
    }

    @Transactional
    public void logout(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        appUserRepository.findByUsername(username).ifPresent(user -> {
            refreshTokenRepository.deleteByUserId(user.getId());
        });
    }

    @Transactional
    public void createUser(String username, String plainPassword, String role, UUID memberId) {
        if (appUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        Member member = null;
        if (memberId != null) {
            member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        }

        AppUser appUser = new AppUser(
                username,
                passwordEncoder.encode(plainPassword),
                AppUserRole.valueOf(role),
                member,
                true);

        appUserRepository.save(appUser);
    }

    private void storeRefreshToken(AppUser user, String refreshToken) {
        String hash = hashToken(refreshToken);
        refreshTokenRepository.deleteByUserId(user.getId());
        RefreshToken tokenEntity = new RefreshToken(user, hash,
                Instant.now().plusMillis(jwtService.getRefreshTokenExpiration()));
        refreshTokenRepository.save(tokenEntity);
    }

    private String hashToken(String token) {
        return Base64.getEncoder().encodeToString(sha256(token));
    }

    private byte[] sha256(String value) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            return md.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash refresh token", ex);
        }
    }
}
