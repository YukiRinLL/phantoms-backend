-- Compatibility-first RLS notes.
-- This file is intentionally non-destructive and must be reviewed before execution.
-- It does not drop columns, delete rows, or remove the existing policies used by
-- the legacy pages. The legacy public.users.password format is preserved.

-- Before tightening a table, inspect the current callers and policies:
-- SELECT tablename, policyname, roles, cmd, qual, with_check
-- FROM pg_policies
-- WHERE schemaname = 'public';

-- The following policies are safe additions only when the existing permissive
-- policies have first been replaced in a staged deployment. Do not execute them
-- together with the legacy pages unless their public read/write behavior has been
-- migrated and regression-tested.

-- Example for a future messages-only rollout:
-- CREATE POLICY "Users can insert own messages"
-- ON public.messages FOR INSERT TO authenticated
-- WITH CHECK (user_id = auth.uid());
--
-- CREATE POLICY "Users can update own messages"
-- ON public.messages FOR UPDATE TO authenticated
-- USING (user_id = auth.uid())
-- WITH CHECK (user_id = auth.uid());

-- Do not remove public.users.password until all legacy users have migrated.
-- Do not revoke anonymous grants until every legacy page has been migrated.
