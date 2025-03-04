package se.sundsvall.billingpreprocessor.service.creator;

import static org.apache.commons.text.StringEscapeUtils.unescapeJava;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.AccountInformationEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType;
import se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorProperties;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@ActiveProfiles("junit")
class InternalInvoiceCreatorTest {
	// Invoice constants
	private static final String CUSTOMER_ID = "16";
	private static final String CUSTOMER_REFERENCE = "5ABC30DEF";
	private static final String INVOICE_DESCRIPTION = "Extra utbetalning - Direktinsättning";
	private static final LocalDate DATE = LocalDate.of(2024, 3, 6);
	private static final LocalDate DUE_DATE = LocalDate.of(2024, 3, 30);
	private static final String OUR_REFERENCE = "Johnny Bråttom";
	private static final BigDecimal INVOICE_TOTAL_AMOUNT = BigDecimal.valueOf(1500d);

	// Invoice row constants
	private static final BigDecimal COST_PER_UNIT = BigDecimal.valueOf(1500d);
	private static final String DESCRIPTION = "Uppdrag: ABC-123";
	private static final String DETAILED_DESCRIPTION = "En mer detaljerad fakturaradsbeskrivning";
	private static final BigDecimal QUANTITY = BigDecimal.ONE;

	// Account information constants (post 1)
	private static final BigDecimal ACCOUNTING_AMOUNT_1 = BigDecimal.valueOf(300d);
	private static final String ACCURAL_KEY_1 = "117";
	private static final String ACTIVITY_1 = "5756";
	private static final String ARTICLE_1 = "117";
	private static final String COST_CENTER_1 = "15800100";
	private static final String COUNTERPART_1 = "116";
	private static final String DEPARTMENT_1 = "920360";
	private static final String PROJECT_1 = "11041";
	private static final String SUBACCOUNT_1 = "936300";

	// Account information constants (post 2)
	private static final BigDecimal ACCOUNTING_AMOUNT_2 = BigDecimal.valueOf(1200d);
	private static final String ACCURAL_KEY_2 = "119";
	private static final String ACTIVITY_2 = "5546";
	private static final String ARTICLE_2 = "119";
	private static final String COST_CENTER_2 = "16300200";
	private static final String COUNTERPART_2 = "114";
	private static final String DEPARTMENT_2 = "902011";
	private static final String PROJECT_2 = "11042";
	private static final String SUBACCOUNT_2 = "554800";

	@MockitoBean
	private InvoiceFileConfigurationRepository invoiceFileConfigurationRepositoryMock;

	@Autowired
	@Qualifier(value = "internalInvoiceCreator")
	private InternalInvoiceCreator creator;

	@Autowired
	private InvoiceCreatorProperties properties;

	@Test
	void validateImplementation() {
		assertThat(creator).isInstanceOf(InvoiceCreator.class);
	}

	@Test
	void getProcessableCategory() {
		final var category = "category";
		final var config = InvoiceFileConfigurationEntity.create().withCategoryTag(category);

		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("InternalInvoiceCreator")).thenReturn(Optional.of(config));

		assertThat(creator.getProcessableCategory()).isEqualTo(category);
		verify(invoiceFileConfigurationRepositoryMock).findByCreatorName("InternalInvoiceCreator");
	}

	@Test
	void getProcessableType() {
		final var type = INTERNAL;
		final var config = InvoiceFileConfigurationEntity.create().withType(type.toString());

		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("InternalInvoiceCreator")).thenReturn(Optional.of(config));

		assertThat(creator.getProcessableType()).isEqualTo(type);
		verify(invoiceFileConfigurationRepositoryMock).findByCreatorName("InternalInvoiceCreator");
	}

	@Test
	void getProcessablesWhenNoConfigFound() {
		final var e1 = assertThrows(ThrowableProblem.class, () -> creator.getProcessableCategory());
		final var e2 = assertThrows(ThrowableProblem.class, () -> creator.getProcessableType());

		assertThat(List.of(e1, e2)).allSatisfy(e -> {
			assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
			assertThat(e.getMessage()).isEqualTo("Internal Server Error: No configuration present for invoice creator with name InternalInvoiceCreator");
		});
	}

	@Test
	void createFileHeader() throws Exception {
		final var config = InvoiceFileConfigurationEntity.create().withEncoding(StandardCharsets.ISO_8859_1.name());
		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("InternalInvoiceCreator")).thenReturn(Optional.of(config));

		final var result = creator.createFileHeader();
		final var expected = getResource("validation/internal_header_expected_format.txt");

		assertThat(new String(result, StandardCharsets.ISO_8859_1)).isEqualTo(expected);
	}

	@Test
	void createInvoiceDataFromNull() throws Exception {
		assertThat(creator.createInvoiceData(null)).isEmpty();
	}

	@Test
	void createInvoiceDataWhenInvoiceMissing() {
		final var config = InvoiceFileConfigurationEntity.create().withEncoding(StandardCharsets.ISO_8859_1.name());
		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("InternalInvoiceCreator")).thenReturn(Optional.of(config));

		final var input = createbillingRecordEntity().withInvoice(null);
		final var e = assertThrows(ThrowableProblem.class, () -> creator.createInvoiceData(input));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Invoice is not present");
	}

	@Test
	void createInvoiceDataFromEntity() throws Exception {
		final var config = InvoiceFileConfigurationEntity.create().withEncoding(StandardCharsets.ISO_8859_1.name());
		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("InternalInvoiceCreator")).thenReturn(Optional.of(config));

		final var result = creator.createInvoiceData(createbillingRecordEntity());
		final var expected = getResource("validation/internal_invoicedata_expected_format.txt");

		assertThat(new String(result, StandardCharsets.ISO_8859_1)).isEqualTo(expected);
	}

	private String getResource(final String fileName) throws IOException, URISyntaxException {
		return Files.readString(Paths.get(getClass().getClassLoader().getResource(fileName).toURI()), StandardCharsets.UTF_8)
			.replaceAll(System.lineSeparator(), unescapeJava(properties.recordTerminator()));
	}

	private static BillingRecordEntity createbillingRecordEntity() {
		final var billingRecordEntity = BillingRecordEntity.create();

		return billingRecordEntity.withInvoice(createInvoiceEntity(billingRecordEntity));
	}

	private static InvoiceEntity createInvoiceEntity(BillingRecordEntity billingRecordEntity) {
		final var invoiceEntity = InvoiceEntity.create()
			.withBillingRecord(billingRecordEntity)
			.withCustomerId(CUSTOMER_ID)
			.withCustomerReference(CUSTOMER_REFERENCE)
			.withDescription(INVOICE_DESCRIPTION)
			.withDate(DATE)
			.withDueDate(DUE_DATE)
			.withOurReference(OUR_REFERENCE)
			.withTotalAmount(INVOICE_TOTAL_AMOUNT);

		return invoiceEntity.withInvoiceRows(List.of(createInvoiceRowEntity(1, invoiceEntity)));
	}

	private static InvoiceRowEntity createInvoiceRowEntity(int id, InvoiceEntity invoiceEntity) {
		final var invoiceRowEntity = InvoiceRowEntity.create()
			.withAccountInformation(createAccountInformationEmbeddables())
			.withCostPerUnit(COST_PER_UNIT)
			.withId(id)
			.withInvoice(invoiceEntity)
			.withQuantity(QUANTITY)
			.withTotalAmount(COST_PER_UNIT.multiply(QUANTITY));

		return invoiceRowEntity.withDescriptions(List.of(
			createDescriptionEntity(1, invoiceRowEntity, STANDARD, DESCRIPTION),
			createDescriptionEntity(2, invoiceRowEntity, DETAILED, DETAILED_DESCRIPTION)));
	}

	private static DescriptionEntity createDescriptionEntity(int id, InvoiceRowEntity invoiceRowEntity, DescriptionType type, String text) {
		return DescriptionEntity.create()
			.withId(id)
			.withInvoiceRow(invoiceRowEntity)
			.withText(text)
			.withType(type);
	}

	private static List<AccountInformationEmbeddable> createAccountInformationEmbeddables() {
		return List.of(
			AccountInformationEmbeddable.create()
				.withAccuralKey(ACCURAL_KEY_1)
				.withActivity(ACTIVITY_1)
				.withAmount(ACCOUNTING_AMOUNT_1)
				.withArticle(ARTICLE_1)
				.withCostCenter(COST_CENTER_1)
				.withCounterpart(COUNTERPART_1)
				.withDepartment(DEPARTMENT_1)
				.withProject(PROJECT_1)
				.withSubaccount(SUBACCOUNT_1),
			AccountInformationEmbeddable.create()
				.withAccuralKey(ACCURAL_KEY_2)
				.withActivity(ACTIVITY_2)
				.withAmount(ACCOUNTING_AMOUNT_2)
				.withArticle(ARTICLE_2)
				.withCostCenter(COST_CENTER_2)
				.withCounterpart(COUNTERPART_2)
				.withDepartment(DEPARTMENT_2)
				.withProject(PROJECT_2)
				.withSubaccount(SUBACCOUNT_2));
	}
}
