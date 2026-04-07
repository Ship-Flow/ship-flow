package com.shipflow.shipmentservice.infrastructure.messaging.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shipflow.shipmentservice.application.ShipmentManagerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentManagerCleanupScheduler {

	private final ShipmentManagerService shipmentManagerService;

	@Scheduled(cron = "0 0 0 * * *")
	public void deletePendingManagers() {
		log.info("[Scheduler] 삭제 예정 배송 담당자 일괄 삭제 시작");
		shipmentManagerService.deleteAllPending();
		log.info("[Scheduler] 삭제 예정 배송 담당자 일괄 삭제 완료");
	}
}
