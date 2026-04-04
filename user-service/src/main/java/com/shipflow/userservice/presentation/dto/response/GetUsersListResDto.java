package com.shipflow.userservice.presentation.dto.response;

import java.util.List;

import com.shipflow.userservice.application.dto.GetUsersListResult;
import com.shipflow.userservice.application.dto.GetUsersResult;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUsersListResDto {
	private List<GetUsersResult> content;
	private int page;
	private int size;
	private long totalCount;

	public GetUsersListResDto(GetUsersListResult result) {
		this.content = result.getContent();
		this.page = result.getPage();
		this.size = result.getSize();
		this.totalCount = result.getTotalCount();
	}
}
