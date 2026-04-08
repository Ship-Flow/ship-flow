/**
 * CQRS + 인덱스 Before/After 조회 성능 벤치마크
 *
 * Before 실행 (benchmark/before-index 브랜치):
 *   k6 run -e ORDERER_ID=<uuid> -e USER_ID=<uuid> \
 *     --out json=benchmark/results/01-before.json \
 *     benchmark/k6/01-cqrs-index.js
 *
 * After 실행 (order-test 브랜치, 인덱스 추가 후):
 *   k6 run -e ORDERER_ID=<uuid> -e USER_ID=<uuid> \
 *     --out json=benchmark/results/01-after.json \
 *     benchmark/k6/01-cqrs-index.js
 *
 * ORDERER_ID 추출:
 *   psql -h localhost -U shipflow -d shipflow -c \
 *     "SELECT orderer_id FROM orders.p_order_read_models GROUP BY orderer_id ORDER BY COUNT(*) DESC LIMIT 1;"
 */
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL  = __ENV.BASE_URL   || 'http://localhost:8082';
const ORDERER_ID = __ENV.ORDERER_ID || 'replace-with-real-uuid';
const USER_ID    = __ENV.USER_ID    || ORDERER_ID;

export let options = {
  vus: 50,
  duration: '30s',
  thresholds: {
    // Before 단계에서는 느릴 수 있으므로 넉넉하게 설정
    http_req_duration: ['p(95)<5000'],
    http_req_failed:   ['rate<0.01'],  // 1% 미만 오류 허용
  },
};

const HEADERS = {
  'X-User-Id':   USER_ID,
  'X-User-Role': 'MASTER',  // MASTER 권한 → 모든 주문 조회 가능
};

export default function () {
  const url = `${BASE_URL}/api/orders?ordererId=${ORDERER_ID}&size=10&page=0&sort=createdAt,DESC`;
  const res = http.get(url, { headers: HEADERS });

  check(res, {
    'status is 200': (r) => r.status === 200,
    'has content':   (r) => r.body && r.body.length > 0,
  });

  sleep(0.1);
}
