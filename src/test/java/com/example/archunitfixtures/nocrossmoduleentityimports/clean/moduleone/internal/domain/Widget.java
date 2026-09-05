package com.example.archunitfixtures.nocrossmoduleentityimports.clean.moduleone.internal.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "widget", schema = "moduleone")
public class Widget {

	private UUID id;
	private String name;
}