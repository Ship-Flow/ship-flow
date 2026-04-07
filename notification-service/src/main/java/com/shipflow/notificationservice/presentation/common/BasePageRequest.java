package com.shipflow.notificationservice.presentation.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record BasePageRequest(
	Integer page,
	Integer size
) {
	public BasePageRequest {
		page = (page == null || page < 0) ? 0 : page;

		if (size == null || (size != 10 && size != 30 && size != 50)) {
			size = 10;
		}
	}

	public Pageable toPageable() {
		return PageRequest.of(
			page,
			size,
			Sort.by(Sort.Direction.DESC, "createdAt")
		);
	}

	public Pageable toPageable(Sort sort) {
		return PageRequest.of(page, size, sort);
	}
}