--
-- PostgreSQL database cluster dump
--

-- Started on 2025-03-20 11:52:05

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

CREATE ROLE anon;
ALTER ROLE anon WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE authenticated;
ALTER ROLE authenticated WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE authenticator;
ALTER ROLE authenticator WITH NOSUPERUSER NOINHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE dashboard_user;
ALTER ROLE dashboard_user WITH NOSUPERUSER INHERIT CREATEROLE CREATEDB NOLOGIN REPLICATION NOBYPASSRLS;
CREATE ROLE pgbouncer;
ALTER ROLE pgbouncer WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE pgsodium_keyholder;
ALTER ROLE pgsodium_keyholder WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE pgsodium_keyiduser;
ALTER ROLE pgsodium_keyiduser WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE pgsodium_keymaker;
ALTER ROLE pgsodium_keymaker WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE postgres;
ALTER ROLE postgres WITH NOSUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS;
CREATE ROLE service_role;
ALTER ROLE service_role WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION BYPASSRLS;
CREATE ROLE supabase_admin;
ALTER ROLE supabase_admin WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS;
CREATE ROLE supabase_auth_admin;
ALTER ROLE supabase_auth_admin WITH NOSUPERUSER NOINHERIT CREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE supabase_read_only_user;
ALTER ROLE supabase_read_only_user WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION BYPASSRLS;
CREATE ROLE supabase_realtime_admin;
ALTER ROLE supabase_realtime_admin WITH NOSUPERUSER NOINHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE supabase_replication_admin;
ALTER ROLE supabase_replication_admin WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN REPLICATION NOBYPASSRLS;
CREATE ROLE supabase_storage_admin;
ALTER ROLE supabase_storage_admin WITH NOSUPERUSER NOINHERIT CREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;

--
-- User Configurations
--

--
-- User Config "anon"
--

ALTER ROLE anon SET statement_timeout TO '3s';

--
-- User Config "authenticated"
--

ALTER ROLE authenticated SET statement_timeout TO '8s';

--
-- User Config "authenticator"
--

ALTER ROLE authenticator SET session_preload_libraries TO 'safeupdate';
ALTER ROLE authenticator SET statement_timeout TO '8s';
ALTER ROLE authenticator SET lock_timeout TO '8s';

--
-- User Config "postgres"
--

ALTER ROLE postgres SET search_path TO E'\\$user', 'public', 'extensions';

--
-- User Config "supabase_admin"
--

ALTER ROLE supabase_admin SET search_path TO '$user', 'public', 'auth', 'extensions';
ALTER ROLE supabase_admin SET log_statement TO 'none';

--
-- User Config "supabase_auth_admin"
--

ALTER ROLE supabase_auth_admin SET search_path TO 'auth';
ALTER ROLE supabase_auth_admin SET idle_in_transaction_session_timeout TO '60000';
ALTER ROLE supabase_auth_admin SET log_statement TO 'none';

--
-- User Config "supabase_storage_admin"
--

ALTER ROLE supabase_storage_admin SET search_path TO 'storage';
ALTER ROLE supabase_storage_admin SET log_statement TO 'none';


--
-- Role memberships
--

GRANT anon TO authenticator;
GRANT anon TO postgres;
GRANT authenticated TO authenticator;
GRANT authenticated TO postgres;
GRANT authenticator TO supabase_storage_admin;
GRANT pg_monitor TO postgres;
GRANT pg_read_all_data TO postgres;
GRANT pg_read_all_data TO supabase_read_only_user;
GRANT pg_signal_backend TO postgres;
GRANT pgsodium_keyholder TO pgsodium_keymaker;
GRANT pgsodium_keyholder TO postgres WITH ADMIN OPTION;
GRANT pgsodium_keyholder TO service_role;
GRANT pgsodium_keyiduser TO pgsodium_keyholder;
GRANT pgsodium_keyiduser TO pgsodium_keymaker;
GRANT pgsodium_keyiduser TO postgres WITH ADMIN OPTION;
GRANT pgsodium_keymaker TO postgres WITH ADMIN OPTION;
GRANT service_role TO authenticator;
GRANT service_role TO postgres;
GRANT supabase_auth_admin TO postgres;
GRANT supabase_realtime_admin TO postgres;
GRANT supabase_storage_admin TO postgres;






--
-- Databases
--

--
-- Database "template1" dump
--

\connect template1

--
-- PostgreSQL database dump
--

-- Dumped from database version 15.8
-- Dumped by pg_dump version 17.0

-- Started on 2025-03-20 11:52:08

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Completed on 2025-03-20 11:52:16

--
-- PostgreSQL database dump complete
--

--
-- Database "postgres" dump
--

\connect postgres

--
-- PostgreSQL database dump
--

-- Dumped from database version 15.8
-- Dumped by pg_dump version 17.0

-- Started on 2025-03-20 11:52:16

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 12 (class 2615 OID 29031)
-- Name: auth; Type: SCHEMA; Schema: -; Owner: supabase_admin
--

CREATE SCHEMA auth;


ALTER SCHEMA auth OWNER TO supabase_admin;

--
-- TOC entry 21 (class 2615 OID 29032)
-- Name: extensions; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA extensions;


ALTER SCHEMA extensions OWNER TO postgres;

--
-- TOC entry 19 (class 2615 OID 29033)
-- Name: graphql; Type: SCHEMA; Schema: -; Owner: supabase_admin
--

CREATE SCHEMA graphql;


ALTER SCHEMA graphql OWNER TO supabase_admin;

--
-- TOC entry 18 (class 2615 OID 29034)
-- Name: graphql_public; Type: SCHEMA; Schema: -; Owner: supabase_admin
--

CREATE SCHEMA graphql_public;


ALTER SCHEMA graphql_public OWNER TO supabase_admin;

--
-- TOC entry 13 (class 2615 OID 29035)
-- Name: pgbouncer; Type: SCHEMA; Schema: -; Owner: pgbouncer
--

CREATE SCHEMA pgbouncer;


ALTER SCHEMA pgbouncer OWNER TO pgbouncer;

--
-- TOC entry 15 (class 2615 OID 29036)
-- Name: pgsodium; Type: SCHEMA; Schema: -; Owner: supabase_admin
--

CREATE SCHEMA pgsodium;


ALTER SCHEMA pgsodium OWNER TO supabase_admin;

--
-- TOC entry 2 (class 3079 OID 29037)
-- Name: pgsodium; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgsodium WITH SCHEMA pgsodium;


--
-- TOC entry 4145 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pgsodium; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgsodium IS 'Pgsodium is a modern cryptography library for Postgres.';


--
-- TOC entry 14 (class 2615 OID 29337)
-- Name: realtime; Type: SCHEMA; Schema: -; Owner: supabase_admin
--

CREATE SCHEMA realtime;


ALTER SCHEMA realtime OWNER TO supabase_admin;

--
-- TOC entry 22 (class 2615 OID 29338)
-- Name: storage; Type: SCHEMA; Schema: -; Owner: supabase_admin
--

CREATE SCHEMA storage;


ALTER SCHEMA storage OWNER TO supabase_admin;

--
-- TOC entry 17 (class 2615 OID 29339)
-- Name: vault; Type: SCHEMA; Schema: -; Owner: supabase_admin
--

CREATE SCHEMA vault;


ALTER SCHEMA vault OWNER TO supabase_admin;

--
-- TOC entry 4 (class 3079 OID 30058)
-- Name: pg_graphql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_graphql WITH SCHEMA graphql;


--
-- TOC entry 4149 (class 0 OID 0)
-- Dependencies: 4
-- Name: EXTENSION pg_graphql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_graphql IS 'pg_graphql: GraphQL support';


--
-- TOC entry 8 (class 3079 OID 29350)
-- Name: pg_stat_statements; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_stat_statements WITH SCHEMA extensions;


--
-- TOC entry 4150 (class 0 OID 0)
-- Dependencies: 8
-- Name: EXTENSION pg_stat_statements; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_stat_statements IS 'track planning and execution statistics of all SQL statements executed';


--
-- TOC entry 7 (class 3079 OID 29381)
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA extensions;


--
-- TOC entry 4151 (class 0 OID 0)
-- Dependencies: 7
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- TOC entry 6 (class 3079 OID 29418)
-- Name: pgjwt; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgjwt WITH SCHEMA extensions;


--
-- TOC entry 4152 (class 0 OID 0)
-- Dependencies: 6
-- Name: EXTENSION pgjwt; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgjwt IS 'JSON Web Token API for Postgresql';


--
-- TOC entry 3 (class 3079 OID 29425)
-- Name: supabase_vault; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS supabase_vault WITH SCHEMA vault;


--
-- TOC entry 4153 (class 0 OID 0)
-- Dependencies: 3
-- Name: EXTENSION supabase_vault; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION supabase_vault IS 'Supabase Vault Extension';


--
-- TOC entry 5 (class 3079 OID 29453)
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA extensions;


--
-- TOC entry 4154 (class 0 OID 0)
-- Dependencies: 5
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
-- TOC entry 1191 (class 1247 OID 29465)
-- Name: aal_level; Type: TYPE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TYPE auth.aal_level AS ENUM (
    'aal1',
    'aal2',
    'aal3'
);


ALTER TYPE auth.aal_level OWNER TO supabase_auth_admin;

--
-- TOC entry 1194 (class 1247 OID 29472)
-- Name: code_challenge_method; Type: TYPE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TYPE auth.code_challenge_method AS ENUM (
    's256',
    'plain'
);


ALTER TYPE auth.code_challenge_method OWNER TO supabase_auth_admin;

--
-- TOC entry 1197 (class 1247 OID 29478)
-- Name: factor_status; Type: TYPE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TYPE auth.factor_status AS ENUM (
    'unverified',
    'verified'
);


ALTER TYPE auth.factor_status OWNER TO supabase_auth_admin;

--
-- TOC entry 1200 (class 1247 OID 29484)
-- Name: factor_type; Type: TYPE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TYPE auth.factor_type AS ENUM (
    'totp',
    'webauthn',
    'phone'
);


ALTER TYPE auth.factor_type OWNER TO supabase_auth_admin;

--
-- TOC entry 1203 (class 1247 OID 29492)
-- Name: one_time_token_type; Type: TYPE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TYPE auth.one_time_token_type AS ENUM (
    'confirmation_token',
    'reauthentication_token',
    'recovery_token',
    'email_change_token_new',
    'email_change_token_current',
    'phone_change_token'
);


ALTER TYPE auth.one_time_token_type OWNER TO supabase_auth_admin;

--
-- TOC entry 1206 (class 1247 OID 29506)
-- Name: action; Type: TYPE; Schema: realtime; Owner: supabase_admin
--

CREATE TYPE realtime.action AS ENUM (
    'INSERT',
    'UPDATE',
    'DELETE',
    'TRUNCATE',
    'ERROR'
);


ALTER TYPE realtime.action OWNER TO supabase_admin;

--
-- TOC entry 1209 (class 1247 OID 29518)
-- Name: equality_op; Type: TYPE; Schema: realtime; Owner: supabase_admin
--

CREATE TYPE realtime.equality_op AS ENUM (
    'eq',
    'neq',
    'lt',
    'lte',
    'gt',
    'gte',
    'in'
);


ALTER TYPE realtime.equality_op OWNER TO supabase_admin;

--
-- TOC entry 1212 (class 1247 OID 29535)
-- Name: user_defined_filter; Type: TYPE; Schema: realtime; Owner: supabase_admin
--

CREATE TYPE realtime.user_defined_filter AS (
	column_name text,
	op realtime.equality_op,
	value text
);


ALTER TYPE realtime.user_defined_filter OWNER TO supabase_admin;

--
-- TOC entry 1215 (class 1247 OID 29538)
-- Name: wal_column; Type: TYPE; Schema: realtime; Owner: supabase_admin
--

CREATE TYPE realtime.wal_column AS (
	name text,
	type_name text,
	type_oid oid,
	value jsonb,
	is_pkey boolean,
	is_selectable boolean
);


ALTER TYPE realtime.wal_column OWNER TO supabase_admin;

--
-- TOC entry 1218 (class 1247 OID 29541)
-- Name: wal_rls; Type: TYPE; Schema: realtime; Owner: supabase_admin
--

CREATE TYPE realtime.wal_rls AS (
	wal jsonb,
	is_rls_enabled boolean,
	subscription_ids uuid[],
	errors text[]
);


ALTER TYPE realtime.wal_rls OWNER TO supabase_admin;

--
-- TOC entry 462 (class 1255 OID 29542)
-- Name: email(); Type: FUNCTION; Schema: auth; Owner: supabase_auth_admin
--

CREATE FUNCTION auth.email() RETURNS text
    LANGUAGE sql STABLE
    AS $$
  select 
  coalesce(
    nullif(current_setting('request.jwt.claim.email', true), ''),
    (nullif(current_setting('request.jwt.claims', true), '')::jsonb ->> 'email')
  )::text
$$;


ALTER FUNCTION auth.email() OWNER TO supabase_auth_admin;

--
-- TOC entry 4155 (class 0 OID 0)
-- Dependencies: 462
-- Name: FUNCTION email(); Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON FUNCTION auth.email() IS 'Deprecated. Use auth.jwt() -> ''email'' instead.';


--
-- TOC entry 353 (class 1255 OID 29543)
-- Name: jwt(); Type: FUNCTION; Schema: auth; Owner: supabase_auth_admin
--

CREATE FUNCTION auth.jwt() RETURNS jsonb
    LANGUAGE sql STABLE
    AS $$
  select 
    coalesce(
        nullif(current_setting('request.jwt.claim', true), ''),
        nullif(current_setting('request.jwt.claims', true), '')
    )::jsonb
$$;


ALTER FUNCTION auth.jwt() OWNER TO supabase_auth_admin;

--
-- TOC entry 463 (class 1255 OID 29544)
-- Name: role(); Type: FUNCTION; Schema: auth; Owner: supabase_auth_admin
--

CREATE FUNCTION auth.role() RETURNS text
    LANGUAGE sql STABLE
    AS $$
  select 
  coalesce(
    nullif(current_setting('request.jwt.claim.role', true), ''),
    (nullif(current_setting('request.jwt.claims', true), '')::jsonb ->> 'role')
  )::text
$$;


ALTER FUNCTION auth.role() OWNER TO supabase_auth_admin;

--
-- TOC entry 4158 (class 0 OID 0)
-- Dependencies: 463
-- Name: FUNCTION role(); Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON FUNCTION auth.role() IS 'Deprecated. Use auth.jwt() -> ''role'' instead.';


--
-- TOC entry 352 (class 1255 OID 29545)
-- Name: uid(); Type: FUNCTION; Schema: auth; Owner: supabase_auth_admin
--

CREATE FUNCTION auth.uid() RETURNS uuid
    LANGUAGE sql STABLE
    AS $$
  select 
  coalesce(
    nullif(current_setting('request.jwt.claim.sub', true), ''),
    (nullif(current_setting('request.jwt.claims', true), '')::jsonb ->> 'sub')
  )::uuid
$$;


ALTER FUNCTION auth.uid() OWNER TO supabase_auth_admin;

--
-- TOC entry 4160 (class 0 OID 0)
-- Dependencies: 352
-- Name: FUNCTION uid(); Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON FUNCTION auth.uid() IS 'Deprecated. Use auth.jwt() -> ''sub'' instead.';


--
-- TOC entry 467 (class 1255 OID 29546)
-- Name: grant_pg_cron_access(); Type: FUNCTION; Schema: extensions; Owner: postgres
--

CREATE FUNCTION extensions.grant_pg_cron_access() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF EXISTS (
    SELECT
    FROM pg_event_trigger_ddl_commands() AS ev
    JOIN pg_extension AS ext
    ON ev.objid = ext.oid
    WHERE ext.extname = 'pg_cron'
  )
  THEN
    grant usage on schema cron to postgres with grant option;

    alter default privileges in schema cron grant all on tables to postgres with grant option;
    alter default privileges in schema cron grant all on functions to postgres with grant option;
    alter default privileges in schema cron grant all on sequences to postgres with grant option;

    alter default privileges for user supabase_admin in schema cron grant all
        on sequences to postgres with grant option;
    alter default privileges for user supabase_admin in schema cron grant all
        on tables to postgres with grant option;
    alter default privileges for user supabase_admin in schema cron grant all
        on functions to postgres with grant option;

    grant all privileges on all tables in schema cron to postgres with grant option;
    revoke all on table cron.job from postgres;
    grant select on table cron.job to postgres with grant option;
  END IF;
END;
$$;


ALTER FUNCTION extensions.grant_pg_cron_access() OWNER TO postgres;

--
-- TOC entry 4177 (class 0 OID 0)
-- Dependencies: 467
-- Name: FUNCTION grant_pg_cron_access(); Type: COMMENT; Schema: extensions; Owner: postgres
--

COMMENT ON FUNCTION extensions.grant_pg_cron_access() IS 'Grants access to pg_cron';


--
-- TOC entry 495 (class 1255 OID 29547)
-- Name: grant_pg_graphql_access(); Type: FUNCTION; Schema: extensions; Owner: supabase_admin
--

CREATE FUNCTION extensions.grant_pg_graphql_access() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $_$
DECLARE
    func_is_graphql_resolve bool;
BEGIN
    func_is_graphql_resolve = (
        SELECT n.proname = 'resolve'
        FROM pg_event_trigger_ddl_commands() AS ev
        LEFT JOIN pg_catalog.pg_proc AS n
        ON ev.objid = n.oid
    );

    IF func_is_graphql_resolve
    THEN
        -- Update public wrapper to pass all arguments through to the pg_graphql resolve func
        DROP FUNCTION IF EXISTS graphql_public.graphql;
        create or replace function graphql_public.graphql(
            "operationName" text default null,
            query text default null,
            variables jsonb default null,
            extensions jsonb default null
        )
            returns jsonb
            language sql
        as $$
            select graphql.resolve(
                query := query,
                variables := coalesce(variables, '{}'),
                "operationName" := "operationName",
                extensions := extensions
            );
        $$;

        -- This hook executes when `graphql.resolve` is created. That is not necessarily the last
        -- function in the extension so we need to grant permissions on existing entities AND
        -- update default permissions to any others that are created after `graphql.resolve`
        grant usage on schema graphql to postgres, anon, authenticated, service_role;
        grant select on all tables in schema graphql to postgres, anon, authenticated, service_role;
        grant execute on all functions in schema graphql to postgres, anon, authenticated, service_role;
        grant all on all sequences in schema graphql to postgres, anon, authenticated, service_role;
        alter default privileges in schema graphql grant all on tables to postgres, anon, authenticated, service_role;
        alter default privileges in schema graphql grant all on functions to postgres, anon, authenticated, service_role;
        alter default privileges in schema graphql grant all on sequences to postgres, anon, authenticated, service_role;

        -- Allow postgres role to allow granting usage on graphql and graphql_public schemas to custom roles
        grant usage on schema graphql_public to postgres with grant option;
        grant usage on schema graphql to postgres with grant option;
    END IF;

END;
$_$;


ALTER FUNCTION extensions.grant_pg_graphql_access() OWNER TO supabase_admin;

--
-- TOC entry 4179 (class 0 OID 0)
-- Dependencies: 495
-- Name: FUNCTION grant_pg_graphql_access(); Type: COMMENT; Schema: extensions; Owner: supabase_admin
--

COMMENT ON FUNCTION extensions.grant_pg_graphql_access() IS 'Grants access to pg_graphql';


--
-- TOC entry 521 (class 1255 OID 29548)
-- Name: grant_pg_net_access(); Type: FUNCTION; Schema: extensions; Owner: postgres
--

CREATE FUNCTION extensions.grant_pg_net_access() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM pg_event_trigger_ddl_commands() AS ev
    JOIN pg_extension AS ext
    ON ev.objid = ext.oid
    WHERE ext.extname = 'pg_net'
  )
  THEN
    IF NOT EXISTS (
      SELECT 1
      FROM pg_roles
      WHERE rolname = 'supabase_functions_admin'
    )
    THEN
      CREATE USER supabase_functions_admin NOINHERIT CREATEROLE LOGIN NOREPLICATION;
    END IF;

    GRANT USAGE ON SCHEMA net TO supabase_functions_admin, postgres, anon, authenticated, service_role;

    IF EXISTS (
      SELECT FROM pg_extension
      WHERE extname = 'pg_net'
      -- all versions in use on existing projects as of 2025-02-20
      -- version 0.12.0 onwards don't need these applied
      AND extversion IN ('0.2', '0.6', '0.7', '0.7.1', '0.8', '0.10.0', '0.11.0')
    ) THEN
      ALTER function net.http_get(url text, params jsonb, headers jsonb, timeout_milliseconds integer) SECURITY DEFINER;
      ALTER function net.http_post(url text, body jsonb, params jsonb, headers jsonb, timeout_milliseconds integer) SECURITY DEFINER;

      ALTER function net.http_get(url text, params jsonb, headers jsonb, timeout_milliseconds integer) SET search_path = net;
      ALTER function net.http_post(url text, body jsonb, params jsonb, headers jsonb, timeout_milliseconds integer) SET search_path = net;

      REVOKE ALL ON FUNCTION net.http_get(url text, params jsonb, headers jsonb, timeout_milliseconds integer) FROM PUBLIC;
      REVOKE ALL ON FUNCTION net.http_post(url text, body jsonb, params jsonb, headers jsonb, timeout_milliseconds integer) FROM PUBLIC;

      GRANT EXECUTE ON FUNCTION net.http_get(url text, params jsonb, headers jsonb, timeout_milliseconds integer) TO supabase_functions_admin, postgres, anon, authenticated, service_role;
      GRANT EXECUTE ON FUNCTION net.http_post(url text, body jsonb, params jsonb, headers jsonb, timeout_milliseconds integer) TO supabase_functions_admin, postgres, anon, authenticated, service_role;
    END IF;
  END IF;
END;
$$;


ALTER FUNCTION extensions.grant_pg_net_access() OWNER TO postgres;

--
-- TOC entry 4181 (class 0 OID 0)
-- Dependencies: 521
-- Name: FUNCTION grant_pg_net_access(); Type: COMMENT; Schema: extensions; Owner: postgres
--

COMMENT ON FUNCTION extensions.grant_pg_net_access() IS 'Grants access to pg_net';


--
-- TOC entry 492 (class 1255 OID 29549)
-- Name: pgrst_ddl_watch(); Type: FUNCTION; Schema: extensions; Owner: supabase_admin
--

CREATE FUNCTION extensions.pgrst_ddl_watch() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  cmd record;
BEGIN
  FOR cmd IN SELECT * FROM pg_event_trigger_ddl_commands()
  LOOP
    IF cmd.command_tag IN (
      'CREATE SCHEMA', 'ALTER SCHEMA'
    , 'CREATE TABLE', 'CREATE TABLE AS', 'SELECT INTO', 'ALTER TABLE'
    , 'CREATE FOREIGN TABLE', 'ALTER FOREIGN TABLE'
    , 'CREATE VIEW', 'ALTER VIEW'
    , 'CREATE MATERIALIZED VIEW', 'ALTER MATERIALIZED VIEW'
    , 'CREATE FUNCTION', 'ALTER FUNCTION'
    , 'CREATE TRIGGER'
    , 'CREATE TYPE', 'ALTER TYPE'
    , 'CREATE RULE'
    , 'COMMENT'
    )
    -- don't notify in case of CREATE TEMP table or other objects created on pg_temp
    AND cmd.schema_name is distinct from 'pg_temp'
    THEN
      NOTIFY pgrst, 'reload schema';
    END IF;
  END LOOP;
END; $$;


ALTER FUNCTION extensions.pgrst_ddl_watch() OWNER TO supabase_admin;

--
-- TOC entry 350 (class 1255 OID 29550)
-- Name: pgrst_drop_watch(); Type: FUNCTION; Schema: extensions; Owner: supabase_admin
--

CREATE FUNCTION extensions.pgrst_drop_watch() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  obj record;
BEGIN
  FOR obj IN SELECT * FROM pg_event_trigger_dropped_objects()
  LOOP
    IF obj.object_type IN (
      'schema'
    , 'table'
    , 'foreign table'
    , 'view'
    , 'materialized view'
    , 'function'
    , 'trigger'
    , 'type'
    , 'rule'
    )
    AND obj.is_temporary IS false -- no pg_temp objects
    THEN
      NOTIFY pgrst, 'reload schema';
    END IF;
  END LOOP;
END; $$;


ALTER FUNCTION extensions.pgrst_drop_watch() OWNER TO supabase_admin;

--
-- TOC entry 514 (class 1255 OID 29551)
-- Name: set_graphql_placeholder(); Type: FUNCTION; Schema: extensions; Owner: supabase_admin
--

CREATE FUNCTION extensions.set_graphql_placeholder() RETURNS event_trigger
    LANGUAGE plpgsql
    AS $_$
    DECLARE
    graphql_is_dropped bool;
    BEGIN
    graphql_is_dropped = (
        SELECT ev.schema_name = 'graphql_public'
        FROM pg_event_trigger_dropped_objects() AS ev
        WHERE ev.schema_name = 'graphql_public'
    );

    IF graphql_is_dropped
    THEN
        create or replace function graphql_public.graphql(
            "operationName" text default null,
            query text default null,
            variables jsonb default null,
            extensions jsonb default null
        )
            returns jsonb
            language plpgsql
        as $$
            DECLARE
                server_version float;
            BEGIN
                server_version = (SELECT (SPLIT_PART((select version()), ' ', 2))::float);

                IF server_version >= 14 THEN
                    RETURN jsonb_build_object(
                        'errors', jsonb_build_array(
                            jsonb_build_object(
                                'message', 'pg_graphql extension is not enabled.'
                            )
                        )
                    );
                ELSE
                    RETURN jsonb_build_object(
                        'errors', jsonb_build_array(
                            jsonb_build_object(
                                'message', 'pg_graphql is only available on projects running Postgres 14 onwards.'
                            )
                        )
                    );
                END IF;
            END;
        $$;
    END IF;

    END;
$_$;


ALTER FUNCTION extensions.set_graphql_placeholder() OWNER TO supabase_admin;

--
-- TOC entry 4210 (class 0 OID 0)
-- Dependencies: 514
-- Name: FUNCTION set_graphql_placeholder(); Type: COMMENT; Schema: extensions; Owner: supabase_admin
--

COMMENT ON FUNCTION extensions.set_graphql_placeholder() IS 'Reintroduces placeholder function for graphql_public.graphql';


--
-- TOC entry 448 (class 1255 OID 29552)
-- Name: get_auth(text); Type: FUNCTION; Schema: pgbouncer; Owner: supabase_admin
--

CREATE FUNCTION pgbouncer.get_auth(p_usename text) RETURNS TABLE(username text, password text)
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
    RAISE WARNING 'PgBouncer auth request: %', p_usename;

    RETURN QUERY
    SELECT usename::TEXT, passwd::TEXT FROM pg_catalog.pg_shadow
    WHERE usename = p_usename;
END;
$$;


ALTER FUNCTION pgbouncer.get_auth(p_usename text) OWNER TO supabase_admin;

--
-- TOC entry 468 (class 1255 OID 29553)
-- Name: apply_rls(jsonb, integer); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.apply_rls(wal jsonb, max_record_bytes integer DEFAULT (1024 * 1024)) RETURNS SETOF realtime.wal_rls
    LANGUAGE plpgsql
    AS $$
declare
-- Regclass of the table e.g. public.notes
entity_ regclass = (quote_ident(wal ->> 'schema') || '.' || quote_ident(wal ->> 'table'))::regclass;

-- I, U, D, T: insert, update ...
action realtime.action = (
    case wal ->> 'action'
        when 'I' then 'INSERT'
        when 'U' then 'UPDATE'
        when 'D' then 'DELETE'
        else 'ERROR'
    end
);

-- Is row level security enabled for the table
is_rls_enabled bool = relrowsecurity from pg_class where oid = entity_;

subscriptions realtime.subscription[] = array_agg(subs)
    from
        realtime.subscription subs
    where
        subs.entity = entity_;

-- Subscription vars
roles regrole[] = array_agg(distinct us.claims_role::text)
    from
        unnest(subscriptions) us;

working_role regrole;
claimed_role regrole;
claims jsonb;

subscription_id uuid;
subscription_has_access bool;
visible_to_subscription_ids uuid[] = '{}';

-- structured info for wal's columns
columns realtime.wal_column[];
-- previous identity values for update/delete
old_columns realtime.wal_column[];

error_record_exceeds_max_size boolean = octet_length(wal::text) > max_record_bytes;

-- Primary jsonb output for record
output jsonb;

begin
perform set_config('role', null, true);

columns =
    array_agg(
        (
            x->>'name',
            x->>'type',
            x->>'typeoid',
            realtime.cast(
                (x->'value') #>> '{}',
                coalesce(
                    (x->>'typeoid')::regtype, -- null when wal2json version <= 2.4
                    (x->>'type')::regtype
                )
            ),
            (pks ->> 'name') is not null,
            true
        )::realtime.wal_column
    )
    from
        jsonb_array_elements(wal -> 'columns') x
        left join jsonb_array_elements(wal -> 'pk') pks
            on (x ->> 'name') = (pks ->> 'name');

old_columns =
    array_agg(
        (
            x->>'name',
            x->>'type',
            x->>'typeoid',
            realtime.cast(
                (x->'value') #>> '{}',
                coalesce(
                    (x->>'typeoid')::regtype, -- null when wal2json version <= 2.4
                    (x->>'type')::regtype
                )
            ),
            (pks ->> 'name') is not null,
            true
        )::realtime.wal_column
    )
    from
        jsonb_array_elements(wal -> 'identity') x
        left join jsonb_array_elements(wal -> 'pk') pks
            on (x ->> 'name') = (pks ->> 'name');

for working_role in select * from unnest(roles) loop

    -- Update `is_selectable` for columns and old_columns
    columns =
        array_agg(
            (
                c.name,
                c.type_name,
                c.type_oid,
                c.value,
                c.is_pkey,
                pg_catalog.has_column_privilege(working_role, entity_, c.name, 'SELECT')
            )::realtime.wal_column
        )
        from
            unnest(columns) c;

    old_columns =
            array_agg(
                (
                    c.name,
                    c.type_name,
                    c.type_oid,
                    c.value,
                    c.is_pkey,
                    pg_catalog.has_column_privilege(working_role, entity_, c.name, 'SELECT')
                )::realtime.wal_column
            )
            from
                unnest(old_columns) c;

    if action <> 'DELETE' and count(1) = 0 from unnest(columns) c where c.is_pkey then
        return next (
            jsonb_build_object(
                'schema', wal ->> 'schema',
                'table', wal ->> 'table',
                'type', action
            ),
            is_rls_enabled,
            -- subscriptions is already filtered by entity
            (select array_agg(s.subscription_id) from unnest(subscriptions) as s where claims_role = working_role),
            array['Error 400: Bad Request, no primary key']
        )::realtime.wal_rls;

    -- The claims role does not have SELECT permission to the primary key of entity
    elsif action <> 'DELETE' and sum(c.is_selectable::int) <> count(1) from unnest(columns) c where c.is_pkey then
        return next (
            jsonb_build_object(
                'schema', wal ->> 'schema',
                'table', wal ->> 'table',
                'type', action
            ),
            is_rls_enabled,
            (select array_agg(s.subscription_id) from unnest(subscriptions) as s where claims_role = working_role),
            array['Error 401: Unauthorized']
        )::realtime.wal_rls;

    else
        output = jsonb_build_object(
            'schema', wal ->> 'schema',
            'table', wal ->> 'table',
            'type', action,
            'commit_timestamp', to_char(
                ((wal ->> 'timestamp')::timestamptz at time zone 'utc'),
                'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"'
            ),
            'columns', (
                select
                    jsonb_agg(
                        jsonb_build_object(
                            'name', pa.attname,
                            'type', pt.typname
                        )
                        order by pa.attnum asc
                    )
                from
                    pg_attribute pa
                    join pg_type pt
                        on pa.atttypid = pt.oid
                where
                    attrelid = entity_
                    and attnum > 0
                    and pg_catalog.has_column_privilege(working_role, entity_, pa.attname, 'SELECT')
            )
        )
        -- Add "record" key for insert and update
        || case
            when action in ('INSERT', 'UPDATE') then
                jsonb_build_object(
                    'record',
                    (
                        select
                            jsonb_object_agg(
                                -- if unchanged toast, get column name and value from old record
                                coalesce((c).name, (oc).name),
                                case
                                    when (c).name is null then (oc).value
                                    else (c).value
                                end
                            )
                        from
                            unnest(columns) c
                            full outer join unnest(old_columns) oc
                                on (c).name = (oc).name
                        where
                            coalesce((c).is_selectable, (oc).is_selectable)
                            and ( not error_record_exceeds_max_size or (octet_length((c).value::text) <= 64))
                    )
                )
            else '{}'::jsonb
        end
        -- Add "old_record" key for update and delete
        || case
            when action = 'UPDATE' then
                jsonb_build_object(
                        'old_record',
                        (
                            select jsonb_object_agg((c).name, (c).value)
                            from unnest(old_columns) c
                            where
                                (c).is_selectable
                                and ( not error_record_exceeds_max_size or (octet_length((c).value::text) <= 64))
                        )
                    )
            when action = 'DELETE' then
                jsonb_build_object(
                    'old_record',
                    (
                        select jsonb_object_agg((c).name, (c).value)
                        from unnest(old_columns) c
                        where
                            (c).is_selectable
                            and ( not error_record_exceeds_max_size or (octet_length((c).value::text) <= 64))
                            and ( not is_rls_enabled or (c).is_pkey ) -- if RLS enabled, we can't secure deletes so filter to pkey
                    )
                )
            else '{}'::jsonb
        end;

        -- Create the prepared statement
        if is_rls_enabled and action <> 'DELETE' then
            if (select 1 from pg_prepared_statements where name = 'walrus_rls_stmt' limit 1) > 0 then
                deallocate walrus_rls_stmt;
            end if;
            execute realtime.build_prepared_statement_sql('walrus_rls_stmt', entity_, columns);
        end if;

        visible_to_subscription_ids = '{}';

        for subscription_id, claims in (
                select
                    subs.subscription_id,
                    subs.claims
                from
                    unnest(subscriptions) subs
                where
                    subs.entity = entity_
                    and subs.claims_role = working_role
                    and (
                        realtime.is_visible_through_filters(columns, subs.filters)
                        or (
                          action = 'DELETE'
                          and realtime.is_visible_through_filters(old_columns, subs.filters)
                        )
                    )
        ) loop

            if not is_rls_enabled or action = 'DELETE' then
                visible_to_subscription_ids = visible_to_subscription_ids || subscription_id;
            else
                -- Check if RLS allows the role to see the record
                perform
                    -- Trim leading and trailing quotes from working_role because set_config
                    -- doesn't recognize the role as valid if they are included
                    set_config('role', trim(both '"' from working_role::text), true),
                    set_config('request.jwt.claims', claims::text, true);

                execute 'execute walrus_rls_stmt' into subscription_has_access;

                if subscription_has_access then
                    visible_to_subscription_ids = visible_to_subscription_ids || subscription_id;
                end if;
            end if;
        end loop;

        perform set_config('role', null, true);

        return next (
            output,
            is_rls_enabled,
            visible_to_subscription_ids,
            case
                when error_record_exceeds_max_size then array['Error 413: Payload Too Large']
                else '{}'
            end
        )::realtime.wal_rls;

    end if;
end loop;

perform set_config('role', null, true);
end;
$$;


ALTER FUNCTION realtime.apply_rls(wal jsonb, max_record_bytes integer) OWNER TO supabase_admin;

--
-- TOC entry 451 (class 1255 OID 29555)
-- Name: broadcast_changes(text, text, text, text, text, record, record, text); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.broadcast_changes(topic_name text, event_name text, operation text, table_name text, table_schema text, new record, old record, level text DEFAULT 'ROW'::text) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
    -- Declare a variable to hold the JSONB representation of the row
    row_data jsonb := '{}'::jsonb;
BEGIN
    IF level = 'STATEMENT' THEN
        RAISE EXCEPTION 'function can only be triggered for each row, not for each statement';
    END IF;
    -- Check the operation type and handle accordingly
    IF operation = 'INSERT' OR operation = 'UPDATE' OR operation = 'DELETE' THEN
        row_data := jsonb_build_object('old_record', OLD, 'record', NEW, 'operation', operation, 'table', table_name, 'schema', table_schema);
        PERFORM realtime.send (row_data, event_name, topic_name);
    ELSE
        RAISE EXCEPTION 'Unexpected operation type: %', operation;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Failed to process the row: %', SQLERRM;
END;

$$;


ALTER FUNCTION realtime.broadcast_changes(topic_name text, event_name text, operation text, table_name text, table_schema text, new record, old record, level text) OWNER TO supabase_admin;

--
-- TOC entry 465 (class 1255 OID 29556)
-- Name: build_prepared_statement_sql(text, regclass, realtime.wal_column[]); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]) RETURNS text
    LANGUAGE sql
    AS $$
      /*
      Builds a sql string that, if executed, creates a prepared statement to
      tests retrive a row from *entity* by its primary key columns.
      Example
          select realtime.build_prepared_statement_sql('public.notes', '{"id"}'::text[], '{"bigint"}'::text[])
      */
          select
      'prepare ' || prepared_statement_name || ' as
          select
              exists(
                  select
                      1
                  from
                      ' || entity || '
                  where
                      ' || string_agg(quote_ident(pkc.name) || '=' || quote_nullable(pkc.value #>> '{}') , ' and ') || '
              )'
          from
              unnest(columns) pkc
          where
              pkc.is_pkey
          group by
              entity
      $$;


ALTER FUNCTION realtime.build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]) OWNER TO supabase_admin;

--
-- TOC entry 447 (class 1255 OID 29557)
-- Name: cast(text, regtype); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime."cast"(val text, type_ regtype) RETURNS jsonb
    LANGUAGE plpgsql IMMUTABLE
    AS $$
    declare
      res jsonb;
    begin
      execute format('select to_jsonb(%L::'|| type_::text || ')', val)  into res;
      return res;
    end
    $$;


ALTER FUNCTION realtime."cast"(val text, type_ regtype) OWNER TO supabase_admin;

--
-- TOC entry 512 (class 1255 OID 29558)
-- Name: check_equality_op(realtime.equality_op, regtype, text, text); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text) RETURNS boolean
    LANGUAGE plpgsql IMMUTABLE
    AS $$
      /*
      Casts *val_1* and *val_2* as type *type_* and check the *op* condition for truthiness
      */
      declare
          op_symbol text = (
              case
                  when op = 'eq' then '='
                  when op = 'neq' then '!='
                  when op = 'lt' then '<'
                  when op = 'lte' then '<='
                  when op = 'gt' then '>'
                  when op = 'gte' then '>='
                  when op = 'in' then '= any'
                  else 'UNKNOWN OP'
              end
          );
          res boolean;
      begin
          execute format(
              'select %L::'|| type_::text || ' ' || op_symbol
              || ' ( %L::'
              || (
                  case
                      when op = 'in' then type_::text || '[]'
                      else type_::text end
              )
              || ')', val_1, val_2) into res;
          return res;
      end;
      $$;


ALTER FUNCTION realtime.check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text) OWNER TO supabase_admin;

--
-- TOC entry 496 (class 1255 OID 29559)
-- Name: is_visible_through_filters(realtime.wal_column[], realtime.user_defined_filter[]); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$
    /*
    Should the record be visible (true) or filtered out (false) after *filters* are applied
    */
        select
            -- Default to allowed when no filters present
            $2 is null -- no filters. this should not happen because subscriptions has a default
            or array_length($2, 1) is null -- array length of an empty array is null
            or bool_and(
                coalesce(
                    realtime.check_equality_op(
                        op:=f.op,
                        type_:=coalesce(
                            col.type_oid::regtype, -- null when wal2json version <= 2.4
                            col.type_name::regtype
                        ),
                        -- cast jsonb to text
                        val_1:=col.value #>> '{}',
                        val_2:=f.value
                    ),
                    false -- if null, filter does not match
                )
            )
        from
            unnest(filters) f
            join unnest(columns) col
                on f.column_name = col.name;
    $_$;


ALTER FUNCTION realtime.is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]) OWNER TO supabase_admin;

--
-- TOC entry 497 (class 1255 OID 29560)
-- Name: list_changes(name, name, integer, integer); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer) RETURNS SETOF realtime.wal_rls
    LANGUAGE sql
    SET log_min_messages TO 'fatal'
    AS $$
      with pub as (
        select
          concat_ws(
            ',',
            case when bool_or(pubinsert) then 'insert' else null end,
            case when bool_or(pubupdate) then 'update' else null end,
            case when bool_or(pubdelete) then 'delete' else null end
          ) as w2j_actions,
          coalesce(
            string_agg(
              realtime.quote_wal2json(format('%I.%I', schemaname, tablename)::regclass),
              ','
            ) filter (where ppt.tablename is not null and ppt.tablename not like '% %'),
            ''
          ) w2j_add_tables
        from
          pg_publication pp
          left join pg_publication_tables ppt
            on pp.pubname = ppt.pubname
        where
          pp.pubname = publication
        group by
          pp.pubname
        limit 1
      ),
      w2j as (
        select
          x.*, pub.w2j_add_tables
        from
          pub,
          pg_logical_slot_get_changes(
            slot_name, null, max_changes,
            'include-pk', 'true',
            'include-transaction', 'false',
            'include-timestamp', 'true',
            'include-type-oids', 'true',
            'format-version', '2',
            'actions', pub.w2j_actions,
            'add-tables', pub.w2j_add_tables
          ) x
      )
      select
        xyz.wal,
        xyz.is_rls_enabled,
        xyz.subscription_ids,
        xyz.errors
      from
        w2j,
        realtime.apply_rls(
          wal := w2j.data::jsonb,
          max_record_bytes := max_record_bytes
        ) xyz(wal, is_rls_enabled, subscription_ids, errors)
      where
        w2j.w2j_add_tables <> ''
        and xyz.subscription_ids[1] is not null
    $$;


ALTER FUNCTION realtime.list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer) OWNER TO supabase_admin;

--
-- TOC entry 501 (class 1255 OID 29561)
-- Name: quote_wal2json(regclass); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.quote_wal2json(entity regclass) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $$
      select
        (
          select string_agg('' || ch,'')
          from unnest(string_to_array(nsp.nspname::text, null)) with ordinality x(ch, idx)
          where
            not (x.idx = 1 and x.ch = '"')
            and not (
              x.idx = array_length(string_to_array(nsp.nspname::text, null), 1)
              and x.ch = '"'
            )
        )
        || '.'
        || (
          select string_agg('' || ch,'')
          from unnest(string_to_array(pc.relname::text, null)) with ordinality x(ch, idx)
          where
            not (x.idx = 1 and x.ch = '"')
            and not (
              x.idx = array_length(string_to_array(nsp.nspname::text, null), 1)
              and x.ch = '"'
            )
          )
      from
        pg_class pc
        join pg_namespace nsp
          on pc.relnamespace = nsp.oid
      where
        pc.oid = entity
    $$;


ALTER FUNCTION realtime.quote_wal2json(entity regclass) OWNER TO supabase_admin;

--
-- TOC entry 508 (class 1255 OID 29562)
-- Name: send(jsonb, text, text, boolean); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.send(payload jsonb, event text, topic text, private boolean DEFAULT true) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
  BEGIN
    -- Set the topic configuration
    EXECUTE format('SET LOCAL realtime.topic TO %L', topic);

    -- Attempt to insert the message
    INSERT INTO realtime.messages (payload, event, topic, private, extension)
    VALUES (payload, event, topic, private, 'broadcast');
  EXCEPTION
    WHEN OTHERS THEN
      -- Capture and notify the error
      PERFORM pg_notify(
          'realtime:system',
          jsonb_build_object(
              'error', SQLERRM,
              'function', 'realtime.send',
              'event', event,
              'topic', topic,
              'private', private
          )::text
      );
  END;
END;
$$;


ALTER FUNCTION realtime.send(payload jsonb, event text, topic text, private boolean) OWNER TO supabase_admin;

--
-- TOC entry 513 (class 1255 OID 29563)
-- Name: subscription_check_filters(); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.subscription_check_filters() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    /*
    Validates that the user defined filters for a subscription:
    - refer to valid columns that the claimed role may access
    - values are coercable to the correct column type
    */
    declare
        col_names text[] = coalesce(
                array_agg(c.column_name order by c.ordinal_position),
                '{}'::text[]
            )
            from
                information_schema.columns c
            where
                format('%I.%I', c.table_schema, c.table_name)::regclass = new.entity
                and pg_catalog.has_column_privilege(
                    (new.claims ->> 'role'),
                    format('%I.%I', c.table_schema, c.table_name)::regclass,
                    c.column_name,
                    'SELECT'
                );
        filter realtime.user_defined_filter;
        col_type regtype;

        in_val jsonb;
    begin
        for filter in select * from unnest(new.filters) loop
            -- Filtered column is valid
            if not filter.column_name = any(col_names) then
                raise exception 'invalid column for filter %', filter.column_name;
            end if;

            -- Type is sanitized and safe for string interpolation
            col_type = (
                select atttypid::regtype
                from pg_catalog.pg_attribute
                where attrelid = new.entity
                      and attname = filter.column_name
            );
            if col_type is null then
                raise exception 'failed to lookup type for column %', filter.column_name;
            end if;

            -- Set maximum number of entries for in filter
            if filter.op = 'in'::realtime.equality_op then
                in_val = realtime.cast(filter.value, (col_type::text || '[]')::regtype);
                if coalesce(jsonb_array_length(in_val), 0) > 100 then
                    raise exception 'too many values for `in` filter. Maximum 100';
                end if;
            else
                -- raises an exception if value is not coercable to type
                perform realtime.cast(filter.value, col_type);
            end if;

        end loop;

        -- Apply consistent order to filters so the unique constraint on
        -- (subscription_id, entity, filters) can't be tricked by a different filter order
        new.filters = coalesce(
            array_agg(f order by f.column_name, f.op, f.value),
            '{}'
        ) from unnest(new.filters) f;

        return new;
    end;
    $$;


ALTER FUNCTION realtime.subscription_check_filters() OWNER TO supabase_admin;

--
-- TOC entry 452 (class 1255 OID 29564)
-- Name: to_regrole(text); Type: FUNCTION; Schema: realtime; Owner: supabase_admin
--

CREATE FUNCTION realtime.to_regrole(role_name text) RETURNS regrole
    LANGUAGE sql IMMUTABLE
    AS $$ select role_name::regrole $$;


ALTER FUNCTION realtime.to_regrole(role_name text) OWNER TO supabase_admin;

--
-- TOC entry 453 (class 1255 OID 29565)
-- Name: topic(); Type: FUNCTION; Schema: realtime; Owner: supabase_realtime_admin
--

CREATE FUNCTION realtime.topic() RETURNS text
    LANGUAGE sql STABLE
    AS $$
select nullif(current_setting('realtime.topic', true), '')::text;
$$;


ALTER FUNCTION realtime.topic() OWNER TO supabase_realtime_admin;

--
-- TOC entry 454 (class 1255 OID 29566)
-- Name: can_insert_object(text, text, uuid, jsonb); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.can_insert_object(bucketid text, name text, owner uuid, metadata jsonb) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
  INSERT INTO "storage"."objects" ("bucket_id", "name", "owner", "metadata") VALUES (bucketid, name, owner, metadata);
  -- hack to rollback the successful insert
  RAISE sqlstate 'PT200' using
  message = 'ROLLBACK',
  detail = 'rollback successful insert';
END
$$;


ALTER FUNCTION storage.can_insert_object(bucketid text, name text, owner uuid, metadata jsonb) OWNER TO supabase_storage_admin;

--
-- TOC entry 449 (class 1255 OID 29567)
-- Name: extension(text); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.extension(name text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
_parts text[];
_filename text;
BEGIN
	select string_to_array(name, '/') into _parts;
	select _parts[array_length(_parts,1)] into _filename;
	-- @todo return the last part instead of 2
	return reverse(split_part(reverse(_filename), '.', 1));
END
$$;


ALTER FUNCTION storage.extension(name text) OWNER TO supabase_storage_admin;

--
-- TOC entry 450 (class 1255 OID 29568)
-- Name: filename(text); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.filename(name text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
_parts text[];
BEGIN
	select string_to_array(name, '/') into _parts;
	return _parts[array_length(_parts,1)];
END
$$;


ALTER FUNCTION storage.filename(name text) OWNER TO supabase_storage_admin;

--
-- TOC entry 455 (class 1255 OID 29569)
-- Name: foldername(text); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.foldername(name text) RETURNS text[]
    LANGUAGE plpgsql
    AS $$
DECLARE
_parts text[];
BEGIN
	select string_to_array(name, '/') into _parts;
	return _parts[1:array_length(_parts,1)-1];
END
$$;


ALTER FUNCTION storage.foldername(name text) OWNER TO supabase_storage_admin;

--
-- TOC entry 354 (class 1255 OID 29570)
-- Name: get_size_by_bucket(); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.get_size_by_bucket() RETURNS TABLE(size bigint, bucket_id text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    return query
        select sum((metadata->>'size')::int) as size, obj.bucket_id
        from "storage".objects as obj
        group by obj.bucket_id;
END
$$;


ALTER FUNCTION storage.get_size_by_bucket() OWNER TO supabase_storage_admin;

--
-- TOC entry 503 (class 1255 OID 29571)
-- Name: list_multipart_uploads_with_delimiter(text, text, text, integer, text, text); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.list_multipart_uploads_with_delimiter(bucket_id text, prefix_param text, delimiter_param text, max_keys integer DEFAULT 100, next_key_token text DEFAULT ''::text, next_upload_token text DEFAULT ''::text) RETURNS TABLE(key text, id text, created_at timestamp with time zone)
    LANGUAGE plpgsql
    AS $_$
BEGIN
    RETURN QUERY EXECUTE
        'SELECT DISTINCT ON(key COLLATE "C") * from (
            SELECT
                CASE
                    WHEN position($2 IN substring(key from length($1) + 1)) > 0 THEN
                        substring(key from 1 for length($1) + position($2 IN substring(key from length($1) + 1)))
                    ELSE
                        key
                END AS key, id, created_at
            FROM
                storage.s3_multipart_uploads
            WHERE
                bucket_id = $5 AND
                key ILIKE $1 || ''%'' AND
                CASE
                    WHEN $4 != '''' AND $6 = '''' THEN
                        CASE
                            WHEN position($2 IN substring(key from length($1) + 1)) > 0 THEN
                                substring(key from 1 for length($1) + position($2 IN substring(key from length($1) + 1))) COLLATE "C" > $4
                            ELSE
                                key COLLATE "C" > $4
                            END
                    ELSE
                        true
                END AND
                CASE
                    WHEN $6 != '''' THEN
                        id COLLATE "C" > $6
                    ELSE
                        true
                    END
            ORDER BY
                key COLLATE "C" ASC, created_at ASC) as e order by key COLLATE "C" LIMIT $3'
        USING prefix_param, delimiter_param, max_keys, next_key_token, bucket_id, next_upload_token;
END;
$_$;


ALTER FUNCTION storage.list_multipart_uploads_with_delimiter(bucket_id text, prefix_param text, delimiter_param text, max_keys integer, next_key_token text, next_upload_token text) OWNER TO supabase_storage_admin;

--
-- TOC entry 502 (class 1255 OID 29572)
-- Name: list_objects_with_delimiter(text, text, text, integer, text, text); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.list_objects_with_delimiter(bucket_id text, prefix_param text, delimiter_param text, max_keys integer DEFAULT 100, start_after text DEFAULT ''::text, next_token text DEFAULT ''::text) RETURNS TABLE(name text, id uuid, metadata jsonb, updated_at timestamp with time zone)
    LANGUAGE plpgsql
    AS $_$
BEGIN
    RETURN QUERY EXECUTE
        'SELECT DISTINCT ON(name COLLATE "C") * from (
            SELECT
                CASE
                    WHEN position($2 IN substring(name from length($1) + 1)) > 0 THEN
                        substring(name from 1 for length($1) + position($2 IN substring(name from length($1) + 1)))
                    ELSE
                        name
                END AS name, id, metadata, updated_at
            FROM
                storage.objects
            WHERE
                bucket_id = $5 AND
                name ILIKE $1 || ''%'' AND
                CASE
                    WHEN $6 != '''' THEN
                    name COLLATE "C" > $6
                ELSE true END
                AND CASE
                    WHEN $4 != '''' THEN
                        CASE
                            WHEN position($2 IN substring(name from length($1) + 1)) > 0 THEN
                                substring(name from 1 for length($1) + position($2 IN substring(name from length($1) + 1))) COLLATE "C" > $4
                            ELSE
                                name COLLATE "C" > $4
                            END
                    ELSE
                        true
                END
            ORDER BY
                name COLLATE "C" ASC) as e order by name COLLATE "C" LIMIT $3'
        USING prefix_param, delimiter_param, max_keys, next_token, bucket_id, start_after;
END;
$_$;


ALTER FUNCTION storage.list_objects_with_delimiter(bucket_id text, prefix_param text, delimiter_param text, max_keys integer, start_after text, next_token text) OWNER TO supabase_storage_admin;

--
-- TOC entry 461 (class 1255 OID 29573)
-- Name: operation(); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.operation() RETURNS text
    LANGUAGE plpgsql STABLE
    AS $$
BEGIN
    RETURN current_setting('storage.operation', true);
END;
$$;


ALTER FUNCTION storage.operation() OWNER TO supabase_storage_admin;

--
-- TOC entry 459 (class 1255 OID 29574)
-- Name: search(text, text, integer, integer, integer, text, text, text); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.search(prefix text, bucketname text, limits integer DEFAULT 100, levels integer DEFAULT 1, offsets integer DEFAULT 0, search text DEFAULT ''::text, sortcolumn text DEFAULT 'name'::text, sortorder text DEFAULT 'asc'::text) RETURNS TABLE(name text, id uuid, updated_at timestamp with time zone, created_at timestamp with time zone, last_accessed_at timestamp with time zone, metadata jsonb)
    LANGUAGE plpgsql STABLE
    AS $_$
declare
  v_order_by text;
  v_sort_order text;
begin
  case
    when sortcolumn = 'name' then
      v_order_by = 'name';
    when sortcolumn = 'updated_at' then
      v_order_by = 'updated_at';
    when sortcolumn = 'created_at' then
      v_order_by = 'created_at';
    when sortcolumn = 'last_accessed_at' then
      v_order_by = 'last_accessed_at';
    else
      v_order_by = 'name';
  end case;

  case
    when sortorder = 'asc' then
      v_sort_order = 'asc';
    when sortorder = 'desc' then
      v_sort_order = 'desc';
    else
      v_sort_order = 'asc';
  end case;

  v_order_by = v_order_by || ' ' || v_sort_order;

  return query execute
    'with folders as (
       select path_tokens[$1] as folder
       from storage.objects
         where objects.name ilike $2 || $3 || ''%''
           and bucket_id = $4
           and array_length(objects.path_tokens, 1) <> $1
       group by folder
       order by folder ' || v_sort_order || '
     )
     (select folder as "name",
            null as id,
            null as updated_at,
            null as created_at,
            null as last_accessed_at,
            null as metadata from folders)
     union all
     (select path_tokens[$1] as "name",
            id,
            updated_at,
            created_at,
            last_accessed_at,
            metadata
     from storage.objects
     where objects.name ilike $2 || $3 || ''%''
       and bucket_id = $4
       and array_length(objects.path_tokens, 1) = $1
     order by ' || v_order_by || ')
     limit $5
     offset $6' using levels, prefix, search, bucketname, limits, offsets;
end;
$_$;


ALTER FUNCTION storage.search(prefix text, bucketname text, limits integer, levels integer, offsets integer, search text, sortcolumn text, sortorder text) OWNER TO supabase_storage_admin;

--
-- TOC entry 460 (class 1255 OID 29575)
-- Name: update_updated_at_column(); Type: FUNCTION; Schema: storage; Owner: supabase_storage_admin
--

CREATE FUNCTION storage.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW; 
END;
$$;


ALTER FUNCTION storage.update_updated_at_column() OWNER TO supabase_storage_admin;

--
-- TOC entry 332 (class 1255 OID 29449)
-- Name: secrets_encrypt_secret_secret(); Type: FUNCTION; Schema: vault; Owner: supabase_admin
--

CREATE FUNCTION vault.secrets_encrypt_secret_secret() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
		BEGIN
		        new.secret = CASE WHEN new.secret IS NULL THEN NULL ELSE
			CASE WHEN new.key_id IS NULL THEN NULL ELSE pg_catalog.encode(
			  pgsodium.crypto_aead_det_encrypt(
				pg_catalog.convert_to(new.secret, 'utf8'),
				pg_catalog.convert_to((new.id::text || new.description::text || new.created_at::text || new.updated_at::text)::text, 'utf8'),
				new.key_id::uuid,
				new.nonce
			  ),
				'base64') END END;
		RETURN new;
		END;
		$$;


ALTER FUNCTION vault.secrets_encrypt_secret_secret() OWNER TO supabase_admin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 251 (class 1259 OID 29576)
-- Name: audit_log_entries; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.audit_log_entries (
    instance_id uuid,
    id uuid NOT NULL,
    payload json,
    created_at timestamp with time zone,
    ip_address character varying(64) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE auth.audit_log_entries OWNER TO supabase_auth_admin;

--
-- TOC entry 4254 (class 0 OID 0)
-- Dependencies: 251
-- Name: TABLE audit_log_entries; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.audit_log_entries IS 'Auth: Audit trail for user actions.';


--
-- TOC entry 252 (class 1259 OID 29582)
-- Name: flow_state; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.flow_state (
    id uuid NOT NULL,
    user_id uuid,
    auth_code text NOT NULL,
    code_challenge_method auth.code_challenge_method NOT NULL,
    code_challenge text NOT NULL,
    provider_type text NOT NULL,
    provider_access_token text,
    provider_refresh_token text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    authentication_method text NOT NULL,
    auth_code_issued_at timestamp with time zone
);


ALTER TABLE auth.flow_state OWNER TO supabase_auth_admin;

--
-- TOC entry 4256 (class 0 OID 0)
-- Dependencies: 252
-- Name: TABLE flow_state; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.flow_state IS 'stores metadata for pkce logins';


--
-- TOC entry 253 (class 1259 OID 29587)
-- Name: identities; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.identities (
    provider_id text NOT NULL,
    user_id uuid NOT NULL,
    identity_data jsonb NOT NULL,
    provider text NOT NULL,
    last_sign_in_at timestamp with time zone,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    email text GENERATED ALWAYS AS (lower((identity_data ->> 'email'::text))) STORED,
    id uuid DEFAULT gen_random_uuid() NOT NULL
);


ALTER TABLE auth.identities OWNER TO supabase_auth_admin;

--
-- TOC entry 4258 (class 0 OID 0)
-- Dependencies: 253
-- Name: TABLE identities; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.identities IS 'Auth: Stores identities associated to a user.';


--
-- TOC entry 4259 (class 0 OID 0)
-- Dependencies: 253
-- Name: COLUMN identities.email; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON COLUMN auth.identities.email IS 'Auth: Email is a generated column that references the optional email property in the identity_data';


--
-- TOC entry 254 (class 1259 OID 29594)
-- Name: instances; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.instances (
    id uuid NOT NULL,
    uuid uuid,
    raw_base_config text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone
);


ALTER TABLE auth.instances OWNER TO supabase_auth_admin;

--
-- TOC entry 4261 (class 0 OID 0)
-- Dependencies: 254
-- Name: TABLE instances; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.instances IS 'Auth: Manages users across multiple sites.';


--
-- TOC entry 255 (class 1259 OID 29599)
-- Name: mfa_amr_claims; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.mfa_amr_claims (
    session_id uuid NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    authentication_method text NOT NULL,
    id uuid NOT NULL
);


ALTER TABLE auth.mfa_amr_claims OWNER TO supabase_auth_admin;

--
-- TOC entry 4263 (class 0 OID 0)
-- Dependencies: 255
-- Name: TABLE mfa_amr_claims; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.mfa_amr_claims IS 'auth: stores authenticator method reference claims for multi factor authentication';


--
-- TOC entry 256 (class 1259 OID 29604)
-- Name: mfa_challenges; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.mfa_challenges (
    id uuid NOT NULL,
    factor_id uuid NOT NULL,
    created_at timestamp with time zone NOT NULL,
    verified_at timestamp with time zone,
    ip_address inet NOT NULL,
    otp_code text,
    web_authn_session_data jsonb
);


ALTER TABLE auth.mfa_challenges OWNER TO supabase_auth_admin;

--
-- TOC entry 4265 (class 0 OID 0)
-- Dependencies: 256
-- Name: TABLE mfa_challenges; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.mfa_challenges IS 'auth: stores metadata about challenge requests made';


--
-- TOC entry 257 (class 1259 OID 29609)
-- Name: mfa_factors; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.mfa_factors (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    friendly_name text,
    factor_type auth.factor_type NOT NULL,
    status auth.factor_status NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    secret text,
    phone text,
    last_challenged_at timestamp with time zone,
    web_authn_credential jsonb,
    web_authn_aaguid uuid
);


ALTER TABLE auth.mfa_factors OWNER TO supabase_auth_admin;

--
-- TOC entry 4267 (class 0 OID 0)
-- Dependencies: 257
-- Name: TABLE mfa_factors; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.mfa_factors IS 'auth: stores metadata about factors';


--
-- TOC entry 258 (class 1259 OID 29614)
-- Name: one_time_tokens; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.one_time_tokens (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    token_type auth.one_time_token_type NOT NULL,
    token_hash text NOT NULL,
    relates_to text NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT one_time_tokens_token_hash_check CHECK ((char_length(token_hash) > 0))
);


ALTER TABLE auth.one_time_tokens OWNER TO supabase_auth_admin;

--
-- TOC entry 259 (class 1259 OID 29622)
-- Name: refresh_tokens; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.refresh_tokens (
    instance_id uuid,
    id bigint NOT NULL,
    token character varying(255),
    user_id character varying(255),
    revoked boolean,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    parent character varying(255),
    session_id uuid
);


ALTER TABLE auth.refresh_tokens OWNER TO supabase_auth_admin;

--
-- TOC entry 4270 (class 0 OID 0)
-- Dependencies: 259
-- Name: TABLE refresh_tokens; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.refresh_tokens IS 'Auth: Store of tokens used to refresh JWT tokens once they expire.';


--
-- TOC entry 260 (class 1259 OID 29627)
-- Name: refresh_tokens_id_seq; Type: SEQUENCE; Schema: auth; Owner: supabase_auth_admin
--

CREATE SEQUENCE auth.refresh_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE auth.refresh_tokens_id_seq OWNER TO supabase_auth_admin;

--
-- TOC entry 4272 (class 0 OID 0)
-- Dependencies: 260
-- Name: refresh_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: auth; Owner: supabase_auth_admin
--

ALTER SEQUENCE auth.refresh_tokens_id_seq OWNED BY auth.refresh_tokens.id;


--
-- TOC entry 261 (class 1259 OID 29628)
-- Name: saml_providers; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.saml_providers (
    id uuid NOT NULL,
    sso_provider_id uuid NOT NULL,
    entity_id text NOT NULL,
    metadata_xml text NOT NULL,
    metadata_url text,
    attribute_mapping jsonb,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    name_id_format text,
    CONSTRAINT "entity_id not empty" CHECK ((char_length(entity_id) > 0)),
    CONSTRAINT "metadata_url not empty" CHECK (((metadata_url = NULL::text) OR (char_length(metadata_url) > 0))),
    CONSTRAINT "metadata_xml not empty" CHECK ((char_length(metadata_xml) > 0))
);


ALTER TABLE auth.saml_providers OWNER TO supabase_auth_admin;

--
-- TOC entry 4274 (class 0 OID 0)
-- Dependencies: 261
-- Name: TABLE saml_providers; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.saml_providers IS 'Auth: Manages SAML Identity Provider connections.';


--
-- TOC entry 262 (class 1259 OID 29636)
-- Name: saml_relay_states; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.saml_relay_states (
    id uuid NOT NULL,
    sso_provider_id uuid NOT NULL,
    request_id text NOT NULL,
    for_email text,
    redirect_to text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    flow_state_id uuid,
    CONSTRAINT "request_id not empty" CHECK ((char_length(request_id) > 0))
);


ALTER TABLE auth.saml_relay_states OWNER TO supabase_auth_admin;

--
-- TOC entry 4276 (class 0 OID 0)
-- Dependencies: 262
-- Name: TABLE saml_relay_states; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.saml_relay_states IS 'Auth: Contains SAML Relay State information for each Service Provider initiated login.';


--
-- TOC entry 263 (class 1259 OID 29642)
-- Name: schema_migrations; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.schema_migrations (
    version character varying(255) NOT NULL
);


ALTER TABLE auth.schema_migrations OWNER TO supabase_auth_admin;

--
-- TOC entry 4278 (class 0 OID 0)
-- Dependencies: 263
-- Name: TABLE schema_migrations; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.schema_migrations IS 'Auth: Manages updates to the auth system.';


--
-- TOC entry 264 (class 1259 OID 29645)
-- Name: sessions; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.sessions (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    factor_id uuid,
    aal auth.aal_level,
    not_after timestamp with time zone,
    refreshed_at timestamp without time zone,
    user_agent text,
    ip inet,
    tag text
);


ALTER TABLE auth.sessions OWNER TO supabase_auth_admin;

--
-- TOC entry 4280 (class 0 OID 0)
-- Dependencies: 264
-- Name: TABLE sessions; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.sessions IS 'Auth: Stores session data associated to a user.';


--
-- TOC entry 4281 (class 0 OID 0)
-- Dependencies: 264
-- Name: COLUMN sessions.not_after; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON COLUMN auth.sessions.not_after IS 'Auth: Not after is a nullable column that contains a timestamp after which the session should be regarded as expired.';


--
-- TOC entry 265 (class 1259 OID 29650)
-- Name: sso_domains; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.sso_domains (
    id uuid NOT NULL,
    sso_provider_id uuid NOT NULL,
    domain text NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    CONSTRAINT "domain not empty" CHECK ((char_length(domain) > 0))
);


ALTER TABLE auth.sso_domains OWNER TO supabase_auth_admin;

--
-- TOC entry 4283 (class 0 OID 0)
-- Dependencies: 265
-- Name: TABLE sso_domains; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.sso_domains IS 'Auth: Manages SSO email address domain mapping to an SSO Identity Provider.';


--
-- TOC entry 266 (class 1259 OID 29656)
-- Name: sso_providers; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.sso_providers (
    id uuid NOT NULL,
    resource_id text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    CONSTRAINT "resource_id not empty" CHECK (((resource_id = NULL::text) OR (char_length(resource_id) > 0)))
);


ALTER TABLE auth.sso_providers OWNER TO supabase_auth_admin;

--
-- TOC entry 4285 (class 0 OID 0)
-- Dependencies: 266
-- Name: TABLE sso_providers; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.sso_providers IS 'Auth: Manages SSO identity provider information; see saml_providers for SAML.';


--
-- TOC entry 4286 (class 0 OID 0)
-- Dependencies: 266
-- Name: COLUMN sso_providers.resource_id; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON COLUMN auth.sso_providers.resource_id IS 'Auth: Uniquely identifies a SSO provider according to a user-chosen resource ID (case insensitive), useful in infrastructure as code.';


--
-- TOC entry 267 (class 1259 OID 29662)
-- Name: users; Type: TABLE; Schema: auth; Owner: supabase_auth_admin
--

CREATE TABLE auth.users (
    instance_id uuid,
    id uuid NOT NULL,
    aud character varying(255),
    role character varying(255),
    email character varying(255),
    encrypted_password character varying(255),
    email_confirmed_at timestamp with time zone,
    invited_at timestamp with time zone,
    confirmation_token character varying(255),
    confirmation_sent_at timestamp with time zone,
    recovery_token character varying(255),
    recovery_sent_at timestamp with time zone,
    email_change_token_new character varying(255),
    email_change character varying(255),
    email_change_sent_at timestamp with time zone,
    last_sign_in_at timestamp with time zone,
    raw_app_meta_data jsonb,
    raw_user_meta_data jsonb,
    is_super_admin boolean,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    phone text DEFAULT NULL::character varying,
    phone_confirmed_at timestamp with time zone,
    phone_change text DEFAULT ''::character varying,
    phone_change_token character varying(255) DEFAULT ''::character varying,
    phone_change_sent_at timestamp with time zone,
    confirmed_at timestamp with time zone GENERATED ALWAYS AS (LEAST(email_confirmed_at, phone_confirmed_at)) STORED,
    email_change_token_current character varying(255) DEFAULT ''::character varying,
    email_change_confirm_status smallint DEFAULT 0,
    banned_until timestamp with time zone,
    reauthentication_token character varying(255) DEFAULT ''::character varying,
    reauthentication_sent_at timestamp with time zone,
    is_sso_user boolean DEFAULT false NOT NULL,
    deleted_at timestamp with time zone,
    is_anonymous boolean DEFAULT false NOT NULL,
    CONSTRAINT users_email_change_confirm_status_check CHECK (((email_change_confirm_status >= 0) AND (email_change_confirm_status <= 2)))
);


ALTER TABLE auth.users OWNER TO supabase_auth_admin;

--
-- TOC entry 4288 (class 0 OID 0)
-- Dependencies: 267
-- Name: TABLE users; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON TABLE auth.users IS 'Auth: Stores user login data within a secure schema.';


--
-- TOC entry 4289 (class 0 OID 0)
-- Dependencies: 267
-- Name: COLUMN users.is_sso_user; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON COLUMN auth.users.is_sso_user IS 'Auth: Set this column to true when the account comes from SSO. These accounts can have duplicate emails.';


--
-- TOC entry 268 (class 1259 OID 29677)
-- Name: images; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.images (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(255) NOT NULL,
    data text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    uploaded_by character varying(255)
);


ALTER TABLE public.images OWNER TO postgres;

--
-- TOC entry 269 (class 1259 OID 29684)
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messages (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    legacy_user_id uuid,
    message text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    user_id uuid DEFAULT auth.uid()
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- TOC entry 270 (class 1259 OID 29692)
-- Name: user_profile; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_profile (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    legacy_user_id uuid,
    name character varying(255) NOT NULL,
    data text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    uploaded_by character varying(255),
    user_id uuid DEFAULT auth.uid()
);


ALTER TABLE public.user_profile OWNER TO postgres;

--
-- TOC entry 271 (class 1259 OID 29700)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    username character varying(255),
    password character varying(255),
    email character varying(255),
    user_id uuid DEFAULT auth.uid()
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 272 (class 1259 OID 29707)
-- Name: messages; Type: TABLE; Schema: realtime; Owner: supabase_realtime_admin
--

CREATE TABLE realtime.messages (
    topic text NOT NULL,
    extension text NOT NULL,
    payload jsonb,
    event text,
    private boolean DEFAULT false,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    inserted_at timestamp without time zone DEFAULT now() NOT NULL,
    id uuid DEFAULT gen_random_uuid() NOT NULL
)
PARTITION BY RANGE (inserted_at);


ALTER TABLE realtime.messages OWNER TO supabase_realtime_admin;

--
-- TOC entry 273 (class 1259 OID 29714)
-- Name: schema_migrations; Type: TABLE; Schema: realtime; Owner: supabase_admin
--

CREATE TABLE realtime.schema_migrations (
    version bigint NOT NULL,
    inserted_at timestamp(0) without time zone
);


ALTER TABLE realtime.schema_migrations OWNER TO supabase_admin;

--
-- TOC entry 274 (class 1259 OID 29717)
-- Name: subscription; Type: TABLE; Schema: realtime; Owner: supabase_admin
--

CREATE TABLE realtime.subscription (
    id bigint NOT NULL,
    subscription_id uuid NOT NULL,
    entity regclass NOT NULL,
    filters realtime.user_defined_filter[] DEFAULT '{}'::realtime.user_defined_filter[] NOT NULL,
    claims jsonb NOT NULL,
    claims_role regrole GENERATED ALWAYS AS (realtime.to_regrole((claims ->> 'role'::text))) STORED NOT NULL,
    created_at timestamp without time zone DEFAULT timezone('utc'::text, now()) NOT NULL
);


ALTER TABLE realtime.subscription OWNER TO supabase_admin;

--
-- TOC entry 275 (class 1259 OID 29725)
-- Name: subscription_id_seq; Type: SEQUENCE; Schema: realtime; Owner: supabase_admin
--

ALTER TABLE realtime.subscription ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME realtime.subscription_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 276 (class 1259 OID 29726)
-- Name: buckets; Type: TABLE; Schema: storage; Owner: supabase_storage_admin
--

CREATE TABLE storage.buckets (
    id text NOT NULL,
    name text NOT NULL,
    owner uuid,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now(),
    public boolean DEFAULT false,
    avif_autodetection boolean DEFAULT false,
    file_size_limit bigint,
    allowed_mime_types text[],
    owner_id text
);


ALTER TABLE storage.buckets OWNER TO supabase_storage_admin;

--
-- TOC entry 4304 (class 0 OID 0)
-- Dependencies: 276
-- Name: COLUMN buckets.owner; Type: COMMENT; Schema: storage; Owner: supabase_storage_admin
--

COMMENT ON COLUMN storage.buckets.owner IS 'Field is deprecated, use owner_id instead';


--
-- TOC entry 277 (class 1259 OID 29735)
-- Name: migrations; Type: TABLE; Schema: storage; Owner: supabase_storage_admin
--

CREATE TABLE storage.migrations (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    hash character varying(40) NOT NULL,
    executed_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE storage.migrations OWNER TO supabase_storage_admin;

--
-- TOC entry 278 (class 1259 OID 29739)
-- Name: objects; Type: TABLE; Schema: storage; Owner: supabase_storage_admin
--

CREATE TABLE storage.objects (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    bucket_id text,
    name text,
    owner uuid,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now(),
    last_accessed_at timestamp with time zone DEFAULT now(),
    metadata jsonb,
    path_tokens text[] GENERATED ALWAYS AS (string_to_array(name, '/'::text)) STORED,
    version text,
    owner_id text,
    user_metadata jsonb
);


ALTER TABLE storage.objects OWNER TO supabase_storage_admin;

--
-- TOC entry 4307 (class 0 OID 0)
-- Dependencies: 278
-- Name: COLUMN objects.owner; Type: COMMENT; Schema: storage; Owner: supabase_storage_admin
--

COMMENT ON COLUMN storage.objects.owner IS 'Field is deprecated, use owner_id instead';


--
-- TOC entry 279 (class 1259 OID 29749)
-- Name: s3_multipart_uploads; Type: TABLE; Schema: storage; Owner: supabase_storage_admin
--

CREATE TABLE storage.s3_multipart_uploads (
    id text NOT NULL,
    in_progress_size bigint DEFAULT 0 NOT NULL,
    upload_signature text NOT NULL,
    bucket_id text NOT NULL,
    key text NOT NULL COLLATE pg_catalog."C",
    version text NOT NULL,
    owner_id text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    user_metadata jsonb
);


ALTER TABLE storage.s3_multipart_uploads OWNER TO supabase_storage_admin;

--
-- TOC entry 280 (class 1259 OID 29756)
-- Name: s3_multipart_uploads_parts; Type: TABLE; Schema: storage; Owner: supabase_storage_admin
--

CREATE TABLE storage.s3_multipart_uploads_parts (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    upload_id text NOT NULL,
    size bigint DEFAULT 0 NOT NULL,
    part_number integer NOT NULL,
    bucket_id text NOT NULL,
    key text NOT NULL COLLATE pg_catalog."C",
    etag text NOT NULL,
    owner_id text,
    version text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE storage.s3_multipart_uploads_parts OWNER TO supabase_storage_admin;

--
-- TOC entry 247 (class 1259 OID 29445)
-- Name: decrypted_secrets; Type: VIEW; Schema: vault; Owner: supabase_admin
--

CREATE VIEW vault.decrypted_secrets AS
 SELECT secrets.id,
    secrets.name,
    secrets.description,
    secrets.secret,
        CASE
            WHEN (secrets.secret IS NULL) THEN NULL::text
            ELSE
            CASE
                WHEN (secrets.key_id IS NULL) THEN NULL::text
                ELSE convert_from(pgsodium.crypto_aead_det_decrypt(decode(secrets.secret, 'base64'::text), convert_to(((((secrets.id)::text || secrets.description) || (secrets.created_at)::text) || (secrets.updated_at)::text), 'utf8'::name), secrets.key_id, secrets.nonce), 'utf8'::name)
            END
        END AS decrypted_secret,
    secrets.key_id,
    secrets.nonce,
    secrets.created_at,
    secrets.updated_at
   FROM vault.secrets;


ALTER VIEW vault.decrypted_secrets OWNER TO supabase_admin;

--
-- TOC entry 3722 (class 2604 OID 29764)
-- Name: refresh_tokens id; Type: DEFAULT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.refresh_tokens ALTER COLUMN id SET DEFAULT nextval('auth.refresh_tokens_id_seq'::regclass);


--
-- TOC entry 4109 (class 0 OID 29576)
-- Dependencies: 251
-- Data for Name: audit_log_entries; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.audit_log_entries (instance_id, id, payload, created_at, ip_address) FROM stdin;
00000000-0000-0000-0000-000000000000	95053e7a-e1bc-49d7-8fc8-d71f18f7fa7d	{"action":"user_confirmation_requested","actor_id":"76eb93b7-7918-49ac-9345-3744417d44df","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 05:16:52.945862+00	
00000000-0000-0000-0000-000000000000	25600132-2c9e-4655-b32d-0990593c8ad7	{"action":"user_confirmation_requested","actor_id":"76eb93b7-7918-49ac-9345-3744417d44df","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 05:48:27.853732+00	
00000000-0000-0000-0000-000000000000	1de71fad-d040-4d62-89f3-319981eeaac5	{"action":"user_confirmation_requested","actor_id":"76eb93b7-7918-49ac-9345-3744417d44df","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 05:52:49.779702+00	
00000000-0000-0000-0000-000000000000	44d7e336-ee3b-480f-8171-7dbde0983501	{"action":"user_confirmation_requested","actor_id":"76eb93b7-7918-49ac-9345-3744417d44df","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 05:54:21.220501+00	
00000000-0000-0000-0000-000000000000	7666dd8f-c2ff-4d12-89c4-89685ec68fd1	{"action":"user_signedup","actor_id":"76eb93b7-7918-49ac-9345-3744417d44df","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team"}	2024-12-23 05:57:34.30603+00	
00000000-0000-0000-0000-000000000000	2cc9ce75-010c-49b7-8bb6-a82eaf9f6680	{"action":"user_repeated_signup","actor_id":"76eb93b7-7918-49ac-9345-3744417d44df","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 05:58:14.619601+00	
00000000-0000-0000-0000-000000000000	be7e7c2d-f4bf-421c-ab14-25ee8e90a0bd	{"action":"user_repeated_signup","actor_id":"76eb93b7-7918-49ac-9345-3744417d44df","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 06:09:00.541763+00	
00000000-0000-0000-0000-000000000000	86668cf2-89bd-4bbf-ae5a-41846fb69a35	{"action":"login","actor_id":"76eb93b7-7918-49ac-9345-3744417d44df","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 06:12:23.519896+00	
00000000-0000-0000-0000-000000000000	569d25aa-fd09-4b04-a216-162b53d8f396	{"action":"login","actor_id":"76eb93b7-7918-49ac-9345-3744417d44df","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 06:13:06.662955+00	
00000000-0000-0000-0000-000000000000	39284763-3ae7-4c9b-a002-91aef1d29cb6	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"76eb93b7-7918-49ac-9345-3744417d44df","user_phone":""}}	2024-12-23 06:14:03.250577+00	
00000000-0000-0000-0000-000000000000	c1b4ac0a-cae7-4a06-aaf5-ad1fcdb4832f	{"action":"user_confirmation_requested","actor_id":"3ab65678-2fc9-4cd6-907a-3e633e2969c4","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 06:14:21.737077+00	
00000000-0000-0000-0000-000000000000	d9e38d62-1f79-483f-b25c-5a8f96460def	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"3ab65678-2fc9-4cd6-907a-3e633e2969c4","user_phone":""}}	2024-12-23 07:02:46.910193+00	
00000000-0000-0000-0000-000000000000	c5c4a8fa-b41f-4196-9c0b-8b12c0e55250	{"action":"user_confirmation_requested","actor_id":"8ab71f6c-56fe-4485-9194-c8af1de7dedb","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 07:03:41.084036+00	
00000000-0000-0000-0000-000000000000	2972cec0-6cf4-4a36-88d3-ed455e7d2c27	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"8ab71f6c-56fe-4485-9194-c8af1de7dedb","user_phone":""}}	2024-12-23 07:04:53.875594+00	
00000000-0000-0000-0000-000000000000	e43ad77e-ac32-403e-8b0b-0982f4777ef8	{"action":"user_signedup","actor_id":"6d9402bc-f86d-433e-984a-74a140f6fcd0","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 07:05:30.98659+00	
00000000-0000-0000-0000-000000000000	f22a3ae1-c65d-424d-818e-9d7f83b94b00	{"action":"login","actor_id":"6d9402bc-f86d-433e-984a-74a140f6fcd0","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:05:30.990561+00	
00000000-0000-0000-0000-000000000000	ed191153-736d-4255-b70c-aeb13982a19c	{"action":"login","actor_id":"6d9402bc-f86d-433e-984a-74a140f6fcd0","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:06:42.744767+00	
00000000-0000-0000-0000-000000000000	ba6aed65-d0c9-49e9-8939-eed03064b1ba	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"6d9402bc-f86d-433e-984a-74a140f6fcd0","user_phone":""}}	2024-12-23 07:07:49.569896+00	
00000000-0000-0000-0000-000000000000	fa78dcf6-df62-49ce-b912-64cce59a2a33	{"action":"user_signedup","actor_id":"c0d248c5-885f-4788-8395-cbad98e75aad","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 07:08:12.092475+00	
00000000-0000-0000-0000-000000000000	a10fa74e-3afd-4bcc-95d0-c4047db5301b	{"action":"login","actor_id":"c0d248c5-885f-4788-8395-cbad98e75aad","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:08:12.09605+00	
00000000-0000-0000-0000-000000000000	3b00aea8-a7a8-4819-99fb-60dfdf9a480b	{"action":"user_repeated_signup","actor_id":"c0d248c5-885f-4788-8395-cbad98e75aad","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 07:16:10.677222+00	
00000000-0000-0000-0000-000000000000	28b9604a-c25d-468f-bb71-4beaf726cbda	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"c0d248c5-885f-4788-8395-cbad98e75aad","user_phone":""}}	2024-12-23 07:16:37.626166+00	
00000000-0000-0000-0000-000000000000	c16100b1-fe8c-4e11-a54c-4d63b20087da	{"action":"user_signedup","actor_id":"057a3a1a-a631-4ebe-9025-131961a693c5","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 07:17:31.271373+00	
00000000-0000-0000-0000-000000000000	a835a3fe-70bf-46e1-91f2-923dd4edd4d4	{"action":"login","actor_id":"057a3a1a-a631-4ebe-9025-131961a693c5","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:17:31.274237+00	
00000000-0000-0000-0000-000000000000	9ffda271-eedd-473e-809d-bb772236b89e	{"action":"login","actor_id":"057a3a1a-a631-4ebe-9025-131961a693c5","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:22:59.899069+00	
00000000-0000-0000-0000-000000000000	5b6a0e9c-c285-4fb6-92f0-51d7b36530fc	{"action":"login","actor_id":"057a3a1a-a631-4ebe-9025-131961a693c5","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:34:18.789894+00	
00000000-0000-0000-0000-000000000000	aa678238-cb2a-401a-9d4b-3802f5bb4c5c	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"057a3a1a-a631-4ebe-9025-131961a693c5","user_phone":""}}	2024-12-23 07:34:29.577449+00	
00000000-0000-0000-0000-000000000000	b97ee0ba-5f6e-4e5c-ae2f-33447ae9df17	{"action":"user_signedup","actor_id":"2fd1b9d7-0efd-4676-92db-05be35bd7c90","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 07:34:53.71491+00	
00000000-0000-0000-0000-000000000000	f007cd8c-444b-46d4-95a5-8b7157746af2	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 07:03:23.187596+00	
00000000-0000-0000-0000-000000000000	88e0c647-223e-49e7-991d-7426e1c8ab81	{"action":"login","actor_id":"2fd1b9d7-0efd-4676-92db-05be35bd7c90","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:34:53.718074+00	
00000000-0000-0000-0000-000000000000	e1eb4242-55d6-48b6-b114-a05007977bf5	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"2fd1b9d7-0efd-4676-92db-05be35bd7c90","user_phone":""}}	2024-12-23 07:35:25.109318+00	
00000000-0000-0000-0000-000000000000	ae87bd23-41eb-4fb1-a53f-678d3e7658f2	{"action":"user_signedup","actor_id":"349b0630-109b-4e3b-8a5c-28f9fcaa6425","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 07:35:32.503804+00	
00000000-0000-0000-0000-000000000000	0cf395a1-2878-4e69-8f95-bd57e8aaf5a0	{"action":"login","actor_id":"349b0630-109b-4e3b-8a5c-28f9fcaa6425","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:35:32.506402+00	
00000000-0000-0000-0000-000000000000	c4b183ab-4cfa-498d-a0ba-8266baa80ae3	{"action":"user_repeated_signup","actor_id":"349b0630-109b-4e3b-8a5c-28f9fcaa6425","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 07:38:23.79267+00	
00000000-0000-0000-0000-000000000000	7f4d352a-7a96-4c03-8dd6-099ba3c7c678	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"349b0630-109b-4e3b-8a5c-28f9fcaa6425","user_phone":""}}	2024-12-23 07:38:43.14148+00	
00000000-0000-0000-0000-000000000000	ba6d804d-8e79-41b5-86b5-aae5ec42758f	{"action":"user_signedup","actor_id":"77adb96b-f9fb-4e6a-bdd2-43b974dbb6ad","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 07:39:09.309843+00	
00000000-0000-0000-0000-000000000000	d532dcd5-9284-47be-8867-afcfdccde569	{"action":"login","actor_id":"77adb96b-f9fb-4e6a-bdd2-43b974dbb6ad","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:39:09.315516+00	
00000000-0000-0000-0000-000000000000	4437fd59-50ef-4584-9570-94da5b599d32	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"77adb96b-f9fb-4e6a-bdd2-43b974dbb6ad","user_phone":""}}	2024-12-23 07:43:45.022238+00	
00000000-0000-0000-0000-000000000000	a31a3782-9d22-4c9c-8c07-8ee8296f9809	{"action":"user_signedup","actor_id":"43608a37-7ebc-4a05-8ad3-f9e0b204b5ad","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 07:43:54.626962+00	
00000000-0000-0000-0000-000000000000	f8bcaf1c-0c67-4880-9e52-b8873cbeaf71	{"action":"login","actor_id":"43608a37-7ebc-4a05-8ad3-f9e0b204b5ad","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:43:54.629999+00	
00000000-0000-0000-0000-000000000000	987bcb35-ef1b-44c8-af55-5e4f0caca66e	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"43608a37-7ebc-4a05-8ad3-f9e0b204b5ad","user_phone":""}}	2024-12-23 07:55:38.059441+00	
00000000-0000-0000-0000-000000000000	a5c4d9d4-6b86-4306-9200-6a22469fc29d	{"action":"user_signedup","actor_id":"a6fc3d24-a656-479c-a94e-88ed261de29e","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 07:55:45.142394+00	
00000000-0000-0000-0000-000000000000	3d759e7d-5983-4367-9d35-ff154a48e6c3	{"action":"login","actor_id":"a6fc3d24-a656-479c-a94e-88ed261de29e","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:55:45.147704+00	
00000000-0000-0000-0000-000000000000	77af001f-d1d7-43ab-82fc-084f39a80223	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"a6fc3d24-a656-479c-a94e-88ed261de29e","user_phone":""}}	2024-12-23 07:58:41.995937+00	
00000000-0000-0000-0000-000000000000	f594fd7d-ef49-4bd1-83ed-c571246ee76f	{"action":"user_signedup","actor_id":"26b86df0-fee0-4c91-bfa7-2a3fd4221db1","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 07:58:49.172822+00	
00000000-0000-0000-0000-000000000000	1e0baedd-3f60-4cf7-8c4c-f6d33b24abcf	{"action":"login","actor_id":"26b86df0-fee0-4c91-bfa7-2a3fd4221db1","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 07:58:49.175781+00	
00000000-0000-0000-0000-000000000000	22b1be62-0ebb-43a3-b0df-e3d4f8706801	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"26b86df0-fee0-4c91-bfa7-2a3fd4221db1","user_phone":""}}	2024-12-23 08:05:36.626186+00	
00000000-0000-0000-0000-000000000000	2363c6cf-d6d3-4370-8ae1-31bbdf08ec85	{"action":"user_signedup","actor_id":"3c99a547-270b-4853-bc03-87309aa84ade","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 08:07:23.529264+00	
00000000-0000-0000-0000-000000000000	d191cc24-960c-4628-813d-b7631af095d1	{"action":"login","actor_id":"3c99a547-270b-4853-bc03-87309aa84ade","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 08:07:23.532688+00	
00000000-0000-0000-0000-000000000000	13839562-58c3-42eb-9707-469f6b9ae58e	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"3c99a547-270b-4853-bc03-87309aa84ade","user_phone":""}}	2024-12-23 08:08:36.499454+00	
00000000-0000-0000-0000-000000000000	43838adb-85c1-422d-b934-e603f9ea3209	{"action":"user_signedup","actor_id":"8985a3eb-771b-4919-a4f2-d416ca087c0f","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 08:08:41.497435+00	
00000000-0000-0000-0000-000000000000	25ce33b1-0a33-4dcd-b7fe-006d2f7ac5ab	{"action":"login","actor_id":"8985a3eb-771b-4919-a4f2-d416ca087c0f","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 08:08:41.500411+00	
00000000-0000-0000-0000-000000000000	c9a6b78a-a0c5-4406-8cb7-190c5a9887e9	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"8985a3eb-771b-4919-a4f2-d416ca087c0f","user_phone":""}}	2024-12-23 08:10:01.850721+00	
00000000-0000-0000-0000-000000000000	addbe552-b594-4ce0-8c5a-99eb9ba50f4e	{"action":"user_signedup","actor_id":"f1cd6f3f-a2be-47c7-97fe-19589ac68fe9","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 08:10:11.679454+00	
00000000-0000-0000-0000-000000000000	8566662e-08d7-4642-af6b-182df0a14a8f	{"action":"login","actor_id":"f1cd6f3f-a2be-47c7-97fe-19589ac68fe9","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 08:10:11.683318+00	
00000000-0000-0000-0000-000000000000	55a2d555-b7a5-4934-a77d-067097dd89e1	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"f1cd6f3f-a2be-47c7-97fe-19589ac68fe9","user_phone":""}}	2024-12-23 08:25:55.961336+00	
00000000-0000-0000-0000-000000000000	1321d2ef-c51f-4408-b1d6-50261b3de873	{"action":"user_signedup","actor_id":"9add25bf-936d-4b42-b200-28d4849a6995","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 08:26:07.746324+00	
00000000-0000-0000-0000-000000000000	f111785a-081b-4259-946a-1b5148c31d8b	{"action":"login","actor_id":"9add25bf-936d-4b42-b200-28d4849a6995","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 08:26:07.749129+00	
00000000-0000-0000-0000-000000000000	0f5c088e-89c3-4db1-89cc-c53fac18f6f6	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"9add25bf-936d-4b42-b200-28d4849a6995","user_phone":""}}	2024-12-23 08:30:05.428715+00	
00000000-0000-0000-0000-000000000000	9fa8c692-a4db-4922-ac41-43a361948c2d	{"action":"user_signedup","actor_id":"d831ce95-08cb-442d-b1aa-99b6bfa6d5ec","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 08:30:14.90634+00	
00000000-0000-0000-0000-000000000000	94cf6991-2c6d-40ae-a608-616510bc49da	{"action":"login","actor_id":"d831ce95-08cb-442d-b1aa-99b6bfa6d5ec","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 08:30:14.909563+00	
00000000-0000-0000-0000-000000000000	4a07ae7b-10cd-44d8-9d72-a9e3a4b3e356	{"action":"user_repeated_signup","actor_id":"d831ce95-08cb-442d-b1aa-99b6bfa6d5ec","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2024-12-23 08:31:46.425729+00	
00000000-0000-0000-0000-000000000000	eb4e4ba0-14b6-4bff-9207-5a2a0aa28e6f	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"d831ce95-08cb-442d-b1aa-99b6bfa6d5ec","user_phone":""}}	2024-12-23 08:32:22.413239+00	
00000000-0000-0000-0000-000000000000	a81a2f83-2ddc-4b93-b4cb-1ad6883ca534	{"action":"user_signedup","actor_id":"bf671def-5d40-48f6-8e43-b4db20298b4c","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 08:32:29.826474+00	
00000000-0000-0000-0000-000000000000	36e87529-fb5f-4922-8426-112fb89d8e04	{"action":"login","actor_id":"bf671def-5d40-48f6-8e43-b4db20298b4c","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 08:32:29.829552+00	
00000000-0000-0000-0000-000000000000	8c73eeb0-19f0-4f2d-9fae-c9233fb4fe39	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"bf671def-5d40-48f6-8e43-b4db20298b4c","user_phone":""}}	2024-12-23 08:36:08.854652+00	
00000000-0000-0000-0000-000000000000	5f49178d-5a14-4560-a625-3be8ff10c700	{"action":"user_signedup","actor_id":"d5e9aa10-3012-4d6f-97b9-412ef4494025","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 08:36:37.4885+00	
00000000-0000-0000-0000-000000000000	cdc011ee-5ba9-4b63-a7e3-94966ae43754	{"action":"login","actor_id":"d5e9aa10-3012-4d6f-97b9-412ef4494025","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 08:36:37.492292+00	
00000000-0000-0000-0000-000000000000	7f09f2b9-1f1f-4f19-89be-cf8a97c484bf	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"d5e9aa10-3012-4d6f-97b9-412ef4494025","user_phone":""}}	2024-12-23 08:37:36.321503+00	
00000000-0000-0000-0000-000000000000	fce8ffcc-5ef9-4581-9542-16d18e5fa3b4	{"action":"user_signedup","actor_id":"84e0a9e9-f639-4895-8baf-9ef7482cbf42","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 08:37:52.971883+00	
00000000-0000-0000-0000-000000000000	6e73cf2a-3f83-420b-bf40-0c60d4fde36a	{"action":"login","actor_id":"84e0a9e9-f639-4895-8baf-9ef7482cbf42","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 08:37:52.974591+00	
00000000-0000-0000-0000-000000000000	96e92202-44ee-48e8-b0bf-cc054798bdd2	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"944989026@qq.com","user_id":"84e0a9e9-f639-4895-8baf-9ef7482cbf42","user_phone":""}}	2024-12-23 08:53:32.457402+00	
00000000-0000-0000-0000-000000000000	e0f36462-537b-4d3e-a654-b0fb632242e7	{"action":"user_signedup","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2024-12-23 08:53:44.768468+00	
00000000-0000-0000-0000-000000000000	3b19a57a-8c53-4cce-85e3-0630a6cd24a6	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 08:53:44.771331+00	
00000000-0000-0000-0000-000000000000	f8906e60-cabf-47e3-9d8a-2621b1f8d6e4	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2024-12-23 09:03:44.231188+00	
00000000-0000-0000-0000-000000000000	fb881a3a-59af-47e7-9571-4f6dfa180abb	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 09:04:25.103799+00	
00000000-0000-0000-0000-000000000000	599dc09d-c93a-4919-8194-7ccc01833cf9	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 09:04:33.441673+00	
00000000-0000-0000-0000-000000000000	c0daa2f6-413a-4758-b88e-72234d2a7833	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 10:31:28.844077+00	
00000000-0000-0000-0000-000000000000	19127023-4e0a-4c6c-b47f-2b76c43be7c5	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2024-12-23 10:31:34.686247+00	
00000000-0000-0000-0000-000000000000	0ff1cd6e-ff1d-4d5f-a8bf-faaccc43e9d2	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 10:31:49.584399+00	
00000000-0000-0000-0000-000000000000	efd18487-1e8e-401c-8c8d-f086027cfe77	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2024-12-23 10:31:56.861974+00	
00000000-0000-0000-0000-000000000000	c8996441-9968-41f2-a61a-1148d71a895c	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 10:32:12.123629+00	
00000000-0000-0000-0000-000000000000	947eb44e-10fd-4bde-8fee-5ff5c398cc4e	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2024-12-23 10:32:56.739355+00	
00000000-0000-0000-0000-000000000000	ef1c3789-bb24-4464-a2e2-87eeca390702	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 10:33:02.121759+00	
00000000-0000-0000-0000-000000000000	2f05acfc-ccbf-46c3-acc9-d1c6e538a47a	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2024-12-23 10:33:07.04212+00	
00000000-0000-0000-0000-000000000000	2f082aae-3de9-4324-9e70-91f691a6631b	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2024-12-23 10:33:27.029789+00	
00000000-0000-0000-0000-000000000000	ab31e0ea-2d82-48cc-8191-7134f8f1a6a4	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 07:02:50.350157+00	
00000000-0000-0000-0000-000000000000	11ebedf3-7d9e-41f6-b630-e3a32eb9e752	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 07:05:15.85901+00	
00000000-0000-0000-0000-000000000000	fef0247f-b0e2-4ef7-8ad7-5f4f8c8bfc48	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 07:05:19.843287+00	
00000000-0000-0000-0000-000000000000	6ed8a35e-1d4a-42a7-99ea-bfb4cf2a9ead	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 07:46:18.43323+00	
00000000-0000-0000-0000-000000000000	ea121bee-39e7-41d5-ad82-8cae09908069	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 07:46:26.072748+00	
00000000-0000-0000-0000-000000000000	93a0ff0b-f795-4c45-965c-431bd6310391	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 07:47:13.108459+00	
00000000-0000-0000-0000-000000000000	65ccf489-665b-4cea-adeb-83065ab81233	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 07:47:22.815606+00	
00000000-0000-0000-0000-000000000000	bdee6053-12bb-4592-864c-2dfd745bfca9	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 07:47:36.60207+00	
00000000-0000-0000-0000-000000000000	441662bf-6b54-471a-9d53-a6fab1e658f8	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 07:47:52.898083+00	
00000000-0000-0000-0000-000000000000	4af9b60c-f878-482b-89ed-a148f3f20b7f	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 07:48:07.390822+00	
00000000-0000-0000-0000-000000000000	9dd24bab-d25b-41bf-8f5a-bdd322244d6b	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 07:51:01.384055+00	
00000000-0000-0000-0000-000000000000	dd8b6aae-4e0e-4252-bb9f-b14c30ca1f65	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 07:51:12.296531+00	
00000000-0000-0000-0000-000000000000	a4bd4d45-4bbe-4b2f-8e26-0c47b820a7e9	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 07:51:27.976261+00	
00000000-0000-0000-0000-000000000000	a1b1c9c3-34d6-4b5b-bc45-a3a130762a01	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 07:51:42.22052+00	
00000000-0000-0000-0000-000000000000	b08d59c6-fcd3-4f9b-ba13-ea4d72d65b9c	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 08:37:24.505251+00	
00000000-0000-0000-0000-000000000000	a5c72f8b-9561-4ee5-a664-ddc16bc3c96e	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 08:37:44.433418+00	
00000000-0000-0000-0000-000000000000	c29fe242-8efd-4927-b2f6-9e2c898626ef	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 08:37:58.32079+00	
00000000-0000-0000-0000-000000000000	987e623d-936a-42a3-831c-dd71e99b1b76	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 08:38:10.742828+00	
00000000-0000-0000-0000-000000000000	01faf86b-acf4-4d78-9818-573f738694c7	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 08:38:17.560297+00	
00000000-0000-0000-0000-000000000000	3daa6977-9ec0-4c3c-babf-689003a081e6	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 08:39:11.738494+00	
00000000-0000-0000-0000-000000000000	39e70d99-f1c0-4ab0-a1ac-666e5b377f3a	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-01-08 08:39:20.51395+00	
00000000-0000-0000-0000-000000000000	2f71935e-afdf-4017-ad38-643e85563cf4	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 08:39:53.401412+00	
00000000-0000-0000-0000-000000000000	427cd746-18c6-4f4e-9ab8-7b96530ccfeb	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 10:36:02.456401+00	
00000000-0000-0000-0000-000000000000	adf89ef2-6eb8-432c-83d7-c68af74c81d5	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-01-08 10:36:18.85517+00	
00000000-0000-0000-0000-000000000000	59c8178f-2d87-4368-8b64-c0c132951583	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 11:49:49.606905+00	
00000000-0000-0000-0000-000000000000	0a8603ae-b6f9-493d-adbb-6cb9b681efb1	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 11:50:11.738367+00	
00000000-0000-0000-0000-000000000000	c0969b3a-b1c3-445e-b202-adaab51d4241	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 11:51:09.517212+00	
00000000-0000-0000-0000-000000000000	4bdf13a6-677b-4939-b11d-6a8bc1648eb6	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 11:52:55.258721+00	
00000000-0000-0000-0000-000000000000	a1ddda6b-7a35-42a9-ac57-8bb3e563fff2	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 11:53:39.34659+00	
00000000-0000-0000-0000-000000000000	543ecbaa-380f-4d92-a10f-fdcf384acd76	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:01:13.459472+00	
00000000-0000-0000-0000-000000000000	3c2053f0-22b5-44ad-a9c5-18d261daf4cf	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:01:29.970293+00	
00000000-0000-0000-0000-000000000000	e4d1ba5f-fb75-4f71-8dbf-78c14bc08ac4	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 12:01:35.989357+00	
00000000-0000-0000-0000-000000000000	49b3b582-28d7-4228-8222-d4c2baede71a	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:01:42.149797+00	
00000000-0000-0000-0000-000000000000	87124867-6fed-4750-bd85-e5a762a5c762	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 12:01:48.739587+00	
00000000-0000-0000-0000-000000000000	ad2cd72d-5195-4437-9f0c-0f03426087e9	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:01:58.60204+00	
00000000-0000-0000-0000-000000000000	864c3542-9519-4be2-a06d-e5fc02cef072	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 12:02:05.377971+00	
00000000-0000-0000-0000-000000000000	5bf811e6-4e40-4ff0-a2b6-060a8af76775	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:02:26.929329+00	
00000000-0000-0000-0000-000000000000	419f6959-1e28-4420-9bdf-40a115e88bd3	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:02:45.484825+00	
00000000-0000-0000-0000-000000000000	0862e01c-ed04-4b3e-8151-96d616a2c65f	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 12:02:51.694541+00	
00000000-0000-0000-0000-000000000000	348fc312-31ca-4198-8795-ba045e3ac117	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:42:07.952052+00	
00000000-0000-0000-0000-000000000000	a2e64a54-dcca-4e85-ad72-3b1dd95bf931	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:42:11.82901+00	
00000000-0000-0000-0000-000000000000	6bd13088-9cd1-4f3b-837c-89284c065fc3	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:55:01.349551+00	
00000000-0000-0000-0000-000000000000	3cdaa09e-bda3-4469-a7f1-2743de415adb	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 12:55:22.683764+00	
00000000-0000-0000-0000-000000000000	e5662116-c036-4372-800b-8d0afa63f72b	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 13:09:57.892082+00	
00000000-0000-0000-0000-000000000000	bb8ac59c-7779-49ed-9ec2-e5e0ff385c78	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 13:17:20.894699+00	
00000000-0000-0000-0000-000000000000	0da55544-41db-483b-8755-2460a437f97e	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 13:41:53.86482+00	
00000000-0000-0000-0000-000000000000	78939637-897a-44cd-82e9-dde80cbe63e8	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 13:43:31.671008+00	
00000000-0000-0000-0000-000000000000	0d4f6479-ccd5-420f-ad24-ff9b9a560811	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 13:44:41.284572+00	
00000000-0000-0000-0000-000000000000	5ef61eb4-f42e-41cc-b75a-a0a536c37c6f	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 13:44:41.284446+00	
00000000-0000-0000-0000-000000000000	0c0a869a-777e-45de-b6cf-376b56c7a642	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 13:44:41.286907+00	
00000000-0000-0000-0000-000000000000	4b4e351d-a037-4f8a-935e-6700eca37bc0	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 13:44:41.286791+00	
00000000-0000-0000-0000-000000000000	35f2588d-3516-42ff-953b-90463c42297a	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 13:44:41.305133+00	
00000000-0000-0000-0000-000000000000	cedd3399-0ea6-4a97-a195-77a286039637	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 13:47:13.6259+00	
00000000-0000-0000-0000-000000000000	ab8c0e1d-0a4e-445b-a33e-f21a6b239283	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 13:50:03.52815+00	
00000000-0000-0000-0000-000000000000	e3347c05-0ae7-4562-a777-a022e0c1a263	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:03:02.658543+00	
00000000-0000-0000-0000-000000000000	054f3485-5294-4865-9fad-7143025fb9bb	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:22:03.612294+00	
00000000-0000-0000-0000-000000000000	9f46990e-cef7-4d4d-930e-bbf4c9251f58	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 14:22:49.26831+00	
00000000-0000-0000-0000-000000000000	e33636a7-415a-47cf-87f9-6899ea5303a4	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:23:06.867814+00	
00000000-0000-0000-0000-000000000000	fc33e274-cb19-42d5-8b03-b9ea36078e12	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:23:59.143795+00	
00000000-0000-0000-0000-000000000000	e3315f40-8183-4f97-a4a6-f7d0838975d6	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 14:24:08.856973+00	
00000000-0000-0000-0000-000000000000	aa7caa4b-bc81-46ff-8cce-76e357dd9bef	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:24:19.309459+00	
00000000-0000-0000-0000-000000000000	a8d5ef25-c995-4565-a777-840b85d27b48	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 14:25:28.556961+00	
00000000-0000-0000-0000-000000000000	0301fb2a-e5bd-46b2-9e63-21ec4310cac2	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:30:12.485017+00	
00000000-0000-0000-0000-000000000000	5aa3c262-5183-46cb-bc18-5fcab63fb6e8	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:30:22.861403+00	
00000000-0000-0000-0000-000000000000	6af69065-2ad0-437e-a0fa-7cd26d9c80f1	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 14:30:57.187084+00	
00000000-0000-0000-0000-000000000000	534c8d8d-8f11-4e73-a205-c70cebf29ec6	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:40:30.359783+00	
00000000-0000-0000-0000-000000000000	df4d9442-97d6-426c-93b4-317a53e8bebf	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:40:52.861883+00	
00000000-0000-0000-0000-000000000000	f8a91833-c0a7-44b5-b2a1-351a6e59f379	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:46:38.123507+00	
00000000-0000-0000-0000-000000000000	797f0fdd-3c02-4534-a8b1-40d424056916	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:47:34.315746+00	
00000000-0000-0000-0000-000000000000	78172b8f-fe5a-4e53-b09d-4e81f4058a4c	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:48:30.789748+00	
00000000-0000-0000-0000-000000000000	79606c9e-1c02-4af8-8df3-9ff4467d5e7a	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 14:48:36.227811+00	
00000000-0000-0000-0000-000000000000	0e4aa304-e7a1-480d-b114-d6bbec61b75f	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:53:55.654822+00	
00000000-0000-0000-0000-000000000000	f9a5b4a6-be58-496f-863b-c93321b4fc92	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:55:26.930614+00	
00000000-0000-0000-0000-000000000000	00079793-77a5-40c1-8cbd-eb7274e63264	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 14:58:41.27547+00	
00000000-0000-0000-0000-000000000000	5b44fa36-f92d-493e-9808-dd7cac220aeb	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:58:45.45949+00	
00000000-0000-0000-0000-000000000000	0b64d76b-714b-4767-b044-2f71f0498540	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:58:50.74038+00	
00000000-0000-0000-0000-000000000000	05c1642c-a758-4f9f-91c5-63ffcbb1db4d	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 14:58:55.514397+00	
00000000-0000-0000-0000-000000000000	4ce49112-34f0-44de-b4bd-cf4cd42276d4	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 14:59:03.675494+00	
00000000-0000-0000-0000-000000000000	d9bca400-8ad4-4e3b-961a-0eec14e37f3d	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 14:59:13.020476+00	
00000000-0000-0000-0000-000000000000	1421dc56-a9ce-4e57-8f36-e16fc54f7ada	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:00:05.546655+00	
00000000-0000-0000-0000-000000000000	b74e970d-276b-43d7-bf1d-86a7d6690e0c	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 15:03:27.055941+00	
00000000-0000-0000-0000-000000000000	0ce7fc10-100c-4040-b6cc-bfecf95b7b78	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:04:07.224635+00	
00000000-0000-0000-0000-000000000000	8985e456-f2b7-447b-b174-bdc0100eb19c	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 15:05:20.517794+00	
00000000-0000-0000-0000-000000000000	5dab2eb3-56e5-4a69-96b9-335b7c7f04af	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:19:44.405396+00	
00000000-0000-0000-0000-000000000000	b7763ef4-8e16-4259-ad30-1703417fbd37	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:19:59.655789+00	
00000000-0000-0000-0000-000000000000	23c3c42f-2775-46f6-aa59-4642bc6525b5	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:20:02.469303+00	
00000000-0000-0000-0000-000000000000	dafc1402-a3c6-4f73-8d99-65d9c7de5fd6	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:20:03.896633+00	
00000000-0000-0000-0000-000000000000	106e5393-0b05-4df2-a642-a7e4f2e5ee89	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:20:04.106488+00	
00000000-0000-0000-0000-000000000000	9ae9ebf9-8e29-46cd-810b-00bbf49e867d	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:26:44.943632+00	
00000000-0000-0000-0000-000000000000	32eed202-3352-4716-a100-779bd50f4e14	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:30:51.511193+00	
00000000-0000-0000-0000-000000000000	0693e68e-da02-4af0-a327-8e930113cc20	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 15:31:59.319131+00	
00000000-0000-0000-0000-000000000000	6527c0d1-fea4-45eb-9f3d-39c846f2fe5c	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:32:03.866324+00	
00000000-0000-0000-0000-000000000000	0c54d449-ebff-4c55-9e1d-6b17a95f35a4	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 15:32:10.186301+00	
00000000-0000-0000-0000-000000000000	fd26cd9d-7d24-4ffe-a34a-bb34ab3665ed	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:32:15.317902+00	
00000000-0000-0000-0000-000000000000	223d74ae-88e4-48c3-9578-4572d094fc1e	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 15:32:19.88511+00	
00000000-0000-0000-0000-000000000000	85a8c95a-bd7f-4bef-ac3b-d7cf5854e18a	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:36:42.290782+00	
00000000-0000-0000-0000-000000000000	540e886a-9084-424c-b20c-a29139bafc01	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:36:52.383867+00	
00000000-0000-0000-0000-000000000000	1707894d-89af-4b04-bfff-94f9770865b8	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 15:37:06.805097+00	
00000000-0000-0000-0000-000000000000	84c7a435-555a-4b85-a431-9ec42c141ca6	{"action":"user_signedup","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-01 15:38:39.715426+00	
00000000-0000-0000-0000-000000000000	ad7fda97-4574-439b-8f1f-c0385fa1eb34	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:38:39.721899+00	
00000000-0000-0000-0000-000000000000	6a4e4527-b6a7-487c-8934-905f8c291095	{"action":"logout","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 15:40:44.403338+00	
00000000-0000-0000-0000-000000000000	3c1388d5-2211-48f6-8b64-e91b813f3eff	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:41:10.438293+00	
00000000-0000-0000-0000-000000000000	e7b114a1-5824-4468-8006-d2055cd64f85	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:41:32.83361+00	
00000000-0000-0000-0000-000000000000	2266a87d-09ac-44b0-a39e-91f9fd5bc6a9	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:41:49.036956+00	
00000000-0000-0000-0000-000000000000	8b3c0236-7d1f-48e5-be53-f67f145d6615	{"action":"logout","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 15:41:55.250857+00	
00000000-0000-0000-0000-000000000000	62ae7611-c2b7-4f80-a2b5-ace61bca35c8	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:08.923046+00	
00000000-0000-0000-0000-000000000000	45a941fb-3fc4-4a03-b31c-8c66b3140765	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:11.986748+00	
00000000-0000-0000-0000-000000000000	9d334d4e-d1c7-4313-acbe-62a71837276e	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:15.65106+00	
00000000-0000-0000-0000-000000000000	30c0599e-bbdf-4046-a486-c5449ca001ac	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:16.101366+00	
00000000-0000-0000-0000-000000000000	6042ddbb-1af9-4888-990b-692dab79dccf	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:18.359762+00	
00000000-0000-0000-0000-000000000000	6146da20-5e1b-428f-9788-2da9580f2837	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:19.300565+00	
00000000-0000-0000-0000-000000000000	27ec7a00-e45b-4529-8182-74fa555f3550	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:19.44675+00	
00000000-0000-0000-0000-000000000000	1e1c9d8a-721f-4812-837d-38fc3adfa2ab	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:20.062639+00	
00000000-0000-0000-0000-000000000000	40667e5a-1391-454c-94f4-e060b9dc686d	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:20.218988+00	
00000000-0000-0000-0000-000000000000	23f1bea4-df6f-49ef-8de9-2f67d5920c43	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:20.783734+00	
00000000-0000-0000-0000-000000000000	04e0995a-21c9-4722-b8de-4cd5d274480e	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:20.949062+00	
00000000-0000-0000-0000-000000000000	1c3bf6f7-de6c-4bd4-a5b0-c85cbe356b2d	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:21.560721+00	
00000000-0000-0000-0000-000000000000	49161c3c-06a7-4e0e-9485-e1d43adef282	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:21.844056+00	
00000000-0000-0000-0000-000000000000	7d358dbc-adca-473f-99b8-d9f99c6f837e	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:22.610937+00	
00000000-0000-0000-0000-000000000000	c811222a-3676-4505-814e-cadab6a31e2b	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:23.158806+00	
00000000-0000-0000-0000-000000000000	ab28d5fa-baa3-43f9-aebf-33104ffc8cc6	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:23.541029+00	
00000000-0000-0000-0000-000000000000	634dc0c8-2855-4d6e-b5b3-fb12acf574b7	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:23.687426+00	
00000000-0000-0000-0000-000000000000	bfaa1dea-e402-4320-a103-b77901c1379e	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:24.298804+00	
00000000-0000-0000-0000-000000000000	e47d3e22-2f2d-45d8-8b3e-d66343aa0906	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:24.92562+00	
00000000-0000-0000-0000-000000000000	0ca0e99c-51cb-4d64-9988-6d8208ee7436	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:25.001564+00	
00000000-0000-0000-0000-000000000000	7a33a9bf-7ccb-43a1-9a54-b20d4eee1cdf	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:25.130389+00	
00000000-0000-0000-0000-000000000000	daab3b8c-aaa7-4eee-8dee-bf89c22fafb8	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:25.66514+00	
00000000-0000-0000-0000-000000000000	f069eba0-8838-4b7e-8051-99c86b8c57c9	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:25.838304+00	
00000000-0000-0000-0000-000000000000	12c8ea16-9770-4359-bf83-95b7d3985f23	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:26.319993+00	
00000000-0000-0000-0000-000000000000	b9eb178a-269b-4005-b2b8-f998e1501da3	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:26.483893+00	
00000000-0000-0000-0000-000000000000	a8cae65c-183c-4a6e-b4bb-e5e50d42bce0	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:26.965932+00	
00000000-0000-0000-0000-000000000000	800b070d-4efd-4d25-85a0-b2f8256db425	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:27.127489+00	
00000000-0000-0000-0000-000000000000	881ec877-0c55-4b40-a708-99f018f5081a	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:27.601609+00	
00000000-0000-0000-0000-000000000000	d12ad147-3dad-4853-8d07-57c85243d575	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:27.759243+00	
00000000-0000-0000-0000-000000000000	4aa2fad9-c2a0-4430-aa06-263e9dda8a3a	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:28.186035+00	
00000000-0000-0000-0000-000000000000	33ce5b7d-abbf-45f0-9464-93a8f838e118	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:28.323634+00	
00000000-0000-0000-0000-000000000000	965c589a-aa24-4e49-bec5-52bd9f7d9f1f	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:42:35.797295+00	
00000000-0000-0000-0000-000000000000	6faad046-f685-415a-8e46-7338cb8920d9	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:43:48.13261+00	
00000000-0000-0000-0000-000000000000	ce58a8e7-009d-427e-b184-67e14cab1b53	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:44:13.51885+00	
00000000-0000-0000-0000-000000000000	798830d3-2e95-4792-bb24-08b52d1202dd	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:44:14.620298+00	
00000000-0000-0000-0000-000000000000	154de45b-8c4c-4c64-8541-2c51e9338fcc	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:49:41.982241+00	
00000000-0000-0000-0000-000000000000	1a28004b-a72f-47bd-aad9-cadd34b2b8b4	{"action":"logout","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-01 15:50:47.544314+00	
00000000-0000-0000-0000-000000000000	1a58d606-c327-49ee-8f64-0d8c3aa39198	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:51:11.545479+00	
00000000-0000-0000-0000-000000000000	b02bacb0-e949-4f83-933d-ff32ef1462ff	{"action":"login","actor_id":"05e7c68f-1dae-449c-bc4d-7901afbe4f0c","actor_username":"2454164865@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:51:23.016779+00	
00000000-0000-0000-0000-000000000000	acb24f3e-2119-4bb3-861d-2a1fcbd26261	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-01 15:56:35.387552+00	
00000000-0000-0000-0000-000000000000	1480a687-ec31-46d7-8d70-6ccb34d38275	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:20:47.670649+00	
00000000-0000-0000-0000-000000000000	597a4118-4f93-4c7b-b864-6d7d17ff00d6	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:20:47.898896+00	
00000000-0000-0000-0000-000000000000	147eee69-45a0-49df-bfb9-ed47572df555	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:20:48.564677+00	
00000000-0000-0000-0000-000000000000	aa4748e9-b03a-4d2e-a749-86d9c37d6710	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:20:54.78161+00	
00000000-0000-0000-0000-000000000000	77300834-1cee-457f-8dea-d7d43b099b8f	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:20:59.105554+00	
00000000-0000-0000-0000-000000000000	dafe8641-35d8-47b6-aef4-803bc096af87	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:21:04.760867+00	
00000000-0000-0000-0000-000000000000	035ed3a3-3b02-47da-9be8-230deaea8240	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:27:04.728271+00	
00000000-0000-0000-0000-000000000000	706d6cfb-bcd7-470d-8478-9b8abf5706eb	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:27:09.477454+00	
00000000-0000-0000-0000-000000000000	caf4f6dd-c291-491a-86cf-6c389071df0f	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:27:14.482683+00	
00000000-0000-0000-0000-000000000000	70066d8c-f8f3-4968-bfa0-ab49bbf2d244	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:27:16.888168+00	
00000000-0000-0000-0000-000000000000	69895bd7-0b8f-43b0-aefa-1fbcecddd9c6	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:27:20.466063+00	
00000000-0000-0000-0000-000000000000	e687f1af-d6f3-4c9e-a134-41e6f99539a7	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:31:26.51544+00	
00000000-0000-0000-0000-000000000000	4661f536-77f2-4f52-9bcf-a3384acae0d3	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:31:30.648269+00	
00000000-0000-0000-0000-000000000000	785e8f82-6d30-41bc-8d7d-6c5be0614048	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:37:26.225443+00	
00000000-0000-0000-0000-000000000000	04861873-8cb0-4867-9ac6-6d2297b95d4f	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:37:31.433581+00	
00000000-0000-0000-0000-000000000000	9abe9239-8824-4f70-ac5a-e76a0e3cca14	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:37:35.403517+00	
00000000-0000-0000-0000-000000000000	8896ea3c-cef7-4baa-af40-9b1c22e7ac8a	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:37:39.368151+00	
00000000-0000-0000-0000-000000000000	bcb01134-7c52-4491-9b5e-4a968bb6647c	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:37:44.922195+00	
00000000-0000-0000-0000-000000000000	6a66a1f1-d98d-4af8-9e58-c151939efbe3	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:37:50.942693+00	
00000000-0000-0000-0000-000000000000	51719ea1-8931-471d-8572-37d8aef38bab	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:38:14.272112+00	
00000000-0000-0000-0000-000000000000	71cca876-e717-435d-a6dc-08c30d83bc20	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:38:20.723038+00	
00000000-0000-0000-0000-000000000000	1f7a1bfe-fd5d-4849-ad67-48d9c41f79d4	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:38:25.75567+00	
00000000-0000-0000-0000-000000000000	d3dcaf3f-a8d9-40b4-84d0-a723d41fbd37	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:38:39.403258+00	
00000000-0000-0000-0000-000000000000	e7f68f32-fe2f-4b56-8c79-867f59ae77ff	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:45:25.412478+00	
00000000-0000-0000-0000-000000000000	b70c4581-cee5-4110-b36c-bcb79742e104	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:47:25.815206+00	
00000000-0000-0000-0000-000000000000	99929f0d-49de-4635-a6f1-830de2dbcd95	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:47:25.823138+00	
00000000-0000-0000-0000-000000000000	0833c86d-0dc9-4f84-b748-c926111819e4	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:47:32.829743+00	
00000000-0000-0000-0000-000000000000	060508c4-d3fe-43cf-a0ab-885448cd4c0d	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:47:36.816662+00	
00000000-0000-0000-0000-000000000000	7d171b23-8f58-47f8-8d87-1546a90e8858	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:47:36.838187+00	
00000000-0000-0000-0000-000000000000	7490690e-0185-4c5a-9266-50fd7511923f	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:47:52.710864+00	
00000000-0000-0000-0000-000000000000	1499bbde-6171-4c1d-a5db-80535e77be86	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:47:55.632562+00	
00000000-0000-0000-0000-000000000000	8501cb01-76f2-4a2a-ba0e-f17bd0906e56	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:47:56.001834+00	
00000000-0000-0000-0000-000000000000	89f84c24-7f9e-4388-8bc4-d80626222ee0	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:48:08.567654+00	
00000000-0000-0000-0000-000000000000	f24faa9b-a000-42d8-af82-1876a83a1727	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:48:11.841885+00	
00000000-0000-0000-0000-000000000000	6e490d96-aa53-4294-8aaa-a234c97a19b0	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:48:11.850966+00	
00000000-0000-0000-0000-000000000000	b4dfac52-fc77-4f58-933b-b371465ba742	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:48:31.159843+00	
00000000-0000-0000-0000-000000000000	7dce47d2-5dfc-4695-9447-1b3b58fe55a5	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:48:43.722019+00	
00000000-0000-0000-0000-000000000000	8d586041-7fc5-4a53-85ec-78043f90cb6a	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:48:43.744042+00	
00000000-0000-0000-0000-000000000000	054e0917-5d9f-4a52-8574-669e1b146d32	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:48:50.631401+00	
00000000-0000-0000-0000-000000000000	e3c7a76b-396d-45f8-9782-ca0c2e9611fd	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:50:35.279667+00	
00000000-0000-0000-0000-000000000000	d12bfafa-370d-4617-b65c-35c9ffa2c30c	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:50:35.294315+00	
00000000-0000-0000-0000-000000000000	f99fce91-ecf0-4794-b731-bf02dac6b4c4	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:50:47.53741+00	
00000000-0000-0000-0000-000000000000	27a4cbd9-1f58-48b2-a4f1-51416d6f65e7	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:54:25.607495+00	
00000000-0000-0000-0000-000000000000	7ee7d4f8-1dcf-4d50-9fa5-ee8426d95744	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:54:33.275368+00	
00000000-0000-0000-0000-000000000000	4b38763f-519e-4f76-b7f4-90e3aac456c9	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:54:52.16249+00	
00000000-0000-0000-0000-000000000000	85a2ddf5-0e01-4640-9ae2-620ec900c11e	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:56:13.88693+00	
00000000-0000-0000-0000-000000000000	5a926e8e-a13e-42b0-b84d-fc9dc1c6bc6d	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:56:17.722787+00	
00000000-0000-0000-0000-000000000000	32efac34-7efe-4f4d-aa47-1872c938faed	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:56:22.296073+00	
00000000-0000-0000-0000-000000000000	46da2273-99fa-4d16-9b2e-e0a514db39d8	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 03:56:34.457063+00	
00000000-0000-0000-0000-000000000000	d9d80bf7-7fa3-49b3-8df6-ee2c27d99a5a	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 03:56:41.576509+00	
00000000-0000-0000-0000-000000000000	126f20af-d7b5-4ea8-b8b2-e11e87ae15d8	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:01:09.531067+00	
00000000-0000-0000-0000-000000000000	6f368e6a-e968-4185-95bb-f2d4730fc3b6	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 04:01:15.103533+00	
00000000-0000-0000-0000-000000000000	4a7eff9c-f67c-4ee8-990b-97cad55ba5b4	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:01:55.033828+00	
00000000-0000-0000-0000-000000000000	fc649165-11af-4622-b7e7-3c4722eef159	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 04:01:58.545256+00	
00000000-0000-0000-0000-000000000000	d325fabc-8ab2-4748-a40f-0010dc134179	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:02:13.018311+00	
00000000-0000-0000-0000-000000000000	b4ba2b59-501c-4c78-b8b2-52472e7a64d5	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 04:02:16.647634+00	
00000000-0000-0000-0000-000000000000	e0b9d800-ecd2-41ef-9862-52361bbf4fa3	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:02:41.023908+00	
00000000-0000-0000-0000-000000000000	d812cd95-6e46-4170-8255-ad97da0e802e	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 04:02:45.421783+00	
00000000-0000-0000-0000-000000000000	bb50534a-da3d-45ff-8555-49e14aa0aca0	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:15:16.504437+00	
00000000-0000-0000-0000-000000000000	944c72d2-67d8-48f8-9f92-b92ff87c56ff	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 04:15:37.735944+00	
00000000-0000-0000-0000-000000000000	0445db04-24b7-4a79-97f8-56bc797529ed	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:16:38.085032+00	
00000000-0000-0000-0000-000000000000	c7aaa8f7-18da-47bd-b705-89a05a79bc63	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 04:17:39.271889+00	
00000000-0000-0000-0000-000000000000	4fb59da9-ea77-481d-a36f-477086f5b46d	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:31:38.470217+00	
00000000-0000-0000-0000-000000000000	715018c0-4775-4f7f-965b-229c03ae535f	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 04:32:40.087902+00	
00000000-0000-0000-0000-000000000000	95eb164f-5a2d-4d4c-9b4c-ccfaf69b6cea	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:55:25.382302+00	
00000000-0000-0000-0000-000000000000	80049686-fa6b-4cc5-be6c-63fa3b61d36d	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:55:55.160517+00	
00000000-0000-0000-0000-000000000000	b32fd2f3-32f0-4046-84fb-ca6af3b253cf	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 04:56:02.582333+00	
00000000-0000-0000-0000-000000000000	aa4c4988-eb89-4b91-8289-1231f7e3815d	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:57:00.56766+00	
00000000-0000-0000-0000-000000000000	7de96479-c7e4-4216-b567-a094ee8b5f9f	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 04:59:28.228431+00	
00000000-0000-0000-0000-000000000000	ff2e80b9-6842-4536-90f6-2c75715d3064	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 05:02:26.865586+00	
00000000-0000-0000-0000-000000000000	e113b896-2a7a-440b-a655-b1d41930dbca	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 05:03:26.025098+00	
00000000-0000-0000-0000-000000000000	c45a1dcc-fae5-4765-b48a-9e610479c067	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 05:08:54.122479+00	
00000000-0000-0000-0000-000000000000	06c42341-54c2-4d88-905c-7ffdb098f420	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 05:09:18.659549+00	
00000000-0000-0000-0000-000000000000	d3bd23f4-288b-41b9-90bb-6f1109d266a8	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 05:18:50.01945+00	
00000000-0000-0000-0000-000000000000	75fa5b16-400b-4659-9ffd-b0e11444feb7	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 05:19:02.478251+00	
00000000-0000-0000-0000-000000000000	9a3db314-202a-437f-a27e-a02ab82523b2	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 05:21:58.013388+00	
00000000-0000-0000-0000-000000000000	9d5b49ef-61d7-447b-8e50-6a985b2ec56b	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 12:07:35.011897+00	
00000000-0000-0000-0000-000000000000	1107a12b-f3a6-43d7-af5c-e6ad0b44ec2e	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 12:15:02.904401+00	
00000000-0000-0000-0000-000000000000	d4f74511-0c22-41ec-a6c4-5779c856b1da	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 12:15:09.180405+00	
00000000-0000-0000-0000-000000000000	e9e8df81-27d2-47d7-9027-15409ead0115	{"action":"user_signedup","actor_id":"74574277-62b6-43a3-9054-8ef218af71c3","actor_username":"1625354205@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 13:30:23.995603+00	
00000000-0000-0000-0000-000000000000	03b8f20d-c952-4c2f-a9ac-346939180a57	{"action":"login","actor_id":"74574277-62b6-43a3-9054-8ef218af71c3","actor_username":"1625354205@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 13:30:24.002978+00	
00000000-0000-0000-0000-000000000000	a06bcdce-7a48-4437-949a-859cb71f26dd	{"action":"user_repeated_signup","actor_id":"74574277-62b6-43a3-9054-8ef218af71c3","actor_username":"1625354205@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:30:35.988126+00	
00000000-0000-0000-0000-000000000000	d40eaae6-626d-42e5-8370-5d93b6aa53d8	{"action":"user_signedup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 13:40:18.556607+00	
00000000-0000-0000-0000-000000000000	848e4881-8bef-4b00-9e7c-0578f18480cd	{"action":"login","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 13:40:18.565181+00	
00000000-0000-0000-0000-000000000000	934e0b72-3f95-449d-be65-d8eaea9d1864	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:40:27.299201+00	
00000000-0000-0000-0000-000000000000	eb124061-e5dd-41ff-b489-9b0f7ba2cd86	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:40:30.627455+00	
00000000-0000-0000-0000-000000000000	0890d6c9-05cf-4a9b-80cb-0cf846440e4a	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:40:36.334214+00	
00000000-0000-0000-0000-000000000000	01b6dbc0-1e9d-4bfd-8609-cbfbe0baed81	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:41:50.325732+00	
00000000-0000-0000-0000-000000000000	a95fd88f-f6fb-4145-a1b3-3a9a4701d254	{"action":"logout","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 13:42:04.940351+00	
00000000-0000-0000-0000-000000000000	ee71e83b-84b4-454c-9f27-b4247fe6469c	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:42:22.071327+00	
00000000-0000-0000-0000-000000000000	0436956f-8ee0-4c96-8d81-8d96d3595752	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:42:29.896653+00	
00000000-0000-0000-0000-000000000000	c167a6e3-6196-431d-8b64-0ce565ab1139	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:42:56.53559+00	
00000000-0000-0000-0000-000000000000	a1e2d542-ecc5-4b1f-9221-adc47a0fa91a	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:42:58.545142+00	
00000000-0000-0000-0000-000000000000	40018317-2459-4c15-8e2f-072a16694ab5	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:42:58.792078+00	
00000000-0000-0000-0000-000000000000	bf55d854-4120-44e0-b7bc-ac21a82fd6dc	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:42:59.738553+00	
00000000-0000-0000-0000-000000000000	1b8a8214-02f2-492f-a70c-31bb0142c854	{"action":"user_signedup","actor_id":"f535898e-d166-4213-aca4-2686d2e7d638","actor_username":"eeeika127@gmail.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 13:43:00.81343+00	
00000000-0000-0000-0000-000000000000	feab892e-d764-4a6a-90f0-935948b6613c	{"action":"login","actor_id":"f535898e-d166-4213-aca4-2686d2e7d638","actor_username":"eeeika127@gmail.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 13:43:00.817361+00	
00000000-0000-0000-0000-000000000000	31265c3e-224f-4fab-be12-6e212d7b0e62	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:43:01.120615+00	
00000000-0000-0000-0000-000000000000	47e102bd-82be-47e2-8670-bb4e271c1b46	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:43:03.507753+00	
00000000-0000-0000-0000-000000000000	e568066f-43bf-4cb8-9d8c-ed5d1006b90b	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:43:04.76343+00	
00000000-0000-0000-0000-000000000000	ba1bc2be-f560-4fce-b371-47fa5460ee9a	{"action":"user_repeated_signup","actor_id":"5f73472a-de3e-4b72-a3e8-f024d0256622","actor_username":"reki21@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 13:46:01.018712+00	
00000000-0000-0000-0000-000000000000	5d805b31-a0f9-4d9a-8ff9-60d5aef866b3	{"action":"user_signedup","actor_id":"83177677-be70-444d-a96f-5dc29ca2e32b","actor_username":"201645394@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 13:50:25.191542+00	
00000000-0000-0000-0000-000000000000	f9188dba-e264-4bd7-a82a-33c0c339b5fb	{"action":"login","actor_id":"83177677-be70-444d-a96f-5dc29ca2e32b","actor_username":"201645394@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 13:50:25.198573+00	
00000000-0000-0000-0000-000000000000	7ec73cfb-d109-427b-9e3a-552867fa1acf	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 14:12:02.488856+00	
00000000-0000-0000-0000-000000000000	6e1f2c33-71be-482b-b315-cb0cc0c5ca96	{"action":"user_signedup","actor_id":"e47e61e5-3965-49de-934a-0b7c3c30eb5a","actor_username":"1340908812@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 15:14:00.650711+00	
00000000-0000-0000-0000-000000000000	e531c4aa-9cc6-4c02-9d11-700e29ce687a	{"action":"login","actor_id":"e47e61e5-3965-49de-934a-0b7c3c30eb5a","actor_username":"1340908812@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 15:14:00.656283+00	
00000000-0000-0000-0000-000000000000	1dc27257-db5f-41d4-b484-fb1a43d1ddb8	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 15:20:52.134206+00	
00000000-0000-0000-0000-000000000000	a1682d9c-b1dd-4af1-ad5c-6909489af28e	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 15:26:56.410406+00	
00000000-0000-0000-0000-000000000000	dafa462f-7ded-4235-bf5f-4396bf6fc633	{"action":"user_signedup","actor_id":"7867db73-8f37-4cee-ad28-b8a779d3b669","actor_username":"iseealllllll@qq.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 15:34:37.913135+00	
00000000-0000-0000-0000-000000000000	6e37ec26-7bf2-4a8f-a77b-eebb5a279473	{"action":"login","actor_id":"7867db73-8f37-4cee-ad28-b8a779d3b669","actor_username":"iseealllllll@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 15:34:37.918202+00	
00000000-0000-0000-0000-000000000000	e21b3d7e-f4c8-4b45-b609-3a940b663091	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 15:35:58.796628+00	
00000000-0000-0000-0000-000000000000	3dd67502-4b2c-425e-ae0e-a652a2ff8999	{"action":"user_signedup","actor_id":"5b5638b7-2f4d-422f-bf2c-ed9f28abf38c","actor_username":"wjy_zxwy@163.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 15:37:52.734222+00	
00000000-0000-0000-0000-000000000000	620b9e7c-1662-4cd2-aee7-9307b296ebc7	{"action":"login","actor_id":"5b5638b7-2f4d-422f-bf2c-ed9f28abf38c","actor_username":"wjy_zxwy@163.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 15:37:52.740454+00	
00000000-0000-0000-0000-000000000000	f0ef288c-1f3a-4f07-bab1-fbfc63881c8a	{"action":"user_repeated_signup","actor_id":"5b5638b7-2f4d-422f-bf2c-ed9f28abf38c","actor_username":"wjy_zxwy@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:37:54.502298+00	
00000000-0000-0000-0000-000000000000	a2f6aeff-a560-4ad8-92a8-2a90429621cc	{"action":"user_signedup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 15:38:03.468449+00	
00000000-0000-0000-0000-000000000000	d8ab5ef1-2a9e-485e-befb-cb8b0e28c0d9	{"action":"login","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 15:38:03.47382+00	
00000000-0000-0000-0000-000000000000	8e66ca0a-f55d-46b5-a974-d624e0c30659	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:13.702786+00	
00000000-0000-0000-0000-000000000000	7dcc6601-e5ea-43cd-8f7f-fd807d491929	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:17.720654+00	
00000000-0000-0000-0000-000000000000	86f1a5ae-e370-4a67-a932-ccaeb13c96d9	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:19.55347+00	
00000000-0000-0000-0000-000000000000	17bb60a0-f8ef-4f4d-8654-d91890ba9d69	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:20.56278+00	
00000000-0000-0000-0000-000000000000	b8e8f84e-e797-4f27-9438-b547a1f2b6ee	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:20.571277+00	
00000000-0000-0000-0000-000000000000	fe1ab1ac-6bca-4b45-bc3c-2d14c105be0d	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:21.001256+00	
00000000-0000-0000-0000-000000000000	f2bbcc99-5cb3-4fa0-ad4e-68b0f778d234	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:37.371773+00	
00000000-0000-0000-0000-000000000000	00e7b236-330c-4702-ae97-c2295360ade9	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:38.895522+00	
00000000-0000-0000-0000-000000000000	caa037e3-3521-476f-930e-54996dce2de4	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:39.038705+00	
00000000-0000-0000-0000-000000000000	190bd5e9-b548-4157-afdc-5bd4cc0e1d8a	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.071272+00	
00000000-0000-0000-0000-000000000000	1b5da049-8b94-43ee-809a-cfc9e2ad7a45	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.080245+00	
00000000-0000-0000-0000-000000000000	1612cd03-ebe1-484d-807c-516ede22c895	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.218465+00	
00000000-0000-0000-0000-000000000000	e130b384-9947-4eac-ba60-3ed70b27ec1f	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.367884+00	
00000000-0000-0000-0000-000000000000	fc051034-ebd9-432f-a781-c29c98fe4028	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.461576+00	
00000000-0000-0000-0000-000000000000	95b90640-52f2-4036-91ef-296ea6ef4a7d	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.559886+00	
00000000-0000-0000-0000-000000000000	ba15b14f-cd82-4a29-a2cd-455386e75471	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.661741+00	
00000000-0000-0000-0000-000000000000	67cc3030-bed2-4c3e-a98d-4dd4beda5d28	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.722939+00	
00000000-0000-0000-0000-000000000000	d37e2702-f156-4717-a507-2f53f18b0ac3	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.787925+00	
00000000-0000-0000-0000-000000000000	b41d64fa-dd1f-446c-bbc7-1c5a356f100e	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.894502+00	
00000000-0000-0000-0000-000000000000	97d4ef70-ff14-4487-ba9b-3b526df1292f	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:40.944464+00	
00000000-0000-0000-0000-000000000000	f280fc36-8f2d-4649-8c37-901305056a3f	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:41.071049+00	
00000000-0000-0000-0000-000000000000	3d9b91ad-91b1-4536-ae9a-5181823ba0c4	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:41.160422+00	
00000000-0000-0000-0000-000000000000	c1b8e60d-5de0-4548-b0f3-e36aab508a1c	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:41.241574+00	
00000000-0000-0000-0000-000000000000	2fa14348-3c7e-46bb-9860-56866deb7798	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:41.708694+00	
00000000-0000-0000-0000-000000000000	89dbcb29-1625-4e71-855a-a167d4e1855d	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:41.77798+00	
00000000-0000-0000-0000-000000000000	80fa2133-39ec-404e-ba38-544dc90e7e03	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:42.312489+00	
00000000-0000-0000-0000-000000000000	b25f39cc-60b0-4052-9cfa-5af3f1009a20	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:46.760379+00	
00000000-0000-0000-0000-000000000000	1536d05a-8fcb-4003-aab2-83b1f28dc1c4	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:47.142331+00	
00000000-0000-0000-0000-000000000000	29bc39a8-bfee-448c-8e72-e8c381649378	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:48.410136+00	
00000000-0000-0000-0000-000000000000	144f9f40-d87e-414b-951f-0f3e7e2775a7	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:49.345305+00	
00000000-0000-0000-0000-000000000000	c1d420a8-8b31-4472-a811-d352b5360198	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:50.396373+00	
00000000-0000-0000-0000-000000000000	1aaf6168-0364-49e9-97da-2275f97f37ed	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:50.595378+00	
00000000-0000-0000-0000-000000000000	262163bc-0e14-427d-93dc-6254a18049c2	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:38:50.705872+00	
00000000-0000-0000-0000-000000000000	08fd2f9b-f4d6-4b08-ad6a-49267fe00bd2	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:39:34.561076+00	
00000000-0000-0000-0000-000000000000	55259ca7-f7ed-48af-90bd-9f0bcfac124d	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:39:38.545019+00	
00000000-0000-0000-0000-000000000000	2ac49cdb-6b73-46fd-8d6a-456ef1ffbe21	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:39:38.687967+00	
00000000-0000-0000-0000-000000000000	8817b293-8843-44ff-b8e9-799e9b85c845	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:39:38.870256+00	
00000000-0000-0000-0000-000000000000	ded09323-b6c5-4731-8f64-e00278303a16	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:39:38.90704+00	
00000000-0000-0000-0000-000000000000	89e1b538-f48e-4d23-a33d-d9976d66c7e3	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:39:43.859856+00	
00000000-0000-0000-0000-000000000000	aeea4fe3-ec27-42e4-be3a-7db710288ce1	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:40:01.020028+00	
00000000-0000-0000-0000-000000000000	575db5a3-7374-461a-9f78-e2e4f6fe9588	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:40:10.000903+00	
00000000-0000-0000-0000-000000000000	f9d8d480-011e-46af-b521-bfdd2a7c3c83	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:00.854979+00	
00000000-0000-0000-0000-000000000000	ae7d68f0-bd28-4a4f-976c-7734e0ec89ac	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:00.996283+00	
00000000-0000-0000-0000-000000000000	ab17a78b-e437-4c67-998b-37f33fa3050c	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:01.200592+00	
00000000-0000-0000-0000-000000000000	9ce2b083-fd63-4144-a27f-7d9e61d285cb	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:01.485516+00	
00000000-0000-0000-0000-000000000000	0a151157-91df-4abc-b29e-71f7dd0eb76b	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:01.638635+00	
00000000-0000-0000-0000-000000000000	83ec2fb7-0c16-4eeb-a1e1-55762b33f2ac	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:03.891276+00	
00000000-0000-0000-0000-000000000000	7d7ff7f9-e5dc-4f3e-ada7-a275af42e867	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:14.774344+00	
00000000-0000-0000-0000-000000000000	3db1100a-1abc-4fb3-b42c-13109de81318	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:23.725651+00	
00000000-0000-0000-0000-000000000000	a9d71631-1be1-4b01-afa0-4e09a4a59738	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:56.34715+00	
00000000-0000-0000-0000-000000000000	e394f3dd-3738-4c6b-a23f-f9de9f754a14	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:56.720062+00	
00000000-0000-0000-0000-000000000000	18b2c91e-e4c2-4869-a866-7fe2d3655477	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:41:56.768966+00	
00000000-0000-0000-0000-000000000000	b753aa8f-0ab4-4f57-9bbd-fac31a00070b	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:42:23.956188+00	
00000000-0000-0000-0000-000000000000	58a5b845-cf1c-40a6-8c01-b756fd023129	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:42:24.133202+00	
00000000-0000-0000-0000-000000000000	057d1848-8666-4101-a099-0afe4303e9a0	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:42:24.302882+00	
00000000-0000-0000-0000-000000000000	6502420a-8aaf-4ef5-8550-7887b0253711	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:42:45.276971+00	
00000000-0000-0000-0000-000000000000	e8f34a29-8c74-4f61-9b56-1c1ba66ca3fc	{"action":"user_repeated_signup","actor_id":"d4e7dd12-e347-4c19-a4b8-1165485eec35","actor_username":"arefrite@163.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 15:42:45.742428+00	
00000000-0000-0000-0000-000000000000	1c8df0ba-f6ab-46d9-98c3-812eab9e164d	{"action":"user_signedup","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"old_@test.com","user_id":"611e2e76-fa46-4688-b2f0-2f28e9b9ff4b","user_phone":""}}	2025-03-14 15:57:39.028098+00	
00000000-0000-0000-0000-000000000000	7d579176-7669-47c0-a071-b404891c3893	{"action":"user_deleted","actor_id":"00000000-0000-0000-0000-000000000000","actor_username":"service_role","actor_via_sso":false,"log_type":"team","traits":{"user_email":"old_@test.com","user_id":"611e2e76-fa46-4688-b2f0-2f28e9b9ff4b","user_phone":""}}	2025-03-14 15:58:20.853315+00	
00000000-0000-0000-0000-000000000000	bdb1b848-3744-4a5e-9c80-ac7e9951ac8f	{"action":"user_repeated_signup","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 16:22:13.329759+00	
00000000-0000-0000-0000-000000000000	b39989b8-d2bb-43c0-af5f-985127654601	{"action":"user_repeated_signup","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 16:22:26.129141+00	
00000000-0000-0000-0000-000000000000	182d9bb1-74d7-4fee-9de9-46b92fe2f6c4	{"action":"user_repeated_signup","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 16:22:31.533749+00	
00000000-0000-0000-0000-000000000000	fab7a3e8-72d4-46a7-8d1c-24d822fcf756	{"action":"user_repeated_signup","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"user","traits":{"provider":"email"}}	2025-03-14 16:22:37.311552+00	
00000000-0000-0000-0000-000000000000	c8a23ea3-100f-4390-a2de-d95dedb9414f	{"action":"user_signedup","actor_id":"8d550457-ecaf-46d4-907a-ab4ba2232366","actor_username":"test@email.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 16:22:55.846801+00	
00000000-0000-0000-0000-000000000000	caebce40-5457-417c-a334-8fcb34111b4c	{"action":"login","actor_id":"8d550457-ecaf-46d4-907a-ab4ba2232366","actor_username":"test@email.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 16:22:55.851782+00	
00000000-0000-0000-0000-000000000000	2898e181-c122-4027-b705-fb65cf3a4007	{"action":"logout","actor_id":"8d550457-ecaf-46d4-907a-ab4ba2232366","actor_username":"test@email.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 16:25:43.881545+00	
00000000-0000-0000-0000-000000000000	68221c45-6671-47f9-951c-5df3aa38d7af	{"action":"user_signedup","actor_id":"0af92adf-bca9-43f5-89f6-333dc3426e13","actor_username":"luocanyu20000506@gmail.com","actor_via_sso":false,"log_type":"team","traits":{"provider":"email"}}	2025-03-14 16:28:11.522326+00	
00000000-0000-0000-0000-000000000000	9bf72278-7ba2-4b1b-a5e6-50761303d63d	{"action":"login","actor_id":"0af92adf-bca9-43f5-89f6-333dc3426e13","actor_username":"luocanyu20000506@gmail.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 16:28:11.528059+00	
00000000-0000-0000-0000-000000000000	60f90c88-0175-4758-8a16-ae5b810bb136	{"action":"logout","actor_id":"0af92adf-bca9-43f5-89f6-333dc3426e13","actor_username":"luocanyu20000506@gmail.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 16:34:24.464277+00	
00000000-0000-0000-0000-000000000000	87706655-d1f8-414f-8f4d-8d0c30829320	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 16:44:58.065874+00	
00000000-0000-0000-0000-000000000000	3e0440ac-b9f5-409a-bdaa-9897573420d9	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 16:45:29.921136+00	
00000000-0000-0000-0000-000000000000	7161554c-2a04-4544-b489-d77a0efff633	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 16:47:34.40926+00	
00000000-0000-0000-0000-000000000000	b0919898-aa4b-4bd0-a065-55a13670b053	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 16:48:13.013819+00	
00000000-0000-0000-0000-000000000000	cd754738-4ff1-4cb7-8b7b-a828eff10a50	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 16:53:21.54085+00	
00000000-0000-0000-0000-000000000000	1098092b-c209-46e2-b850-dc35e5a7ea8c	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-14 16:53:33.27335+00	
00000000-0000-0000-0000-000000000000	703cd803-5484-48db-ba3e-9e500f71ee19	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 16:55:29.623816+00	
00000000-0000-0000-0000-000000000000	268b9023-8664-4bf5-a2c5-e4f5a277346b	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 17:08:55.487908+00	
00000000-0000-0000-0000-000000000000	a40864d6-90ed-4e4c-93e8-3aae3d5df7e8	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-14 17:23:23.169973+00	
00000000-0000-0000-0000-000000000000	a59688e1-d334-4a77-876d-a6d0abc2957d	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-15 15:54:56.864767+00	
00000000-0000-0000-0000-000000000000	fc536ab4-d12f-46a6-924d-1f7ca45571db	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-16 07:51:13.453832+00	
00000000-0000-0000-0000-000000000000	f7f91642-f113-44a2-be93-b54583a56142	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-18 04:15:44.472906+00	
00000000-0000-0000-0000-000000000000	ee4a954d-5bae-4472-8da4-ea23d8e43eba	{"action":"logout","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account"}	2025-03-18 04:16:13.971166+00	
00000000-0000-0000-0000-000000000000	aa279582-d523-485a-ab77-ede772822e97	{"action":"login","actor_id":"60b55240-c1a6-4418-9f26-f3c9e28dd969","actor_username":"944989026@qq.com","actor_via_sso":false,"log_type":"account","traits":{"provider":"email"}}	2025-03-18 04:17:53.32605+00	
\.


--
-- TOC entry 4110 (class 0 OID 29582)
-- Dependencies: 252
-- Data for Name: flow_state; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.flow_state (id, user_id, auth_code, code_challenge_method, code_challenge, provider_type, provider_access_token, provider_refresh_token, created_at, updated_at, authentication_method, auth_code_issued_at) FROM stdin;
\.


--
-- TOC entry 4111 (class 0 OID 29587)
-- Dependencies: 253
-- Data for Name: identities; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.identities (provider_id, user_id, identity_data, provider, last_sign_in_at, created_at, updated_at, id) FROM stdin;
60b55240-c1a6-4418-9f26-f3c9e28dd969	60b55240-c1a6-4418-9f26-f3c9e28dd969	{"sub": "60b55240-c1a6-4418-9f26-f3c9e28dd969", "email": "944989026@qq.com", "email_verified": false, "phone_verified": false}	email	2024-12-23 08:53:44.765392+00	2024-12-23 08:53:44.765441+00	2024-12-23 08:53:44.765441+00	92248ec3-b9e7-474e-bfa9-d488a77ef39c
05e7c68f-1dae-449c-bc4d-7901afbe4f0c	05e7c68f-1dae-449c-bc4d-7901afbe4f0c	{"sub": "05e7c68f-1dae-449c-bc4d-7901afbe4f0c", "email": "2454164865@qq.com", "email_verified": false, "phone_verified": false}	email	2025-03-01 15:38:39.709759+00	2025-03-01 15:38:39.70984+00	2025-03-01 15:38:39.70984+00	3ecb13a6-1c14-4660-b66f-a7f652bf6dbf
74574277-62b6-43a3-9054-8ef218af71c3	74574277-62b6-43a3-9054-8ef218af71c3	{"sub": "74574277-62b6-43a3-9054-8ef218af71c3", "email": "1625354205@qq.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 13:30:23.990573+00	2025-03-14 13:30:23.99063+00	2025-03-14 13:30:23.99063+00	f885c30b-728a-42b5-baae-f75dedaffced
5f73472a-de3e-4b72-a3e8-f024d0256622	5f73472a-de3e-4b72-a3e8-f024d0256622	{"sub": "5f73472a-de3e-4b72-a3e8-f024d0256622", "email": "reki21@163.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 13:40:18.553256+00	2025-03-14 13:40:18.553336+00	2025-03-14 13:40:18.553336+00	34dc7f39-a21d-4a9b-af3d-0c2a31f23020
f535898e-d166-4213-aca4-2686d2e7d638	f535898e-d166-4213-aca4-2686d2e7d638	{"sub": "f535898e-d166-4213-aca4-2686d2e7d638", "email": "eeeika127@gmail.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 13:43:00.810396+00	2025-03-14 13:43:00.810448+00	2025-03-14 13:43:00.810448+00	c58a68bd-5529-4769-93c8-62d57b00cf22
83177677-be70-444d-a96f-5dc29ca2e32b	83177677-be70-444d-a96f-5dc29ca2e32b	{"sub": "83177677-be70-444d-a96f-5dc29ca2e32b", "email": "201645394@qq.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 13:50:25.188051+00	2025-03-14 13:50:25.188103+00	2025-03-14 13:50:25.188103+00	6d3f96c0-1f64-4fe9-aaaa-c3e0d92527cf
e47e61e5-3965-49de-934a-0b7c3c30eb5a	e47e61e5-3965-49de-934a-0b7c3c30eb5a	{"sub": "e47e61e5-3965-49de-934a-0b7c3c30eb5a", "email": "1340908812@qq.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 15:14:00.647344+00	2025-03-14 15:14:00.647408+00	2025-03-14 15:14:00.647408+00	3b466657-4409-4520-9fab-1081a2553049
7867db73-8f37-4cee-ad28-b8a779d3b669	7867db73-8f37-4cee-ad28-b8a779d3b669	{"sub": "7867db73-8f37-4cee-ad28-b8a779d3b669", "email": "iseealllllll@qq.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 15:34:37.910658+00	2025-03-14 15:34:37.910707+00	2025-03-14 15:34:37.910707+00	f54bd84a-24fb-4e92-a714-dc92774501dd
5b5638b7-2f4d-422f-bf2c-ed9f28abf38c	5b5638b7-2f4d-422f-bf2c-ed9f28abf38c	{"sub": "5b5638b7-2f4d-422f-bf2c-ed9f28abf38c", "email": "wjy_zxwy@163.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 15:37:52.731537+00	2025-03-14 15:37:52.731584+00	2025-03-14 15:37:52.731584+00	f58a8214-d256-4a48-ac7c-2bf39a578748
d4e7dd12-e347-4c19-a4b8-1165485eec35	d4e7dd12-e347-4c19-a4b8-1165485eec35	{"sub": "d4e7dd12-e347-4c19-a4b8-1165485eec35", "email": "arefrite@163.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 15:38:03.466069+00	2025-03-14 15:38:03.466118+00	2025-03-14 15:38:03.466118+00	21dc44cf-29f8-4050-9543-9278596a58fd
8d550457-ecaf-46d4-907a-ab4ba2232366	8d550457-ecaf-46d4-907a-ab4ba2232366	{"sub": "8d550457-ecaf-46d4-907a-ab4ba2232366", "email": "test@email.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 16:22:55.843085+00	2025-03-14 16:22:55.84314+00	2025-03-14 16:22:55.84314+00	7bcfaece-4544-488f-85dd-b387c751c2e7
0af92adf-bca9-43f5-89f6-333dc3426e13	0af92adf-bca9-43f5-89f6-333dc3426e13	{"sub": "0af92adf-bca9-43f5-89f6-333dc3426e13", "email": "luocanyu20000506@gmail.com", "email_verified": false, "phone_verified": false}	email	2025-03-14 16:28:11.519003+00	2025-03-14 16:28:11.519066+00	2025-03-14 16:28:11.519066+00	1fc7c10f-44c1-48f8-a0b7-d8c918a0f9eb
\.


--
-- TOC entry 4112 (class 0 OID 29594)
-- Dependencies: 254
-- Data for Name: instances; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.instances (id, uuid, raw_base_config, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 4113 (class 0 OID 29599)
-- Dependencies: 255
-- Data for Name: mfa_amr_claims; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.mfa_amr_claims (session_id, created_at, updated_at, authentication_method, id) FROM stdin;
6cea40e5-883f-426c-9376-99aff6d03d88	2025-03-14 13:30:24.008726+00	2025-03-14 13:30:24.008726+00	password	ee702c77-803b-47c6-b008-da187b3f7dc1
bb0cec92-f79b-46a4-b001-2385f1b0b1e2	2025-03-14 13:43:00.823604+00	2025-03-14 13:43:00.823604+00	password	38f29a5b-f76b-4f76-b0c7-69762d2dba73
d28949e1-e28e-4b7f-b4ae-4e1148777330	2025-03-01 15:51:11.549011+00	2025-03-01 15:51:11.549011+00	password	01fa4c02-ece0-49a5-87d4-cdcc98f8c154
b895d06e-414c-4f77-b9c4-484c333727f1	2025-03-01 15:51:23.020396+00	2025-03-01 15:51:23.020396+00	password	a3828024-38aa-4537-9aac-0b80f5706581
08b5a886-48b1-4be8-8944-b51032f8dccd	2025-03-14 13:50:25.203247+00	2025-03-14 13:50:25.203247+00	password	f1d0ada6-c76f-4cee-b4c7-de520ab594ad
638b1f2d-ab5c-46fc-8035-f64c69639d25	2025-03-14 15:14:00.66173+00	2025-03-14 15:14:00.66173+00	password	2cde5323-ce2b-4549-82ed-cb07b959d3c9
bbd51299-7431-4de4-af77-9c8a157bbd9c	2025-03-14 15:34:37.925979+00	2025-03-14 15:34:37.925979+00	password	bb5ea6cd-dbeb-4568-9ca5-52a7b8c1973c
410934c0-121d-4a27-a23b-3e7b01f1b46c	2025-03-14 15:37:52.74367+00	2025-03-14 15:37:52.74367+00	password	95a24f67-1b03-4bbf-baa1-feb2345eaa50
0dd52d98-c5da-4ddd-a4c3-a4f0daddab44	2025-03-14 15:38:03.476569+00	2025-03-14 15:38:03.476569+00	password	72712c88-e3e3-45c3-9ba7-da37fa406df7
fe4e8843-dab2-4244-9e1d-e63def3dd9cd	2025-03-18 04:17:53.334931+00	2025-03-18 04:17:53.334931+00	password	d8358bff-2436-4a03-b151-c296bc6c7db6
\.


--
-- TOC entry 4114 (class 0 OID 29604)
-- Dependencies: 256
-- Data for Name: mfa_challenges; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.mfa_challenges (id, factor_id, created_at, verified_at, ip_address, otp_code, web_authn_session_data) FROM stdin;
\.


--
-- TOC entry 4115 (class 0 OID 29609)
-- Dependencies: 257
-- Data for Name: mfa_factors; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.mfa_factors (id, user_id, friendly_name, factor_type, status, created_at, updated_at, secret, phone, last_challenged_at, web_authn_credential, web_authn_aaguid) FROM stdin;
\.


--
-- TOC entry 4116 (class 0 OID 29614)
-- Dependencies: 258
-- Data for Name: one_time_tokens; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.one_time_tokens (id, user_id, token_type, token_hash, relates_to, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 4117 (class 0 OID 29622)
-- Dependencies: 259
-- Data for Name: refresh_tokens; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.refresh_tokens (instance_id, id, token, user_id, revoked, created_at, updated_at, parent, session_id) FROM stdin;
00000000-0000-0000-0000-000000000000	188	5hO1p0t0w2lo1ZcW9AdEow	74574277-62b6-43a3-9054-8ef218af71c3	f	2025-03-14 13:30:24.005921+00	2025-03-14 13:30:24.005921+00	\N	6cea40e5-883f-426c-9376-99aff6d03d88
00000000-0000-0000-0000-000000000000	190	1o0dPFl6PZrYPYCITUlODQ	f535898e-d166-4213-aca4-2686d2e7d638	f	2025-03-14 13:43:00.818941+00	2025-03-14 13:43:00.818941+00	\N	bb0cec92-f79b-46a4-b001-2385f1b0b1e2
00000000-0000-0000-0000-000000000000	136	fwAQDAf87p7oJq-nqJ68AQ	05e7c68f-1dae-449c-bc4d-7901afbe4f0c	f	2025-03-01 15:51:11.547237+00	2025-03-01 15:51:11.547237+00	\N	d28949e1-e28e-4b7f-b4ae-4e1148777330
00000000-0000-0000-0000-000000000000	137	J-AD-4Dz1c8xbG8H3oyY4w	05e7c68f-1dae-449c-bc4d-7901afbe4f0c	f	2025-03-01 15:51:23.018607+00	2025-03-01 15:51:23.018607+00	\N	b895d06e-414c-4f77-b9c4-484c333727f1
00000000-0000-0000-0000-000000000000	191	YhvVizgzZfhRNg6PbBK7rQ	83177677-be70-444d-a96f-5dc29ca2e32b	f	2025-03-14 13:50:25.200514+00	2025-03-14 13:50:25.200514+00	\N	08b5a886-48b1-4be8-8944-b51032f8dccd
00000000-0000-0000-0000-000000000000	193	8tzS3bDkJbr3zbrnUgvirA	e47e61e5-3965-49de-934a-0b7c3c30eb5a	f	2025-03-14 15:14:00.65873+00	2025-03-14 15:14:00.65873+00	\N	638b1f2d-ab5c-46fc-8035-f64c69639d25
00000000-0000-0000-0000-000000000000	196	BOIUrtTSWH8D7NEs4vDevg	7867db73-8f37-4cee-ad28-b8a779d3b669	f	2025-03-14 15:34:37.921536+00	2025-03-14 15:34:37.921536+00	\N	bbd51299-7431-4de4-af77-9c8a157bbd9c
00000000-0000-0000-0000-000000000000	197	1isGp9s8dfCbNz85TSwMCw	5b5638b7-2f4d-422f-bf2c-ed9f28abf38c	f	2025-03-14 15:37:52.742004+00	2025-03-14 15:37:52.742004+00	\N	410934c0-121d-4a27-a23b-3e7b01f1b46c
00000000-0000-0000-0000-000000000000	198	t6SxpUWgjRiQmptkiBov0g	d4e7dd12-e347-4c19-a4b8-1165485eec35	f	2025-03-14 15:38:03.475283+00	2025-03-14 15:38:03.475283+00	\N	0dd52d98-c5da-4ddd-a4c3-a4f0daddab44
00000000-0000-0000-0000-000000000000	210	LdFw9bC3GSvZRhK0_ZfNlg	60b55240-c1a6-4418-9f26-f3c9e28dd969	f	2025-03-18 04:17:53.330043+00	2025-03-18 04:17:53.330043+00	\N	fe4e8843-dab2-4244-9e1d-e63def3dd9cd
\.


--
-- TOC entry 4119 (class 0 OID 29628)
-- Dependencies: 261
-- Data for Name: saml_providers; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.saml_providers (id, sso_provider_id, entity_id, metadata_xml, metadata_url, attribute_mapping, created_at, updated_at, name_id_format) FROM stdin;
\.


--
-- TOC entry 4120 (class 0 OID 29636)
-- Dependencies: 262
-- Data for Name: saml_relay_states; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.saml_relay_states (id, sso_provider_id, request_id, for_email, redirect_to, created_at, updated_at, flow_state_id) FROM stdin;
\.


--
-- TOC entry 4121 (class 0 OID 29642)
-- Dependencies: 263
-- Data for Name: schema_migrations; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.schema_migrations (version) FROM stdin;
20171026211738
20171026211808
20171026211834
20180103212743
20180108183307
20180119214651
20180125194653
00
20210710035447
20210722035447
20210730183235
20210909172000
20210927181326
20211122151130
20211124214934
20211202183645
20220114185221
20220114185340
20220224000811
20220323170000
20220429102000
20220531120530
20220614074223
20220811173540
20221003041349
20221003041400
20221011041400
20221020193600
20221021073300
20221021082433
20221027105023
20221114143122
20221114143410
20221125140132
20221208132122
20221215195500
20221215195800
20221215195900
20230116124310
20230116124412
20230131181311
20230322519590
20230402418590
20230411005111
20230508135423
20230523124323
20230818113222
20230914180801
20231027141322
20231114161723
20231117164230
20240115144230
20240214120130
20240306115329
20240314092811
20240427152123
20240612123726
20240729123726
20240802193726
20240806073726
20241009103726
\.


--
-- TOC entry 4122 (class 0 OID 29645)
-- Dependencies: 264
-- Data for Name: sessions; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.sessions (id, user_id, created_at, updated_at, factor_id, aal, not_after, refreshed_at, user_agent, ip, tag) FROM stdin;
6cea40e5-883f-426c-9376-99aff6d03d88	74574277-62b6-43a3-9054-8ef218af71c3	2025-03-14 13:30:24.004747+00	2025-03-14 13:30:24.004747+00	\N	aal1	\N	\N	Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36	207.148.102.104	\N
bb0cec92-f79b-46a4-b001-2385f1b0b1e2	f535898e-d166-4213-aca4-2686d2e7d638	2025-03-14 13:43:00.818006+00	2025-03-14 13:43:00.818006+00	\N	aal1	\N	\N	Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36	172.97.158.74	\N
d28949e1-e28e-4b7f-b4ae-4e1148777330	05e7c68f-1dae-449c-bc4d-7901afbe4f0c	2025-03-01 15:51:11.546249+00	2025-03-01 15:51:11.546249+00	\N	aal1	\N	\N	Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.5845.97 Safari/537.36 SE 2.X MetaSr 1.0	183.46.131.67	\N
b895d06e-414c-4f77-b9c4-484c333727f1	05e7c68f-1dae-449c-bc4d-7901afbe4f0c	2025-03-01 15:51:23.017608+00	2025-03-01 15:51:23.017608+00	\N	aal1	\N	\N	Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.5845.97 Safari/537.36 SE 2.X MetaSr 1.0	183.46.131.67	\N
08b5a886-48b1-4be8-8944-b51032f8dccd	83177677-be70-444d-a96f-5dc29ca2e32b	2025-03-14 13:50:25.199333+00	2025-03-14 13:50:25.199333+00	\N	aal1	\N	\N	Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0	183.238.221.212	\N
638b1f2d-ab5c-46fc-8035-f64c69639d25	e47e61e5-3965-49de-934a-0b7c3c30eb5a	2025-03-14 15:14:00.657539+00	2025-03-14 15:14:00.657539+00	\N	aal1	\N	\N	Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:136.0) Gecko/20100101 Firefox/136.0	150.203.65.177	\N
bbd51299-7431-4de4-af77-9c8a157bbd9c	7867db73-8f37-4cee-ad28-b8a779d3b669	2025-03-14 15:34:37.918924+00	2025-03-14 15:34:37.918924+00	\N	aal1	\N	\N	Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36	207.148.102.104	\N
410934c0-121d-4a27-a23b-3e7b01f1b46c	5b5638b7-2f4d-422f-bf2c-ed9f28abf38c	2025-03-14 15:37:52.74121+00	2025-03-14 15:37:52.74121+00	\N	aal1	\N	\N	Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0	223.72.71.12	\N
0dd52d98-c5da-4ddd-a4c3-a4f0daddab44	d4e7dd12-e347-4c19-a4b8-1165485eec35	2025-03-14 15:38:03.474514+00	2025-03-14 15:38:03.474514+00	\N	aal1	\N	\N	Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0	171.114.59.69	\N
fe4e8843-dab2-4244-9e1d-e63def3dd9cd	60b55240-c1a6-4418-9f26-f3c9e28dd969	2025-03-18 04:17:53.328805+00	2025-03-18 04:17:53.328805+00	\N	aal1	\N	\N	Mozilla/5.0 (iPhone; CPU iPhone OS 18_3_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/134.0.6998.99 Mobile/15E148 Safari/604.1	125.70.163.25	\N
\.


--
-- TOC entry 4123 (class 0 OID 29650)
-- Dependencies: 265
-- Data for Name: sso_domains; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.sso_domains (id, sso_provider_id, domain, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 4124 (class 0 OID 29656)
-- Dependencies: 266
-- Data for Name: sso_providers; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.sso_providers (id, resource_id, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 4125 (class 0 OID 29662)
-- Dependencies: 267
-- Data for Name: users; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.users (instance_id, id, aud, role, email, encrypted_password, email_confirmed_at, invited_at, confirmation_token, confirmation_sent_at, recovery_token, recovery_sent_at, email_change_token_new, email_change, email_change_sent_at, last_sign_in_at, raw_app_meta_data, raw_user_meta_data, is_super_admin, created_at, updated_at, phone, phone_confirmed_at, phone_change, phone_change_token, phone_change_sent_at, email_change_token_current, email_change_confirm_status, banned_until, reauthentication_token, reauthentication_sent_at, is_sso_user, deleted_at, is_anonymous) FROM stdin;
00000000-0000-0000-0000-000000000000	05e7c68f-1dae-449c-bc4d-7901afbe4f0c	authenticated	authenticated	2454164865@qq.com	$2a$10$GhKt.XYG/nPY7CztdSKIHOVxs4SUMKma.HUuG7Kcq6IbGCNi30CLi	2025-03-01 15:38:39.716459+00	\N		\N		\N			\N	2025-03-01 15:51:23.017531+00	{"provider": "email", "providers": ["email"]}	{"sub": "05e7c68f-1dae-449c-bc4d-7901afbe4f0c", "email": "2454164865@qq.com", "email_verified": true, "phone_verified": false}	\N	2025-03-01 15:38:39.691678+00	2025-03-01 15:51:23.020015+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	5b5638b7-2f4d-422f-bf2c-ed9f28abf38c	authenticated	authenticated	wjy_zxwy@163.com	$2a$10$BkxGaAaYYoKMLP5A0p7Jl.Dr7Qr39I5l5GfKn6Dw4Rj8WI44CzYvm	2025-03-14 15:37:52.73556+00	\N		\N		\N			\N	2025-03-14 15:37:52.741133+00	{"provider": "email", "providers": ["email"]}	{"sub": "5b5638b7-2f4d-422f-bf2c-ed9f28abf38c", "email": "wjy_zxwy@163.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 15:37:52.728054+00	2025-03-14 15:37:52.743133+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	7867db73-8f37-4cee-ad28-b8a779d3b669	authenticated	authenticated	iseealllllll@qq.com	$2a$10$iwLlY9U4K.kRWXPQUmSu3ucdWqDoFv75t2u5tO7p0kh1QhzRwa71C	2025-03-14 15:34:37.914577+00	\N		\N		\N			\N	2025-03-14 15:34:37.918851+00	{"provider": "email", "providers": ["email"]}	{"sub": "7867db73-8f37-4cee-ad28-b8a779d3b669", "email": "iseealllllll@qq.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 15:34:37.907005+00	2025-03-14 15:34:37.925351+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	f535898e-d166-4213-aca4-2686d2e7d638	authenticated	authenticated	eeeika127@gmail.com	$2a$10$PNQPLncCMRZr8.UymPVYfO0kpuUTBaxHDMxwtebeRvvVumFB8pMou	2025-03-14 13:43:00.813943+00	\N		\N		\N			\N	2025-03-14 13:43:00.817924+00	{"provider": "email", "providers": ["email"]}	{"sub": "f535898e-d166-4213-aca4-2686d2e7d638", "email": "eeeika127@gmail.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 13:43:00.806915+00	2025-03-14 13:43:00.822935+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	74574277-62b6-43a3-9054-8ef218af71c3	authenticated	authenticated	1625354205@qq.com	$2a$10$lM4RnIn6HU3dv4G8gVQWi.11BAZYOO7YN2JLJDRcq9bJoMur9t8uu	2025-03-14 13:30:23.996951+00	\N		\N		\N			\N	2025-03-14 13:30:24.004672+00	{"provider": "email", "providers": ["email"]}	{"sub": "74574277-62b6-43a3-9054-8ef218af71c3", "email": "1625354205@qq.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 13:30:23.978953+00	2025-03-14 13:30:24.008174+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	e47e61e5-3965-49de-934a-0b7c3c30eb5a	authenticated	authenticated	1340908812@qq.com	$2a$10$gp1iUyWoCdQaZQ7cyDFFX.cbjVDBZCdDvnJeNjTU6NyDgO51Lp.HC	2025-03-14 15:14:00.652456+00	\N		\N		\N			\N	2025-03-14 15:14:00.65746+00	{"provider": "email", "providers": ["email"]}	{"sub": "e47e61e5-3965-49de-934a-0b7c3c30eb5a", "email": "1340908812@qq.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 15:14:00.635162+00	2025-03-14 15:14:00.661165+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	d4e7dd12-e347-4c19-a4b8-1165485eec35	authenticated	authenticated	arefrite@163.com	$2a$10$tGNS51qZfgPJzWknusqg2uMq.oN/lgB/nCDKhjoWQOo2sjFkafGE.	2025-03-14 15:38:03.469059+00	\N		\N		\N			\N	2025-03-14 15:38:03.474438+00	{"provider": "email", "providers": ["email"]}	{"sub": "d4e7dd12-e347-4c19-a4b8-1165485eec35", "email": "arefrite@163.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 15:38:03.462682+00	2025-03-14 15:38:03.476246+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	5f73472a-de3e-4b72-a3e8-f024d0256622	authenticated	authenticated	reki21@163.com	$2a$10$cN2I2NVMElQ84YG0JE0v4.NrbT0PmyOC52jSNvGJ4hmYuiuT.XdWm	2025-03-14 13:40:18.560028+00	\N		\N		\N			\N	2025-03-14 13:40:18.565754+00	{"provider": "email", "providers": ["email"]}	{"sub": "5f73472a-de3e-4b72-a3e8-f024d0256622", "email": "reki21@163.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 13:40:18.548506+00	2025-03-14 13:40:18.568312+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	83177677-be70-444d-a96f-5dc29ca2e32b	authenticated	authenticated	201645394@qq.com	$2a$10$Xc6TLJ5Z/b8ZfjSd/3s9ye9Cxxxmkn54z8k6F7rrdsCyFHJFQo8xe	2025-03-14 13:50:25.194073+00	\N		\N		\N			\N	2025-03-14 13:50:25.199245+00	{"provider": "email", "providers": ["email"]}	{"sub": "83177677-be70-444d-a96f-5dc29ca2e32b", "email": "201645394@qq.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 13:50:25.183344+00	2025-03-14 13:50:25.202705+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	60b55240-c1a6-4418-9f26-f3c9e28dd969	authenticated	authenticated	944989026@qq.com	$2a$10$7TX7u0d9.iLSYbY4thJJi.tP1IMl4FrTNKShjtomIrJYsuLfgiKw2	2024-12-23 08:53:44.768971+00	\N		\N		\N			\N	2025-03-18 04:17:53.32873+00	{"provider": "email", "providers": ["email"]}	{"sub": "60b55240-c1a6-4418-9f26-f3c9e28dd969", "email": "944989026@qq.com", "email_verified": false, "phone_verified": false}	\N	2024-12-23 08:53:44.7594+00	2025-03-18 04:17:53.334147+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	8d550457-ecaf-46d4-907a-ab4ba2232366	authenticated	authenticated	test@email.com	$2a$10$Yb/rgD5sra5O/aswY9QzEeCNqi6eozbltfKZXe.tGF1RHecttmBri	2025-03-14 16:22:55.847318+00	\N		\N		\N			\N	2025-03-14 16:22:55.852511+00	{"provider": "email", "providers": ["email"]}	{"sub": "8d550457-ecaf-46d4-907a-ab4ba2232366", "email": "test@email.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 16:22:55.835184+00	2025-03-14 16:22:55.864186+00	\N	\N			\N		0	\N		\N	f	\N	f
00000000-0000-0000-0000-000000000000	0af92adf-bca9-43f5-89f6-333dc3426e13	authenticated	authenticated	luocanyu20000506@gmail.com	$2a$10$EB34TPwI4vbfUkdhI0JESODWuShdl8pMaJddSD7sK2yL0vBSc/Q6C	2025-03-14 16:28:11.523236+00	\N		\N		\N			\N	2025-03-14 16:28:11.528712+00	{"provider": "email", "providers": ["email"]}	{"sub": "0af92adf-bca9-43f5-89f6-333dc3426e13", "email": "luocanyu20000506@gmail.com", "email_verified": true, "phone_verified": false}	\N	2025-03-14 16:28:11.513442+00	2025-03-14 16:28:11.531674+00	\N	\N			\N		0	\N		\N	f	\N	f
\.


--
-- TOC entry 3702 (class 0 OID 29178)
-- Dependencies: 238
-- Data for Name: key; Type: TABLE DATA; Schema: pgsodium; Owner: supabase_admin
--

COPY pgsodium.key (id, status, created, expires, key_type, key_id, key_context, name, associated_data, raw_key, raw_key_nonce, parent_key, comment, user_data) FROM stdin;
\.


--
-- TOC entry 4126 (class 0 OID 29677)
-- Dependencies: 268
-- Data for Name: images; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.images (id, name, data, created_at, uploaded_by) FROM stdin;
\.


--
-- TOC entry 4127 (class 0 OID 29684)
-- Dependencies: 269
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.messages (id, legacy_user_id, message, created_at, user_id) FROM stdin;
255aa12f-0801-4359-9701-2927bda855d5	cb7ecfef-bf23-4542-8f12-012798872f49	留言板功能修复好了OwO	2025-03-14 05:00:44.484301	60b55240-c1a6-4418-9f26-f3c9e28dd969
44579ecd-70e2-4787-84d1-aa8d8cf3f611	32d2cc17-7447-4e48-8cce-10eb08a4e35b	给酒吧打个广告：《EVERY DAY IS NIGHT》「调制饮料，改变人生」轻r/挂机 赛博朋克风格酒吧 Valhalla狒狒分店营业中~（拂晓海26区 公寓1号楼4号房间）	2025-03-14 16:07:11.908129	5b5638b7-2f4d-422f-bf2c-ed9f28abf38c
f2a47235-c2cc-4c7a-8fcc-9f8d593e059d	062b7d7b-fcd8-4df2-9b6f-a30633067e6a	世界是个巨大的meme	2024-03-29 13:49:17	\N
a83d41e6-c26d-4952-85bd-2dcb58c13088	1778c383-b20e-4973-ac64-6e00c0a5bb7e	来踩踩，另外怎么改名字啊！（天哪居然还可以更新（我看到了！我什么都看到了！我成小鱼了！（我什么都没看到	2025-03-14 15:37:00.68199	7867db73-8f37-4cee-ad28-b8a779d3b669
ae965972-810e-4c75-b3f2-df3003879824	f7426dac-4372-4ff2-88fa-cab13063b18e	哈喽----------------------------	2024-03-30 14:32:46	\N
549ee355-d3a0-4dcc-bf81-f5bb60563241	979f51e8-cba6-4d85-89cb-b999bf138826	嗨嗨嗨	2024-03-29 13:48:39	\N
4516ded8-26f9-4530-a78c-dc9e9e791e85	b2062468-b84f-4911-9ad5-ada259f063cd	test_new_message	2025-03-14 16:24:21.652887	8d550457-ecaf-46d4-907a-ab4ba2232366
2f6e0102-1ca7-49c1-839e-660a49791c63	c46afcbf-f084-42a8-a284-06c6251ededd	今天也要搞点行为艺术	2024-03-02 16:30:34	0af92adf-bca9-43f5-89f6-333dc3426e13
\.


--
-- TOC entry 4128 (class 0 OID 29692)
-- Dependencies: 270
-- Data for Name: user_profile; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_profile (id, legacy_user_id, name, data, created_at, uploaded_by, user_id) FROM stdin;
901a271c-78f0-46f7-9f83-e82706e4db78	4676fd14-67f9-4022-beb1-b358a4ad9d20	1740843578326_profile	data:image/jpeg;base64,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	2025-03-01 15:39:38.426169	木头睦头	05e7c68f-1dae-449c-bc4d-7901afbe4f0c
d978b54d-05ce-4ccd-9c52-878f261a8d3b	cb7ecfef-bf23-4542-8f12-012798872f49	1741954109317_profile	data:image/jpeg;base64,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	2025-03-14 12:08:30.734008	Yuki	60b55240-c1a6-4418-9f26-f3c9e28dd969
2293655e-dfcf-44e5-b689-dd60fff36bc0	6fa2321f-8383-4d7b-a5c6-b523ba2c0848	1741960430334_profile	data:image/jpeg;base64,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	2025-03-14 13:53:50.789162	Vricr	83177677-be70-444d-a96f-5dc29ca2e32b
1bde84d6-39c1-418b-9285-46bfd488ab07	b5480cc8-d748-4fa7-8a55-082413a36a09	1741965327999_profile	data:image/jpeg;base64,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	2025-03-14 15:15:28.171224	小桃	e47e61e5-3965-49de-934a-0b7c3c30eb5a
187462d5-b320-46ed-81b3-62f277e0cf08	1778c383-b20e-4973-ac64-6e00c0a5bb7e	1741966487068_profile	data:image/jpeg;base64,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	2025-03-14 15:34:47.711791	土司	7867db73-8f37-4cee-ad28-b8a779d3b669
adb8df7b-266c-4807-a9ec-103490d14c17	32d2cc17-7447-4e48-8cce-10eb08a4e35b	1741968347643_profile	data:image/jpeg;base64,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	2025-03-14 16:05:45.493774	朝夕优	5b5638b7-2f4d-422f-bf2c-ed9f28abf38c
c9efac18-b730-4f36-b45f-db45689b9e32	b2062468-b84f-4911-9ad5-ada259f063cd	1741969393334_profile	data:image/jpeg;base64,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	2025-03-14 16:23:13.549145	Administrator	8d550457-ecaf-46d4-907a-ab4ba2232366
31aea5ff-d059-4a38-860f-970ea9e6daff	c46afcbf-f084-42a8-a284-06c6251ededd	1741969708243_profile	data:image/jpeg;base64,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	2025-03-14 16:28:28.263638	Seka	0af92adf-bca9-43f5-89f6-333dc3426e13
\.


--
-- TOC entry 4129 (class 0 OID 29700)
-- Dependencies: 271
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, username, password, email, user_id) FROM stdin;
cb7ecfef-bf23-4542-8f12-012798872f49	Yuki	9973	944989026@qq.com	60b55240-c1a6-4418-9f26-f3c9e28dd969
4676fd14-67f9-4022-beb1-b358a4ad9d20	木头睦头	1204	2454164865@qq.com	05e7c68f-1dae-449c-bc4d-7901afbe4f0c
097240da-57dd-4f24-aa05-84c36ea763e3	Eika大雪	0409	eeeika127@gmail.com	f535898e-d166-4213-aca4-2686d2e7d638
6fa2321f-8383-4d7b-a5c6-b523ba2c0848	Vricr	1220	201645394@qq.com	83177677-be70-444d-a96f-5dc29ca2e32b
b5480cc8-d748-4fa7-8a55-082413a36a09	小桃	0718	1340908812@qq.com	e47e61e5-3965-49de-934a-0b7c3c30eb5a
32d2cc17-7447-4e48-8cce-10eb08a4e35b	朝夕优	1213	wjy_zxwy@163.com	5b5638b7-2f4d-422f-bf2c-ed9f28abf38c
1778c383-b20e-4973-ac64-6e00c0a5bb7e	土司	0329	iseealllllll@qq.com	7867db73-8f37-4cee-ad28-b8a779d3b669
062b7d7b-fcd8-4df2-9b6f-a30633067e6a	鲸波万仞	\N	\N	\N
f7426dac-4372-4ff2-88fa-cab13063b18e	Yan	\N	\N	\N
979f51e8-cba6-4d85-89cb-b999bf138826	Unvol	\N	\N	\N
b2062468-b84f-4911-9ad5-ada259f063cd	Administrator	9973	test@email.com	8d550457-ecaf-46d4-907a-ab4ba2232366
c46afcbf-f084-42a8-a284-06c6251ededd	Seka	9973	luocanyu20000506@gmail.com	0af92adf-bca9-43f5-89f6-333dc3426e13
f626b78d-4440-491c-b67f-e6dfad25e2f8	爱瑠	\N	\N	\N
9efff61c-554b-4f7c-a381-12b4e6468ca5	虫语	\N	\N	\N
\.


--
-- TOC entry 4130 (class 0 OID 29714)
-- Dependencies: 273
-- Data for Name: schema_migrations; Type: TABLE DATA; Schema: realtime; Owner: supabase_admin
--

COPY realtime.schema_migrations (version, inserted_at) FROM stdin;
20211116024918	2025-03-01 11:31:47
20211116045059	2025-03-01 11:31:50
20211116050929	2025-03-01 11:31:52
20211116051442	2025-03-01 11:31:54
20211116212300	2025-03-01 11:31:57
20211116213355	2025-03-01 11:31:59
20211116213934	2025-03-01 11:32:01
20211116214523	2025-03-01 11:32:04
20211122062447	2025-03-01 11:32:07
20211124070109	2025-03-01 11:32:09
20211202204204	2025-03-01 11:32:11
20211202204605	2025-03-01 11:32:13
20211210212804	2025-03-01 11:32:20
20211228014915	2025-03-01 11:32:22
20220107221237	2025-03-01 11:32:24
20220228202821	2025-03-01 11:32:26
20220312004840	2025-03-01 11:32:29
20220603231003	2025-03-01 11:32:32
20220603232444	2025-03-01 11:32:34
20220615214548	2025-03-01 11:32:37
20220712093339	2025-03-01 11:32:39
20220908172859	2025-03-01 11:32:41
20220916233421	2025-03-01 11:32:43
20230119133233	2025-03-01 11:32:45
20230128025114	2025-03-01 11:32:48
20230128025212	2025-03-01 11:32:51
20230227211149	2025-03-01 11:32:53
20230228184745	2025-03-01 11:32:55
20230308225145	2025-03-01 11:32:57
20230328144023	2025-03-01 11:32:59
20231018144023	2025-03-01 11:33:02
20231204144023	2025-03-01 11:33:05
20231204144024	2025-03-01 11:33:07
20231204144025	2025-03-01 11:33:10
20240108234812	2025-03-01 11:33:12
20240109165339	2025-03-01 11:33:14
20240227174441	2025-03-01 11:33:18
20240311171622	2025-03-01 11:33:21
20240321100241	2025-03-01 11:33:25
20240401105812	2025-03-01 11:33:31
20240418121054	2025-03-01 11:33:34
20240523004032	2025-03-01 11:33:42
20240618124746	2025-03-01 11:33:44
20240801235015	2025-03-01 11:33:47
20240805133720	2025-03-01 11:33:49
20240827160934	2025-03-01 11:33:51
20240919163303	2025-03-01 11:33:54
20240919163305	2025-03-01 11:33:56
20241019105805	2025-03-01 11:33:58
20241030150047	2025-03-01 11:34:06
20241108114728	2025-03-01 11:34:09
20241121104152	2025-03-01 11:34:12
20241130184212	2025-03-01 11:34:14
20241220035512	2025-03-01 11:34:16
20241220123912	2025-03-01 11:34:18
20241224161212	2025-03-01 11:34:21
20250107150512	2025-03-01 11:34:23
20250110162412	2025-03-01 11:34:25
20250123174212	2025-03-01 11:34:27
20250128220012	2025-03-01 11:34:29
\.


--
-- TOC entry 4131 (class 0 OID 29717)
-- Dependencies: 274
-- Data for Name: subscription; Type: TABLE DATA; Schema: realtime; Owner: supabase_admin
--

COPY realtime.subscription (id, subscription_id, entity, filters, claims, created_at) FROM stdin;
\.


--
-- TOC entry 4133 (class 0 OID 29726)
-- Dependencies: 276
-- Data for Name: buckets; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.buckets (id, name, owner, created_at, updated_at, public, avif_autodetection, file_size_limit, allowed_mime_types, owner_id) FROM stdin;
\.


--
-- TOC entry 4134 (class 0 OID 29735)
-- Dependencies: 277
-- Data for Name: migrations; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.migrations (id, name, hash, executed_at) FROM stdin;
0	create-migrations-table	e18db593bcde2aca2a408c4d1100f6abba2195df	2024-12-23 03:09:54.711307
1	initialmigration	6ab16121fbaa08bbd11b712d05f358f9b555d777	2024-12-23 03:09:54.729239
2	storage-schema	5c7968fd083fcea04050c1b7f6253c9771b99011	2024-12-23 03:09:54.740386
3	pathtoken-column	2cb1b0004b817b29d5b0a971af16bafeede4b70d	2024-12-23 03:09:54.773501
4	add-migrations-rls	427c5b63fe1c5937495d9c635c263ee7a5905058	2024-12-23 03:09:54.810247
5	add-size-functions	79e081a1455b63666c1294a440f8ad4b1e6a7f84	2024-12-23 03:09:54.82913
6	change-column-name-in-get-size	f93f62afdf6613ee5e7e815b30d02dc990201044	2024-12-23 03:09:54.844736
7	add-rls-to-buckets	e7e7f86adbc51049f341dfe8d30256c1abca17aa	2024-12-23 03:09:54.857848
8	add-public-to-buckets	fd670db39ed65f9d08b01db09d6202503ca2bab3	2024-12-23 03:09:54.881867
9	fix-search-function	3a0af29f42e35a4d101c259ed955b67e1bee6825	2024-12-23 03:09:54.896337
10	search-files-search-function	68dc14822daad0ffac3746a502234f486182ef6e	2024-12-23 03:09:54.910459
11	add-trigger-to-auto-update-updated_at-column	7425bdb14366d1739fa8a18c83100636d74dcaa2	2024-12-23 03:09:54.940382
12	add-automatic-avif-detection-flag	8e92e1266eb29518b6a4c5313ab8f29dd0d08df9	2024-12-23 03:09:54.99922
13	add-bucket-custom-limits	cce962054138135cd9a8c4bcd531598684b25e7d	2024-12-23 03:09:55.183088
14	use-bytes-for-max-size	941c41b346f9802b411f06f30e972ad4744dad27	2024-12-23 03:09:55.304777
15	add-can-insert-object-function	934146bc38ead475f4ef4b555c524ee5d66799e5	2024-12-23 03:09:55.447654
16	add-version	76debf38d3fd07dcfc747ca49096457d95b1221b	2024-12-23 03:09:55.488605
17	drop-owner-foreign-key	f1cbb288f1b7a4c1eb8c38504b80ae2a0153d101	2024-12-23 03:09:55.572619
18	add_owner_id_column_deprecate_owner	e7a511b379110b08e2f214be852c35414749fe66	2024-12-23 03:09:55.597299
19	alter-default-value-objects-id	02e5e22a78626187e00d173dc45f58fa66a4f043	2024-12-23 03:09:55.621374
20	list-objects-with-delimiter	cd694ae708e51ba82bf012bba00caf4f3b6393b7	2024-12-23 03:09:55.638497
21	s3-multipart-uploads	8c804d4a566c40cd1e4cc5b3725a664a9303657f	2024-12-23 03:09:55.675141
22	s3-multipart-uploads-big-ints	9737dc258d2397953c9953d9b86920b8be0cdb73	2024-12-23 03:09:55.722522
23	optimize-search-function	9d7e604cddc4b56a5422dc68c9313f4a1b6f132c	2024-12-23 03:09:55.757454
24	operation-function	8312e37c2bf9e76bbe841aa5fda889206d2bf8aa	2024-12-23 03:09:55.771671
25	custom-metadata	67eb93b7e8d401cafcdc97f9ac779e71a79bfe03	2024-12-23 03:09:55.792259
\.


--
-- TOC entry 4135 (class 0 OID 29739)
-- Dependencies: 278
-- Data for Name: objects; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.objects (id, bucket_id, name, owner, created_at, updated_at, last_accessed_at, metadata, version, owner_id, user_metadata) FROM stdin;
\.


--
-- TOC entry 4136 (class 0 OID 29749)
-- Dependencies: 279
-- Data for Name: s3_multipart_uploads; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.s3_multipart_uploads (id, in_progress_size, upload_signature, bucket_id, key, version, owner_id, created_at, user_metadata) FROM stdin;
\.


--
-- TOC entry 4137 (class 0 OID 29756)
-- Dependencies: 280
-- Data for Name: s3_multipart_uploads_parts; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.s3_multipart_uploads_parts (id, upload_id, size, part_number, bucket_id, key, etag, owner_id, version, created_at) FROM stdin;
\.


--
-- TOC entry 3704 (class 0 OID 29426)
-- Dependencies: 246
-- Data for Name: secrets; Type: TABLE DATA; Schema: vault; Owner: supabase_admin
--

COPY vault.secrets (id, name, description, secret, key_id, nonce, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 4311 (class 0 OID 0)
-- Dependencies: 260
-- Name: refresh_tokens_id_seq; Type: SEQUENCE SET; Schema: auth; Owner: supabase_auth_admin
--

SELECT pg_catalog.setval('auth.refresh_tokens_id_seq', 210, true);


--
-- TOC entry 4312 (class 0 OID 0)
-- Dependencies: 237
-- Name: key_key_id_seq; Type: SEQUENCE SET; Schema: pgsodium; Owner: supabase_admin
--

SELECT pg_catalog.setval('pgsodium.key_key_id_seq', 1, false);


--
-- TOC entry 4313 (class 0 OID 0)
-- Dependencies: 275
-- Name: subscription_id_seq; Type: SEQUENCE SET; Schema: realtime; Owner: supabase_admin
--

SELECT pg_catalog.setval('realtime.subscription_id_seq', 1, false);


--
-- TOC entry 3801 (class 2606 OID 29766)
-- Name: mfa_amr_claims amr_id_pk; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.mfa_amr_claims
    ADD CONSTRAINT amr_id_pk PRIMARY KEY (id);


--
-- TOC entry 3785 (class 2606 OID 29768)
-- Name: audit_log_entries audit_log_entries_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.audit_log_entries
    ADD CONSTRAINT audit_log_entries_pkey PRIMARY KEY (id);


--
-- TOC entry 3789 (class 2606 OID 29770)
-- Name: flow_state flow_state_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.flow_state
    ADD CONSTRAINT flow_state_pkey PRIMARY KEY (id);


--
-- TOC entry 3794 (class 2606 OID 29772)
-- Name: identities identities_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.identities
    ADD CONSTRAINT identities_pkey PRIMARY KEY (id);


--
-- TOC entry 3796 (class 2606 OID 29774)
-- Name: identities identities_provider_id_provider_unique; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.identities
    ADD CONSTRAINT identities_provider_id_provider_unique UNIQUE (provider_id, provider);


--
-- TOC entry 3799 (class 2606 OID 29776)
-- Name: instances instances_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.instances
    ADD CONSTRAINT instances_pkey PRIMARY KEY (id);


--
-- TOC entry 3803 (class 2606 OID 29778)
-- Name: mfa_amr_claims mfa_amr_claims_session_id_authentication_method_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.mfa_amr_claims
    ADD CONSTRAINT mfa_amr_claims_session_id_authentication_method_pkey UNIQUE (session_id, authentication_method);


--
-- TOC entry 3806 (class 2606 OID 29780)
-- Name: mfa_challenges mfa_challenges_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.mfa_challenges
    ADD CONSTRAINT mfa_challenges_pkey PRIMARY KEY (id);


--
-- TOC entry 3809 (class 2606 OID 29782)
-- Name: mfa_factors mfa_factors_last_challenged_at_key; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.mfa_factors
    ADD CONSTRAINT mfa_factors_last_challenged_at_key UNIQUE (last_challenged_at);


--
-- TOC entry 3811 (class 2606 OID 29784)
-- Name: mfa_factors mfa_factors_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.mfa_factors
    ADD CONSTRAINT mfa_factors_pkey PRIMARY KEY (id);


--
-- TOC entry 3816 (class 2606 OID 29786)
-- Name: one_time_tokens one_time_tokens_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.one_time_tokens
    ADD CONSTRAINT one_time_tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 3824 (class 2606 OID 29788)
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 3827 (class 2606 OID 29790)
-- Name: refresh_tokens refresh_tokens_token_unique; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.refresh_tokens
    ADD CONSTRAINT refresh_tokens_token_unique UNIQUE (token);


--
-- TOC entry 3830 (class 2606 OID 29792)
-- Name: saml_providers saml_providers_entity_id_key; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.saml_providers
    ADD CONSTRAINT saml_providers_entity_id_key UNIQUE (entity_id);


--
-- TOC entry 3832 (class 2606 OID 29794)
-- Name: saml_providers saml_providers_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.saml_providers
    ADD CONSTRAINT saml_providers_pkey PRIMARY KEY (id);


--
-- TOC entry 3837 (class 2606 OID 29796)
-- Name: saml_relay_states saml_relay_states_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.saml_relay_states
    ADD CONSTRAINT saml_relay_states_pkey PRIMARY KEY (id);


--
-- TOC entry 3840 (class 2606 OID 29798)
-- Name: schema_migrations schema_migrations_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.schema_migrations
    ADD CONSTRAINT schema_migrations_pkey PRIMARY KEY (version);


--
-- TOC entry 3843 (class 2606 OID 29800)
-- Name: sessions sessions_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.sessions
    ADD CONSTRAINT sessions_pkey PRIMARY KEY (id);


--
-- TOC entry 3848 (class 2606 OID 29802)
-- Name: sso_domains sso_domains_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.sso_domains
    ADD CONSTRAINT sso_domains_pkey PRIMARY KEY (id);


--
-- TOC entry 3851 (class 2606 OID 29804)
-- Name: sso_providers sso_providers_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.sso_providers
    ADD CONSTRAINT sso_providers_pkey PRIMARY KEY (id);


--
-- TOC entry 3863 (class 2606 OID 29806)
-- Name: users users_phone_key; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.users
    ADD CONSTRAINT users_phone_key UNIQUE (phone);


--
-- TOC entry 3865 (class 2606 OID 29808)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3867 (class 2606 OID 29810)
-- Name: images images_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id);


--
-- TOC entry 3869 (class 2606 OID 29812)
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- TOC entry 3871 (class 2606 OID 29814)
-- Name: user_profile user_profile_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_profile
    ADD CONSTRAINT user_profile_pkey PRIMARY KEY (id);


--
-- TOC entry 3873 (class 2606 OID 29816)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 3875 (class 2606 OID 29818)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3877 (class 2606 OID 29820)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 3879 (class 2606 OID 29822)
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: realtime; Owner: supabase_realtime_admin
--

ALTER TABLE ONLY realtime.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id, inserted_at);


--
-- TOC entry 3884 (class 2606 OID 29824)
-- Name: subscription pk_subscription; Type: CONSTRAINT; Schema: realtime; Owner: supabase_admin
--

ALTER TABLE ONLY realtime.subscription
    ADD CONSTRAINT pk_subscription PRIMARY KEY (id);


--
-- TOC entry 3881 (class 2606 OID 29826)
-- Name: schema_migrations schema_migrations_pkey; Type: CONSTRAINT; Schema: realtime; Owner: supabase_admin
--

ALTER TABLE ONLY realtime.schema_migrations
    ADD CONSTRAINT schema_migrations_pkey PRIMARY KEY (version);


--
-- TOC entry 3888 (class 2606 OID 29828)
-- Name: buckets buckets_pkey; Type: CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.buckets
    ADD CONSTRAINT buckets_pkey PRIMARY KEY (id);


--
-- TOC entry 3890 (class 2606 OID 29830)
-- Name: migrations migrations_name_key; Type: CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.migrations
    ADD CONSTRAINT migrations_name_key UNIQUE (name);


--
-- TOC entry 3892 (class 2606 OID 29832)
-- Name: migrations migrations_pkey; Type: CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.migrations
    ADD CONSTRAINT migrations_pkey PRIMARY KEY (id);


--
-- TOC entry 3897 (class 2606 OID 29834)
-- Name: objects objects_pkey; Type: CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.objects
    ADD CONSTRAINT objects_pkey PRIMARY KEY (id);


--
-- TOC entry 3902 (class 2606 OID 29836)
-- Name: s3_multipart_uploads_parts s3_multipart_uploads_parts_pkey; Type: CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.s3_multipart_uploads_parts
    ADD CONSTRAINT s3_multipart_uploads_parts_pkey PRIMARY KEY (id);


--
-- TOC entry 3900 (class 2606 OID 29838)
-- Name: s3_multipart_uploads s3_multipart_uploads_pkey; Type: CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.s3_multipart_uploads
    ADD CONSTRAINT s3_multipart_uploads_pkey PRIMARY KEY (id);


--
-- TOC entry 3786 (class 1259 OID 29839)
-- Name: audit_logs_instance_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX audit_logs_instance_id_idx ON auth.audit_log_entries USING btree (instance_id);


--
-- TOC entry 3853 (class 1259 OID 29840)
-- Name: confirmation_token_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX confirmation_token_idx ON auth.users USING btree (confirmation_token) WHERE ((confirmation_token)::text !~ '^[0-9 ]*$'::text);


--
-- TOC entry 3854 (class 1259 OID 29841)
-- Name: email_change_token_current_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX email_change_token_current_idx ON auth.users USING btree (email_change_token_current) WHERE ((email_change_token_current)::text !~ '^[0-9 ]*$'::text);


--
-- TOC entry 3855 (class 1259 OID 29842)
-- Name: email_change_token_new_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX email_change_token_new_idx ON auth.users USING btree (email_change_token_new) WHERE ((email_change_token_new)::text !~ '^[0-9 ]*$'::text);


--
-- TOC entry 3807 (class 1259 OID 29843)
-- Name: factor_id_created_at_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX factor_id_created_at_idx ON auth.mfa_factors USING btree (user_id, created_at);


--
-- TOC entry 3787 (class 1259 OID 29844)
-- Name: flow_state_created_at_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX flow_state_created_at_idx ON auth.flow_state USING btree (created_at DESC);


--
-- TOC entry 3792 (class 1259 OID 29845)
-- Name: identities_email_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX identities_email_idx ON auth.identities USING btree (email text_pattern_ops);


--
-- TOC entry 4314 (class 0 OID 0)
-- Dependencies: 3792
-- Name: INDEX identities_email_idx; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON INDEX auth.identities_email_idx IS 'Auth: Ensures indexed queries on the email column';


--
-- TOC entry 3797 (class 1259 OID 29846)
-- Name: identities_user_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX identities_user_id_idx ON auth.identities USING btree (user_id);


--
-- TOC entry 3790 (class 1259 OID 29847)
-- Name: idx_auth_code; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX idx_auth_code ON auth.flow_state USING btree (auth_code);


--
-- TOC entry 3791 (class 1259 OID 29848)
-- Name: idx_user_id_auth_method; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX idx_user_id_auth_method ON auth.flow_state USING btree (user_id, authentication_method);


--
-- TOC entry 3804 (class 1259 OID 29849)
-- Name: mfa_challenge_created_at_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX mfa_challenge_created_at_idx ON auth.mfa_challenges USING btree (created_at DESC);


--
-- TOC entry 3812 (class 1259 OID 29850)
-- Name: mfa_factors_user_friendly_name_unique; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX mfa_factors_user_friendly_name_unique ON auth.mfa_factors USING btree (friendly_name, user_id) WHERE (TRIM(BOTH FROM friendly_name) <> ''::text);


--
-- TOC entry 3813 (class 1259 OID 29851)
-- Name: mfa_factors_user_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX mfa_factors_user_id_idx ON auth.mfa_factors USING btree (user_id);


--
-- TOC entry 3817 (class 1259 OID 29852)
-- Name: one_time_tokens_relates_to_hash_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX one_time_tokens_relates_to_hash_idx ON auth.one_time_tokens USING hash (relates_to);


--
-- TOC entry 3818 (class 1259 OID 29853)
-- Name: one_time_tokens_token_hash_hash_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX one_time_tokens_token_hash_hash_idx ON auth.one_time_tokens USING hash (token_hash);


--
-- TOC entry 3819 (class 1259 OID 29854)
-- Name: one_time_tokens_user_id_token_type_key; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX one_time_tokens_user_id_token_type_key ON auth.one_time_tokens USING btree (user_id, token_type);


--
-- TOC entry 3856 (class 1259 OID 29855)
-- Name: reauthentication_token_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX reauthentication_token_idx ON auth.users USING btree (reauthentication_token) WHERE ((reauthentication_token)::text !~ '^[0-9 ]*$'::text);


--
-- TOC entry 3857 (class 1259 OID 29856)
-- Name: recovery_token_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX recovery_token_idx ON auth.users USING btree (recovery_token) WHERE ((recovery_token)::text !~ '^[0-9 ]*$'::text);


--
-- TOC entry 3820 (class 1259 OID 29857)
-- Name: refresh_tokens_instance_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX refresh_tokens_instance_id_idx ON auth.refresh_tokens USING btree (instance_id);


--
-- TOC entry 3821 (class 1259 OID 29858)
-- Name: refresh_tokens_instance_id_user_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX refresh_tokens_instance_id_user_id_idx ON auth.refresh_tokens USING btree (instance_id, user_id);


--
-- TOC entry 3822 (class 1259 OID 29859)
-- Name: refresh_tokens_parent_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX refresh_tokens_parent_idx ON auth.refresh_tokens USING btree (parent);


--
-- TOC entry 3825 (class 1259 OID 29860)
-- Name: refresh_tokens_session_id_revoked_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX refresh_tokens_session_id_revoked_idx ON auth.refresh_tokens USING btree (session_id, revoked);


--
-- TOC entry 3828 (class 1259 OID 29861)
-- Name: refresh_tokens_updated_at_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX refresh_tokens_updated_at_idx ON auth.refresh_tokens USING btree (updated_at DESC);


--
-- TOC entry 3833 (class 1259 OID 29862)
-- Name: saml_providers_sso_provider_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX saml_providers_sso_provider_id_idx ON auth.saml_providers USING btree (sso_provider_id);


--
-- TOC entry 3834 (class 1259 OID 29863)
-- Name: saml_relay_states_created_at_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX saml_relay_states_created_at_idx ON auth.saml_relay_states USING btree (created_at DESC);


--
-- TOC entry 3835 (class 1259 OID 29864)
-- Name: saml_relay_states_for_email_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX saml_relay_states_for_email_idx ON auth.saml_relay_states USING btree (for_email);


--
-- TOC entry 3838 (class 1259 OID 29865)
-- Name: saml_relay_states_sso_provider_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX saml_relay_states_sso_provider_id_idx ON auth.saml_relay_states USING btree (sso_provider_id);


--
-- TOC entry 3841 (class 1259 OID 29866)
-- Name: sessions_not_after_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX sessions_not_after_idx ON auth.sessions USING btree (not_after DESC);


--
-- TOC entry 3844 (class 1259 OID 29867)
-- Name: sessions_user_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX sessions_user_id_idx ON auth.sessions USING btree (user_id);


--
-- TOC entry 3846 (class 1259 OID 29868)
-- Name: sso_domains_domain_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX sso_domains_domain_idx ON auth.sso_domains USING btree (lower(domain));


--
-- TOC entry 3849 (class 1259 OID 29869)
-- Name: sso_domains_sso_provider_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX sso_domains_sso_provider_id_idx ON auth.sso_domains USING btree (sso_provider_id);


--
-- TOC entry 3852 (class 1259 OID 29870)
-- Name: sso_providers_resource_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX sso_providers_resource_id_idx ON auth.sso_providers USING btree (lower(resource_id));


--
-- TOC entry 3814 (class 1259 OID 29871)
-- Name: unique_phone_factor_per_user; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX unique_phone_factor_per_user ON auth.mfa_factors USING btree (user_id, phone);


--
-- TOC entry 3845 (class 1259 OID 29872)
-- Name: user_id_created_at_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX user_id_created_at_idx ON auth.sessions USING btree (user_id, created_at);


--
-- TOC entry 3858 (class 1259 OID 29873)
-- Name: users_email_partial_key; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE UNIQUE INDEX users_email_partial_key ON auth.users USING btree (email) WHERE (is_sso_user = false);


--
-- TOC entry 4315 (class 0 OID 0)
-- Dependencies: 3858
-- Name: INDEX users_email_partial_key; Type: COMMENT; Schema: auth; Owner: supabase_auth_admin
--

COMMENT ON INDEX auth.users_email_partial_key IS 'Auth: A partial unique index that applies only when is_sso_user is false';


--
-- TOC entry 3859 (class 1259 OID 29874)
-- Name: users_instance_id_email_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX users_instance_id_email_idx ON auth.users USING btree (instance_id, lower((email)::text));


--
-- TOC entry 3860 (class 1259 OID 29875)
-- Name: users_instance_id_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX users_instance_id_idx ON auth.users USING btree (instance_id);


--
-- TOC entry 3861 (class 1259 OID 29876)
-- Name: users_is_anonymous_idx; Type: INDEX; Schema: auth; Owner: supabase_auth_admin
--

CREATE INDEX users_is_anonymous_idx ON auth.users USING btree (is_anonymous);


--
-- TOC entry 3882 (class 1259 OID 29877)
-- Name: ix_realtime_subscription_entity; Type: INDEX; Schema: realtime; Owner: supabase_admin
--

CREATE INDEX ix_realtime_subscription_entity ON realtime.subscription USING btree (entity);


--
-- TOC entry 3885 (class 1259 OID 29878)
-- Name: subscription_subscription_id_entity_filters_key; Type: INDEX; Schema: realtime; Owner: supabase_admin
--

CREATE UNIQUE INDEX subscription_subscription_id_entity_filters_key ON realtime.subscription USING btree (subscription_id, entity, filters);


--
-- TOC entry 3886 (class 1259 OID 29879)
-- Name: bname; Type: INDEX; Schema: storage; Owner: supabase_storage_admin
--

CREATE UNIQUE INDEX bname ON storage.buckets USING btree (name);


--
-- TOC entry 3893 (class 1259 OID 29880)
-- Name: bucketid_objname; Type: INDEX; Schema: storage; Owner: supabase_storage_admin
--

CREATE UNIQUE INDEX bucketid_objname ON storage.objects USING btree (bucket_id, name);


--
-- TOC entry 3898 (class 1259 OID 29881)
-- Name: idx_multipart_uploads_list; Type: INDEX; Schema: storage; Owner: supabase_storage_admin
--

CREATE INDEX idx_multipart_uploads_list ON storage.s3_multipart_uploads USING btree (bucket_id, key, created_at);


--
-- TOC entry 3894 (class 1259 OID 29882)
-- Name: idx_objects_bucket_id_name; Type: INDEX; Schema: storage; Owner: supabase_storage_admin
--

CREATE INDEX idx_objects_bucket_id_name ON storage.objects USING btree (bucket_id, name COLLATE "C");


--
-- TOC entry 3895 (class 1259 OID 29883)
-- Name: name_prefix_search; Type: INDEX; Schema: storage; Owner: supabase_storage_admin
--

CREATE INDEX name_prefix_search ON storage.objects USING btree (name text_pattern_ops);


--
-- TOC entry 3923 (class 2620 OID 29884)
-- Name: subscription tr_check_filters; Type: TRIGGER; Schema: realtime; Owner: supabase_admin
--

CREATE TRIGGER tr_check_filters BEFORE INSERT OR UPDATE ON realtime.subscription FOR EACH ROW EXECUTE FUNCTION realtime.subscription_check_filters();


--
-- TOC entry 3924 (class 2620 OID 29885)
-- Name: objects update_objects_updated_at; Type: TRIGGER; Schema: storage; Owner: supabase_storage_admin
--

CREATE TRIGGER update_objects_updated_at BEFORE UPDATE ON storage.objects FOR EACH ROW EXECUTE FUNCTION storage.update_updated_at_column();


--
-- TOC entry 3903 (class 2606 OID 29886)
-- Name: identities identities_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.identities
    ADD CONSTRAINT identities_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- TOC entry 3904 (class 2606 OID 29891)
-- Name: mfa_amr_claims mfa_amr_claims_session_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.mfa_amr_claims
    ADD CONSTRAINT mfa_amr_claims_session_id_fkey FOREIGN KEY (session_id) REFERENCES auth.sessions(id) ON DELETE CASCADE;


--
-- TOC entry 3905 (class 2606 OID 29896)
-- Name: mfa_challenges mfa_challenges_auth_factor_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.mfa_challenges
    ADD CONSTRAINT mfa_challenges_auth_factor_id_fkey FOREIGN KEY (factor_id) REFERENCES auth.mfa_factors(id) ON DELETE CASCADE;


--
-- TOC entry 3906 (class 2606 OID 29901)
-- Name: mfa_factors mfa_factors_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.mfa_factors
    ADD CONSTRAINT mfa_factors_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- TOC entry 3907 (class 2606 OID 29906)
-- Name: one_time_tokens one_time_tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.one_time_tokens
    ADD CONSTRAINT one_time_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- TOC entry 3908 (class 2606 OID 29911)
-- Name: refresh_tokens refresh_tokens_session_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.refresh_tokens
    ADD CONSTRAINT refresh_tokens_session_id_fkey FOREIGN KEY (session_id) REFERENCES auth.sessions(id) ON DELETE CASCADE;


--
-- TOC entry 3909 (class 2606 OID 29916)
-- Name: saml_providers saml_providers_sso_provider_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.saml_providers
    ADD CONSTRAINT saml_providers_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE;


--
-- TOC entry 3910 (class 2606 OID 29921)
-- Name: saml_relay_states saml_relay_states_flow_state_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.saml_relay_states
    ADD CONSTRAINT saml_relay_states_flow_state_id_fkey FOREIGN KEY (flow_state_id) REFERENCES auth.flow_state(id) ON DELETE CASCADE;


--
-- TOC entry 3911 (class 2606 OID 29926)
-- Name: saml_relay_states saml_relay_states_sso_provider_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.saml_relay_states
    ADD CONSTRAINT saml_relay_states_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE;


--
-- TOC entry 3912 (class 2606 OID 29931)
-- Name: sessions sessions_user_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.sessions
    ADD CONSTRAINT sessions_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- TOC entry 3913 (class 2606 OID 29936)
-- Name: sso_domains sso_domains_sso_provider_id_fkey; Type: FK CONSTRAINT; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE ONLY auth.sso_domains
    ADD CONSTRAINT sso_domains_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE;


--
-- TOC entry 3914 (class 2606 OID 29941)
-- Name: messages messages_legacy_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_legacy_user_id_fkey FOREIGN KEY (legacy_user_id) REFERENCES public.users(id);


--
-- TOC entry 3915 (class 2606 OID 29946)
-- Name: messages messages_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id);


--
-- TOC entry 3916 (class 2606 OID 29951)
-- Name: user_profile user_profile_legacy_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_profile
    ADD CONSTRAINT user_profile_legacy_user_id_fkey FOREIGN KEY (legacy_user_id) REFERENCES public.users(id);


--
-- TOC entry 3917 (class 2606 OID 29956)
-- Name: user_profile user_profile_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_profile
    ADD CONSTRAINT user_profile_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id);


--
-- TOC entry 3918 (class 2606 OID 29961)
-- Name: users users_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id);


--
-- TOC entry 3919 (class 2606 OID 29966)
-- Name: objects objects_bucketId_fkey; Type: FK CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.objects
    ADD CONSTRAINT "objects_bucketId_fkey" FOREIGN KEY (bucket_id) REFERENCES storage.buckets(id);


--
-- TOC entry 3920 (class 2606 OID 29971)
-- Name: s3_multipart_uploads s3_multipart_uploads_bucket_id_fkey; Type: FK CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.s3_multipart_uploads
    ADD CONSTRAINT s3_multipart_uploads_bucket_id_fkey FOREIGN KEY (bucket_id) REFERENCES storage.buckets(id);


--
-- TOC entry 3921 (class 2606 OID 29976)
-- Name: s3_multipart_uploads_parts s3_multipart_uploads_parts_bucket_id_fkey; Type: FK CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.s3_multipart_uploads_parts
    ADD CONSTRAINT s3_multipart_uploads_parts_bucket_id_fkey FOREIGN KEY (bucket_id) REFERENCES storage.buckets(id);


--
-- TOC entry 3922 (class 2606 OID 29981)
-- Name: s3_multipart_uploads_parts s3_multipart_uploads_parts_upload_id_fkey; Type: FK CONSTRAINT; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE ONLY storage.s3_multipart_uploads_parts
    ADD CONSTRAINT s3_multipart_uploads_parts_upload_id_fkey FOREIGN KEY (upload_id) REFERENCES storage.s3_multipart_uploads(id) ON DELETE CASCADE;


--
-- TOC entry 4074 (class 0 OID 29576)
-- Dependencies: 251
-- Name: audit_log_entries; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.audit_log_entries ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4075 (class 0 OID 29582)
-- Dependencies: 252
-- Name: flow_state; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.flow_state ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4076 (class 0 OID 29587)
-- Dependencies: 253
-- Name: identities; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.identities ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4077 (class 0 OID 29594)
-- Dependencies: 254
-- Name: instances; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.instances ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4078 (class 0 OID 29599)
-- Dependencies: 255
-- Name: mfa_amr_claims; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.mfa_amr_claims ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4079 (class 0 OID 29604)
-- Dependencies: 256
-- Name: mfa_challenges; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.mfa_challenges ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4080 (class 0 OID 29609)
-- Dependencies: 257
-- Name: mfa_factors; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.mfa_factors ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4081 (class 0 OID 29614)
-- Dependencies: 258
-- Name: one_time_tokens; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.one_time_tokens ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4082 (class 0 OID 29622)
-- Dependencies: 259
-- Name: refresh_tokens; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.refresh_tokens ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4083 (class 0 OID 29628)
-- Dependencies: 261
-- Name: saml_providers; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.saml_providers ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4084 (class 0 OID 29636)
-- Dependencies: 262
-- Name: saml_relay_states; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.saml_relay_states ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4085 (class 0 OID 29642)
-- Dependencies: 263
-- Name: schema_migrations; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.schema_migrations ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4086 (class 0 OID 29645)
-- Dependencies: 264
-- Name: sessions; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.sessions ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4087 (class 0 OID 29650)
-- Dependencies: 265
-- Name: sso_domains; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.sso_domains ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4088 (class 0 OID 29656)
-- Dependencies: 266
-- Name: sso_providers; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.sso_providers ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4089 (class 0 OID 29662)
-- Dependencies: 267
-- Name: users; Type: ROW SECURITY; Schema: auth; Owner: supabase_auth_admin
--

ALTER TABLE auth.users ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4106 (class 3256 OID 30085)
-- Name: messages Enable delete for users based on user_id; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable delete for users based on user_id" ON public.messages FOR DELETE USING ((( SELECT auth.uid() AS uid) = user_id));


--
-- TOC entry 4099 (class 3256 OID 30077)
-- Name: users Enable insert for all users; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable insert for all users" ON public.users FOR INSERT WITH CHECK (true);


--
-- TOC entry 4101 (class 3256 OID 30080)
-- Name: images Enable insert for authenticated users only; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable insert for authenticated users only" ON public.images FOR INSERT TO authenticated WITH CHECK (true);


--
-- TOC entry 4105 (class 3256 OID 30084)
-- Name: messages Enable insert for authenticated users only; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable insert for authenticated users only" ON public.messages FOR INSERT TO authenticated WITH CHECK (true);


--
-- TOC entry 4103 (class 3256 OID 30082)
-- Name: user_profile Enable insert for authenticated users only; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable insert for authenticated users only" ON public.user_profile FOR INSERT TO authenticated WITH CHECK (true);


--
-- TOC entry 4100 (class 3256 OID 30078)
-- Name: images Enable read access for all users; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable read access for all users" ON public.images FOR SELECT USING (true);


--
-- TOC entry 4104 (class 3256 OID 30083)
-- Name: messages Enable read access for all users; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable read access for all users" ON public.messages FOR SELECT USING (true);


--
-- TOC entry 4102 (class 3256 OID 30081)
-- Name: user_profile Enable read access for all users; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable read access for all users" ON public.user_profile FOR SELECT USING (true);


--
-- TOC entry 4098 (class 3256 OID 30076)
-- Name: users Enable read access for all users; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable read access for all users" ON public.users FOR SELECT USING (true);


--
-- TOC entry 4107 (class 3256 OID 30086)
-- Name: messages Enable update for users based on user_id; Type: POLICY; Schema: public; Owner: postgres
--

CREATE POLICY "Enable update for users based on user_id" ON public.messages FOR UPDATE USING ((( SELECT auth.uid() AS uid) = user_id)) WITH CHECK (true);


--
-- TOC entry 4090 (class 0 OID 29684)
-- Dependencies: 269
-- Name: messages; Type: ROW SECURITY; Schema: public; Owner: postgres
--

ALTER TABLE public.messages ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4091 (class 0 OID 29700)
-- Dependencies: 271
-- Name: users; Type: ROW SECURITY; Schema: public; Owner: postgres
--

ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4092 (class 0 OID 29707)
-- Dependencies: 272
-- Name: messages; Type: ROW SECURITY; Schema: realtime; Owner: supabase_realtime_admin
--

ALTER TABLE realtime.messages ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4093 (class 0 OID 29726)
-- Dependencies: 276
-- Name: buckets; Type: ROW SECURITY; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE storage.buckets ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4094 (class 0 OID 29735)
-- Dependencies: 277
-- Name: migrations; Type: ROW SECURITY; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE storage.migrations ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4095 (class 0 OID 29739)
-- Dependencies: 278
-- Name: objects; Type: ROW SECURITY; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE storage.objects ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4096 (class 0 OID 29749)
-- Dependencies: 279
-- Name: s3_multipart_uploads; Type: ROW SECURITY; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE storage.s3_multipart_uploads ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4097 (class 0 OID 29756)
-- Dependencies: 280
-- Name: s3_multipart_uploads_parts; Type: ROW SECURITY; Schema: storage; Owner: supabase_storage_admin
--

ALTER TABLE storage.s3_multipart_uploads_parts ENABLE ROW LEVEL SECURITY;

--
-- TOC entry 4108 (class 6104 OID 29986)
-- Name: supabase_realtime; Type: PUBLICATION; Schema: -; Owner: postgres
--

CREATE PUBLICATION supabase_realtime WITH (publish = 'insert, update, delete, truncate');


ALTER PUBLICATION supabase_realtime OWNER TO postgres;

--
-- TOC entry 4143 (class 0 OID 0)
-- Dependencies: 12
-- Name: SCHEMA auth; Type: ACL; Schema: -; Owner: supabase_admin
--

GRANT USAGE ON SCHEMA auth TO anon;
GRANT USAGE ON SCHEMA auth TO authenticated;
GRANT USAGE ON SCHEMA auth TO service_role;
GRANT ALL ON SCHEMA auth TO supabase_auth_admin;
GRANT ALL ON SCHEMA auth TO dashboard_user;
GRANT ALL ON SCHEMA auth TO postgres;


--
-- TOC entry 4144 (class 0 OID 0)
-- Dependencies: 21
-- Name: SCHEMA extensions; Type: ACL; Schema: -; Owner: postgres
--

GRANT USAGE ON SCHEMA extensions TO anon;
GRANT USAGE ON SCHEMA extensions TO authenticated;
GRANT USAGE ON SCHEMA extensions TO service_role;
GRANT ALL ON SCHEMA extensions TO dashboard_user;


--
-- TOC entry 4146 (class 0 OID 0)
-- Dependencies: 20
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT USAGE ON SCHEMA public TO postgres;
GRANT USAGE ON SCHEMA public TO anon;
GRANT USAGE ON SCHEMA public TO authenticated;
GRANT USAGE ON SCHEMA public TO service_role;


--
-- TOC entry 4147 (class 0 OID 0)
-- Dependencies: 14
-- Name: SCHEMA realtime; Type: ACL; Schema: -; Owner: supabase_admin
--

GRANT USAGE ON SCHEMA realtime TO postgres;
GRANT USAGE ON SCHEMA realtime TO anon;
GRANT USAGE ON SCHEMA realtime TO authenticated;
GRANT USAGE ON SCHEMA realtime TO service_role;
GRANT ALL ON SCHEMA realtime TO supabase_realtime_admin;


--
-- TOC entry 4148 (class 0 OID 0)
-- Dependencies: 22
-- Name: SCHEMA storage; Type: ACL; Schema: -; Owner: supabase_admin
--

GRANT ALL ON SCHEMA storage TO postgres;
GRANT USAGE ON SCHEMA storage TO anon;
GRANT USAGE ON SCHEMA storage TO authenticated;
GRANT USAGE ON SCHEMA storage TO service_role;
GRANT ALL ON SCHEMA storage TO supabase_storage_admin;
GRANT ALL ON SCHEMA storage TO dashboard_user;


--
-- TOC entry 4156 (class 0 OID 0)
-- Dependencies: 462
-- Name: FUNCTION email(); Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT ALL ON FUNCTION auth.email() TO dashboard_user;
GRANT ALL ON FUNCTION auth.email() TO postgres;


--
-- TOC entry 4157 (class 0 OID 0)
-- Dependencies: 353
-- Name: FUNCTION jwt(); Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT ALL ON FUNCTION auth.jwt() TO postgres;
GRANT ALL ON FUNCTION auth.jwt() TO dashboard_user;


--
-- TOC entry 4159 (class 0 OID 0)
-- Dependencies: 463
-- Name: FUNCTION role(); Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT ALL ON FUNCTION auth.role() TO dashboard_user;
GRANT ALL ON FUNCTION auth.role() TO postgres;


--
-- TOC entry 4161 (class 0 OID 0)
-- Dependencies: 352
-- Name: FUNCTION uid(); Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT ALL ON FUNCTION auth.uid() TO dashboard_user;
GRANT ALL ON FUNCTION auth.uid() TO postgres;


--
-- TOC entry 4162 (class 0 OID 0)
-- Dependencies: 464
-- Name: FUNCTION algorithm_sign(signables text, secret text, algorithm text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.algorithm_sign(signables text, secret text, algorithm text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.algorithm_sign(signables text, secret text, algorithm text) TO dashboard_user;


--
-- TOC entry 4163 (class 0 OID 0)
-- Dependencies: 435
-- Name: FUNCTION armor(bytea); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.armor(bytea) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.armor(bytea) TO dashboard_user;


--
-- TOC entry 4164 (class 0 OID 0)
-- Dependencies: 434
-- Name: FUNCTION armor(bytea, text[], text[]); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.armor(bytea, text[], text[]) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.armor(bytea, text[], text[]) TO dashboard_user;


--
-- TOC entry 4165 (class 0 OID 0)
-- Dependencies: 456
-- Name: FUNCTION crypt(text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.crypt(text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.crypt(text, text) TO dashboard_user;


--
-- TOC entry 4166 (class 0 OID 0)
-- Dependencies: 436
-- Name: FUNCTION dearmor(text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.dearmor(text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.dearmor(text) TO dashboard_user;


--
-- TOC entry 4167 (class 0 OID 0)
-- Dependencies: 457
-- Name: FUNCTION decrypt(bytea, bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.decrypt(bytea, bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.decrypt(bytea, bytea, text) TO dashboard_user;


--
-- TOC entry 4168 (class 0 OID 0)
-- Dependencies: 446
-- Name: FUNCTION decrypt_iv(bytea, bytea, bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.decrypt_iv(bytea, bytea, bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.decrypt_iv(bytea, bytea, bytea, text) TO dashboard_user;


--
-- TOC entry 4169 (class 0 OID 0)
-- Dependencies: 489
-- Name: FUNCTION digest(bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.digest(bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.digest(bytea, text) TO dashboard_user;


--
-- TOC entry 4170 (class 0 OID 0)
-- Dependencies: 458
-- Name: FUNCTION digest(text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.digest(text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.digest(text, text) TO dashboard_user;


--
-- TOC entry 4171 (class 0 OID 0)
-- Dependencies: 490
-- Name: FUNCTION encrypt(bytea, bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.encrypt(bytea, bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.encrypt(bytea, bytea, text) TO dashboard_user;


--
-- TOC entry 4172 (class 0 OID 0)
-- Dependencies: 491
-- Name: FUNCTION encrypt_iv(bytea, bytea, bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.encrypt_iv(bytea, bytea, bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.encrypt_iv(bytea, bytea, bytea, text) TO dashboard_user;


--
-- TOC entry 4173 (class 0 OID 0)
-- Dependencies: 443
-- Name: FUNCTION gen_random_bytes(integer); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.gen_random_bytes(integer) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.gen_random_bytes(integer) TO dashboard_user;


--
-- TOC entry 4174 (class 0 OID 0)
-- Dependencies: 472
-- Name: FUNCTION gen_random_uuid(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.gen_random_uuid() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.gen_random_uuid() TO dashboard_user;


--
-- TOC entry 4175 (class 0 OID 0)
-- Dependencies: 473
-- Name: FUNCTION gen_salt(text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.gen_salt(text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.gen_salt(text) TO dashboard_user;


--
-- TOC entry 4176 (class 0 OID 0)
-- Dependencies: 474
-- Name: FUNCTION gen_salt(text, integer); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.gen_salt(text, integer) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.gen_salt(text, integer) TO dashboard_user;


--
-- TOC entry 4178 (class 0 OID 0)
-- Dependencies: 467
-- Name: FUNCTION grant_pg_cron_access(); Type: ACL; Schema: extensions; Owner: postgres
--

REVOKE ALL ON FUNCTION extensions.grant_pg_cron_access() FROM postgres;
GRANT ALL ON FUNCTION extensions.grant_pg_cron_access() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.grant_pg_cron_access() TO dashboard_user;


--
-- TOC entry 4180 (class 0 OID 0)
-- Dependencies: 495
-- Name: FUNCTION grant_pg_graphql_access(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.grant_pg_graphql_access() TO postgres WITH GRANT OPTION;


--
-- TOC entry 4182 (class 0 OID 0)
-- Dependencies: 521
-- Name: FUNCTION grant_pg_net_access(); Type: ACL; Schema: extensions; Owner: postgres
--

REVOKE ALL ON FUNCTION extensions.grant_pg_net_access() FROM postgres;
GRANT ALL ON FUNCTION extensions.grant_pg_net_access() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.grant_pg_net_access() TO dashboard_user;


--
-- TOC entry 4183 (class 0 OID 0)
-- Dependencies: 466
-- Name: FUNCTION hmac(bytea, bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.hmac(bytea, bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.hmac(bytea, bytea, text) TO dashboard_user;


--
-- TOC entry 4184 (class 0 OID 0)
-- Dependencies: 475
-- Name: FUNCTION hmac(text, text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.hmac(text, text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.hmac(text, text, text) TO dashboard_user;


--
-- TOC entry 4185 (class 0 OID 0)
-- Dependencies: 509
-- Name: FUNCTION pg_stat_statements(showtext boolean, OUT userid oid, OUT dbid oid, OUT toplevel boolean, OUT queryid bigint, OUT query text, OUT plans bigint, OUT total_plan_time double precision, OUT min_plan_time double precision, OUT max_plan_time double precision, OUT mean_plan_time double precision, OUT stddev_plan_time double precision, OUT calls bigint, OUT total_exec_time double precision, OUT min_exec_time double precision, OUT max_exec_time double precision, OUT mean_exec_time double precision, OUT stddev_exec_time double precision, OUT rows bigint, OUT shared_blks_hit bigint, OUT shared_blks_read bigint, OUT shared_blks_dirtied bigint, OUT shared_blks_written bigint, OUT local_blks_hit bigint, OUT local_blks_read bigint, OUT local_blks_dirtied bigint, OUT local_blks_written bigint, OUT temp_blks_read bigint, OUT temp_blks_written bigint, OUT blk_read_time double precision, OUT blk_write_time double precision, OUT temp_blk_read_time double precision, OUT temp_blk_write_time double precision, OUT wal_records bigint, OUT wal_fpi bigint, OUT wal_bytes numeric, OUT jit_functions bigint, OUT jit_generation_time double precision, OUT jit_inlining_count bigint, OUT jit_inlining_time double precision, OUT jit_optimization_count bigint, OUT jit_optimization_time double precision, OUT jit_emission_count bigint, OUT jit_emission_time double precision); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pg_stat_statements(showtext boolean, OUT userid oid, OUT dbid oid, OUT toplevel boolean, OUT queryid bigint, OUT query text, OUT plans bigint, OUT total_plan_time double precision, OUT min_plan_time double precision, OUT max_plan_time double precision, OUT mean_plan_time double precision, OUT stddev_plan_time double precision, OUT calls bigint, OUT total_exec_time double precision, OUT min_exec_time double precision, OUT max_exec_time double precision, OUT mean_exec_time double precision, OUT stddev_exec_time double precision, OUT rows bigint, OUT shared_blks_hit bigint, OUT shared_blks_read bigint, OUT shared_blks_dirtied bigint, OUT shared_blks_written bigint, OUT local_blks_hit bigint, OUT local_blks_read bigint, OUT local_blks_dirtied bigint, OUT local_blks_written bigint, OUT temp_blks_read bigint, OUT temp_blks_written bigint, OUT blk_read_time double precision, OUT blk_write_time double precision, OUT temp_blk_read_time double precision, OUT temp_blk_write_time double precision, OUT wal_records bigint, OUT wal_fpi bigint, OUT wal_bytes numeric, OUT jit_functions bigint, OUT jit_generation_time double precision, OUT jit_inlining_count bigint, OUT jit_inlining_time double precision, OUT jit_optimization_count bigint, OUT jit_optimization_time double precision, OUT jit_emission_count bigint, OUT jit_emission_time double precision) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pg_stat_statements(showtext boolean, OUT userid oid, OUT dbid oid, OUT toplevel boolean, OUT queryid bigint, OUT query text, OUT plans bigint, OUT total_plan_time double precision, OUT min_plan_time double precision, OUT max_plan_time double precision, OUT mean_plan_time double precision, OUT stddev_plan_time double precision, OUT calls bigint, OUT total_exec_time double precision, OUT min_exec_time double precision, OUT max_exec_time double precision, OUT mean_exec_time double precision, OUT stddev_exec_time double precision, OUT rows bigint, OUT shared_blks_hit bigint, OUT shared_blks_read bigint, OUT shared_blks_dirtied bigint, OUT shared_blks_written bigint, OUT local_blks_hit bigint, OUT local_blks_read bigint, OUT local_blks_dirtied bigint, OUT local_blks_written bigint, OUT temp_blks_read bigint, OUT temp_blks_written bigint, OUT blk_read_time double precision, OUT blk_write_time double precision, OUT temp_blk_read_time double precision, OUT temp_blk_write_time double precision, OUT wal_records bigint, OUT wal_fpi bigint, OUT wal_bytes numeric, OUT jit_functions bigint, OUT jit_generation_time double precision, OUT jit_inlining_count bigint, OUT jit_inlining_time double precision, OUT jit_optimization_count bigint, OUT jit_optimization_time double precision, OUT jit_emission_count bigint, OUT jit_emission_time double precision) TO dashboard_user;


--
-- TOC entry 4186 (class 0 OID 0)
-- Dependencies: 442
-- Name: FUNCTION pg_stat_statements_info(OUT dealloc bigint, OUT stats_reset timestamp with time zone); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pg_stat_statements_info(OUT dealloc bigint, OUT stats_reset timestamp with time zone) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pg_stat_statements_info(OUT dealloc bigint, OUT stats_reset timestamp with time zone) TO dashboard_user;


--
-- TOC entry 4187 (class 0 OID 0)
-- Dependencies: 444
-- Name: FUNCTION pg_stat_statements_reset(userid oid, dbid oid, queryid bigint); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pg_stat_statements_reset(userid oid, dbid oid, queryid bigint) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pg_stat_statements_reset(userid oid, dbid oid, queryid bigint) TO dashboard_user;


--
-- TOC entry 4188 (class 0 OID 0)
-- Dependencies: 437
-- Name: FUNCTION pgp_armor_headers(text, OUT key text, OUT value text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_armor_headers(text, OUT key text, OUT value text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_armor_headers(text, OUT key text, OUT value text) TO dashboard_user;


--
-- TOC entry 4189 (class 0 OID 0)
-- Dependencies: 469
-- Name: FUNCTION pgp_key_id(bytea); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_key_id(bytea) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_key_id(bytea) TO dashboard_user;


--
-- TOC entry 4190 (class 0 OID 0)
-- Dependencies: 476
-- Name: FUNCTION pgp_pub_decrypt(bytea, bytea); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt(bytea, bytea) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt(bytea, bytea) TO dashboard_user;


--
-- TOC entry 4191 (class 0 OID 0)
-- Dependencies: 478
-- Name: FUNCTION pgp_pub_decrypt(bytea, bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt(bytea, bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt(bytea, bytea, text) TO dashboard_user;


--
-- TOC entry 4192 (class 0 OID 0)
-- Dependencies: 445
-- Name: FUNCTION pgp_pub_decrypt(bytea, bytea, text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt(bytea, bytea, text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt(bytea, bytea, text, text) TO dashboard_user;


--
-- TOC entry 4193 (class 0 OID 0)
-- Dependencies: 517
-- Name: FUNCTION pgp_pub_decrypt_bytea(bytea, bytea); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt_bytea(bytea, bytea) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt_bytea(bytea, bytea) TO dashboard_user;


--
-- TOC entry 4194 (class 0 OID 0)
-- Dependencies: 479
-- Name: FUNCTION pgp_pub_decrypt_bytea(bytea, bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt_bytea(bytea, bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt_bytea(bytea, bytea, text) TO dashboard_user;


--
-- TOC entry 4195 (class 0 OID 0)
-- Dependencies: 470
-- Name: FUNCTION pgp_pub_decrypt_bytea(bytea, bytea, text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt_bytea(bytea, bytea, text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_decrypt_bytea(bytea, bytea, text, text) TO dashboard_user;


--
-- TOC entry 4196 (class 0 OID 0)
-- Dependencies: 519
-- Name: FUNCTION pgp_pub_encrypt(text, bytea); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_encrypt(text, bytea) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_encrypt(text, bytea) TO dashboard_user;


--
-- TOC entry 4197 (class 0 OID 0)
-- Dependencies: 518
-- Name: FUNCTION pgp_pub_encrypt(text, bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_encrypt(text, bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_encrypt(text, bytea, text) TO dashboard_user;


--
-- TOC entry 4198 (class 0 OID 0)
-- Dependencies: 477
-- Name: FUNCTION pgp_pub_encrypt_bytea(bytea, bytea); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_encrypt_bytea(bytea, bytea) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_encrypt_bytea(bytea, bytea) TO dashboard_user;


--
-- TOC entry 4199 (class 0 OID 0)
-- Dependencies: 520
-- Name: FUNCTION pgp_pub_encrypt_bytea(bytea, bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_pub_encrypt_bytea(bytea, bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_pub_encrypt_bytea(bytea, bytea, text) TO dashboard_user;


--
-- TOC entry 4200 (class 0 OID 0)
-- Dependencies: 480
-- Name: FUNCTION pgp_sym_decrypt(bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_sym_decrypt(bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_sym_decrypt(bytea, text) TO dashboard_user;


--
-- TOC entry 4201 (class 0 OID 0)
-- Dependencies: 481
-- Name: FUNCTION pgp_sym_decrypt(bytea, text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_sym_decrypt(bytea, text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_sym_decrypt(bytea, text, text) TO dashboard_user;


--
-- TOC entry 4202 (class 0 OID 0)
-- Dependencies: 482
-- Name: FUNCTION pgp_sym_decrypt_bytea(bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_sym_decrypt_bytea(bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_sym_decrypt_bytea(bytea, text) TO dashboard_user;


--
-- TOC entry 4203 (class 0 OID 0)
-- Dependencies: 483
-- Name: FUNCTION pgp_sym_decrypt_bytea(bytea, text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_sym_decrypt_bytea(bytea, text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_sym_decrypt_bytea(bytea, text, text) TO dashboard_user;


--
-- TOC entry 4204 (class 0 OID 0)
-- Dependencies: 484
-- Name: FUNCTION pgp_sym_encrypt(text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_sym_encrypt(text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_sym_encrypt(text, text) TO dashboard_user;


--
-- TOC entry 4205 (class 0 OID 0)
-- Dependencies: 485
-- Name: FUNCTION pgp_sym_encrypt(text, text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_sym_encrypt(text, text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_sym_encrypt(text, text, text) TO dashboard_user;


--
-- TOC entry 4206 (class 0 OID 0)
-- Dependencies: 486
-- Name: FUNCTION pgp_sym_encrypt_bytea(bytea, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_sym_encrypt_bytea(bytea, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_sym_encrypt_bytea(bytea, text) TO dashboard_user;


--
-- TOC entry 4207 (class 0 OID 0)
-- Dependencies: 487
-- Name: FUNCTION pgp_sym_encrypt_bytea(bytea, text, text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgp_sym_encrypt_bytea(bytea, text, text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.pgp_sym_encrypt_bytea(bytea, text, text) TO dashboard_user;


--
-- TOC entry 4208 (class 0 OID 0)
-- Dependencies: 492
-- Name: FUNCTION pgrst_ddl_watch(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgrst_ddl_watch() TO postgres WITH GRANT OPTION;


--
-- TOC entry 4209 (class 0 OID 0)
-- Dependencies: 350
-- Name: FUNCTION pgrst_drop_watch(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.pgrst_drop_watch() TO postgres WITH GRANT OPTION;


--
-- TOC entry 4211 (class 0 OID 0)
-- Dependencies: 514
-- Name: FUNCTION set_graphql_placeholder(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.set_graphql_placeholder() TO postgres WITH GRANT OPTION;


--
-- TOC entry 4212 (class 0 OID 0)
-- Dependencies: 510
-- Name: FUNCTION sign(payload json, secret text, algorithm text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.sign(payload json, secret text, algorithm text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.sign(payload json, secret text, algorithm text) TO dashboard_user;


--
-- TOC entry 4213 (class 0 OID 0)
-- Dependencies: 471
-- Name: FUNCTION try_cast_double(inp text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.try_cast_double(inp text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.try_cast_double(inp text) TO dashboard_user;


--
-- TOC entry 4214 (class 0 OID 0)
-- Dependencies: 507
-- Name: FUNCTION url_decode(data text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.url_decode(data text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.url_decode(data text) TO dashboard_user;


--
-- TOC entry 4215 (class 0 OID 0)
-- Dependencies: 488
-- Name: FUNCTION url_encode(data bytea); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.url_encode(data bytea) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.url_encode(data bytea) TO dashboard_user;


--
-- TOC entry 4216 (class 0 OID 0)
-- Dependencies: 335
-- Name: FUNCTION uuid_generate_v1(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_generate_v1() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_generate_v1() TO dashboard_user;


--
-- TOC entry 4217 (class 0 OID 0)
-- Dependencies: 336
-- Name: FUNCTION uuid_generate_v1mc(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_generate_v1mc() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_generate_v1mc() TO dashboard_user;


--
-- TOC entry 4218 (class 0 OID 0)
-- Dependencies: 511
-- Name: FUNCTION uuid_generate_v3(namespace uuid, name text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_generate_v3(namespace uuid, name text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_generate_v3(namespace uuid, name text) TO dashboard_user;


--
-- TOC entry 4219 (class 0 OID 0)
-- Dependencies: 337
-- Name: FUNCTION uuid_generate_v4(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_generate_v4() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_generate_v4() TO dashboard_user;


--
-- TOC entry 4220 (class 0 OID 0)
-- Dependencies: 338
-- Name: FUNCTION uuid_generate_v5(namespace uuid, name text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_generate_v5(namespace uuid, name text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_generate_v5(namespace uuid, name text) TO dashboard_user;


--
-- TOC entry 4221 (class 0 OID 0)
-- Dependencies: 339
-- Name: FUNCTION uuid_nil(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_nil() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_nil() TO dashboard_user;


--
-- TOC entry 4222 (class 0 OID 0)
-- Dependencies: 504
-- Name: FUNCTION uuid_ns_dns(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_ns_dns() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_ns_dns() TO dashboard_user;


--
-- TOC entry 4223 (class 0 OID 0)
-- Dependencies: 505
-- Name: FUNCTION uuid_ns_oid(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_ns_oid() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_ns_oid() TO dashboard_user;


--
-- TOC entry 4224 (class 0 OID 0)
-- Dependencies: 493
-- Name: FUNCTION uuid_ns_url(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_ns_url() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_ns_url() TO dashboard_user;


--
-- TOC entry 4225 (class 0 OID 0)
-- Dependencies: 506
-- Name: FUNCTION uuid_ns_x500(); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.uuid_ns_x500() TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.uuid_ns_x500() TO dashboard_user;


--
-- TOC entry 4226 (class 0 OID 0)
-- Dependencies: 494
-- Name: FUNCTION verify(token text, secret text, algorithm text); Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT ALL ON FUNCTION extensions.verify(token text, secret text, algorithm text) TO postgres WITH GRANT OPTION;
GRANT ALL ON FUNCTION extensions.verify(token text, secret text, algorithm text) TO dashboard_user;


--
-- TOC entry 4227 (class 0 OID 0)
-- Dependencies: 522
-- Name: FUNCTION graphql("operationName" text, query text, variables jsonb, extensions jsonb); Type: ACL; Schema: graphql_public; Owner: supabase_admin
--

GRANT ALL ON FUNCTION graphql_public.graphql("operationName" text, query text, variables jsonb, extensions jsonb) TO postgres;
GRANT ALL ON FUNCTION graphql_public.graphql("operationName" text, query text, variables jsonb, extensions jsonb) TO anon;
GRANT ALL ON FUNCTION graphql_public.graphql("operationName" text, query text, variables jsonb, extensions jsonb) TO authenticated;
GRANT ALL ON FUNCTION graphql_public.graphql("operationName" text, query text, variables jsonb, extensions jsonb) TO service_role;


--
-- TOC entry 4228 (class 0 OID 0)
-- Dependencies: 448
-- Name: FUNCTION get_auth(p_usename text); Type: ACL; Schema: pgbouncer; Owner: supabase_admin
--

REVOKE ALL ON FUNCTION pgbouncer.get_auth(p_usename text) FROM PUBLIC;
GRANT ALL ON FUNCTION pgbouncer.get_auth(p_usename text) TO pgbouncer;
GRANT ALL ON FUNCTION pgbouncer.get_auth(p_usename text) TO postgres;


--
-- TOC entry 4229 (class 0 OID 0)
-- Dependencies: 515
-- Name: FUNCTION crypto_aead_det_decrypt(message bytea, additional bytea, key_uuid uuid, nonce bytea); Type: ACL; Schema: pgsodium; Owner: pgsodium_keymaker
--

GRANT ALL ON FUNCTION pgsodium.crypto_aead_det_decrypt(message bytea, additional bytea, key_uuid uuid, nonce bytea) TO service_role;


--
-- TOC entry 4230 (class 0 OID 0)
-- Dependencies: 516
-- Name: FUNCTION crypto_aead_det_encrypt(message bytea, additional bytea, key_uuid uuid, nonce bytea); Type: ACL; Schema: pgsodium; Owner: pgsodium_keymaker
--

GRANT ALL ON FUNCTION pgsodium.crypto_aead_det_encrypt(message bytea, additional bytea, key_uuid uuid, nonce bytea) TO service_role;


--
-- TOC entry 4231 (class 0 OID 0)
-- Dependencies: 441
-- Name: FUNCTION crypto_aead_det_keygen(); Type: ACL; Schema: pgsodium; Owner: supabase_admin
--

GRANT ALL ON FUNCTION pgsodium.crypto_aead_det_keygen() TO service_role;


--
-- TOC entry 4232 (class 0 OID 0)
-- Dependencies: 468
-- Name: FUNCTION apply_rls(wal jsonb, max_record_bytes integer); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.apply_rls(wal jsonb, max_record_bytes integer) TO postgres;
GRANT ALL ON FUNCTION realtime.apply_rls(wal jsonb, max_record_bytes integer) TO dashboard_user;
GRANT ALL ON FUNCTION realtime.apply_rls(wal jsonb, max_record_bytes integer) TO anon;
GRANT ALL ON FUNCTION realtime.apply_rls(wal jsonb, max_record_bytes integer) TO authenticated;
GRANT ALL ON FUNCTION realtime.apply_rls(wal jsonb, max_record_bytes integer) TO service_role;
GRANT ALL ON FUNCTION realtime.apply_rls(wal jsonb, max_record_bytes integer) TO supabase_realtime_admin;


--
-- TOC entry 4233 (class 0 OID 0)
-- Dependencies: 451
-- Name: FUNCTION broadcast_changes(topic_name text, event_name text, operation text, table_name text, table_schema text, new record, old record, level text); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.broadcast_changes(topic_name text, event_name text, operation text, table_name text, table_schema text, new record, old record, level text) TO postgres;
GRANT ALL ON FUNCTION realtime.broadcast_changes(topic_name text, event_name text, operation text, table_name text, table_schema text, new record, old record, level text) TO dashboard_user;


--
-- TOC entry 4234 (class 0 OID 0)
-- Dependencies: 465
-- Name: FUNCTION build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]) TO postgres;
GRANT ALL ON FUNCTION realtime.build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]) TO dashboard_user;
GRANT ALL ON FUNCTION realtime.build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]) TO anon;
GRANT ALL ON FUNCTION realtime.build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]) TO authenticated;
GRANT ALL ON FUNCTION realtime.build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]) TO service_role;
GRANT ALL ON FUNCTION realtime.build_prepared_statement_sql(prepared_statement_name text, entity regclass, columns realtime.wal_column[]) TO supabase_realtime_admin;


--
-- TOC entry 4235 (class 0 OID 0)
-- Dependencies: 447
-- Name: FUNCTION "cast"(val text, type_ regtype); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime."cast"(val text, type_ regtype) TO postgres;
GRANT ALL ON FUNCTION realtime."cast"(val text, type_ regtype) TO dashboard_user;
GRANT ALL ON FUNCTION realtime."cast"(val text, type_ regtype) TO anon;
GRANT ALL ON FUNCTION realtime."cast"(val text, type_ regtype) TO authenticated;
GRANT ALL ON FUNCTION realtime."cast"(val text, type_ regtype) TO service_role;
GRANT ALL ON FUNCTION realtime."cast"(val text, type_ regtype) TO supabase_realtime_admin;


--
-- TOC entry 4236 (class 0 OID 0)
-- Dependencies: 512
-- Name: FUNCTION check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text) TO postgres;
GRANT ALL ON FUNCTION realtime.check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text) TO dashboard_user;
GRANT ALL ON FUNCTION realtime.check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text) TO anon;
GRANT ALL ON FUNCTION realtime.check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text) TO authenticated;
GRANT ALL ON FUNCTION realtime.check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text) TO service_role;
GRANT ALL ON FUNCTION realtime.check_equality_op(op realtime.equality_op, type_ regtype, val_1 text, val_2 text) TO supabase_realtime_admin;


--
-- TOC entry 4237 (class 0 OID 0)
-- Dependencies: 496
-- Name: FUNCTION is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]) TO postgres;
GRANT ALL ON FUNCTION realtime.is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]) TO dashboard_user;
GRANT ALL ON FUNCTION realtime.is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]) TO anon;
GRANT ALL ON FUNCTION realtime.is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]) TO authenticated;
GRANT ALL ON FUNCTION realtime.is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]) TO service_role;
GRANT ALL ON FUNCTION realtime.is_visible_through_filters(columns realtime.wal_column[], filters realtime.user_defined_filter[]) TO supabase_realtime_admin;


--
-- TOC entry 4238 (class 0 OID 0)
-- Dependencies: 497
-- Name: FUNCTION list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer) TO postgres;
GRANT ALL ON FUNCTION realtime.list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer) TO dashboard_user;
GRANT ALL ON FUNCTION realtime.list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer) TO anon;
GRANT ALL ON FUNCTION realtime.list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer) TO authenticated;
GRANT ALL ON FUNCTION realtime.list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer) TO service_role;
GRANT ALL ON FUNCTION realtime.list_changes(publication name, slot_name name, max_changes integer, max_record_bytes integer) TO supabase_realtime_admin;


--
-- TOC entry 4239 (class 0 OID 0)
-- Dependencies: 501
-- Name: FUNCTION quote_wal2json(entity regclass); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.quote_wal2json(entity regclass) TO postgres;
GRANT ALL ON FUNCTION realtime.quote_wal2json(entity regclass) TO dashboard_user;
GRANT ALL ON FUNCTION realtime.quote_wal2json(entity regclass) TO anon;
GRANT ALL ON FUNCTION realtime.quote_wal2json(entity regclass) TO authenticated;
GRANT ALL ON FUNCTION realtime.quote_wal2json(entity regclass) TO service_role;
GRANT ALL ON FUNCTION realtime.quote_wal2json(entity regclass) TO supabase_realtime_admin;


--
-- TOC entry 4240 (class 0 OID 0)
-- Dependencies: 508
-- Name: FUNCTION send(payload jsonb, event text, topic text, private boolean); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.send(payload jsonb, event text, topic text, private boolean) TO postgres;
GRANT ALL ON FUNCTION realtime.send(payload jsonb, event text, topic text, private boolean) TO dashboard_user;


--
-- TOC entry 4241 (class 0 OID 0)
-- Dependencies: 513
-- Name: FUNCTION subscription_check_filters(); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.subscription_check_filters() TO postgres;
GRANT ALL ON FUNCTION realtime.subscription_check_filters() TO dashboard_user;
GRANT ALL ON FUNCTION realtime.subscription_check_filters() TO anon;
GRANT ALL ON FUNCTION realtime.subscription_check_filters() TO authenticated;
GRANT ALL ON FUNCTION realtime.subscription_check_filters() TO service_role;
GRANT ALL ON FUNCTION realtime.subscription_check_filters() TO supabase_realtime_admin;


--
-- TOC entry 4242 (class 0 OID 0)
-- Dependencies: 452
-- Name: FUNCTION to_regrole(role_name text); Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON FUNCTION realtime.to_regrole(role_name text) TO postgres;
GRANT ALL ON FUNCTION realtime.to_regrole(role_name text) TO dashboard_user;
GRANT ALL ON FUNCTION realtime.to_regrole(role_name text) TO anon;
GRANT ALL ON FUNCTION realtime.to_regrole(role_name text) TO authenticated;
GRANT ALL ON FUNCTION realtime.to_regrole(role_name text) TO service_role;
GRANT ALL ON FUNCTION realtime.to_regrole(role_name text) TO supabase_realtime_admin;


--
-- TOC entry 4243 (class 0 OID 0)
-- Dependencies: 453
-- Name: FUNCTION topic(); Type: ACL; Schema: realtime; Owner: supabase_realtime_admin
--

GRANT ALL ON FUNCTION realtime.topic() TO postgres;
GRANT ALL ON FUNCTION realtime.topic() TO dashboard_user;


--
-- TOC entry 4244 (class 0 OID 0)
-- Dependencies: 454
-- Name: FUNCTION can_insert_object(bucketid text, name text, owner uuid, metadata jsonb); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.can_insert_object(bucketid text, name text, owner uuid, metadata jsonb) TO postgres;


--
-- TOC entry 4245 (class 0 OID 0)
-- Dependencies: 449
-- Name: FUNCTION extension(name text); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.extension(name text) TO postgres;


--
-- TOC entry 4246 (class 0 OID 0)
-- Dependencies: 450
-- Name: FUNCTION filename(name text); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.filename(name text) TO postgres;


--
-- TOC entry 4247 (class 0 OID 0)
-- Dependencies: 455
-- Name: FUNCTION foldername(name text); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.foldername(name text) TO postgres;


--
-- TOC entry 4248 (class 0 OID 0)
-- Dependencies: 354
-- Name: FUNCTION get_size_by_bucket(); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.get_size_by_bucket() TO postgres;


--
-- TOC entry 4249 (class 0 OID 0)
-- Dependencies: 503
-- Name: FUNCTION list_multipart_uploads_with_delimiter(bucket_id text, prefix_param text, delimiter_param text, max_keys integer, next_key_token text, next_upload_token text); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.list_multipart_uploads_with_delimiter(bucket_id text, prefix_param text, delimiter_param text, max_keys integer, next_key_token text, next_upload_token text) TO postgres;


--
-- TOC entry 4250 (class 0 OID 0)
-- Dependencies: 502
-- Name: FUNCTION list_objects_with_delimiter(bucket_id text, prefix_param text, delimiter_param text, max_keys integer, start_after text, next_token text); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.list_objects_with_delimiter(bucket_id text, prefix_param text, delimiter_param text, max_keys integer, start_after text, next_token text) TO postgres;


--
-- TOC entry 4251 (class 0 OID 0)
-- Dependencies: 461
-- Name: FUNCTION operation(); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.operation() TO postgres;


--
-- TOC entry 4252 (class 0 OID 0)
-- Dependencies: 459
-- Name: FUNCTION search(prefix text, bucketname text, limits integer, levels integer, offsets integer, search text, sortcolumn text, sortorder text); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.search(prefix text, bucketname text, limits integer, levels integer, offsets integer, search text, sortcolumn text, sortorder text) TO postgres;


--
-- TOC entry 4253 (class 0 OID 0)
-- Dependencies: 460
-- Name: FUNCTION update_updated_at_column(); Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT ALL ON FUNCTION storage.update_updated_at_column() TO postgres;


--
-- TOC entry 4255 (class 0 OID 0)
-- Dependencies: 251
-- Name: TABLE audit_log_entries; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.audit_log_entries TO dashboard_user;
GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.audit_log_entries TO postgres;
GRANT SELECT ON TABLE auth.audit_log_entries TO postgres WITH GRANT OPTION;


--
-- TOC entry 4257 (class 0 OID 0)
-- Dependencies: 252
-- Name: TABLE flow_state; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.flow_state TO postgres;
GRANT SELECT ON TABLE auth.flow_state TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.flow_state TO dashboard_user;


--
-- TOC entry 4260 (class 0 OID 0)
-- Dependencies: 253
-- Name: TABLE identities; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.identities TO postgres;
GRANT SELECT ON TABLE auth.identities TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.identities TO dashboard_user;


--
-- TOC entry 4262 (class 0 OID 0)
-- Dependencies: 254
-- Name: TABLE instances; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.instances TO dashboard_user;
GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.instances TO postgres;
GRANT SELECT ON TABLE auth.instances TO postgres WITH GRANT OPTION;


--
-- TOC entry 4264 (class 0 OID 0)
-- Dependencies: 255
-- Name: TABLE mfa_amr_claims; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.mfa_amr_claims TO postgres;
GRANT SELECT ON TABLE auth.mfa_amr_claims TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.mfa_amr_claims TO dashboard_user;


--
-- TOC entry 4266 (class 0 OID 0)
-- Dependencies: 256
-- Name: TABLE mfa_challenges; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.mfa_challenges TO postgres;
GRANT SELECT ON TABLE auth.mfa_challenges TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.mfa_challenges TO dashboard_user;


--
-- TOC entry 4268 (class 0 OID 0)
-- Dependencies: 257
-- Name: TABLE mfa_factors; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.mfa_factors TO postgres;
GRANT SELECT ON TABLE auth.mfa_factors TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.mfa_factors TO dashboard_user;


--
-- TOC entry 4269 (class 0 OID 0)
-- Dependencies: 258
-- Name: TABLE one_time_tokens; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.one_time_tokens TO postgres;
GRANT SELECT ON TABLE auth.one_time_tokens TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.one_time_tokens TO dashboard_user;


--
-- TOC entry 4271 (class 0 OID 0)
-- Dependencies: 259
-- Name: TABLE refresh_tokens; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.refresh_tokens TO dashboard_user;
GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.refresh_tokens TO postgres;
GRANT SELECT ON TABLE auth.refresh_tokens TO postgres WITH GRANT OPTION;


--
-- TOC entry 4273 (class 0 OID 0)
-- Dependencies: 260
-- Name: SEQUENCE refresh_tokens_id_seq; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT ALL ON SEQUENCE auth.refresh_tokens_id_seq TO dashboard_user;
GRANT ALL ON SEQUENCE auth.refresh_tokens_id_seq TO postgres;


--
-- TOC entry 4275 (class 0 OID 0)
-- Dependencies: 261
-- Name: TABLE saml_providers; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.saml_providers TO postgres;
GRANT SELECT ON TABLE auth.saml_providers TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.saml_providers TO dashboard_user;


--
-- TOC entry 4277 (class 0 OID 0)
-- Dependencies: 262
-- Name: TABLE saml_relay_states; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.saml_relay_states TO postgres;
GRANT SELECT ON TABLE auth.saml_relay_states TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.saml_relay_states TO dashboard_user;


--
-- TOC entry 4279 (class 0 OID 0)
-- Dependencies: 263
-- Name: TABLE schema_migrations; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.schema_migrations TO dashboard_user;
GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.schema_migrations TO postgres;
GRANT SELECT ON TABLE auth.schema_migrations TO postgres WITH GRANT OPTION;


--
-- TOC entry 4282 (class 0 OID 0)
-- Dependencies: 264
-- Name: TABLE sessions; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.sessions TO postgres;
GRANT SELECT ON TABLE auth.sessions TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.sessions TO dashboard_user;


--
-- TOC entry 4284 (class 0 OID 0)
-- Dependencies: 265
-- Name: TABLE sso_domains; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.sso_domains TO postgres;
GRANT SELECT ON TABLE auth.sso_domains TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.sso_domains TO dashboard_user;


--
-- TOC entry 4287 (class 0 OID 0)
-- Dependencies: 266
-- Name: TABLE sso_providers; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.sso_providers TO postgres;
GRANT SELECT ON TABLE auth.sso_providers TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.sso_providers TO dashboard_user;


--
-- TOC entry 4290 (class 0 OID 0)
-- Dependencies: 267
-- Name: TABLE users; Type: ACL; Schema: auth; Owner: supabase_auth_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.users TO dashboard_user;
GRANT INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE auth.users TO postgres;
GRANT SELECT ON TABLE auth.users TO postgres WITH GRANT OPTION;


--
-- TOC entry 4291 (class 0 OID 0)
-- Dependencies: 245
-- Name: TABLE pg_stat_statements; Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE extensions.pg_stat_statements TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE extensions.pg_stat_statements TO dashboard_user;


--
-- TOC entry 4292 (class 0 OID 0)
-- Dependencies: 244
-- Name: TABLE pg_stat_statements_info; Type: ACL; Schema: extensions; Owner: supabase_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE extensions.pg_stat_statements_info TO postgres WITH GRANT OPTION;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE extensions.pg_stat_statements_info TO dashboard_user;


--
-- TOC entry 4293 (class 0 OID 0)
-- Dependencies: 243
-- Name: TABLE decrypted_key; Type: ACL; Schema: pgsodium; Owner: supabase_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE pgsodium.decrypted_key TO pgsodium_keyholder;


--
-- TOC entry 4294 (class 0 OID 0)
-- Dependencies: 241
-- Name: TABLE masking_rule; Type: ACL; Schema: pgsodium; Owner: supabase_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE pgsodium.masking_rule TO pgsodium_keyholder;


--
-- TOC entry 4295 (class 0 OID 0)
-- Dependencies: 242
-- Name: TABLE mask_columns; Type: ACL; Schema: pgsodium; Owner: supabase_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE pgsodium.mask_columns TO pgsodium_keyholder;


--
-- TOC entry 4296 (class 0 OID 0)
-- Dependencies: 268
-- Name: TABLE images; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.images TO anon;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.images TO authenticated;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.images TO service_role;


--
-- TOC entry 4297 (class 0 OID 0)
-- Dependencies: 269
-- Name: TABLE messages; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.messages TO anon;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.messages TO authenticated;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.messages TO service_role;


--
-- TOC entry 4298 (class 0 OID 0)
-- Dependencies: 270
-- Name: TABLE user_profile; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.user_profile TO anon;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.user_profile TO authenticated;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.user_profile TO service_role;


--
-- TOC entry 4299 (class 0 OID 0)
-- Dependencies: 271
-- Name: TABLE users; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.users TO anon;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.users TO authenticated;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.users TO service_role;


--
-- TOC entry 4300 (class 0 OID 0)
-- Dependencies: 272
-- Name: TABLE messages; Type: ACL; Schema: realtime; Owner: supabase_realtime_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE realtime.messages TO postgres;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE realtime.messages TO dashboard_user;
GRANT SELECT,INSERT,UPDATE ON TABLE realtime.messages TO anon;
GRANT SELECT,INSERT,UPDATE ON TABLE realtime.messages TO authenticated;
GRANT SELECT,INSERT,UPDATE ON TABLE realtime.messages TO service_role;


--
-- TOC entry 4301 (class 0 OID 0)
-- Dependencies: 273
-- Name: TABLE schema_migrations; Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE realtime.schema_migrations TO postgres;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE realtime.schema_migrations TO dashboard_user;
GRANT SELECT ON TABLE realtime.schema_migrations TO anon;
GRANT SELECT ON TABLE realtime.schema_migrations TO authenticated;
GRANT SELECT ON TABLE realtime.schema_migrations TO service_role;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE realtime.schema_migrations TO supabase_realtime_admin;


--
-- TOC entry 4302 (class 0 OID 0)
-- Dependencies: 274
-- Name: TABLE subscription; Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE realtime.subscription TO postgres;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE realtime.subscription TO dashboard_user;
GRANT SELECT ON TABLE realtime.subscription TO anon;
GRANT SELECT ON TABLE realtime.subscription TO authenticated;
GRANT SELECT ON TABLE realtime.subscription TO service_role;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE realtime.subscription TO supabase_realtime_admin;


--
-- TOC entry 4303 (class 0 OID 0)
-- Dependencies: 275
-- Name: SEQUENCE subscription_id_seq; Type: ACL; Schema: realtime; Owner: supabase_admin
--

GRANT ALL ON SEQUENCE realtime.subscription_id_seq TO postgres;
GRANT ALL ON SEQUENCE realtime.subscription_id_seq TO dashboard_user;
GRANT USAGE ON SEQUENCE realtime.subscription_id_seq TO anon;
GRANT USAGE ON SEQUENCE realtime.subscription_id_seq TO authenticated;
GRANT USAGE ON SEQUENCE realtime.subscription_id_seq TO service_role;
GRANT ALL ON SEQUENCE realtime.subscription_id_seq TO supabase_realtime_admin;


--
-- TOC entry 4305 (class 0 OID 0)
-- Dependencies: 276
-- Name: TABLE buckets; Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.buckets TO anon;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.buckets TO authenticated;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.buckets TO service_role;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.buckets TO postgres;


--
-- TOC entry 4306 (class 0 OID 0)
-- Dependencies: 277
-- Name: TABLE migrations; Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.migrations TO anon;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.migrations TO authenticated;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.migrations TO service_role;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.migrations TO postgres;


--
-- TOC entry 4308 (class 0 OID 0)
-- Dependencies: 278
-- Name: TABLE objects; Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.objects TO anon;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.objects TO authenticated;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.objects TO service_role;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.objects TO postgres;


--
-- TOC entry 4309 (class 0 OID 0)
-- Dependencies: 279
-- Name: TABLE s3_multipart_uploads; Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.s3_multipart_uploads TO service_role;
GRANT SELECT ON TABLE storage.s3_multipart_uploads TO authenticated;
GRANT SELECT ON TABLE storage.s3_multipart_uploads TO anon;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.s3_multipart_uploads TO postgres;


--
-- TOC entry 4310 (class 0 OID 0)
-- Dependencies: 280
-- Name: TABLE s3_multipart_uploads_parts; Type: ACL; Schema: storage; Owner: supabase_storage_admin
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.s3_multipart_uploads_parts TO service_role;
GRANT SELECT ON TABLE storage.s3_multipart_uploads_parts TO authenticated;
GRANT SELECT ON TABLE storage.s3_multipart_uploads_parts TO anon;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE storage.s3_multipart_uploads_parts TO postgres;


--
-- TOC entry 2504 (class 826 OID 29993)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: auth; Owner: supabase_auth_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_auth_admin IN SCHEMA auth GRANT ALL ON SEQUENCES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_auth_admin IN SCHEMA auth GRANT ALL ON SEQUENCES TO dashboard_user;


--
-- TOC entry 2505 (class 826 OID 29994)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: auth; Owner: supabase_auth_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_auth_admin IN SCHEMA auth GRANT ALL ON FUNCTIONS TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_auth_admin IN SCHEMA auth GRANT ALL ON FUNCTIONS TO dashboard_user;


--
-- TOC entry 2503 (class 826 OID 29995)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: auth; Owner: supabase_auth_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_auth_admin IN SCHEMA auth GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_auth_admin IN SCHEMA auth GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO dashboard_user;


--
-- TOC entry 2525 (class 826 OID 29996)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: extensions; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA extensions GRANT ALL ON SEQUENCES TO postgres WITH GRANT OPTION;


--
-- TOC entry 2524 (class 826 OID 29997)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: extensions; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA extensions GRANT ALL ON FUNCTIONS TO postgres WITH GRANT OPTION;


--
-- TOC entry 2522 (class 826 OID 29998)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: extensions; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA extensions GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO postgres WITH GRANT OPTION;


--
-- TOC entry 2529 (class 826 OID 29999)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: graphql; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT ALL ON SEQUENCES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT ALL ON SEQUENCES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT ALL ON SEQUENCES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT ALL ON SEQUENCES TO service_role;


--
-- TOC entry 2528 (class 826 OID 30000)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: graphql; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT ALL ON FUNCTIONS TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT ALL ON FUNCTIONS TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT ALL ON FUNCTIONS TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT ALL ON FUNCTIONS TO service_role;


--
-- TOC entry 2527 (class 826 OID 30001)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: graphql; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO service_role;


--
-- TOC entry 2515 (class 826 OID 30002)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: graphql_public; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT ALL ON SEQUENCES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT ALL ON SEQUENCES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT ALL ON SEQUENCES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT ALL ON SEQUENCES TO service_role;


--
-- TOC entry 2518 (class 826 OID 30003)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: graphql_public; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT ALL ON FUNCTIONS TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT ALL ON FUNCTIONS TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT ALL ON FUNCTIONS TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT ALL ON FUNCTIONS TO service_role;


--
-- TOC entry 2516 (class 826 OID 30004)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: graphql_public; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA graphql_public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO service_role;


--
-- TOC entry 2509 (class 826 OID 29227)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: pgsodium; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA pgsodium GRANT ALL ON SEQUENCES TO pgsodium_keyholder;


--
-- TOC entry 2510 (class 826 OID 29226)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: pgsodium; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA pgsodium GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO pgsodium_keyholder;


--
-- TOC entry 2511 (class 826 OID 29224)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: pgsodium_masks; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA pgsodium_masks GRANT ALL ON SEQUENCES TO pgsodium_keyiduser;


--
-- TOC entry 2512 (class 826 OID 29225)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: pgsodium_masks; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA pgsodium_masks GRANT ALL ON FUNCTIONS TO pgsodium_keyiduser;


--
-- TOC entry 2513 (class 826 OID 29223)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: pgsodium_masks; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA pgsodium_masks GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO pgsodium_keyiduser;


--
-- TOC entry 2514 (class 826 OID 30005)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON SEQUENCES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON SEQUENCES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON SEQUENCES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON SEQUENCES TO service_role;


--
-- TOC entry 2517 (class 826 OID 30006)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT ALL ON SEQUENCES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT ALL ON SEQUENCES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT ALL ON SEQUENCES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT ALL ON SEQUENCES TO service_role;


--
-- TOC entry 2519 (class 826 OID 30007)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON FUNCTIONS TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON FUNCTIONS TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON FUNCTIONS TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON FUNCTIONS TO service_role;


--
-- TOC entry 2520 (class 826 OID 30008)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: public; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT ALL ON FUNCTIONS TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT ALL ON FUNCTIONS TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT ALL ON FUNCTIONS TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT ALL ON FUNCTIONS TO service_role;


--
-- TOC entry 2521 (class 826 OID 30009)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO service_role;


--
-- TOC entry 2523 (class 826 OID 30010)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO service_role;


--
-- TOC entry 2507 (class 826 OID 30011)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: realtime; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA realtime GRANT ALL ON SEQUENCES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA realtime GRANT ALL ON SEQUENCES TO dashboard_user;


--
-- TOC entry 2508 (class 826 OID 30012)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: realtime; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA realtime GRANT ALL ON FUNCTIONS TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA realtime GRANT ALL ON FUNCTIONS TO dashboard_user;


--
-- TOC entry 2506 (class 826 OID 30013)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: realtime; Owner: supabase_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA realtime GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE supabase_admin IN SCHEMA realtime GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO dashboard_user;


--
-- TOC entry 2526 (class 826 OID 30014)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: storage; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT ALL ON SEQUENCES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT ALL ON SEQUENCES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT ALL ON SEQUENCES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT ALL ON SEQUENCES TO service_role;


--
-- TOC entry 2501 (class 826 OID 30015)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: storage; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT ALL ON FUNCTIONS TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT ALL ON FUNCTIONS TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT ALL ON FUNCTIONS TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT ALL ON FUNCTIONS TO service_role;


--
-- TOC entry 2502 (class 826 OID 30016)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: storage; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO anon;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO authenticated;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA storage GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO service_role;


--
-- TOC entry 3696 (class 3466 OID 30031)
-- Name: issue_graphql_placeholder; Type: EVENT TRIGGER; Schema: -; Owner: supabase_admin
--

CREATE EVENT TRIGGER issue_graphql_placeholder ON sql_drop
         WHEN TAG IN ('DROP EXTENSION')
   EXECUTE FUNCTION extensions.set_graphql_placeholder();


ALTER EVENT TRIGGER issue_graphql_placeholder OWNER TO supabase_admin;

--
-- TOC entry 3701 (class 3466 OID 30069)
-- Name: issue_pg_cron_access; Type: EVENT TRIGGER; Schema: -; Owner: supabase_admin
--

CREATE EVENT TRIGGER issue_pg_cron_access ON ddl_command_end
         WHEN TAG IN ('CREATE EXTENSION')
   EXECUTE FUNCTION extensions.grant_pg_cron_access();


ALTER EVENT TRIGGER issue_pg_cron_access OWNER TO supabase_admin;

--
-- TOC entry 3695 (class 3466 OID 30030)
-- Name: issue_pg_graphql_access; Type: EVENT TRIGGER; Schema: -; Owner: supabase_admin
--

CREATE EVENT TRIGGER issue_pg_graphql_access ON ddl_command_end
         WHEN TAG IN ('CREATE FUNCTION')
   EXECUTE FUNCTION extensions.grant_pg_graphql_access();


ALTER EVENT TRIGGER issue_pg_graphql_access OWNER TO supabase_admin;

--
-- TOC entry 3694 (class 3466 OID 30020)
-- Name: issue_pg_net_access; Type: EVENT TRIGGER; Schema: -; Owner: postgres
--

CREATE EVENT TRIGGER issue_pg_net_access ON ddl_command_end
         WHEN TAG IN ('CREATE EXTENSION')
   EXECUTE FUNCTION extensions.grant_pg_net_access();


ALTER EVENT TRIGGER issue_pg_net_access OWNER TO postgres;

--
-- TOC entry 3697 (class 3466 OID 30032)
-- Name: pgrst_ddl_watch; Type: EVENT TRIGGER; Schema: -; Owner: supabase_admin
--

CREATE EVENT TRIGGER pgrst_ddl_watch ON ddl_command_end
   EXECUTE FUNCTION extensions.pgrst_ddl_watch();


ALTER EVENT TRIGGER pgrst_ddl_watch OWNER TO supabase_admin;

--
-- TOC entry 3698 (class 3466 OID 30033)
-- Name: pgrst_drop_watch; Type: EVENT TRIGGER; Schema: -; Owner: supabase_admin
--

CREATE EVENT TRIGGER pgrst_drop_watch ON sql_drop
   EXECUTE FUNCTION extensions.pgrst_drop_watch();


ALTER EVENT TRIGGER pgrst_drop_watch OWNER TO supabase_admin;

-- Completed on 2025-03-20 11:52:46

--
-- PostgreSQL database dump complete
--

-- Completed on 2025-03-20 11:52:46

--
-- PostgreSQL database cluster dump complete
--

