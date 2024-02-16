package org.springframework.samples.petclinic.system;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.HibernateQueryMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
	@Bean
	MeterBinder hibernateQueryMetrics(EntityManagerFactory entityManager) {
		return new HibernateQueryMetrics(entityManager.unwrap(SessionFactoryImplementor.class), "mySess", Tags.empty());
	}
}
