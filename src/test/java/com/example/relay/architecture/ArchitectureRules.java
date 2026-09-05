package com.example.relay.architecture;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Custom architecture rules enforcing module and schema isolation between the
 * modules of {@code com.example.relay} (or, in tests, between the synthetic
 * "modules" under a given test-fixture base package).
 */
public final class ArchitectureRules {

	private ArchitectureRules() {
	}

	/**
	 * No class residing under {@code basePackage} may depend on an
	 * {@code @Entity} class belonging to a different module, where "module" is
	 * the package segment immediately after {@code basePackage}.
	 */
	public static ArchRule noCrossModuleEntityImports(String basePackage) {
		return classes()
				.that().resideInAPackage(basePackage + "..")
				.should(notDependOnEntitiesOfAnotherModule(basePackage))
				.allowEmptyShould(true)
				.because("a module must not depend on another module's @Entity classes directly; "
						+ "reference the other module's aggregate by id instead");
	}

	private static ArchCondition<JavaClass> notDependOnEntitiesOfAnotherModule(String basePackage) {
		return new ArchCondition<JavaClass>("not depend on @Entity classes of another module") {
			@Override
			public void check(JavaClass javaClass, ConditionEvents events) {
				Optional<String> sourceModule = moduleOf(javaClass, basePackage);
				if (sourceModule.isEmpty()) {
					return;
				}
				for (Dependency dependency : javaClass.getDirectDependenciesFromSelf()) {
					JavaClass target = dependency.getTargetClass();
					if (!target.isAnnotatedWith(Entity.class)) {
						continue;
					}
					Optional<String> targetModule = moduleOf(target, basePackage);
					if (targetModule.isPresent() && !targetModule.equals(sourceModule)) {
						events.add(SimpleConditionEvent.violated(javaClass,
								dependency.getDescription()
										+ " -- module '" + sourceModule.get()
										+ "' must not depend on an entity in module '"
										+ targetModule.get() + "'"));
					}
				}
			}
		};
	}

	/**
	 * Every {@code @Entity}'s relationship fields ({@code @OneToMany},
	 * {@code @ManyToOne}, {@code @OneToOne}, {@code @ManyToMany}, and
	 * defensively {@code @JoinColumn}/{@code @JoinTable}) must target an
	 * {@code @Entity} mapped to the same {@code @Table(schema = ...)} value.
	 */
	public static ArchRule noCrossSchemaJoins() {
		return classes()
				.that().areAnnotatedWith(Entity.class)
				.should(notJoinEntitiesOfAnotherSchema())
				.allowEmptyShould(true)
				.because("a relationship between entities in different schemas hides a "
						+ "cross-module coupling; reference the other module's aggregate by id instead");
	}

	private static ArchCondition<JavaClass> notJoinEntitiesOfAnotherSchema() {
		return new ArchCondition<JavaClass>(
				"not have relationship fields to @Entity classes mapped to a different schema") {
			@Override
			public void check(JavaClass javaClass, ConditionEvents events) {
				Optional<String> sourceSchema = schemaOf(javaClass);
				if (sourceSchema.isEmpty()) {
					return;
				}
				for (JavaField field : javaClass.getAllFields()) {
					if (!isRelationshipField(field)) {
						continue;
					}
					for (JavaClass targetEntity : relationshipTargets(field)) {
						if (!targetEntity.isAnnotatedWith(Entity.class)) {
							continue;
						}
						Optional<String> targetSchema = schemaOf(targetEntity);
						if (targetSchema.isPresent() && !targetSchema.equals(sourceSchema)) {
							events.add(SimpleConditionEvent.violated(javaClass,
									field.getFullName() + " joins " + targetEntity.getFullName()
											+ " (schema '" + targetSchema.get() + "'), but "
											+ javaClass.getFullName() + " is mapped to schema '"
											+ sourceSchema.get() + "'"));
						}
					}
				}
			}
		};
	}

	private static boolean isRelationshipField(JavaField field) {
		return field.isAnnotatedWith(OneToMany.class)
				|| field.isAnnotatedWith(ManyToOne.class)
				|| field.isAnnotatedWith(OneToOne.class)
				|| field.isAnnotatedWith(ManyToMany.class)
				|| field.isAnnotatedWith(JoinColumn.class)
				|| field.isAnnotatedWith(JoinTable.class);
	}

	/**
	 * Resolves the entity type(s) a relationship field points at. Handles both
	 * direct reference fields ({@code @ManyToOne private Product product;}) and
	 * collection fields ({@code @OneToMany private List<OrderLine> lines;}) via
	 * ArchUnit's bytecode-level generic-type model, so no reflection/classloading
	 * of the target types is required.
	 */
	private static List<JavaClass> relationshipTargets(JavaField field) {
		JavaType fieldType = field.getType();
		if (fieldType instanceof JavaParameterizedType parameterizedType) {
			return parameterizedType.getActualTypeArguments().stream()
					.filter(JavaClass.class::isInstance)
					.map(JavaClass.class::cast)
					.toList();
		}
		return List.of(field.getRawType());
	}

	private static Optional<String> schemaOf(JavaClass entityClass) {
		return entityClass.tryGetAnnotationOfType(Table.class)
				.map(Table::schema)
				.filter(schema -> !schema.isBlank());
	}

	static Optional<String> moduleOf(JavaClass javaClass, String basePackage) {
		String packageName = javaClass.getPackageName();
		String prefix = basePackage + ".";
		if (!packageName.startsWith(prefix)) {
			return Optional.empty();
		}
		String remainder = packageName.substring(prefix.length());
		int firstDot = remainder.indexOf('.');
		String module = firstDot == -1 ? remainder : remainder.substring(0, firstDot);
		return module.isEmpty() ? Optional.empty() : Optional.of(module);
	}
}