package org.springframework.samples.petclinic.config;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.HibernateQueryMetrics;
import org.jmolecules.architecture.layered.InfrastructureLayer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@InfrastructureLayer
public class MetricsConfig {

	@Profile("metrics")
	@Bean
	MeterBinder hibernateQueryMetrics(EntityManagerFactory entityManager) {
		return new HibernateQueryMetrics(entityManager.unwrap(SessionFactoryImplementor.class), "mySess", Tags.empty());
	}

	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}

	@Bean
	public CountedAspect countedAspect(MeterRegistry registry) {
		return new CountedAspect(registry);
	}
}
