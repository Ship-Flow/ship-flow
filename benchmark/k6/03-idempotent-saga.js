/**
 * Idempotent Saga — 동일 eventId 중복 메시지 처리 벤치마크
 *
 * ─────────────────────────────────────────────────
 * 검증 핵심
 *   - 동일 eventId 로 N개 메시지 동시 발행
 *   - 실제 도메인 로직 실행(processed_saga_events 저장) = 1건
 *   - 중복 차단율 = (N-1) / N × 100%
 *
 * ─────────────────────────────────────────────────
 * Before 상태 재현 (benchmark/before-saga 브랜치):
 *   IdempotentSagaHandler.process() 에서 Redis·DB 체크 제거:
 *
 *     @Override
 *     protected final void process(T event) {
 *         sagaExecutor.executeWithIdempotency(event, "unused", this::doProcess);
 *     }
 *
 *   → order-service 재빌드/재시작 후 아래 Before 명령 실행
 *   → 중복 메시지가 모두 doProcess() 진입 → InvalidOrderStateException → 재시도 → DLQ
 *
 * After 상태 (order-test 브랜치 — 현재 Redis+DB 멱등성):
 *   변경 없음. order-service 재시작 후 After 명령 실행
 *   → 첫 메시지만 처리, 나머지는 Redis 1ms 체크로 즉시 차단
 * ─────────────────────────────────────────────────
 *
 * 사전 준비:
 *   1) Docker Compose로 order-service, RabbitMQ, PostgreSQL, Redis 실행
 *   2) CREATING 상태 주문 생성:
 *        psql -h localhost -U shipflow -d shipflow -f benchmark/seed/03-saga-seed.sql
 *   3) 생성된 order_id 확인 후 ORDER_ID 환경변수에 세팅
 *   4) EVENT_ID 는 고정 UUID 사용 (중복 판별용)
 *   5) RabbitMQ Management Plugin 활성화 확인 (포트 15672)
 *
 * Before 실행:
 *   k6 run \
 *     -e ORDER_ID=<benchmark-order-uuid> \
 *     -e EVENT_ID=<fixed-uuid> \
 *     --out json=benchmark/results/03-before.json \
 *     benchmark/k6/03-idempotent-saga.js
 *
 * After 실행:
 *   -- processed_saga_events 초기화 (After 시작 전)
 *   psql -h localhost -U shipflow -d shipflow \
 *     -c "DELETE FROM orders.processed_saga_events WHERE event_id = '<EVENT_ID>';"
 *   -- Redis 초기화
 *   redis-cli DEL "idempotent:saga:<EVENT_ID>"
 *   -- 새 CREATING 주문 생성 후:
 *   k6 run \
 *     -e ORDER_ID=<new-benchmark-order-uuid> \
 *     -e EVENT_ID=<same-fixed-uuid> \
 *     --out json=benchmark/results/03-after.json \
 *     benchmark/k6/03-idempotent-saga.js
 *
 * 결과 검증 쿼리 (k6 실행 직후):
 *   -- 실제 처리 건수 (1이어야 함)
 *   SELECT COUNT(*) FROM orders.processed_saga_events WHERE event_id = '<EVENT_ID>';
 *
 *   -- DLQ 메시지 수 (After 에서는 0이어야 함)
 *   curl -s -u guest:guest \
 *     http://localhost:15672/api/queues/%2F/order.product.stock.decreased.dlq \
 *     | python3 -c "import sys,json; q=json.load(sys.stdin); print('DLQ:', q['messages'])"
 */
import http from 'k6/http';
import { check } from 'k6';

const RABBIT_BASE  = __ENV.RABBIT_BASE  || 'http://localhost:15672';
const RABBIT_USER  = __ENV.RABBIT_USER  || 'guest';
const RABBIT_PASS  = __ENV.RABBIT_PASS  || 'guest';

const ORDER_ID  = __ENV.ORDER_ID  || 'replace-with-benchmark-order-uuid';
const EVENT_ID  = __ENV.EVENT_ID  || 'replace-with-fixed-event-uuid';

// 50 VU 가 동시에 동일 eventId 를 발행 → 중복 처리 시나리오
export let options = {
  vus: 50,
  iterations: 100,   // 총 100회 발행 (VU 당 2회)
  thresholds: {
    http_req_duration: ['p(95)<3000'],
    http_req_failed:   ['rate<0.01'],
  },
};

// RabbitMQ Management API Basic Auth
const CREDS = `${RABBIT_USER}:${RABBIT_PASS}`;
const AUTH  = `Basic ${__ENV.ENCODED_AUTH || btoa(CREDS)}`;

const PUBLISH_URL = `${RABBIT_BASE}/api/exchanges/%2F/saga.events/publish`;

export default function () {
  // ProductStockDecreasedEvent JSON (동일 eventId 고정)
  const eventPayload = JSON.stringify({
    eventId:     EVENT_ID,
    eventType:   'product.stock.decreased',
    occurredAt:  '2026-04-10T12:00:00',
    orderId:     ORDER_ID,
    productName: 'benchmark-product',
  });

  const body = JSON.stringify({
    properties: {
      content_type: 'application/json',
      headers: {
        // Spring Jackson2JsonMessageConverter 역직렬화용 타입 힌트
        '__TypeId__': 'com.shipflow.orderservice.infrastructure.messaging.event.consume.ProductStockDecreasedEvent',
      },
    },
    routing_key:      'product.stock.decreased',
    payload:          eventPayload,
    payload_encoding: 'string',
  });

  const res = http.post(PUBLISH_URL, body, {
    headers: {
      'Authorization': AUTH,
      'Content-Type':  'application/json',
    },
  });

  check(res, {
    'publish 200': (r) => r.status === 200,
    'routed':      (r) => {
      try { return JSON.parse(r.body).routed === true; } catch { return false; }
    },
  });
}
