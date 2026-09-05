package com.example.archunitfixtures.nocrossmoduleentityimports.clean.moduletwo.internal.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "gadget", schema = "moduletwo")
public class Gadget {

	private UUID id;

	// Reference moduleone's aggregate by id, not by entity/object reference.
	private UUID widgetId;
}