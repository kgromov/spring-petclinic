package org.springframework.samples.petclinic.audit;

import java.time.Instant;
import java.util.Set;

import org.springframework.data.history.RevisionMetadata.RevisionType;

import static java.util.Collections.emptySet;

public record RevisionMetadata(int revisionNumber, Instant revisionTime, RevisionType revisionType, Set<String> modifiedFields) {
	public RevisionMetadata(int revisionNumber, Instant revisionTime, RevisionType revisionType) {
		this(revisionNumber, revisionTime, revisionType, emptySet());
	}
}
