package com.shipflow.userservice.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientApiResponse<T> {

	private boolean success;
	private T data;
}
