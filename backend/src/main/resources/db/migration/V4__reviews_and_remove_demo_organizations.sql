ALTER TABLE queue_entries ADD COLUMN IF NOT EXISTS rating INTEGER;
ALTER TABLE queue_entries ADD COLUMN IF NOT EXISTS review_comment VARCHAR(1000);

DELETE FROM appointment_messages
WHERE queue_entry_id IN (
    SELECT q.id FROM queue_entries q
    JOIN organizations o ON o.id = q.organization_id
    JOIN users u ON u.id = o.owner_id
    WHERE u.email = 'owner@queueless.in'
      AND o.name IN ('Apollo Clinic', 'HDFC Bank', 'Mira’s Nail Studio', 'CityCare Diagnostics')
);
DELETE FROM queue_entries
WHERE organization_id IN (
    SELECT o.id FROM organizations o JOIN users u ON u.id = o.owner_id
    WHERE u.email = 'owner@queueless.in'
      AND o.name IN ('Apollo Clinic', 'HDFC Bank', 'Mira’s Nail Studio', 'CityCare Diagnostics')
);
DELETE FROM services
WHERE organization_id IN (
    SELECT o.id FROM organizations o JOIN users u ON u.id = o.owner_id
    WHERE u.email = 'owner@queueless.in'
      AND o.name IN ('Apollo Clinic', 'HDFC Bank', 'Mira’s Nail Studio', 'CityCare Diagnostics')
);
DELETE FROM staff
WHERE organization_id IN (
    SELECT o.id FROM organizations o JOIN users u ON u.id = o.owner_id
    WHERE u.email = 'owner@queueless.in'
      AND o.name IN ('Apollo Clinic', 'HDFC Bank', 'Mira’s Nail Studio', 'CityCare Diagnostics')
);
DELETE FROM organizations
WHERE owner_id = (SELECT id FROM users WHERE email = 'owner@queueless.in')
  AND name IN ('Apollo Clinic', 'HDFC Bank', 'Mira’s Nail Studio', 'CityCare Diagnostics');
