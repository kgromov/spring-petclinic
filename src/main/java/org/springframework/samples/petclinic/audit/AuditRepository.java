package org.springframework.samples.petclinic.audit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.order.AuditOrder;
import org.hibernate.envers.query.order.internal.PropertyAuditOrder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.data.history.RevisionMetadata.RevisionType;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.springframework.data.history.RevisionMetadata.RevisionType.*;

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

		return getAuditReader().createQuery()
			.forRevisionsOfEntity(clazz, true, false)
			.add(AuditEntity.property(field).eq(value))
			.getResultList();
	}

	public <T, ID> List<RevisionMetadata> getRevisionsMetadata(Class<T> clazz, ID id) {
		var results = getAuditReader()
			.createQuery()
			.forRevisionsOfEntityWithChanges(clazz, true)
			.add(AuditEntity.id().eq(id))
			.addOrder(AuditEntity.revisionNumber().asc())
			.getResultList();
		List<RevisionMetadata> metadata = new ArrayList<>();
		for (Object tuple : results) {
			Object[] rowArray = (Object[]) tuple;
			var entity = rowArray[0];
			var revisionEntity = (DefaultRevisionEntity) rowArray[1];
			var revisionType = (org.hibernate.envers.RevisionType) rowArray[2];
			Set<String> modifiedFields = (Set<String>) rowArray[3];
			metadata.add(new RevisionMetadata(
				revisionEntity.getId(),
				revisionEntity.getRevisionDate().toInstant(),
				from(revisionType),
				modifiedFields
			));
		}
		return metadata;
	}

	private static RevisionType from(org.hibernate.envers.RevisionType revisionType) {
		return switch (revisionType) {
			case ADD -> INSERT;
			case MOD -> UPDATE;
			case DEL -> DELETE;
		};
	}
}
