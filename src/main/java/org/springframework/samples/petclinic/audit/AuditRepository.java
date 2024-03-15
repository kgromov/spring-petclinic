package org.springframework.samples.petclinic.audit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.order.AuditOrder;
import org.hibernate.envers.query.order.internal.PropertyAuditOrder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Objects;

@Repository
public class AuditRepository {

	@PersistenceContext
	private EntityManager entityManager;

	private AuditReader getAuditReader() {
		return AuditReaderFactory.get(entityManager);
	}

	@SuppressWarnings("unchecked")
	public <T, V> List<T> getRevisionsById(Class<T> clazz, V id) {
		return getAuditReader().createQuery()
			.forRevisionsOfEntity(clazz, true, false)
			.add(AuditEntity.id().eq(id))
			.getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T, V> List<T> getRevisionsByFieldValue(Class<T> clazz, String field, V value) {
		if (Objects.isNull(clazz) || !StringUtils.hasText(field) || Objects.isNull(value)) {
			throw new IllegalArgumentException("Invalid params.");
		}
		// for modified flags
        var results = getAuditReader()
			.createQuery()
			.forRevisionsOfEntityWithChanges(clazz, true)
			.getResultList();

		return getAuditReader().createQuery()
//			.forRevisionsOfEntityWithChanges(clazz, true)
			.forRevisionsOfEntity(clazz, true, false)
			.add(AuditEntity.property(field).eq(value))
			.getResultList();
	}

}
