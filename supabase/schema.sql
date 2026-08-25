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
  mobile_number varchar(20) not null unique,
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
  auction_month date not null,
  bid_amount numeric(14,2) not null check (bid_amount >= 0),
  winning_member_id uuid not null references members(id) on delete restrict,
  net_amount_paid numeric(14,2) not null check (net_amount_paid >= 0),
  agent_amount numeric(14,2) not null check (agent_amount >= 0),
  profit_amount numeric(14,2) not null check (profit_amount >= 0),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (chit_id, bid_no, winning_member_id)
);

alter table chit_auctions add column if not exists extra_hand boolean not null default false;

create index if not exists chit_auctions_chit_idx on chit_auctions (chit_id, auction_month);
drop trigger if exists chit_auctions_updated_at on chit_auctions;
create trigger chit_auctions_updated_at before update on chit_auctions for each row execute function set_updated_at();




-- This first version has no authentication. Enable RLS and ownership policies before multi-user access.
