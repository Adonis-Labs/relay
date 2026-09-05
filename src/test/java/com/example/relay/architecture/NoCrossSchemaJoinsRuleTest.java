package com.example.relay.architecture;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.EvaluationResult;

import static org.assertj.core.api.Assertions.assertThat;

class NoCrossSchemaJoinsRuleTest {

	private static final String CLEAN_PACKAGE =
			"com.example.archunitfixtures.nocrossschemajoins.clean";
	private static final String VIOLATING_PACKAGE =
			"com.example.archunitfixtures.nocrossschemajoins.violating";

	@Test
	void passesWhenRelationshipsStayWithinOneSchema() {
		JavaClasses classes = new ClassFileImporter().importPackages(CLEAN_PACKAGE);

		EvaluationResult result = ArchitectureRules.noCrossSchemaJoins().evaluate(classes);

		assertThat(result.hasViolation()).isFalse();
	}

	@Test
	void failsWhenARelationshipCrossesSchemas() {
		JavaClasses classes = new ClassFileImporter().importPackages(VIOLATING_PACKAGE);

		EvaluationResult result = ArchitectureRules.noCrossSchemaJoins().evaluate(classes);

		assertThat(result.hasViolation()).isTrue();
		assertThat(result.getFailureReport().getDetails())
				.anyMatch(detail -> detail.contains("Order") && detail.contains("Product")
						&& detail.contains("ordering") && detail.contains("catalog"));
	}
}
