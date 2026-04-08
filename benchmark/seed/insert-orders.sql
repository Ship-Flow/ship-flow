-- ============================================================
-- 10만 건 주문 + ReadModel 시드 데이터 삽입
-- orderer_id는 1,000명 분산 (UUID 기반)
-- 실행: psql -h localhost -U shipflow -d shipflow -f benchmark/seed/insert-orders.sql
-- ============================================================

DO $$
DECLARE
    orderer_ids UUID[];
    orderer_id  UUID;
    order_id    UUID;
    i           INT;
BEGIN
    -- 1,000명의 orderer_id 생성
    SELECT ARRAY(SELECT gen_random_uuid() FROM generate_series(1, 1000))
    INTO orderer_ids;

    FOR i IN 1..100000 LOOP
        orderer_id := orderer_ids[1 + (i % 1000)];
        order_id   := gen_random_uuid();

        INSERT INTO orders.p_orders (
            id, orderer_id, product_id,
            supplier_company_id, receiver_company_id,
            departure_hub_id, arrival_hub_id,
            value, status,
            created_by, updated_by,
            created_at, updated_at
        ) VALUES (
            order_id,
            orderer_id,
            gen_random_uuid(),
            gen_random_uuid(),
            gen_random_uuid(),
            gen_random_uuid(),
            gen_random_uuid(),
            (random() * 1000)::INT + 1,
            (ARRAY['CREATED','COMPLETED','CANCELED'])[1 + (i % 3)],
            orderer_id, orderer_id,
            NOW() - (random() * INTERVAL '365 days'),
            NOW() - (random() * INTERVAL '30 days')
        );

        INSERT INTO orders.p_order_read_models (
            order_id, orderer_id, orderer_name,
            product_id, product_name,
            order_status,
            supplier_company_id, supplier_company_name,
            receiver_company_id, receiver_company_name,
            departure_hub_id, departure_hub_name,
            arrival_hub_id,   arrival_hub_name,
            quantity,
            created_at, updated_at
        ) VALUES (
            order_id,
            orderer_id,
            'user_' || (i % 1000),
            gen_random_uuid(),
            'product_' || (i % 50),
            (ARRAY['CREATED','COMPLETED','CANCELED'])[1 + (i % 3)],
            gen_random_uuid(), 'supplier_' || (i % 20),
            gen_random_uuid(), 'receiver_' || (i % 20),
            gen_random_uuid(), 'hub_' || (i % 10),
            gen_random_uuid(), 'hub_' || (i % 10),
            (random() * 100)::INT + 1,
            NOW() - (random() * INTERVAL '365 days'),
            NOW() - (random() * INTERVAL '30 days')
        );

        -- 진행 상황 출력 (10,000건 단위)
        IF i % 10000 = 0 THEN
            RAISE NOTICE '% / 100000 rows inserted', i;
        END IF;
    END LOOP;
END $$;
