package com.example.archunitfixtures.nocrossschemajoins.violating.catalog.internal.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "product", schema = "catalog")
public class Product {

	private UUID id;
	private String name;
}
