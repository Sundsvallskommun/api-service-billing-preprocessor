package se.sundsvall.billingpreprocessor.service;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.METHOD_NOT_ALLOWED;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.toBillingRecord;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.toBillingRecordEntities;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.toBillingRecordEntity;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.toBillingRecords;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.updateEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;

@Service
public class BillingRecordService {
	private static final String ENTITY_NOT_FOUND = "A billing record with id '%s' could not be found!";
	private static final String ENTITY_CAN_NOT_BE_DELETED = "The billing record does not have status NEW and is therefore not possible to delete!";
	private static final String ENTITY_CAN_NOT_BE_CREATED = "One or more billing records contain an unknown type or category!";

	private final BillingRecordRepository billingRecordRepository;
	private final InvoiceFileConfigurationRepository invoiceFileConfigurationRepository;

	public BillingRecordService(
		BillingRecordRepository billingRecordRepository,
		InvoiceFileConfigurationRepository invoiceFileConfigurationRepository) {

		this.billingRecordRepository = billingRecordRepository;
		this.invoiceFileConfigurationRepository = invoiceFileConfigurationRepository;
	}

	public String createBillingRecord(final BillingRecord billingRecord) {
		if (invoiceFileConfigurationRepository.existsByTypeAndCategoryTag(billingRecord.getType().name(), billingRecord.getCategory())) {
			return billingRecordRepository.save(toBillingRecordEntity(billingRecord)).getId();
		}

		throw Problem.valueOf(BAD_REQUEST, ENTITY_CAN_NOT_BE_CREATED);
	}

	public List<String> createBillingRecords(final List<BillingRecord> billingRecords) {
		final var batchCanBeProcessed = billingRecords.stream()
			.allMatch(billingRecord -> invoiceFileConfigurationRepository.existsByTypeAndCategoryTag(billingRecord.getType().name(), billingRecord.getCategory()));

		if (batchCanBeProcessed) {
			return billingRecordRepository.saveAll(toBillingRecordEntities(billingRecords)).stream().map(BillingRecordEntity::getId).toList();
		}

		throw Problem.valueOf(BAD_REQUEST, ENTITY_CAN_NOT_BE_CREATED);
	}

	public BillingRecord readBillingRecord(final String id) {
		verifyExistingId(id);
		return toBillingRecord(billingRecordRepository.getReferenceById(id));
	}

	public Page<BillingRecord> findBillingRecords(final Specification<BillingRecordEntity> filter, final Pageable pageable) {
		final var matches = billingRecordRepository.findAll(filter, pageable);
		return new PageImpl<>(toBillingRecords(matches.getContent()), pageable, billingRecordRepository.count(filter));
	}

	public BillingRecord updateBillingRecord(final String id, final BillingRecord billingRecord) {
		verifyExistingId(id);
		final var entity = updateEntity(billingRecordRepository.getReferenceById(id), billingRecord);
		return toBillingRecord(billingRecordRepository.save(entity));
	}

	public void deleteBillingRecord(final String id) {
		verifyDeletionAvailability(id);
		billingRecordRepository.deleteById(id);
	}

	private void verifyDeletionAvailability(final String id) {
		verifyExistingId(id);

		final var status = billingRecordRepository.getReferenceById(id).getStatus();
		if (se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.NEW != status) {
			throw Problem.valueOf(METHOD_NOT_ALLOWED, ENTITY_CAN_NOT_BE_DELETED);
		}
	}

	private void verifyExistingId(final String id) {
		if (!billingRecordRepository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, String.format(ENTITY_NOT_FOUND, id));
		}
	}
}
