package se.sundsvall.billingpreprocessor.service;

import static org.zalando.problem.Status.METHOD_NOT_ALLOWED;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.NEW;
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
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;

@Service
public class BillingRecordService {

	private static final String ENTITY_NOT_FOUND = "A billing record with id '%s' could not be found!";
	private static final String ENTITY_CAN_NOT_BE_DELETED = "The billing record does not have status NEW and is therefore not possible to delete!";

	private final BillingRecordRepository repository;

	public BillingRecordService(BillingRecordRepository repository) {
		this.repository = repository;
	}

	public String createBillingRecord(final BillingRecord billingRecord) {
		return repository.save(toBillingRecordEntity(billingRecord)).getId();
	}

	public List<String> createBillingRecords(final List<BillingRecord> billingRecords) {
		return repository.saveAll(toBillingRecordEntities(billingRecords)).stream().map(BillingRecordEntity::getId).toList();
	}

	public BillingRecord readBillingRecord(final String id) {
		verifyExistingId(id);
		return toBillingRecord(repository.getReferenceById(id));
	}

	public Page<BillingRecord> findBillingIRecords(final Specification<BillingRecordEntity> filter, final Pageable pageable) {
		final var matches = repository.findAll(filter, pageable);
		return new PageImpl<>(toBillingRecords(matches.getContent()), pageable, repository.count(filter));
	}

	public BillingRecord updateBillingRecord(final String id, final BillingRecord billingRecord) {
		verifyExistingId(id);
		final var entity = updateEntity(repository.getReferenceById(id), billingRecord);
		return toBillingRecord(repository.save(entity));
	}

	public void deleteBillingRecord(final String id) {
		verifyDeletionAvailability(id);
		repository.deleteById(id);
	}

	private void verifyDeletionAvailability(final String id) {
		verifyExistingId(id);

		final var status = repository.getReferenceById(id).getStatus();
		if (NEW != status) {
			throw Problem.valueOf(METHOD_NOT_ALLOWED, ENTITY_CAN_NOT_BE_DELETED);
		}
	}

	private void verifyExistingId(final String id) {
		if (!repository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, String.format(ENTITY_NOT_FOUND, id));
		}
	}
}
