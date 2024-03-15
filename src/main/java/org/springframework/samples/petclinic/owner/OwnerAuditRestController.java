package org.springframework.samples.petclinic.owner;

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
public class OwnerAuditRestController {
	private final OwnerRepository ownerRepository;
	private final AuditRepository auditRepository;

    public OwnerAuditRestController(OwnerRepository ownerRepository, AuditRepository auditRepository) {
        this.ownerRepository = ownerRepository;
        this.auditRepository = auditRepository;
    }

	@GetMapping("/{id}/revisions")
	public List<RevisionMetadata> getOwnerRevisions(@PathVariable Integer id) {
		/*return ownerRepository.findRevisions(id)
			.stream()
			.map(revision -> new RevisionMetadata(
				revision.getRequiredRevisionNumber(),
				revision.getRequiredRevisionInstant(),
				revision.getMetadata().getRevisionType())
			)
			.toList();*/
		return ownerRepository.getRevisionsMetadata(id);
	}

	@GetMapping("/{id}/history")
	public List<String> getOwnerAddressHistory(@PathVariable Integer id) {
		// as alternative
		List<Owner> owners = auditRepository.getRevisionsById(Owner.class, id);
		return ownerRepository.findRevisions(id)
			.stream()
			.map(Revision::getEntity)
			.map(Owner::getAddress)
			.toList();
	}
}
