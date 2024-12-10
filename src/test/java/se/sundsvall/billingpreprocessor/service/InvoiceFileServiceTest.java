package se.sundsvall.billingpreprocessor.service;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.SEND_FAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.SEND_SUCCESSFUL;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.INVOICED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.zalando.problem.Problem;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;
import se.sundsvall.billingpreprocessor.integration.sftp.SftpConfiguration.UploadGateway;
import se.sundsvall.billingpreprocessor.integration.sftp.SftpProperties;
import se.sundsvall.billingpreprocessor.integration.sftp.SftpPropertiesConfig;
import se.sundsvall.billingpreprocessor.service.creator.ExternalInvoiceCreator;
import se.sundsvall.billingpreprocessor.service.creator.InternalInvoiceCreator;
import se.sundsvall.billingpreprocessor.service.creator.InvoiceCreator;
import se.sundsvall.billingpreprocessor.service.error.InvoiceFileError;

@ExtendWith(MockitoExtension.class)
class InvoiceFileServiceTest {

	private static final String CATEGORY = "category";

	private static final String FILENAME = "fileName";

	private static final byte[] FILE_HEADER = "file_header".getBytes();

	private static final byte[] INVOICE_DATA = "invoice_data".getBytes();

	private static final Charset ENCODING = StandardCharsets.ISO_8859_1;

	private static final String MUNICIPALITY_ID = "municipality_id";

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

	@Mock
	private MessagingService messagingServiceMock;

	@Mock
	private InvoiceFileEntity invoiceFileEntityMock;

	@Mock
	private UploadGateway uploadGatewayMock;

	@Mock
	private DelegatingSessionFactory<?> sessionFactoryMock;

	@Mock
	private SftpPropertiesConfig sftpPropertiesConfigMock;

	@Mock
	private SftpProperties sftpPropertiesMock;

	@Captor
	private ArgumentCaptor<BillingRecordEntity> billingRecordArgumentCaptor;

	@Captor
	private ArgumentCaptor<InvoiceFileEntity> invoiceFileArgumentCaptor;

	@Captor
	private ArgumentCaptor<List<InvoiceFileError>> creationErrorArgumentCaptor;

	@Captor
	private ArgumentCaptor<ByteArrayResource> byteArrayResourceArgumentCaptor;

	private InvoiceFileService service;

	@BeforeEach
	public void setup() {
		service = new InvoiceFileService(
			billingRecordRepositoryMock,
			invoiceFileRepositoryMock,
			List.of(externalInvoiceCreatorMock, internalInvoiceCreatorMock),
			invoiceFileConfigurationServiceMock,
			messagingServiceMock,
			uploadGatewayMock,
			sessionFactoryMock,
			sftpPropertiesConfigMock);
	}

	@Test
	void transferFilesWhenNoFilesToTransferExists() {
		when(sftpPropertiesConfigMock.getMap()).thenReturn(Map.of(MUNICIPALITY_ID, sftpPropertiesMock));
		service.transferFiles(MUNICIPALITY_ID);

		verify(invoiceFileRepositoryMock).findByStatusInAndMunicipalityId(List.of(GENERATED, SEND_FAILED), MUNICIPALITY_ID);
		verify(sftpPropertiesConfigMock).getMap();
		verify(sessionFactoryMock).setThreadKey(MUNICIPALITY_ID);
		verify(sessionFactoryMock).clearThreadKey();
		verifyNoMoreInterationsOnMocks();
	}

	@Test
	void transferFilesWhenFilesToTransferExists() throws Exception {
		final var content = "content";

		when(sftpPropertiesConfigMock.getMap()).thenReturn(Map.of(MUNICIPALITY_ID, sftpPropertiesMock));
		when(sftpPropertiesMock.getRemoteDir()).thenReturn("remoteDir");
		when(invoiceFileRepositoryMock.findByStatusInAndMunicipalityId(List.of(GENERATED, SEND_FAILED), MUNICIPALITY_ID)).thenReturn(List.of(invoiceFileEntityMock));
		when(invoiceFileEntityMock.getContent()).thenReturn(content);
		when(invoiceFileEntityMock.getName()).thenReturn(FILENAME);
		when(invoiceFileEntityMock.getEncoding()).thenReturn(ENCODING.name());
		when(invoiceFileEntityMock.withStatus(SEND_SUCCESSFUL)).thenReturn(invoiceFileEntityMock);
		when(invoiceFileEntityMock.withSent(any())).thenReturn(invoiceFileEntityMock);
		service.transferFiles(MUNICIPALITY_ID);

		verify(sftpPropertiesConfigMock, times(2)).getMap();
		verify(sessionFactoryMock).setThreadKey(MUNICIPALITY_ID);
		verify(invoiceFileRepositoryMock).findByStatusInAndMunicipalityId(List.of(GENERATED, SEND_FAILED), MUNICIPALITY_ID);
		verify(sftpPropertiesMock).getRemoteDir();
		verify(uploadGatewayMock).sendToSftp(byteArrayResourceArgumentCaptor.capture(), eq(FILENAME), eq("remoteDir"));
		verify(invoiceFileRepositoryMock).save(invoiceFileArgumentCaptor.capture());
		verify(invoiceFileEntityMock).withStatus(SEND_SUCCESSFUL);
		verify(sessionFactoryMock).clearThreadKey();
		verifyNoMoreInterationsOnMocks();

		assertThat(byteArrayResourceArgumentCaptor.getValue().getContentAsByteArray()).isEqualTo(content.getBytes());
		assertThat(invoiceFileArgumentCaptor.getValue()).isSameAs(invoiceFileEntityMock);
	}

	@Test
	void transferFilesWithExceptionInTransfer() throws Exception {
		final var content = "content";

		when(sftpPropertiesConfigMock.getMap()).thenReturn(Map.of(MUNICIPALITY_ID, sftpPropertiesMock));
		when(sftpPropertiesMock.getRemoteDir()).thenReturn("remoteDir");
		when(invoiceFileRepositoryMock.findByStatusInAndMunicipalityId(List.of(GENERATED, SEND_FAILED), MUNICIPALITY_ID)).thenReturn(List.of(invoiceFileEntityMock));
		when(invoiceFileEntityMock.getContent()).thenReturn(content);
		when(invoiceFileEntityMock.getName()).thenReturn(FILENAME);
		when(invoiceFileEntityMock.getEncoding()).thenReturn(ENCODING.name());
		when(invoiceFileEntityMock.withStatus(SEND_FAILED)).thenReturn(invoiceFileEntityMock);
		doThrow(Problem.valueOf(INTERNAL_SERVER_ERROR)).when(uploadGatewayMock).sendToSftp(any(), any(), any());

		service.transferFiles(MUNICIPALITY_ID);

		verify(sftpPropertiesConfigMock, times(2)).getMap();
		verify(sessionFactoryMock).setThreadKey(MUNICIPALITY_ID);
		verify(invoiceFileRepositoryMock).findByStatusInAndMunicipalityId(List.of(GENERATED, SEND_FAILED), MUNICIPALITY_ID);
		verify(sftpPropertiesMock).getRemoteDir();
		verify(uploadGatewayMock).sendToSftp(byteArrayResourceArgumentCaptor.capture(), eq(FILENAME), eq("remoteDir"));
		verify(invoiceFileRepositoryMock).save(invoiceFileArgumentCaptor.capture());
		verify(invoiceFileEntityMock).withStatus(SEND_FAILED);
		verify(messagingServiceMock).sendTransferErrorMail(eq(MUNICIPALITY_ID), creationErrorArgumentCaptor.capture());
		verify(sessionFactoryMock).clearThreadKey();
		verifyNoMoreInterationsOnMocks();

		assertThat(byteArrayResourceArgumentCaptor.getValue().getContentAsByteArray()).isEqualTo(content.getBytes());
		assertThat(invoiceFileArgumentCaptor.getValue()).isSameAs(invoiceFileEntityMock);
		assertThat(creationErrorArgumentCaptor.getValue()).hasSize(1)
			.extracting(InvoiceFileError::getEntityId, InvoiceFileError::getMessage)
			.containsExactly(tuple(null, "Could not transfer file with filename: 'fileName' due to DefaultProblem: Internal Server Error"));
	}

	@Test
	void createBillingFilesWhenNoApprovedEntitiesExists() {
		// Act
		service.createFiles(MUNICIPALITY_ID);

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID);
		verify(externalInvoiceCreatorMock).getProcessableType();
		verify(externalInvoiceCreatorMock).getProcessableCategory();
		verify(internalInvoiceCreatorMock).getProcessableType();
		verify(internalInvoiceCreatorMock).getProcessableCategory();
		verifyNoMoreInterationsOnMocks();
	}

	@Test
	void createBillingFilesWhenApprovedExternalEntitiesExists() throws Exception {
		// Arrange
		final var entity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL, MUNICIPALITY_ID);

		when(billingRecordRepositoryMock.findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID)).thenReturn(List.of(entity));
		when(invoiceFileConfigurationServiceMock.getInvoiceFileNameBy(EXTERNAL.name(), CATEGORY)).thenReturn(FILENAME);
		when(invoiceFileConfigurationServiceMock.getEncoding(EXTERNAL.name(), CATEGORY)).thenReturn(ENCODING);
		when(externalInvoiceCreatorMock.createFileHeader()).thenReturn(FILE_HEADER);
		when(externalInvoiceCreatorMock.createInvoiceData(any())).thenReturn(INVOICE_DATA);
		when(externalInvoiceCreatorMock.getProcessableType()).thenReturn(EXTERNAL);
		when(externalInvoiceCreatorMock.getProcessableCategory()).thenReturn(CATEGORY);

		// Act
		service.createFiles(MUNICIPALITY_ID);

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID);
		verify(internalInvoiceCreatorMock).getProcessableType();
		verify(internalInvoiceCreatorMock).getProcessableCategory();
		verify(externalInvoiceCreatorMock).getProcessableType();
		verify(externalInvoiceCreatorMock).getProcessableCategory();
		verify(externalInvoiceCreatorMock).createFileHeader();
		verify(externalInvoiceCreatorMock).createInvoiceData(entity);
		verify(billingRecordRepositoryMock).save(billingRecordArgumentCaptor.capture());
		verify(invoiceFileRepositoryMock).save(invoiceFileArgumentCaptor.capture());
		verifyNoMoreInterationsOnMocks();

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
		final var entity = createBillingRecordEntity(randomUUID().toString(), INTERNAL, MUNICIPALITY_ID);

		when(billingRecordRepositoryMock.findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID)).thenReturn(List.of(entity));
		when(invoiceFileConfigurationServiceMock.getInvoiceFileNameBy(INTERNAL.name(), CATEGORY)).thenReturn(FILENAME);
		when(invoiceFileConfigurationServiceMock.getEncoding(INTERNAL.name(), CATEGORY)).thenReturn(ENCODING);
		when(internalInvoiceCreatorMock.createFileHeader()).thenReturn(FILE_HEADER);
		when(internalInvoiceCreatorMock.createInvoiceData(any())).thenReturn(INVOICE_DATA);
		when(internalInvoiceCreatorMock.getProcessableType()).thenReturn(INTERNAL);
		when(internalInvoiceCreatorMock.getProcessableCategory()).thenReturn(CATEGORY);

		// Act
		service.createFiles(MUNICIPALITY_ID);

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID);
		verify(externalInvoiceCreatorMock).getProcessableType();
		verify(externalInvoiceCreatorMock).getProcessableCategory();
		verify(internalInvoiceCreatorMock).getProcessableType();
		verify(internalInvoiceCreatorMock).getProcessableCategory();
		verify(internalInvoiceCreatorMock).createFileHeader();
		verify(internalInvoiceCreatorMock).createInvoiceData(entity);
		verify(billingRecordRepositoryMock).save(billingRecordArgumentCaptor.capture());
		verify(invoiceFileRepositoryMock).save(invoiceFileArgumentCaptor.capture());
		verifyNoMoreInterationsOnMocks();

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
	void createExternalBillingFilesWhenInvalidEntitiesExists() throws Exception {
		// Arrange
		final var externalFileName = "externalFileName";
		final var externalFileHeader = "externalFileHeader".getBytes();
		final var externalInvoiceData = "externalInvoiceData".getBytes();
		final var invalidExternalEntity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL, MUNICIPALITY_ID);
		final var externalEntity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL, MUNICIPALITY_ID);

		when(billingRecordRepositoryMock.findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID)).thenReturn(List.of(
			invalidExternalEntity,
			externalEntity));
		when(invoiceFileConfigurationServiceMock.getInvoiceFileNameBy(EXTERNAL.name(), CATEGORY)).thenReturn(externalFileName);
		when(invoiceFileConfigurationServiceMock.getEncoding(EXTERNAL.name(), CATEGORY)).thenReturn(ENCODING);
		when(externalInvoiceCreatorMock.createFileHeader()).thenReturn(externalFileHeader);
		when(externalInvoiceCreatorMock.createInvoiceData(externalEntity)).thenReturn(externalInvoiceData);
		when(externalInvoiceCreatorMock.createInvoiceData(invalidExternalEntity)).thenThrow(Problem.valueOf(INTERNAL_SERVER_ERROR));
		when(externalInvoiceCreatorMock.getProcessableType()).thenReturn(EXTERNAL);
		when(externalInvoiceCreatorMock.getProcessableCategory()).thenReturn(CATEGORY);

		// Act
		service.createFiles(MUNICIPALITY_ID);

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID);

		verify(internalInvoiceCreatorMock, atMostOnce()).getProcessableCategory();
		verify(internalInvoiceCreatorMock, atMostOnce()).getProcessableType();
		verifyInvoiceCreatorMock(externalInvoiceCreatorMock, invalidExternalEntity, externalEntity);
		verify(billingRecordRepositoryMock).save(billingRecordArgumentCaptor.capture());
		verify(invoiceFileRepositoryMock).save(invoiceFileArgumentCaptor.capture());
		verify(messagingServiceMock).sendCreationErrorMail(eq(MUNICIPALITY_ID), creationErrorArgumentCaptor.capture());
		verifyNoMoreInterationsOnMocks();

		assertThat(billingRecordArgumentCaptor.getAllValues()).satisfiesOnlyOnce(billingEntity -> {
			assertThat(billingEntity).usingRecursiveComparison().ignoringFields("status").isEqualTo(externalEntity);
			assertThat(billingEntity.getStatus()).isEqualTo(INVOICED);
		});

		assertThat(invoiceFileArgumentCaptor.getAllValues()).satisfiesOnlyOnce(fileEntity -> {
			assertThat(fileEntity.getType()).isEqualTo(EXTERNAL.name());
			assertThat(fileEntity.getName()).isEqualTo(externalFileName);
			assertThat(fileEntity.getContent()).isEqualTo(new String(ArrayUtils.addAll(externalFileHeader, externalInvoiceData), StandardCharsets.UTF_8));
			assertThat(fileEntity.getStatus()).isEqualTo(GENERATED);
		});

		assertThat(creationErrorArgumentCaptor.getValue()).hasSize(1)
			.extracting(InvoiceFileError::getEntityId, InvoiceFileError::getMessage)
			.containsExactly(tuple(invalidExternalEntity.getId(), "Internal Server Error"));
	}

	@Test
	void createInternalBillingFilesWhenInvalidEntitiesExists() throws Exception {
		// Arrange
		final var internalFileName = "internalFileName";
		final var internalFileHeader = "internalFileHeader".getBytes();
		final var internalInvoiceData = "internalInvoiceData".getBytes();
		final var invalidInternalEntity = createBillingRecordEntity(randomUUID().toString(), INTERNAL, MUNICIPALITY_ID);
		final var internalEntity = createBillingRecordEntity(randomUUID().toString(), INTERNAL, MUNICIPALITY_ID);

		when(billingRecordRepositoryMock.findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID)).thenReturn(List.of(
			invalidInternalEntity,
			internalEntity));
		when(invoiceFileConfigurationServiceMock.getInvoiceFileNameBy(INTERNAL.name(), CATEGORY)).thenReturn(internalFileName);
		when(invoiceFileConfigurationServiceMock.getEncoding(INTERNAL.name(), CATEGORY)).thenReturn(ENCODING);
		when(internalInvoiceCreatorMock.createFileHeader()).thenReturn(internalFileHeader);
		when(internalInvoiceCreatorMock.createInvoiceData(internalEntity)).thenReturn(internalInvoiceData);
		when(internalInvoiceCreatorMock.createInvoiceData(invalidInternalEntity)).thenThrow(Problem.valueOf(INTERNAL_SERVER_ERROR));
		when(internalInvoiceCreatorMock.getProcessableType()).thenReturn(INTERNAL);
		when(internalInvoiceCreatorMock.getProcessableCategory()).thenReturn(CATEGORY);
		when(externalInvoiceCreatorMock.getProcessableType()).thenReturn(EXTERNAL);
		when(externalInvoiceCreatorMock.getProcessableCategory()).thenReturn(CATEGORY);

		// Act
		service.createFiles(MUNICIPALITY_ID);

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID);

		verify(externalInvoiceCreatorMock, atMostOnce()).getProcessableCategory();
		verify(externalInvoiceCreatorMock, atMostOnce()).getProcessableType();
		verifyInvoiceCreatorMock(internalInvoiceCreatorMock, invalidInternalEntity, internalEntity);
		verify(billingRecordRepositoryMock).save(billingRecordArgumentCaptor.capture());
		verify(invoiceFileRepositoryMock).save(invoiceFileArgumentCaptor.capture());
		verify(messagingServiceMock).sendCreationErrorMail(eq(MUNICIPALITY_ID), creationErrorArgumentCaptor.capture());
		verifyNoMoreInterationsOnMocks();

		assertThat(billingRecordArgumentCaptor.getAllValues()).satisfiesOnlyOnce(billingEntity -> {
			assertThat(billingEntity).usingRecursiveComparison().ignoringFields("status").isEqualTo(internalEntity);
			assertThat(billingEntity.getStatus()).isEqualTo(INVOICED);
		});

		assertThat(invoiceFileArgumentCaptor.getAllValues()).satisfiesOnlyOnce(fileEntity -> {
			assertThat(fileEntity.getType()).isEqualTo(INTERNAL.name());
			assertThat(fileEntity.getName()).isEqualTo(internalFileName);
			assertThat(fileEntity.getContent()).isEqualTo(new String(ArrayUtils.addAll(internalFileHeader, internalInvoiceData), StandardCharsets.UTF_8));
			assertThat(fileEntity.getStatus()).isEqualTo(GENERATED);
		});

		assertThat(creationErrorArgumentCaptor.getValue()).hasSize(1)
			.extracting(InvoiceFileError::getEntityId, InvoiceFileError::getMessage)
			.containsExactly(tuple(invalidInternalEntity.getId(), "Internal Server Error"));
	}

	@Test
	void createBillingFilesWhenOnlyInvalidEntitiesExists() throws Exception {
		// Arrange
		final var internalFileHeader = "internalFileHeader".getBytes();
		final var invalidInternalEntity = createBillingRecordEntity(randomUUID().toString(), INTERNAL, MUNICIPALITY_ID);

		when(billingRecordRepositoryMock.findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID)).thenReturn(List.of(invalidInternalEntity));
		when(externalInvoiceCreatorMock.getProcessableType()).thenReturn(EXTERNAL);
		when(externalInvoiceCreatorMock.getProcessableCategory()).thenReturn(CATEGORY);
		when(internalInvoiceCreatorMock.getProcessableType()).thenReturn(INTERNAL);
		when(internalInvoiceCreatorMock.getProcessableCategory()).thenReturn(CATEGORY);
		when(internalInvoiceCreatorMock.createFileHeader()).thenReturn(internalFileHeader);
		when(internalInvoiceCreatorMock.createInvoiceData(invalidInternalEntity)).thenThrow(Problem.valueOf(INTERNAL_SERVER_ERROR));
		when(invoiceFileConfigurationServiceMock.getEncoding(INTERNAL.name(), CATEGORY)).thenReturn(ENCODING);

		// Act
		service.createFiles(MUNICIPALITY_ID);

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID);

		verify(externalInvoiceCreatorMock, atMostOnce()).getProcessableCategory();
		verify(externalInvoiceCreatorMock, atMostOnce()).getProcessableType();
		verify(internalInvoiceCreatorMock).getProcessableCategory();
		verify(internalInvoiceCreatorMock).getProcessableType();
		verify(internalInvoiceCreatorMock).createInvoiceData(invalidInternalEntity);
		verify(invoiceFileConfigurationServiceMock).getInvoiceFileNameBy(INTERNAL.name(), CATEGORY);
		verify(messagingServiceMock).sendCreationErrorMail(eq(MUNICIPALITY_ID), creationErrorArgumentCaptor.capture());
		verifyNoMoreInterationsOnMocks();

		assertThat(creationErrorArgumentCaptor.getValue()).hasSize(1)
			.extracting(InvoiceFileError::getEntityId, InvoiceFileError::getMessage)
			.containsExactly(tuple(invalidInternalEntity.getId(), "Internal Server Error"));
	}

	@Test
	void createBillingFilesWhenMajorExceptionOccurs() throws Exception {
		// Arrange
		final var entity = createBillingRecordEntity(randomUUID().toString(), INTERNAL, MUNICIPALITY_ID);

		when(internalInvoiceCreatorMock.getProcessableType()).thenReturn(INTERNAL);
		when(internalInvoiceCreatorMock.getProcessableCategory()).thenReturn(CATEGORY);
		when(billingRecordRepositoryMock.findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID)).thenReturn(List.of(entity));
		// Not mocking createFileHeader on CreatorMock will cause NPE to be thrown on
		// outputStream.write(invoiceCreator.createFileHeader())

		// Act
		service.createFiles(MUNICIPALITY_ID);

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID);
		verify(externalInvoiceCreatorMock).getProcessableType();
		verify(externalInvoiceCreatorMock).getProcessableCategory();
		verify(internalInvoiceCreatorMock).getProcessableType();
		verify(internalInvoiceCreatorMock).getProcessableCategory();
		verify(internalInvoiceCreatorMock).createFileHeader();
		verify(invoiceFileConfigurationServiceMock).getInvoiceFileNameBy(INTERNAL.name(), CATEGORY);
		verify(invoiceFileConfigurationServiceMock).getEncoding(INTERNAL.name(), CATEGORY);
		verify(messagingServiceMock).sendCreationErrorMail(eq(MUNICIPALITY_ID), creationErrorArgumentCaptor.capture());
		verifyNoMoreInterationsOnMocks();

		assertThat(creationErrorArgumentCaptor.getValue()).hasSize(1)
			.extracting(InvoiceFileError::getEntityId, InvoiceFileError::getMessage)
			.containsExactly(tuple(null, "NullPointerException: Cannot read the array length because \"b\" is null occurred when generating file content"));
	}

	@Test
	void createBillingFilesWhenNoCorrespondingCreatorForTypeExists() {
		// Arrange
		final var entity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL, MUNICIPALITY_ID);

		when(billingRecordRepositoryMock.findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID)).thenReturn(List.of(entity));

		// Act
		service.createFiles(MUNICIPALITY_ID);

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID);
		verify(externalInvoiceCreatorMock).getProcessableType();
		verify(externalInvoiceCreatorMock).getProcessableCategory();
		verify(internalInvoiceCreatorMock).getProcessableType();
		verify(internalInvoiceCreatorMock).getProcessableCategory();
		verify(messagingServiceMock).sendCreationErrorMail(eq(MUNICIPALITY_ID), creationErrorArgumentCaptor.capture());
		verifyNoMoreInterationsOnMocks();

		assertThat(creationErrorArgumentCaptor.getValue()).hasSize(1)
			.extracting(InvoiceFileError::getEntityId, InvoiceFileError::getMessage)
			.containsExactly(tuple(entity.getId(), "No corresponding invoice creator implementation could be found for type: 'EXTERNAL' and category: 'category'"));
	}

	@Test
	void createBillingFilesWhenNoCorrespondingCreatorForCategoryExists() {
		// Arrange
		final var entity = createBillingRecordEntity(randomUUID().toString(), EXTERNAL, MUNICIPALITY_ID);

		when(billingRecordRepositoryMock.findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID)).thenReturn(List.of(entity));
		when(externalInvoiceCreatorMock.getProcessableType()).thenReturn(EXTERNAL);

		// Act
		service.createFiles(MUNICIPALITY_ID);

		// Verify and assert
		verify(billingRecordRepositoryMock).findAllByStatusAndMunicipalityId(APPROVED, MUNICIPALITY_ID);
		verify(externalInvoiceCreatorMock).getProcessableType();
		verify(externalInvoiceCreatorMock).getProcessableCategory();
		verify(internalInvoiceCreatorMock).getProcessableType();
		verify(internalInvoiceCreatorMock).getProcessableCategory();
		verify(messagingServiceMock).sendCreationErrorMail(eq(MUNICIPALITY_ID), creationErrorArgumentCaptor.capture());
		verifyNoMoreInterationsOnMocks();

		assertThat(creationErrorArgumentCaptor.getValue()).hasSize(1)
			.extracting(InvoiceFileError::getEntityId, InvoiceFileError::getMessage)
			.containsExactly(tuple(entity.getId(), "No corresponding invoice creator implementation could be found for type: 'EXTERNAL' and category: 'category'"));
	}

	private void verifyInvoiceCreatorMock(final InvoiceCreator invoiceCreatorMock, final BillingRecordEntity invalidInternalEntity, final BillingRecordEntity internalEntity) throws IOException {
		verify(invoiceCreatorMock).getProcessableType();
		verify(invoiceCreatorMock).getProcessableCategory();
		verify(invoiceCreatorMock).createFileHeader();
		verify(invoiceCreatorMock).createInvoiceData(invalidInternalEntity);
		verify(invoiceCreatorMock).createInvoiceData(internalEntity);
	}

	private void verifyNoMoreInterationsOnMocks() {
		verifyNoMoreInteractions(
			billingRecordRepositoryMock,
			invoiceFileRepositoryMock,
			externalInvoiceCreatorMock,
			internalInvoiceCreatorMock,
			invoiceFileConfigurationServiceMock,
			messagingServiceMock,
			uploadGatewayMock);
	}

	private static BillingRecordEntity createBillingRecordEntity(String id, Type type, String municipalityId) {
		return BillingRecordEntity.create()
			.withId(id)
			.withCategory(CATEGORY)
			.withStatus(APPROVED)
			.withType(type)
			.withMunicipalityId(municipalityId);
	}
}
