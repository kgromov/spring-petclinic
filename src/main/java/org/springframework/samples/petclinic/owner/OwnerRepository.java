/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.audit.ReflectionFieldUtils;
import org.springframework.samples.petclinic.audit.RevisionMetadata;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Repository class for <code>Owner</code> domain objects All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
public interface OwnerRepository extends
	Repository<Owner, Integer>,
	RevisionRepository<Owner, Integer, Integer> {

	/**
	 * Retrieve all {@link PetType}s from the data store.
	 *
	 * @return a Collection of {@link PetType}s.
	 */
	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	List<PetType> findPetTypes();

	/**
	 * Retrieve {@link Owner}s from the data store by last name, returning all owners
	 * whose last name <i>starts</i> with the given name.
	 *
	 * @param lastName Value to search for
	 * @return a Collection of matching {@link Owner}s (or an empty Collection if none
	 * found)
	 */

	@Query("SELECT DISTINCT owner FROM Owner owner left join  owner.pets WHERE owner.lastName LIKE :lastName% ")
	Page<Owner> findByLastName(@Param("lastName") String lastName, Pageable pageable);

	@Query("SELECT owner FROM Owner owner WHERE owner.firstName LIKE :name% OR owner.lastName LIKE :name% ORDER BY owner.firstName")
	List<Owner> findByName(@Param("name") String name);

	/**
	 * Retrieve an {@link Owner} from the data store by id.
	 *
	 * @param id the id to search for
	 * @return the {@link Owner} if found
	 */
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
	Owner findById(@Param("id") Integer id);

	/**
	 * Save an {@link Owner} to the data store, either inserting or updating it.
	 *
	 * @param owner the {@link Owner} to save
	 */
	void save(Owner owner);

	/**
	 * Returns all the owners from data store
	 **/
	@Query("SELECT owner FROM Owner owner")
	Page<Owner> findAll(Pageable pageable);

	default List<RevisionMetadata> getRevisionsMetadata(Integer id) {
		var revisions = this.findRevisions(id).get()
			.sorted(Comparator.comparingInt(Revision::getRequiredRevisionNumber))
			.toList();
		List<RevisionMetadata> metadata =  revisions.stream()
			.limit(1)
			.map(revision -> new RevisionMetadata(
				revision.getRequiredRevisionNumber(),
				revision.getRequiredRevisionInstant(),
				revision.getMetadata().getRevisionType(),
				Arrays.stream(revision.getEntity().getClass().getDeclaredFields()).map(Field::getName).collect(toSet())
			)).collect(toList());
		for (int i = 1; i < revisions.size(); i++) {
            var prevRevision = revisions.get(i - 1);
			var currentRevision = revisions.get(i);
            var prevRevisionState = ReflectionFieldUtils.getObjectValuesByField(prevRevision.getEntity(), BaseEntity.class);
			var currentRevisionState = ReflectionFieldUtils.getObjectValuesByField(currentRevision.getEntity(), BaseEntity.class);
            var modifiedFields = currentRevisionState.entrySet()
				.stream()
				.filter(entry -> !this.isProxyClass(entry.getValue().getClass()))
				.filter(entry -> !Objects.equals(entry.getValue(), prevRevisionState.get(entry.getKey())))
				.map(Map.Entry::getKey)
				.collect(toSet());
			metadata.add(new RevisionMetadata(
				currentRevision.getRequiredRevisionNumber(),
				currentRevision.getRequiredRevisionInstant(),
				currentRevision.getMetadata().getRevisionType(),
				modifiedFields
			));
		}
		return metadata;
	}

	private boolean isProxyClass(Class<?> clazz) {
		String classSimpleName = clazz.getSimpleName();
		return clazz.getName().contains(".proxy.") || classSimpleName.contains("$") || classSimpleName.contains("@");
	}
}
