package se.sundsvall.billingpreprocessor.service;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.INVOICED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;

import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;
import se.sundsvall.billingpreprocessor.service.creator.ExternalInvoiceCreator;
import se.sundsvall.billingpreprocessor.service.creator.InternalInvoiceCreator;

@ExtendWith(MockitoExtension.class)
class InvoiceFileServiceTest {
	private final static String CATEGORY = "category";
	private final static String FILENAME = "fileName";
	private final static byte[] FILE_HEADER = "file_header".getBytes();
	private final static byte[] INVOICE_DATA = "invoice_data".getBytes();

	@Mock
	private BillingRecordRepository billingRecordRepositoryMock;

	@Mock
	private InvoiceFileRepository invoiceFileRepositoryMock;

	@Mock
	private ExternalInvoiceCreator externalInvoiceCreatorMock;

	@Mock
	private InternalInvoiceCreator internalInvoiceCreatorMock;

	@Mock
	private InvoiceFileConfigurationService invoiceFileConfigurationServiceMock;

	private InvoiceFileService service;

	@Captor
	private ArgumentCaptor<BillingRecordEntity> billingRecordArgumentCaptor;

	@Captor
	private ArgumentCaptor<InvoiceFileEntity> invoiceFileArgumentCaptor;

	@BeforeEach
	public void setup() {
		service = new InvoiceFileService(billingRecordRepositoryMock, invoiceFileRepositoryMock, List.of(externalInvoiceCreatorMock, internalInvoiceCreatorMock), invoiceFileConfigurationServiceMock);
	}

	@Test
	void createBillingFilesWhenNoApprovedEntitiesExists() {
		// Act
		service.createFileEntities();

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatus(APPROVED);
		verifyNoMoreInteractions(billingRecordRepositoryMock, invoiceFileRepositoryMock, externalInvoiceCreatorMock, internalInvoiceCreatorMock, invoiceFileConfigurationServiceMock);
	}

	@Test
	void createBillingFilesWhenApprovedExternalEntitiesExists() throws Exception {
		// Arrange
		final var entity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL);

		when(billingRecordRepositoryMock.findAllByStatus(APPROVED)).thenReturn(List.of(entity));
		when(invoiceFileConfigurationServiceMock.getInvoiceFileNameBy(EXTERNAL.name(), CATEGORY)).thenReturn(FILENAME);
		when(externalInvoiceCreatorMock.createFileHeader()).thenReturn(FILE_HEADER);
		when(externalInvoiceCreatorMock.createInvoiceData(any())).thenReturn(INVOICE_DATA);
		when(externalInvoiceCreatorMock.canHandle(EXTERNAL)).thenReturn(true);
		when(externalInvoiceCreatorMock.handledCategories()).thenReturn(List.of(CATEGORY));

		// Act
		service.createFileEntities();

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatus(APPROVED);
		verify(externalInvoiceCreatorMock).createFileHeader();
		verify(externalInvoiceCreatorMock).createInvoiceData(entity);
		verify(billingRecordRepositoryMock).save(billingRecordArgumentCaptor.capture());
		verify(invoiceFileRepositoryMock).save(invoiceFileArgumentCaptor.capture());
		verifyNoMoreInteractions(billingRecordRepositoryMock, invoiceFileRepositoryMock, externalInvoiceCreatorMock, internalInvoiceCreatorMock, invoiceFileConfigurationServiceMock);

		assertThat(billingRecordArgumentCaptor.getValue()).usingRecursiveComparison().ignoringFields("status").isEqualTo(entity);
		assertThat(billingRecordArgumentCaptor.getValue().getStatus()).isEqualTo(INVOICED);
		assertThat(invoiceFileArgumentCaptor.getValue()).satisfies(fileEntity -> {
			assertThat(fileEntity.getType()).isEqualTo(EXTERNAL.name());
			assertThat(fileEntity.getName()).isEqualTo(FILENAME);
			assertThat(fileEntity.getContent()).isEqualTo(new String(ArrayUtils.addAll(FILE_HEADER, INVOICE_DATA), StandardCharsets.UTF_8));
			assertThat(fileEntity.getStatus()).isEqualTo(GENERATED);
		});
	}

	@Test
	void createBillingFilesWhenApprovedInternalEntitiesExists() throws Exception {
		// Arrange
		final var entity = createBillingRecordEntity(randomUUID().toString(), INTERNAL);

		when(billingRecordRepositoryMock.findAllByStatus(APPROVED)).thenReturn(List.of(entity));
		when(invoiceFileConfigurationServiceMock.getInvoiceFileNameBy(INTERNAL.name(), CATEGORY)).thenReturn(FILENAME);
		when(internalInvoiceCreatorMock.createFileHeader()).thenReturn(FILE_HEADER);
		when(internalInvoiceCreatorMock.createInvoiceData(any())).thenReturn(INVOICE_DATA);
		when(internalInvoiceCreatorMock.canHandle(INTERNAL)).thenReturn(true);
		when(internalInvoiceCreatorMock.handledCategories()).thenReturn(List.of(CATEGORY));

		// Act
		service.createFileEntities();

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatus(APPROVED);
		verify(externalInvoiceCreatorMock, atMostOnce()).canHandle(INTERNAL);
		verify(internalInvoiceCreatorMock).canHandle(INTERNAL);
		verify(internalInvoiceCreatorMock).createFileHeader();
		verify(internalInvoiceCreatorMock).createInvoiceData(entity);
		verify(billingRecordRepositoryMock).save(billingRecordArgumentCaptor.capture());
		verify(invoiceFileRepositoryMock).save(invoiceFileArgumentCaptor.capture());
		verifyNoMoreInteractions(billingRecordRepositoryMock, invoiceFileRepositoryMock, externalInvoiceCreatorMock, internalInvoiceCreatorMock, invoiceFileConfigurationServiceMock);

		assertThat(billingRecordArgumentCaptor.getValue()).usingRecursiveComparison().ignoringFields("status").isEqualTo(entity);
		assertThat(billingRecordArgumentCaptor.getValue().getStatus()).isEqualTo(INVOICED);
		assertThat(invoiceFileArgumentCaptor.getValue()).satisfies(fileEntity -> {
			assertThat(fileEntity.getType()).isEqualTo(INTERNAL.name());
			assertThat(fileEntity.getName()).isEqualTo(FILENAME);
			assertThat(fileEntity.getContent()).isEqualTo(new String(ArrayUtils.addAll(FILE_HEADER, INVOICE_DATA), StandardCharsets.UTF_8));
			assertThat(fileEntity.getStatus()).isEqualTo(GENERATED);
		});
	}

	@Test
	void createBillingFilesWhenInvalidEntitiesExists() throws Exception {
		// Arrange
		final var internalFileName = "internalFileName";
		final var externalFileName = "externalFileName";
		final var internalFileHeader = "internalFileHeader".getBytes();
		final var externalFileHeader = "externalFileHeader".getBytes();
		final var internalInvoiceData = "internalInvoiceData".getBytes();
		final var externalInvoiceData = "externalInvoiceData".getBytes();
		final var invalidInternalEntity = createBillingRecordEntity(randomUUID().toString(), INTERNAL);
		final var internalEntity = createBillingRecordEntity(randomUUID().toString(), INTERNAL);
		final var invalidExternalEntity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL);
		final var externalEntity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL);

		when(billingRecordRepositoryMock.findAllByStatus(APPROVED)).thenReturn(List.of(
			invalidInternalEntity, 
			internalEntity,
			invalidExternalEntity,
			externalEntity));
		when(invoiceFileConfigurationServiceMock.getInvoiceFileNameBy(INTERNAL.name(), CATEGORY)).thenReturn(internalFileName);
		when(invoiceFileConfigurationServiceMock.getInvoiceFileNameBy(EXTERNAL.name(), CATEGORY)).thenReturn(externalFileName);
		when(internalInvoiceCreatorMock.createFileHeader()).thenReturn(internalFileHeader);
		when(internalInvoiceCreatorMock.createInvoiceData(internalEntity)).thenReturn(internalInvoiceData);
		when(internalInvoiceCreatorMock.createInvoiceData(invalidInternalEntity)).thenThrow(Problem.valueOf(INTERNAL_SERVER_ERROR));
		when(internalInvoiceCreatorMock.canHandle(INTERNAL)).thenReturn(true);
		when(internalInvoiceCreatorMock.handledCategories()).thenReturn(List.of(CATEGORY));
		when(externalInvoiceCreatorMock.createFileHeader()).thenReturn(externalFileHeader);
		when(externalInvoiceCreatorMock.createInvoiceData(externalEntity)).thenReturn(externalInvoiceData);
		when(externalInvoiceCreatorMock.createInvoiceData(invalidExternalEntity)).thenThrow(Problem.valueOf(INTERNAL_SERVER_ERROR));
		when(externalInvoiceCreatorMock.canHandle(EXTERNAL)).thenReturn(true);
		when(externalInvoiceCreatorMock.handledCategories()).thenReturn(List.of(CATEGORY));

		// Act
		service.createFileEntities();

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatus(APPROVED);

		verify(internalInvoiceCreatorMock).canHandle(INTERNAL);
		verify(internalInvoiceCreatorMock, atMostOnce()).canHandle(EXTERNAL);
		verify(internalInvoiceCreatorMock).createFileHeader();
		verify(internalInvoiceCreatorMock, times(2)).handledCategories();
		verify(internalInvoiceCreatorMock).createInvoiceData(invalidInternalEntity);
		verify(internalInvoiceCreatorMock).createInvoiceData(internalEntity);

		verify(externalInvoiceCreatorMock, atMostOnce()).canHandle(INTERNAL);
		verify(externalInvoiceCreatorMock).canHandle(EXTERNAL);
		verify(externalInvoiceCreatorMock).createFileHeader();
		verify(externalInvoiceCreatorMock, times(2)).handledCategories();
		verify(externalInvoiceCreatorMock).createInvoiceData(invalidExternalEntity);
		verify(externalInvoiceCreatorMock).createInvoiceData(externalEntity);

		verify(billingRecordRepositoryMock, times(2)).save(billingRecordArgumentCaptor.capture());
		verify(invoiceFileRepositoryMock, times(2)).save(invoiceFileArgumentCaptor.capture());
		verifyNoMoreInteractions(billingRecordRepositoryMock, invoiceFileRepositoryMock, externalInvoiceCreatorMock, internalInvoiceCreatorMock, invoiceFileConfigurationServiceMock);

		// TODO: Add verifications and assertions to mail component (task UF-7461)

		assertThat(billingRecordArgumentCaptor.getAllValues()).satisfiesOnlyOnce(billingEntity -> {
			assertThat(billingEntity).usingRecursiveComparison().ignoringFields("status").isEqualTo(internalEntity);
			assertThat(billingEntity.getStatus()).isEqualTo(INVOICED);
		}).satisfiesOnlyOnce(billingEntity -> {
			assertThat(billingEntity).usingRecursiveComparison().ignoringFields("status").isEqualTo(externalEntity);
			assertThat(billingEntity.getStatus()).isEqualTo(INVOICED);
		});

		assertThat(invoiceFileArgumentCaptor.getAllValues())
			.satisfiesOnlyOnce(fileEntity -> {
				assertThat(fileEntity.getType()).isEqualTo(INTERNAL.name());
				assertThat(fileEntity.getName()).isEqualTo(internalFileName);
				assertThat(fileEntity.getContent()).isEqualTo(new String(ArrayUtils.addAll(internalFileHeader, internalInvoiceData), StandardCharsets.UTF_8));
				assertThat(fileEntity.getStatus()).isEqualTo(GENERATED);
			}).satisfiesOnlyOnce(fileEntity -> {
				assertThat(fileEntity.getType()).isEqualTo(EXTERNAL.name());
				assertThat(fileEntity.getName()).isEqualTo(externalFileName);
				assertThat(fileEntity.getContent()).isEqualTo(new String(ArrayUtils.addAll(externalFileHeader, externalInvoiceData), StandardCharsets.UTF_8));
				assertThat(fileEntity.getStatus()).isEqualTo(GENERATED);
			});
	}

	@Test
	void createBillingFilesWhenMajorExceptionOccurs() throws Exception {
		// Arrange
		final var entity = createBillingRecordEntity(randomUUID().toString(), INTERNAL);

		when(internalInvoiceCreatorMock.canHandle(INTERNAL)).thenReturn(true);
		when(billingRecordRepositoryMock.findAllByStatus(APPROVED)).thenReturn(List.of(entity));
		// Not mocking createFileHeader on CreatorMock will cause NPE to be thrown on
		// outputStream.write(invoiceCreator.createFileHeader())

		// Act
		service.createFileEntities();

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatus(APPROVED);
		verify(internalInvoiceCreatorMock).canHandle(INTERNAL);
		verify(externalInvoiceCreatorMock, atMostOnce()).canHandle(INTERNAL);
		verify(internalInvoiceCreatorMock).createFileHeader();
		verifyNoMoreInteractions(billingRecordRepositoryMock, invoiceFileRepositoryMock, externalInvoiceCreatorMock, internalInvoiceCreatorMock, invoiceFileConfigurationServiceMock);

		// TODO: Add verifications and assertions to mail component (task UF-7461)
	}

	@Test
	void createBillingFilesWhenNoCorrespondingCreatorForTypeExists() throws Exception {
		// Arrange
		final var entity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL);

		when(billingRecordRepositoryMock.findAllByStatus(APPROVED)).thenReturn(List.of(entity));

		// Act
		service.createFileEntities();

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatus(APPROVED);
		verify(externalInvoiceCreatorMock).canHandle(EXTERNAL);
		verify(internalInvoiceCreatorMock, atMostOnce()).canHandle(EXTERNAL);
		verifyNoMoreInteractions(billingRecordRepositoryMock, invoiceFileRepositoryMock, externalInvoiceCreatorMock, internalInvoiceCreatorMock, invoiceFileConfigurationServiceMock);

		// TODO: Add verifications and assertions to mail component (task UF-7461)
	}

	@Test
	void createBillingFilesWhenNoCorrespondingCreatorForCategoryExists() throws Exception {
		// Arrange
		final var entity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL);

		when(billingRecordRepositoryMock.findAllByStatus(APPROVED)).thenReturn(List.of(entity));
		when(externalInvoiceCreatorMock.canHandle(EXTERNAL)).thenReturn(true);
		when(externalInvoiceCreatorMock.createFileHeader()).thenReturn(FILE_HEADER);
		when(externalInvoiceCreatorMock.handledCategories()).thenReturn(List.of("OTHER_CATEGORY"));

		// Act
		service.createFileEntities();

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatus(APPROVED);
		verify(externalInvoiceCreatorMock).canHandle(EXTERNAL);
		verify(internalInvoiceCreatorMock, atMostOnce()).canHandle(EXTERNAL);
		verify(externalInvoiceCreatorMock).createFileHeader();
		verifyNoMoreInteractions(billingRecordRepositoryMock, invoiceFileRepositoryMock, externalInvoiceCreatorMock, internalInvoiceCreatorMock, invoiceFileConfigurationServiceMock);

		// TODO: Add verifications and assertions to mail component (task UF-7461)
	}

	private static BillingRecordEntity createBillingRecordEntity(String id, Type type) {
		return BillingRecordEntity.create()
			.withId(id)
			.withCategory(CATEGORY)
			.withStatus(APPROVED)
			.withType(type);
	}
}
