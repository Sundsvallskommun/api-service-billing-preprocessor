package se.sundsvall.billingpreprocessor.integration.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;

@CircuitBreaker(name = "InvoiceFileConfigurationRepository")
public interface InvoiceFileConfigurationRepository extends JpaRepository<InvoiceFileConfigurationEntity, Long> {

	Optional<InvoiceFileConfigurationEntity> findByTypeAndCategoryTag(String type, String categoryTag);

	boolean existsByTypeAndCategoryTag(String type, String categoryTag);

	Optional<InvoiceFileConfigurationEntity> findByCreatorName(String creatorName);
}
