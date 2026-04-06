package com.shipflow.notificationservice.infrastructure.client.ai.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class GeminiRequest {

	private List<Content> contents;

	public GeminiRequest(String prompt) {
		this.contents = List.of(new Content(prompt));
	}

	@Getter
	public static class Content {
		private List<Part> parts;

		public Content(String text) {
			this.parts = List.of(new Part(text));
		}
	}

	@Getter
	public static class Part {
		private String text;

		public Part(String text) {
			this.text = text;
		}
	}
}
