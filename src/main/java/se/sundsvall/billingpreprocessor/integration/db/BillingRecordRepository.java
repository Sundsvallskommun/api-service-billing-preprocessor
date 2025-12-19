package se.sundsvall.billingpreprocessor.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Status;

@Transactional
@CircuitBreaker(name = "BillingRecordRepository")
public interface BillingRecordRepository extends JpaRepository<BillingRecordEntity, String>, JpaSpecificationExecutor<BillingRecordEntity> {

	List<BillingRecordEntity> findAllByStatusAndMunicipalityIdAndTransferDateLessThanEqual(Status status, String municipalityId, LocalDate transferDate);

	boolean existsByIdAndMunicipalityId(String id, String municipalityId);

	BillingRecordEntity getReferenceByIdAndMunicipalityId(String id, String municipalityId);

	void deleteByIdAndMunicipalityId(String id, String municipalityId);
}
