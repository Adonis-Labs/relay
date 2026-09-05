package com.example.relay.architecture;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;

import static org.assertj.core.api.Assertions.assertThat;

class NoCrossModuleEntityImportsRuleTest {

	private static final String CLEAN_BASE_PACKAGE =
			"com.example.archunitfixtures.nocrossmoduleentityimports.clean";
	private static final String VIOLATING_BASE_PACKAGE =
			"com.example.archunitfixtures.nocrossmoduleentityimports.violating";

	@Test
	void passesWhenModulesReferenceEachOtherOnlyById() {
		JavaClasses classes = new ClassFileImporter().importPackages(CLEAN_BASE_PACKAGE);
		ArchRule rule = ArchitectureRules.noCrossModuleEntityImports(CLEAN_BASE_PACKAGE);

		EvaluationResult result = rule.evaluate(classes);

		assertThat(result.hasViolation()).isFalse();
	}

	@Test
	void failsWhenAModuleDependsDirectlyOnAnotherModulesEntity() {
		JavaClasses classes = new ClassFileImporter().importPackages(VIOLATING_BASE_PACKAGE);
		ArchRule rule = ArchitectureRules.noCrossModuleEntityImports(VIOLATING_BASE_PACKAGE);

		EvaluationResult result = rule.evaluate(classes);

		assertThat(result.hasViolation()).isTrue();
		assertThat(result.getFailureReport().getDetails())
				.anyMatch(detail -> detail.contains("GadgetService") && detail.contains("Widget"));
	}
}
