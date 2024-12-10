package se.sundsvall.billingpreprocessor.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus;

@Transactional
@CircuitBreaker(name = "InvoiceFileRepository")
public interface InvoiceFileRepository extends JpaRepository<InvoiceFileEntity, Long>, JpaSpecificationExecutor<InvoiceFileEntity> {

	List<InvoiceFileEntity> findByStatusAndMunicipalityId(InvoiceFileStatus status, String municipalityId);

	List<InvoiceFileEntity> findByStatusInAndMunicipalityId(List<InvoiceFileStatus> statuses, String municipalityId);
}
