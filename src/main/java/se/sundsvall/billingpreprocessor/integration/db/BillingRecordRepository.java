package se.sundsvall.billingpreprocessor.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;

@Transactional
@CircuitBreaker(name = "BillingRecordRepository")
public interface BillingRecordRepository extends JpaRepository<BillingRecordEntity, String>, JpaSpecificationExecutor<BillingRecordEntity> {}
