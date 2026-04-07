-- =============================================
-- p_user 초기 데이터
-- =============================================
INSERT INTO p_user (id, username, name, slack_id, role, hub_id, company_id, status, created_at, created_by, updated_at, updated_by) VALUES
-- MASTER
('0c6a758d-afe4-47a4-9f09-df82c6e99653', 'master',              'master',              'master-admin',             'MASTER',          NULL,                                   NULL,                                   'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
-- HUB_MANAGER (hub UUID는 DataInitializer에서 고정값으로 설정)
('20000000-0000-0000-0000-000000000001', 'hub-manager-seoul',    '서울 허브 관리자',    'hub-seoul',                'HUB_MANAGER',     '10000000-0000-0000-0000-000000000001', NULL,                                   'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('20000000-0000-0000-0000-000000000002', 'hub-manager-gyeonggi', '경기 허브 관리자',    'hub-gyeonggi',             'HUB_MANAGER',     '10000000-0000-0000-0000-000000000002', NULL,                                   'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('20000000-0000-0000-0000-000000000003', 'hub-manager-jeju',     '제주 허브 관리자',    'hub-jeju',                 'HUB_MANAGER',     '10000000-0000-0000-0000-000000000003', NULL,                                   'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
-- COMPANY_MANAGER (company UUID는 p_company INSERT와 동기화)
('40000000-0000-0000-0000-000000000001', 'company-manager-1',    '서울 공급업체 담당자', 'company-seoul-supplier',   'COMPANY_MANAGER', '10000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('40000000-0000-0000-0000-000000000002', 'company-manager-2',    '서울 수령업체 담당자', 'company-seoul-receiver',   'COMPANY_MANAGER', '10000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000002', 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('40000000-0000-0000-0000-000000000003', 'company-manager-3',    '경기 공급업체 담당자', 'company-gyeonggi-supplier','COMPANY_MANAGER', '10000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000003', 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('40000000-0000-0000-0000-000000000004', 'company-manager-4',    '경기 수령업체 담당자', 'company-gyeonggi-receiver','COMPANY_MANAGER', '10000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000004', 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('40000000-0000-0000-0000-000000000005', 'company-manager-5',    '제주 공급업체 담당자', 'company-jeju-supplier',    'COMPANY_MANAGER', '10000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000005', 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
-- SHIPMENT_MANAGER
('50000000-0000-0000-0000-000000000001', 'shipment-manager-hub-1',     '허브 배송 담당자 1', 'shipment-hub-1',     'SHIPMENT_MANAGER', NULL, NULL, 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('50000000-0000-0000-0000-000000000002', 'shipment-manager-hub-2',     '허브 배송 담당자 2', 'shipment-hub-2',     'SHIPMENT_MANAGER', NULL, NULL, 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('50000000-0000-0000-0000-000000000003', 'shipment-manager-company-1', '서울 배송 담당자',  'shipment-company-1', 'SHIPMENT_MANAGER', '10000000-0000-0000-0000-000000000001', NULL, 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('50000000-0000-0000-0000-000000000004', 'shipment-manager-company-2', '경기 배송 담당자',  'shipment-company-2', 'SHIPMENT_MANAGER', '10000000-0000-0000-0000-000000000002', NULL, 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('50000000-0000-0000-0000-000000000005', 'shipment-manager-company-3', '제주 배송 담당자',  'shipment-company-3', 'SHIPMENT_MANAGER', '10000000-0000-0000-0000-000000000003', NULL, 'APPROVED', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653');

-- =============================================
-- p_shipment_manager 초기 데이터
-- =============================================
INSERT INTO p_shipment_manager (id, user_id, name, hub_id, slack_id, type, shipment_sequence, created_at, created_by, updated_at, updated_by) VALUES
-- HUB 타입: hub_id 없음
('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '허브 배송 담당자 1', NULL,                                   'shipment-hub-1',     'HUB',     0, NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', '허브 배송 담당자 2', NULL,                                   'shipment-hub-2',     'HUB',     0, NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
-- COMPANY 타입: 허브별 담당자
('60000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000003', '서울 배송 담당자',   '10000000-0000-0000-0000-000000000001', 'shipment-company-1', 'COMPANY', 0, NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('60000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000004', '경기 배송 담당자',   '10000000-0000-0000-0000-000000000002', 'shipment-company-2', 'COMPANY', 0, NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('60000000-0000-0000-0000-000000000005', '50000000-0000-0000-0000-000000000005', '제주 배송 담당자',   '10000000-0000-0000-0000-000000000003', 'shipment-company-3', 'COMPANY', 0, NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653');

-- =============================================
-- p_company 초기 데이터
-- =============================================
INSERT INTO p_company (id, name, type, hub_id, address, manager_id, manager_name, created_at, created_by, updated_at, updated_by) VALUES
-- 서울 허브 소속
('30000000-0000-0000-0000-000000000001', '서울 공급업체', 'Supplier', '10000000-0000-0000-0000-000000000001', '서울특별시 강남구 테헤란로 100',    '40000000-0000-0000-0000-000000000001', '서울 공급업체 담당자', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('30000000-0000-0000-0000-000000000002', '서울 수령업체', 'Receiver', '10000000-0000-0000-0000-000000000001', '서울특별시 송파구 올림픽로 300',    '40000000-0000-0000-0000-000000000002', '서울 수령업체 담당자', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
-- 경기 허브 소속
('30000000-0000-0000-0000-000000000003', '경기 공급업체', 'Supplier', '10000000-0000-0000-0000-000000000002', '경기도 성남시 분당구 판교역로 166', '40000000-0000-0000-0000-000000000003', '경기 공급업체 담당자', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
('30000000-0000-0000-0000-000000000004', '경기 수령업체', 'Receiver', '10000000-0000-0000-0000-000000000002', '경기도 수원시 팔달구 인계로 178',  '40000000-0000-0000-0000-000000000004', '경기 수령업체 담당자', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653'),
-- 제주 허브 소속
('30000000-0000-0000-0000-000000000005', '제주 공급업체', 'Supplier', '10000000-0000-0000-0000-000000000003', '제주특별자치도 제주시 노형로 100',  '40000000-0000-0000-0000-000000000005', '제주 공급업체 담당자', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653', NOW(), '0c6a758d-afe4-47a4-9f09-df82c6e99653');
