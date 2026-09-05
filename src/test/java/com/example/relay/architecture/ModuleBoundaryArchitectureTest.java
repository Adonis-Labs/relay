package com.example.relay.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.example.relay", importOptions = ImportOption.DoNotIncludeTests.class)
class ModuleBoundaryArchitectureTest {

	@ArchTest
	static final ArchRule NO_CROSS_MODULE_ENTITY_IMPORTS =
			ArchitectureRules.noCrossModuleEntityImports("com.example.relay");

	@ArchTest
	static final ArchRule NO_CROSS_SCHEMA_JOINS =
			ArchitectureRules.noCrossSchemaJoins();
}
