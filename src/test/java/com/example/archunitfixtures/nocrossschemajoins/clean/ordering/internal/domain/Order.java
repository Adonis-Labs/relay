package com.example.archunitfixtures.nocrossschemajoins.clean.ordering.internal.domain;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders", schema = "ordering")
public class Order {

	private UUID id;

	@OneToMany
	private List<OrderLine> lines;
}
