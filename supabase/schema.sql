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

-- This first version has no authentication. Enable RLS and ownership policies before multi-user access.
