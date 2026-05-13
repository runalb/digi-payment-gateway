-- Merchant setup (PostgreSQL) — run on empty tables only

INSERT INTO public.payment_channel
(id, created_date_time, updated_date_time, name, is_active)
VALUES(1, '2026-03-20 18:00:47.644', '2026-03-20 18:00:47.644', 'TEST', true);

INSERT INTO public.merchant
(id, created_date_time, updated_date_time, api_key, email, is_active, "name")
VALUES(1, '2026-03-20 18:00:47.644', '2026-03-20 18:00:47.644', 'b4bdb71a-51c5-4565-985d-e0c13f72b970', 'merchant-r1@digirestro.local', true, 'Merchant R1');

INSERT INTO public.merchant_config
(id, created_date_time, updated_date_time, currency, webhook_url, merchant_id)
VALUES(1, '2026-03-20 18:00:47.644', '2026-03-20 18:00:47.644', 'INR', 'runalb.com', 1);

INSERT INTO public.merchant_payment_channel_config
(id, created_date_time, updated_date_time, config_json, is_active, merchant_id, payment_channel_id)
VALUES(1, '2026-03-20 18:00:47.644', '2026-03-20 18:00:47.644', NULL, true, 1, 1);
