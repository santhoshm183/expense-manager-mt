package com.expensemanager.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.expensemanager.entity.AppUser;

public class UserPrincipal implements UserDetails {
    private final AppUser appUser;
    private final UUID memberId;
    private final UUID memberChitId;

    public UserPrincipal(AppUser appUser) {
        this.appUser = appUser;
        this.memberId = appUser.getMember() != null ? appUser.getMember().getId() : null;
        this.memberChitId = appUser.getMember() != null && appUser.getMember().getChit() != null
                ? appUser.getMember().getChit().getId()
                : null;
    }

    public UUID getUserId() {
        return appUser.getId();
    }

    public UUID getMemberId() {
        return memberId;
    }

    public UUID getMemberChitId() {
        return memberChitId;
    }

    public String getRole() {
        return appUser.getRole().name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getRole().name()));
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return appUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return appUser.isEnabled();
    }

    public AppUser getAppUser() {
        return appUser;
    }
}
