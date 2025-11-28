package se.sundsvall.billingpreprocessor.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus;

@Transactional
@CircuitBreaker(name = "InvoiceFileRepository")
public interface InvoiceFileRepository extends JpaRepository<InvoiceFileEntity, Long>, JpaSpecificationExecutor<InvoiceFileEntity> {

	List<InvoiceFileEntity> findByStatusAndMunicipalityId(InvoiceFileStatus status, String municipalityId);

	List<InvoiceFileEntity> findByStatusInAndMunicipalityId(List<InvoiceFileStatus> statuses, String municipalityId);

	@Query("""
		    SELECT e FROM InvoiceFileEntity e
		    WHERE e.created >= :start
		      AND e.created < :end
		      AND e.municipalityId = :municipalityId
		""")
	List<InvoiceFileEntity> findAllCreatedInMonth(@Param("municipalityId") String municipalityId, @Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

}
