-- Feign 병렬 호출 벤치마크용 시드 데이터
-- 실행:
--   cat benchmark/seed/02-feign-seed.sql | docker exec -i shipflow-postgres psql -U shipflow -d shipflow
--
-- 삽입 후 k6 에 전달할 UUID:
--   USER_ID    = b0000000-0000-0000-0000-000000000001
--   PRODUCT_ID = b0000000-0000-0000-0000-000000000003

DO $$
DECLARE
    v_user_id     UUID := 'b0000000-0000-0000-0000-000000000001';
    v_company_id  UUID := 'b0000000-0000-0000-0000-000000000002';
    v_product_id  UUID := 'b0000000-0000-0000-0000-000000000003';
    v_hub_id      UUID := 'b0000000-0000-0000-0000-000000000004';
    v_supplier_co UUID := 'b0000000-0000-0000-0000-000000000005';
    v_now         TIMESTAMP := NOW();
BEGIN

    -- 기존 벤치마크 데이터 정리 (재실행 안전)
    DELETE FROM product.p_product  WHERE id = v_product_id;
    DELETE FROM company.p_company  WHERE id = v_company_id;
    DELETE FROM users.p_user       WHERE id = v_user_id
                                      OR username = 'benchmark_user_001'
                                      OR slack_id  = 'benchmark_slack_001';

    -- ── 1. users.p_user ──────────────────────────────────────────────
    INSERT INTO users.p_user (
        id, username, name, slack_id,
        role, status,
        company_id, hub_id,
        created_at, created_by,
        updated_at, updated_by
    ) VALUES (
        v_user_id,
        'benchmark_user_001',
        'Benchmark User',
        'benchmark_slack_001',
        'COMPANY_MANAGER',
        'APPROVED',
        v_company_id,
        NULL,
        v_now, v_user_id,
        v_now, v_user_id
    );

    -- ── 2. company.p_company ─────────────────────────────────────────
    INSERT INTO company.p_company (
        id, name, type,
        hub_id, address,
        manager_id, manager_name,
        created_at, created_by,
        updated_at, updated_by
    ) VALUES (
        v_company_id,
        'Benchmark Company',
        'Receiver',
        v_hub_id,
        '서울시 강남구 테헤란로 123',
        v_user_id,
        'Benchmark User',
        v_now, v_user_id,
        v_now, v_user_id
    );

    -- ── 3. product.p_product ─────────────────────────────────────────
    INSERT INTO product.p_product (
        id, name, price, stock, status,
        company_id, company_name, hub_id,
        is_hide,
        created_at, created_by,
        updated_at, updated_by
    ) VALUES (
        v_product_id,
        'Benchmark Product',
        10000,
        99999,
        'ACTIVE',
        v_supplier_co,
        'Benchmark Supplier Co',
        v_hub_id,
        false,
        v_now, v_user_id,
        v_now, v_user_id
    );

    RAISE NOTICE '✅ 시드 데이터 삽입 완료';
    RAISE NOTICE 'USER_ID    = %', v_user_id;
    RAISE NOTICE 'PRODUCT_ID = %', v_product_id;

END $$;
