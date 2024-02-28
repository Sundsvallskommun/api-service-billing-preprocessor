package se.sundsvall.billingpreprocessor.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;

@CircuitBreaker(name = "InvoiceFileConfigurationRepository")
public interface InvoiceFileConfigurationRepository extends JpaRepository<InvoiceFileConfigurationEntity, Long> {

	InvoiceFileConfigurationEntity findByTypeAndCategoryTag(String type, String categoryTag);
	boolean existsByTypeAndCategoryTag(String type, String categoryTag);
}
