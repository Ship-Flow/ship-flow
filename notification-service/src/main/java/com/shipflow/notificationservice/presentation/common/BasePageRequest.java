package com.shipflow.notificationservice.presentation.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record BasePageRequest(
	int page,
	int size
) {
	public BasePageRequest {
		page = Math.max(page, 0);

		if (size != 10 && size != 30 && size != 50) {
			size = 10;
		}
	}

	public Pageable toPageable(Sort sort) {
		return PageRequest.of(page, size, sort);
	}
}