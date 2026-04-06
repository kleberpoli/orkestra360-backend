-- Seed script with sample tenants, users, and tasks for Orkestra360.
-- This script inserts realistic multi-tenant data and task workflow scenarios.

INSERT INTO tenants (id, name, slug, active) VALUES
    ('a1111111-1111-1111-1111-111111111111', 'Alpha Ventures', 'alpha-ventures', TRUE),
    ('b2222222-2222-2222-2222-222222222222', 'Beta Systems', 'beta-systems', TRUE),
    ('c3333333-3333-3333-3333-333333333333', 'Gamma Operations', 'gamma-operations', TRUE),
    ('d4444444-4444-4444-4444-444444444444', 'Delta Labs', 'delta-labs', TRUE),
    ('e5555555-5555-5555-5555-555555555555', 'Epsilon Group', 'epsilon-group', TRUE);

INSERT INTO users (id, tenant_id, name, email, phone, active) VALUES
    ('11111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'Alice Monroe', 'alice.monroe@alpha-ventures.com', '+1-202-555-0141', TRUE),
    ('22222222-2222-2222-2222-222222222222', 'b2222222-2222-2222-2222-222222222222', 'Bob Carter', 'bob.carter@beta-systems.com', '+1-202-555-0178', TRUE),
    ('33333333-3333-3333-3333-333333333333', 'c3333333-3333-3333-3333-333333333333', 'Carol Diaz', 'carol.diaz@gamma-operations.com', '+1-202-555-0199', TRUE),
    ('44444444-4444-4444-4444-444444444444', 'd4444444-4444-4444-4444-444444444444', 'Daniel Park', 'daniel.park@delta-labs.com', '+1-202-555-0123', TRUE);

INSERT INTO tasks (id, tenant_id, assigned_user_id, title, description, status, priority, due_date, active) VALUES
    -- Alice Monroe (Alpha Ventures)
    ('10111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Prepare Q2 roadmap', 'Draft the strategic roadmap for Q2 launch and review with leadership.', 'DOING', 'HIGH', '2026-04-12T09:00:00Z', TRUE),
    ('10111111-1111-1111-1111-111111111112', 'a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Review partner contracts', 'Validate contract terms and confirm signature windows with legal.', 'TODO', 'MEDIUM', '2026-04-17T17:00:00Z', TRUE),
    ('10111111-1111-1111-1111-111111111113', 'a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Finalize onboarding checklist', 'Complete the onboarding checklist for the new sales engineering team.', 'DONE', 'LOW', '2026-04-05T12:00:00Z', TRUE),
    ('10111111-1111-1111-1111-111111111114', 'a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Archive old sprint tickets', 'Move resolved sprint tickets to archive and notify stakeholders.', 'ARCHIVED', 'LOW', '2026-03-30T15:00:00Z', TRUE),
    ('10111111-1111-1111-1111-111111111115', 'a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Respond to partner escalation', 'Address the urgent escalation from the partner success team before end of day.', 'DOING', 'URGENT', '2026-04-08T18:00:00Z', TRUE),

    -- Bob Carter (Beta Systems)
    ('20222222-2222-2222-2222-222222222221', 'b2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Deploy monitoring agent', 'Deploy the new monitoring agent on the staging cluster.', 'TODO', 'MEDIUM', '2026-04-14T11:00:00Z', TRUE),
    ('20222222-2222-2222-2222-222222222222', 'b2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Investigate alert spike', 'Analyze the recent spike in alert volume and determine root cause.', 'DOING', 'HIGH', '2026-04-10T10:30:00Z', TRUE),
    ('20222222-2222-2222-2222-222222222223', 'b2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Update security policy', 'Review and update the access policy document for new compliance requirements.', 'DONE', 'MEDIUM', '2026-04-03T16:00:00Z', TRUE),
    ('20222222-2222-2222-2222-222222222224', 'b2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Schedule incident drill', 'Plan the next incident response drill and invite cross-functional teams.', 'TODO', 'HIGH', '2026-04-21T14:00:00Z', TRUE),
    ('20222222-2222-2222-2222-222222222225', 'b2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Close legacy support tickets', 'Finalize the remaining legacy support tickets before the system migration.', 'ARCHIVED', 'LOW', '2026-03-28T10:00:00Z', TRUE),

    -- Carol Diaz (Gamma Operations)
    ('30333333-3333-3333-3333-333333333331', 'c3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'Design compliance dashboard', 'Create the first version of the new compliance dashboard.', 'DOING', 'HIGH', '2026-04-16T13:00:00Z', TRUE),
    ('30333333-3333-3333-3333-333333333332', 'c3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'Confirm audit evidence', 'Gather all audit evidence for the upcoming quarterly review.', 'TODO', 'URGENT', '2026-04-09T09:00:00Z', TRUE),
    ('30333333-3333-3333-3333-333333333333', 'c3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'Complete user training', 'Finish the end-user training materials and publish to the knowledge base.', 'DONE', 'MEDIUM', '2026-04-01T11:00:00Z', TRUE),
    ('30333333-3333-3333-3333-333333333334', 'c3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'Review automation rules', 'Validate current automation rules and adjust thresholds for new traffic patterns.', 'TODO', 'LOW', '2026-04-22T09:30:00Z', TRUE),
    ('30333333-3333-3333-3333-333333333335', 'c3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'Archive stale reports', 'Move stale operations reports to the archive folder and free storage.', 'ARCHIVED', 'LOW', '2026-03-29T15:30:00Z', TRUE),

    -- Daniel Park (Delta Labs)
    ('40444444-4444-4444-4444-444444444441', 'd4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'Launch new feature pilot', 'Run the pilot for the new feature with the innovation cohort.', 'DOING', 'URGENT', '2026-04-11T08:00:00Z', TRUE),
    ('40444444-4444-4444-4444-444444444442', 'd4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'Validate integration tests', 'Execute integration test suites for the release candidate.', 'TODO', 'HIGH', '2026-04-20T12:00:00Z', TRUE),
    ('40444444-4444-4444-4444-444444444443', 'd4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'Confirm deployment checklist', 'Review deployment readiness checklist with engineering leads.', 'DONE', 'MEDIUM', '2026-04-04T14:00:00Z', TRUE),
    ('40444444-4444-4444-4444-444444444444', 'd4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'Refine incident response playbook', 'Update the incident response playbook based on last quarter learnings.', 'TODO', 'HIGH', '2026-04-18T10:00:00Z', TRUE),
    ('40444444-4444-4444-4444-444444444445', 'd4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'Close pilot feedback survey', 'Collect final feedback from pilot participants and summarize results.', 'DONE', 'LOW', '2026-04-06T17:00:00Z', TRUE);
