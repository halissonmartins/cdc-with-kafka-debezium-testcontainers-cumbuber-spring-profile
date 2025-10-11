package br.com.halisson;

public record CustomerUpdateDto(
		Long id,
		String name,
		String email
	) {}
