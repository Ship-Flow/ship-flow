package com.shipflow.userservice.presentation.dto.response;

import java.util.List;

import com.shipflow.userservice.application.dto.GetSignupRequestResult;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetSignupRequestListResDto {
	private List<GetSignupRequestResult.SignupRequest> content;
	private int page;
	private int size;
	private long totalCount;

	public GetSignupRequestListResDto(GetSignupRequestResult result) {
		this.content = result.getContent();
		this.page = result.getPage();
		this.size = result.getSize();
		this.totalCount = result.getTotalCount();
	}
}