-- Idempotent Saga 벤치마크용 CREATING 상태 주문 생성
-- benchmark/results/03-order-id.txt 에 생성된 UUID를 기록해 두세요.
--
-- 실행:
--   psql -h localhost -U shipflow -d shipflow \
--     -v ON_ERROR_STOP=1 \
--     -f benchmark/seed/03-saga-seed.sql
--
-- 생성된 order_id 확인:
--   psql -h localhost -U shipflow -d shipflow -c \
--     "SELECT id FROM orders.p_orders WHERE request_note = 'benchmark-saga-seed' LIMIT 1;"

SET search_path TO orders;

DO $$
DECLARE
    v_order_id          UUID := gen_random_uuid();
    v_orderer_id        UUID := gen_random_uuid();
    v_product_id        UUID := gen_random_uuid();
    v_supplier_company  UUID := gen_random_uuid();
    v_receiver_company  UUID := gen_random_uuid();
    v_departure_hub     UUID := gen_random_uuid();
    v_arrival_hub       UUID := gen_random_uuid();
BEGIN
    -- p_orders 에 CREATING 상태 주문 삽입
    INSERT INTO p_orders (
        id,
        orderer_id,
        product_id,
        supplier_company_id,
        receiver_company_id,
        departure_hub_id,
        arrival_hub_id,
        quantity,
        order_status,
        request_note,
        delivery_address,
        created_at,
        updated_at,
        created_by,
        updated_by
    ) VALUES (
        v_order_id,
        v_orderer_id,
        v_product_id,
        v_supplier_company,
        v_receiver_company,
        v_departure_hub,
        v_arrival_hub,
        1,
        'CREATING',
        'benchmark-saga-seed',
        'benchmark-delivery-address',
        NOW(),
        NOW(),
        v_orderer_id,
        v_orderer_id
    );

    RAISE NOTICE '✅ benchmark order_id = %', v_order_id;
    RAISE NOTICE '   → k6 실행 시 -e ORDER_ID=% 로 전달하세요', v_order_id;
END $$;
