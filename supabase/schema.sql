-- Expense Manager schema for Supabase PostgreSQL
-- Run this script in Supabase SQL Editor.
create extension if not exists pgcrypto;

do $$ begin
  create type transaction_type as enum ('income', 'savings', 'expense');
exception when duplicate_object then null;
end $$;

create table if not exists transactions (
  id uuid primary key default gen_random_uuid(),
  transaction_date date not null,
  description text not null check (length(trim(description)) > 0),
  amount numeric(14,2) not null check (amount > 0),
  transaction_type transaction_type not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists transactions_month_idx on transactions (transaction_date);

create or replace function set_updated_at() returns trigger language plpgsql as $$
begin new.updated_at = now(); return new; end;
$$;

drop trigger if exists transactions_updated_at on transactions;
create trigger transactions_updated_at before update on transactions for each row execute function set_updated_at();

create type chit_status as enum ('active', 'completed', 'paused');
create type investment_type as enum ('fixed_deposit', 'private_loan', 'business_investment', 'savings_account', 'other');

create table if not exists chits (
  id uuid primary key default gen_random_uuid(),
  chit_id varchar(32) not null unique,
  name text not null check (length(trim(name)) > 0),
  total_amount numeric(14,2) not null check (total_amount > 0),
  member_count integer not null check (member_count > 0),
  monthly_installment numeric(14,2) not null check (monthly_installment > 0),
  agent_percentage numeric(5,2) not null default 0 check (agent_percentage >= 0 and agent_percentage <= 100),
  start_date date not null,
  duration_months integer not null check (duration_months > 0),
  status chit_status not null default 'active',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table chits add column if not exists agent_percentage numeric(5,2) not null default 0;
do $$ begin
  alter table chits add constraint chits_agent_percentage_check check (agent_percentage >= 0 and agent_percentage <= 100);
exception when duplicate_object then null;
end $$;

drop trigger if exists chits_updated_at on chits;
create trigger chits_updated_at before update on chits for each row execute function set_updated_at();

create table if not exists members (
  id uuid primary key default gen_random_uuid(),
  chit_id uuid not null references chits(id) on delete restrict,
  name text not null check (length(trim(name)) > 0),
  mobile_number varchar(20) not null,
  email varchar(255),
  permanent_address text,
  chit_taken boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

do $$ begin
  alter table members add constraint members_id_chit_unique unique (id, chit_id);
exception when duplicate_object then null;
end $$;

do $$ begin
  alter table members add constraint members_chit_fk foreign key (chit_id) references chits(id) on delete restrict;
exception when duplicate_object then null;
end $$;
create index if not exists members_chit_idx on members (chit_id);

drop trigger if exists members_updated_at on members;
create trigger members_updated_at before update on members for each row execute function set_updated_at();

create table if not exists installment (
  id uuid primary key default gen_random_uuid(),
  chit_id uuid not null references chits(id) on delete restrict,
  member_id uuid not null,
  number_of_hand integer not null check (number_of_hand > 0),
  installment_amount numeric(14,2) not null default 0 check (installment_amount >= 0),
  installment_date date not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (member_id, installment_date),
  foreign key (member_id, chit_id) references members(id, chit_id) on delete restrict
);

alter table installment add column if not exists number_of_hand integer not null default 1;
do $$ begin
  alter table installment add constraint installment_number_of_hand_check check (number_of_hand > 0);
exception when duplicate_object then null;
end $$;

create index if not exists installment_chit_idx on installment (chit_id, installment_date);
create index if not exists installment_member_idx on installment (member_id, installment_date);

drop trigger if exists installment_updated_at on installment;
create trigger installment_updated_at before update on installment for each row execute function set_updated_at();

create table if not exists chit_auctions (
  id uuid primary key default gen_random_uuid(),
  chit_id uuid not null references chits(id) on delete cascade,
  bid_no integer not null check (bid_no > 0 and bid_no <= 999),
  extra_hand boolean not null default false,
  hand_type varchar(20) not null default 'ReleaseHand' check (hand_type in ('ExtrHand', 'ReleaseHand', 'AgentHand')),
  partial_amount boolean not null default false,
  auction_month date not null,
  bid_amount numeric(14,2) not null check (bid_amount >= 0),
  winning_member_id uuid not null references members(id) on delete restrict,
  net_amount_paid numeric(14,2) not null check (net_amount_paid >= 0),
  agent_amount numeric(14,2) not null check (agent_amount >= 0),
  profit_amount numeric(14,2) not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (chit_id, bid_no, winning_member_id)
);


update chit_auctions set hand_type = case when extra_hand then 'ExtrHand' else 'ReleaseHand' end where hand_type = 'ReleaseHand';
do $$ begin
  alter table chit_auctions add constraint chit_auctions_hand_type_check check (hand_type in ('ExtrHand', 'ReleaseHand', 'AgentHand'));
exception when duplicate_object then null;
end $$;

create index if not exists chit_auctions_chit_idx on chit_auctions (chit_id, auction_month);
drop trigger if exists chit_auctions_updated_at on chit_auctions;
create trigger chit_auctions_updated_at before update on chit_auctions for each row execute function set_updated_at();

create table if not exists chit_income (
  id uuid primary key default gen_random_uuid(),
  chit_id uuid not null references chits(id) on delete cascade,
  income_amount numeric(14,2) not null check (income_amount >= 0),
  percentage numeric(5,2) not null check (percentage >= 0 and percentage <= 100),
  number_of_months integer not null check (number_of_months between 1 and 12),
  interest_earned_amount numeric(14,2) not null check (interest_earned_amount >= 0),
  active boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- alter table chit_income add column if not exists income_amount numeric(14,2) not null default 0;
-- alter table chit_income add column if not exists percentage numeric(5,2) not null default 0;
-- alter table chit_income add column if not exists number_of_months integer not null default 1;
-- alter table chit_income add column if not exists interest_earned_amount numeric(14,2) not null default 0;
-- alter table chit_income add column if not exists active boolean not null default true;
-- alter table chit_income add column if not exists created_at timestamptz not null default now();
-- alter table chit_income add column if not exists updated_at timestamptz not null default now();
create index if not exists chit_income_chit_idx on chit_income (chit_id, created_at);
drop trigger if exists chit_income_updated_at on chit_income;
create trigger chit_income_updated_at before update on chit_income for each row execute function set_updated_at();

create type app_user_role as enum ('ADMIN', 'MEMBER');

create table if not exists users (
  id uuid primary key default gen_random_uuid(),
  username varchar(64) not null unique,
  password text not null,
  role app_user_role not null,
  member_id uuid null,
  enabled boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  last_login_at timestamptz,
  constraint users_member_fk foreign key (member_id) references members(id) on delete set null
);

create unique index if not exists users_member_unique_idx
  on users (member_id)
  where member_id is not null;

create index if not exists users_username_idx on users (username);
create index if not exists users_member_idx on users (member_id);

create trigger users_updated_at before update on users for each row execute function set_updated_at();

create table if not exists refresh_tokens (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references users(id) on delete cascade,
  token_hash text not null unique,
  expires_at timestamptz not null,
  revoked boolean not null default false,
  created_at timestamptz not null default now()
);

create index if not exists refresh_tokens_user_idx on refresh_tokens (user_id);
create index if not exists refresh_tokens_expires_idx on refresh_tokens (expires_at);

-- This first version has no authentication. Enable RLS and ownership policies before multi-user access.
