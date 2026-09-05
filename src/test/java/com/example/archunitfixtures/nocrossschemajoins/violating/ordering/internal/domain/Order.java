package com.example.archunitfixtures.nocrossschemajoins.violating.ordering.internal.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.example.archunitfixtures.nocrossschemajoins.violating.catalog.internal.domain.Product;

@Entity
@Table(name = "orders", schema = "ordering")
public class Order {

	private UUID id;

	// BAD: cross-schema join ('ordering' -> 'catalog').
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
}
