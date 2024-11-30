package org.springframework.samples.petclinic;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.*;
import com.tngtech.archunit.library.Architectures;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.lang.conditions.ArchConditions.not;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static de.rweisleder.archunit.spring.SpringAnnotationPredicates.springAnnotatedWith;
import static de.rweisleder.archunit.spring.framework.SpringComponentPredicates.springController;
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

	// vanilla approach
	@Test
	void controllers_naming_is_correct() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages(PetClinicApplication.class.getPackageName());

		ArchRule rule = classes()
			.that().areAnnotatedWith(Controller.class)
			.or().areAnnotatedWith(RestController.class)
			.or().haveSimpleNameEndingWith("Controller")
			.should().beAnnotatedWith(Controller.class).orShould().beAnnotatedWith(RestController.class)
			.andShould().haveSimpleNameEndingWith("Controller")
			.because("controller should be easy to find");

		rule.check(importedClasses);
	}

	// using jMolecules
	@ArchTest
	ArchRule jMoleculesLayers = JMoleculesArchitectureRules.ensureLayering();

	// using archunit-spring (rweisleder project with defined common archunit rules for spring)
	@ArchTest
	ArchRule RequestMappingMethods = methods()
		.that(are(springAnnotatedWith(RequestMapping.class)))
		.should().beDeclaredInClassesThat(are(springController()));


	private static ArchCondition<JavaClass> noArgsConstructor(JavaModifier... visibilities) {
		var constructorModifiers = ofNullable(visibilities).map(Set::of).orElse(emptySet());
		String modifiers = constructorModifiers.stream().map(m -> m.name().toLowerCase()).collect(Collectors.joining(" or "));
		return new ArchCondition<>("have %s constructor without parameters".formatted(modifiers)) {
			@Override
			public void check(JavaClass javaClass, ConditionEvents events) {
				boolean satisfied = javaClass.getConstructors().stream()
					.anyMatch(constructor ->
						constructor.getParameters().isEmpty()
							&& constructor.getModifiers().stream().anyMatch(constructorModifiers::contains)
					);
				String message = javaClass.getDescription() + (satisfied ? " has" : " does not have")
					+ " %s constructor without parameters".formatted(modifiers);
				events.add(new SimpleConditionEvent(javaClass, satisfied, message));
			}
		};
	}
}
