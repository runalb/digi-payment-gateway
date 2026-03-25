-- Remove payment_channel.name CHECK constraint permanently.
-- Run manually against db_digi_payment_gateway if inserts fail with:
--   new row for relation "payment_channel" violates check constraint "payment_channel_name_check"
--
-- This intentionally does NOT re-add a replacement CHECK.

ALTER TABLE payment_channel DROP CONSTRAINT IF EXISTS payment_channel_name_check;
