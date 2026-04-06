package com.shipflow.hubservice.application;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.shipflow.hubservice.application.event.HubDeletedEvent;
import com.shipflow.hubservice.application.event.HubManagerChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubCascadeEventListener {

	private final HubCascadeService hubCascadeService;

	/**
	 * 허브 삭제 후 연관 서비스 cascade 처리 (Saga 패턴)
	 *
	 * 실행 순서:
	 * 1. 배송담당자 삭제 (deliveryService)
	 * 2. 업체 삭제 (companyService)
	 * 3. 허브 담당자 해제 (userService)
	 *
	 * 중간 단계 실패 시 이미 완료된 단계의 보상 트랜잭션을 역순으로 실행합니다.
	 * 삭제 API는 복원 API가 없으므로 보상이 제한적이며, 실패 내역을 로그로 남깁니다.
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleHubDeleted(HubDeletedEvent event) {
		UUID hubId = event.hubId();
		UUID managerId = event.managerId();
		UUID requestUserId = event.requestUserId();

		log.info("허브 삭제 cascade 시작: hubId={}", hubId);
		Deque<Runnable> compensations = new ArrayDeque<>();

		try {
			// Step 1: 배송담당자 삭제
			hubCascadeService.deleteDeliveryManagers(hubId, requestUserId);
			// 삭제는 복원 API 없음 — 보상 불가, 실패 시 로그만 기록
			compensations.push(() ->
				log.warn("보상 불가: 배송담당자 삭제는 복원 API가 없습니다. hubId={}", hubId));

			// Step 2: 업체 삭제
			hubCascadeService.deleteCompanies(hubId, requestUserId);
			compensations.push(() ->
				log.warn("보상 불가: 업체 삭제는 복원 API가 없습니다. hubId={}", hubId));

			// Step 3: 허브 담당자 해제
			hubCascadeService.unassignManager(managerId, requestUserId);

			log.info("허브 삭제 cascade 완료: hubId={}", hubId);

		} catch (Exception e) {
			log.error("허브 삭제 cascade 실패 — 보상 트랜잭션 실행: hubId={}", hubId, e);
			compensate(compensations);
		}
	}

	/**
	 * 허브 담당자 변경 후 연관 서비스 cascade 처리 (Saga 패턴)
	 *
	 * 실행 순서:
	 * 1. 기존 담당자 허브 해제 (userService)
	 * 2. 신규 담당자 허브 지정 (userService)
	 *
	 * Step 2 실패 시 Step 1을 보상(기존 담당자 재지정)합니다.
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleHubManagerChanged(HubManagerChangedEvent event) {
		UUID oldManagerId = event.oldManagerId();
		UUID newManagerId = event.newManagerId();
		UUID hubId = event.hubId();
		UUID requestUserId = event.requestUserId();

		log.info("허브 담당자 변경 cascade 시작: hubId={}, {} → {}", hubId, oldManagerId, newManagerId);
		Deque<Runnable> compensations = new ArrayDeque<>();

		try {
			// Step 1: 기존 담당자 해제
			hubCascadeService.unassignManager(oldManagerId, requestUserId);
			compensations.push(() -> {
				log.info("보상: 기존 담당자 재지정 — managerId={}, hubId={}", oldManagerId, hubId);
				hubCascadeService.assignManager(oldManagerId, hubId, requestUserId);
			});

			// Step 2: 신규 담당자 지정
			hubCascadeService.assignManager(newManagerId, hubId, requestUserId);

			log.info("허브 담당자 변경 cascade 완료: hubId={}", hubId);

		} catch (Exception e) {
			log.error("허브 담당자 변경 cascade 실패 — 보상 트랜잭션 실행: hubId={}", hubId, e);
			compensate(compensations);
		}
	}

	private void compensate(Deque<Runnable> compensations) {
		while (!compensations.isEmpty()) {
			try {
				compensations.pop().run();
			} catch (Exception e) {
				log.error("보상 트랜잭션 실행 실패", e);
			}
		}
	}
}
