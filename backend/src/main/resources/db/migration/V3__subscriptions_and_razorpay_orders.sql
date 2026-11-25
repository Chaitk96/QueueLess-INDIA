ALTER TABLE users ADD COLUMN free_access_until TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN subscription_until TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN subscription_plan VARCHAR(40);

UPDATE users SET free_access_until = CASE
  WHEN role = 'OWNER' THEN now() + interval '2 hours'
  ELSE now() + interval '5 days'
END
WHERE free_access_until IS NULL;

CREATE TABLE payment_orders (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  razorpay_order_id VARCHAR(40) NOT NULL UNIQUE,
  plan_code VARCHAR(40) NOT NULL,
  amount_paise INTEGER NOT NULL,
  duration_days INTEGER NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
  payment_id VARCHAR(40),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_payment_user_status ON payment_orders(user_id, status);
