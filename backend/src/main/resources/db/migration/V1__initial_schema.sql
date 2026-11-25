CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(254) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER','OWNER')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE organizations (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(140) NOT NULL,
  category VARCHAR(140) NOT NULL,
  address VARCHAR(180) NOT NULL,
  city VARCHAR(80) NOT NULL,
  description VARCHAR(1000),
  active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  owner_id BIGINT NOT NULL REFERENCES users(id)
);
CREATE INDEX idx_organizations_city_active ON organizations(active,city);
CREATE TABLE services (
  id BIGSERIAL PRIMARY KEY,
  organization_id BIGINT NOT NULL REFERENCES organizations(id),
  name VARCHAR(120) NOT NULL,
  duration_minutes INTEGER NOT NULL CHECK (duration_minutes BETWEEN 1 AND 240),
  active BOOLEAN NOT NULL DEFAULT true
);
CREATE TABLE staff (
  id BIGSERIAL PRIMARY KEY,
  organization_id BIGINT NOT NULL REFERENCES organizations(id),
  name VARCHAR(100) NOT NULL,
  title VARCHAR(120),
  active BOOLEAN NOT NULL DEFAULT true
);
CREATE TABLE queue_entries (
  id BIGSERIAL PRIMARY KEY,
  organization_id BIGINT NOT NULL REFERENCES organizations(id),
  service_id BIGINT NOT NULL REFERENCES services(id),
  staff_id BIGINT REFERENCES staff(id),
  customer_id BIGINT REFERENCES users(id),
  customer_name VARCHAR(100) NOT NULL,
  customer_email VARCHAR(254) NOT NULL,
  tracking_code VARCHAR(36) NOT NULL UNIQUE,
  queue_number INTEGER NOT NULL,
  status VARCHAR(20) NOT NULL CHECK (status IN ('WAITING','SERVING','COMPLETED','CANCELLED','NO_SHOW')),
  visit_date DATE NOT NULL,
  joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  started_at TIMESTAMPTZ,
  completed_at TIMESTAMPTZ,
  UNIQUE(organization_id,visit_date,queue_number)
);
CREATE INDEX idx_queue_org_status ON queue_entries(organization_id,status);
CREATE INDEX idx_queue_customer_status ON queue_entries(customer_id,status);
CREATE INDEX idx_queue_visit_order ON queue_entries(organization_id,visit_date,queue_number);
