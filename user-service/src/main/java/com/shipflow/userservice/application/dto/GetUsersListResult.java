package com.shipflow.userservice.application.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUsersListResult {
	private List<GetUsersResult> content;
	private int page;
	private int size;
	private long totalCount;
}
