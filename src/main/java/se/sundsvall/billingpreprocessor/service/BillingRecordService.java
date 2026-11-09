package se.sundsvall.billingpreprocessor.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity_;
import se.sundsvall.dept44.problem.Problem;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.INVOICED;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.toBillingRecord;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.toBillingRecordEntities;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.toBillingRecordEntity;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.toBillingRecords;
import static se.sundsvall.billingpreprocessor.service.mapper.BillingRecordMapper.updateEntity;

@Service
public class BillingRecordService {
	private static final String ENTITY_NOT_FOUND = "A billing record with id '%s' and municipality ID '%s' could not be found!";
	private static final String ENTITY_CAN_NOT_BE_MODIFIED = "The billing record has status INVOICED or its transfer date has passed and is therefore not possible to %s!";
	private static final String OPERATION_DELETE = "delete";
	private static final String OPERATION_UPDATE = "update";
	private static final String ENTITY_CAN_NOT_BE_CREATED = "One or more billing records contain an unknown type or category!";

	private final BillingRecordRepository billingRecordRepository;
	private final InvoiceFileConfigurationRepository invoiceFileConfigurationRepository;

	public BillingRecordService(
		BillingRecordRepository billingRecordRepository,
		InvoiceFileConfigurationRepository invoiceFileConfigurationRepository) {

		this.billingRecordRepository = billingRecordRepository;
		this.invoiceFileConfigurationRepository = invoiceFileConfigurationRepository;
	}

	public String createBillingRecord(final BillingRecord billingRecord, String municipalityId) {
		if (invoiceFileConfigurationRepository.existsByTypeAndCategoryTag(billingRecord.getType().name(), billingRecord.getCategory())) {
			return billingRecordRepository.save(toBillingRecordEntity(billingRecord, municipalityId)).getId();
		}

		throw Problem.valueOf(BAD_REQUEST, ENTITY_CAN_NOT_BE_CREATED);
	}

	public List<String> createBillingRecords(final List<BillingRecord> billingRecords, String municipalityId) {
		final var batchCanBeProcessed = billingRecords.stream()
			.allMatch(billingRecord -> invoiceFileConfigurationRepository.existsByTypeAndCategoryTag(billingRecord.getType().name(), billingRecord.getCategory()));

		if (batchCanBeProcessed) {
			return billingRecordRepository.saveAll(toBillingRecordEntities(billingRecords, municipalityId)).stream().map(BillingRecordEntity::getId).toList();
		}

		throw Problem.valueOf(BAD_REQUEST, ENTITY_CAN_NOT_BE_CREATED);
	}

	public BillingRecord readBillingRecord(final String id, String municipalityId) {
		verifyExistingId(id, municipalityId);
		return toBillingRecord(billingRecordRepository.getReferenceByIdAndMunicipalityId(id, municipalityId));
	}

	public Page<BillingRecord> findBillingRecords(final Specification<BillingRecordEntity> filter, final Pageable pageable, String municipalityId) {
		final var matches = billingRecordRepository.findAll(filter.and((root, _, cb) -> cb.equal(root.get(BillingRecordEntity_.MUNICIPALITY_ID), municipalityId)), pageable);
		return new PageImpl<>(toBillingRecords(matches.getContent()), pageable, matches.getTotalElements());
	}

	public BillingRecord updateBillingRecord(final String id, final BillingRecord billingRecord, String municipalityId) {
		verifyExistingId(id, municipalityId);
		final var entity = billingRecordRepository.getReferenceByIdAndMunicipalityId(id, municipalityId);
		verifyModifiable(entity, OPERATION_UPDATE);

		return toBillingRecord(billingRecordRepository.save(updateEntity(entity, billingRecord)));
	}

	public void deleteBillingRecord(final String id, String municipalityId) {
		verifyExistingId(id, municipalityId);
		verifyModifiable(billingRecordRepository.getReferenceByIdAndMunicipalityId(id, municipalityId), OPERATION_DELETE);

		billingRecordRepository.deleteByIdAndMunicipalityId(id, municipalityId);
	}

	/**
	 * A billing record can be modified (updated or deleted) as long as it has not been invoiced and its transfer date
	 * has not been reached (records are picked up for invoicing at midnight on the transfer date).
	 */
	private void verifyModifiable(final BillingRecordEntity entity, final String operation) {
		final var today = LocalDate.now(ZoneId.systemDefault());
		final boolean transferDatePassed = ofNullable(entity.getTransferDate())
			.map(transferDate -> !transferDate.isAfter(today))
			.orElse(false);

		if ((INVOICED == entity.getStatus()) || transferDatePassed) {
			throw Problem.valueOf(METHOD_NOT_ALLOWED, ENTITY_CAN_NOT_BE_MODIFIED.formatted(operation));
		}
	}

	private void verifyExistingId(final String id, String municipalityId) {
		if (!billingRecordRepository.existsByIdAndMunicipalityId(id, municipalityId)) {
			throw Problem.valueOf(NOT_FOUND, String.format(ENTITY_NOT_FOUND, id, municipalityId));
		}
	}
}
