package org.springframework.samples.petclinic.owner;

import io.micrometer.core.annotation.Timed;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.springframework.data.history.Revision;
import org.springframework.samples.petclinic.audit.AuditRepository;
import org.springframework.samples.petclinic.audit.RevisionMetadata;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit/owners")
@ApplicationLayer
public class OwnerAuditRestController {
	private final OwnerRepository ownerRepository;
	private final AuditRepository auditRepository;

    public OwnerAuditRestController(OwnerRepository ownerRepository, AuditRepository auditRepository) {
        this.ownerRepository = ownerRepository;
        this.auditRepository = auditRepository;
    }

	@Timed(value = "micrometer.endpoint.time", description = "Duration of the Micrometer endpoint")
	@GetMapping("/{id}/revisions")
	public List<RevisionMetadata> getOwnerRevisions(@PathVariable Integer id) {
		return ownerRepository.getRevisionsMetadata(id);
	}

	@Timed(value = "micrometer.endpoint.time", description = "Duration of the Micrometer endpoint")
	// as alternative
	@GetMapping("/{id}/revisions/envers")
	public List<RevisionMetadata> getOwnerRevisionsWithEnvers(@PathVariable Integer id) {
		return this.auditRepository.getRevisionsMetadata(Owner.class, id);
	}
}
