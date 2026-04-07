package com.shipflow.shipmentservice.application.client;

import java.time.Duration;

public interface CacheClient {
	long increment(String key);

	boolean hasKey(String key);

	void set(String key, String value, Duration ttl);
}