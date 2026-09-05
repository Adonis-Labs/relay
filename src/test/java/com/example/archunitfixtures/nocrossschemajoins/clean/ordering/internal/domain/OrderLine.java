package com.example.archunitfixtures.nocrossschemajoins.clean.ordering.internal.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_line", schema = "ordering")
public class OrderLine {

	private UUID id;

	// catalog's Product referenced by id, not joined across schemas.
	private UUID productId;
}
