package se.sundsvall.billingpreprocessor.integration.db;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;

@Transactional
@CircuitBreaker(name = "BillingRecordRepository")
public interface BillingRecordRepository extends JpaRepository<BillingRecordEntity, String>, JpaSpecificationExecutor<BillingRecordEntity> {}
