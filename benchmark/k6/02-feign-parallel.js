/**
 * Feign 병렬 호출 Before/After 주문 생성 성능 벤치마크
 *
 * ─────────────────────────────────────────────────
 * Before 상태 재현 (benchmark/before-feign 브랜치):
 *   OrderFetchService.fetchAndBuild() 를 순차 호출로 변경:
 *
 *     ProductInfo product = productAdapter.fetch(ordererId.toString(), productId, quantity);
 *     UserInfo    user    = userAdapter.fetch(ordererId);
 *     ReceiverCompanyInfo company = companyAdapter.fetch(user.receiverCompanyId());
 *
 *   → order-service 재빌드/재시작 후 아래 Before 명령 실행
 *
 * After 상태 (order-test 브랜치 — 현재 병렬 구현):
 *   변경 없음. order-service 재시작 후 After 명령 실행
 * ─────────────────────────────────────────────────
 *
 * Before 실행:
 *   k6 run \
 *     -e USER_ID=<orderer-uuid> \
 *     -e PRODUCT_ID=<product-uuid> \
 *     --out json=benchmark/results/02-before.json \
 *     benchmark/k6/02-feign-parallel.js
 *
 * After 실행:
 *   k6 run \
 *     -e USER_ID=<orderer-uuid> \
 *     -e PRODUCT_ID=<product-uuid> \
 *     --out json=benchmark/results/02-after.json \
 *     benchmark/k6/02-feign-parallel.js
 *
 * 사전 준비:
 *   - Docker Compose로 전체 MSA 스택 실행 (Product, User, Company, Order 서비스)
 *   - USER_ID: 실제 존재하는 orderer UUID (User Service에 등록된 사용자)
 *   - PRODUCT_ID: 재고 충분한 상품 UUID (Product Service에 등록된 상품, stock >= 2000)
 */
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL   = __ENV.BASE_URL   || 'http://localhost:8082';
const USER_ID    = __ENV.USER_ID    || 'replace-with-real-orderer-uuid';
const PRODUCT_ID = __ENV.PRODUCT_ID || 'replace-with-real-product-uuid';

export let options = {
  vus: 50,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<5000'],
    http_req_failed:   ['rate<0.01'],
  },
};

const HEADERS = {
  'X-User-Id':   USER_ID,
  'X-User-Role': 'MASTER',
  'Content-Type': 'application/json',
};

export default function () {
  // 7일 후 납기 기한
  const deadline = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
    .toISOString()
    .replace('T', 'T')
    .slice(0, 19);

  const body = JSON.stringify({
    productId:       PRODUCT_ID,
    quantity:        1,
    requestDeadline: deadline,
    requestNote:     'benchmark-feign-parallel',
  });

  const res = http.post(`${BASE_URL}/api/orders`, body, { headers: HEADERS });

  check(res, {
    'status is 201': (r) => r.status === 201,
    'has orderId': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body && body.id != null;
      } catch {
        return false;
      }
    },
  });

  sleep(0.1);
}
