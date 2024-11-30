package org.springframework.samples.petclinic;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.*;
import com.tngtech.archunit.library.Architectures;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.lang.conditions.ArchConditions.not;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

@AnalyzeClasses(packagesOf = PetClinicApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTests {

	@ArchTest
	ArchRule ControllerNaming = classes()
		.that().areAnnotatedWith(Controller.class)
		.or().areAnnotatedWith(RestController.class)
		.or().haveSimpleNameEndingWith("Controller")
		.should().beAnnotatedWith(Controller.class).orShould().beAnnotatedWith(RestController.class)
		.andShould().haveSimpleNameEndingWith("Controller")
		.because("controller should be easy to find");

	@ArchTest
	ArchRule EntityDeclaration = classes()
		.that().areAnnotatedWith(Entity.class)
		.should().beAnnotatedWith(Table.class)
		.andShould(noArgsConstructor(JavaModifier.PUBLIC, JavaModifier.PROTECTED))
		.because("jpa entities should have a public or protected no-arg constructor");

	@ArchTest
	ArchRule DependenciesBetweenModules = CompositeArchRule
		.of(
			classes()
				.that().resideInAPackage("..owner..")
				.should(not(dependOnClassesThat(resideInAPackage("..vet..")))))
		.and(
			classes()
				.that().resideInAPackage("..vet..")
				.should(not(dependOnClassesThat(resideInAPackage("..owner..")))));

	@ArchTest
	ArchRule Layers = Architectures.layeredArchitecture()
		.consideringOnlyDependenciesInLayers()
		.layer("Controller").definedBy(annotatedWith(Controller.class))
		.layer("Domain").definedBy(assignableTo(Entity.class))
		.layer("Persistence").definedBy(annotatedWith(Repository.class))
		.whereLayer("Controller").mayNotBeAccessedByAnyLayer()
		.whereLayer("Persistence").mayOnlyBeAccessedByLayers("Controller")
		.whereLayer("Domain").mayOnlyBeAccessedByLayers("Controller", "Persistence");

	private static ArchCondition<JavaClass> noArgsConstructor(JavaModifier... visibilities) {
		return new ArchCondition<>("have a public constructor without parameters") {
			@Override
			public void check(JavaClass javaClass, ConditionEvents events) {
				Set<JavaModifier> constructorModifiers = ofNullable(visibilities).map(Set::of).orElse(emptySet());
				boolean satisfied = javaClass.getConstructors().stream()
					.anyMatch(constructor ->
						constructor.getParameters().isEmpty()
							&& constructor.getModifiers().stream().anyMatch(constructorModifiers::contains)
					);
				String message = javaClass.getDescription() + (satisfied ? " has" : " does not have")
					+ " a public constructor without parameters";
				events.add(new SimpleConditionEvent(javaClass, satisfied, message));
			}
		};
	}
}
