package se.sundsvall.billingpreprocessor.service.creator;

import static org.apache.commons.text.StringEscapeUtils.unescapeJava;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
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
	private static final LocalDate DATE = LocalDate.of(2024, 3, 06);
	private static final LocalDate DUE_DATE = LocalDate.of(2024, 3, 30);
	private static final String OUR_REFERENCE = "Johnny Bråttom";
	private static final String REFERENCE_ID = "referenceId";
	private static final float INVOICE_TOTAL_AMOUNT = 1500f;

	// Invoice row constants
	private static final float COST_PER_UNIT = 1500f;
	private static final String DESCRIPTION = "Uppdrag: ABC-123";
	private static final int QUANTITY = 1;

	// Account information constants
	private static final String ACCURAL_KEY = "117";
	private static final String ACTIVITY = "5756";
	private static final String ARTICLE = "117";
	private static final String COST_CENTER = "15800100";
	private static final String COUNTERPART = "116";
	private static final String DEPARTMENT = "920360";
	private static final String PROJECT = "11041";
	private static final String SUBACCOUNT = "936300";

	@MockBean
	private InvoiceFileConfigurationRepository invoiceFileConfigurationRepositoryMock;

	@Autowired
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
		final var result = creator.createFileHeader();
		final var expected = getResource("validation/internal_header_expected_format.txt");

		assertThat(new String(result, StandardCharsets.UTF_8)).isEqualTo(expected);
	}

	@Test
	void createInvoiceDataFromNull() throws Exception {
		assertThat(creator.createInvoiceData(null)).isEmpty();
	}

	@Test
	void createInvoiceDataWhenInvoiceMissing() throws Exception {
		final var input = createbillingRecordEntity().withInvoice(null);
		final var e = assertThrows(ThrowableProblem.class, () -> creator.createInvoiceData(input));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Invoice is not present");
	}

	@Test
	void createInvoiceDataFromEntity() throws Exception {
		final var result = creator.createInvoiceData(createbillingRecordEntity());
		final var expected = getResource("validation/internal_invoicedata_expected_format.txt");

		assertThat(new String(result, StandardCharsets.UTF_8)).isEqualTo(expected);
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
			.withReferenceId(REFERENCE_ID)
			.withTotalAmount(INVOICE_TOTAL_AMOUNT);

		return invoiceEntity.withInvoiceRows(List.of(createInvoiceRowEntity(1, invoiceEntity)));
	}

	private static InvoiceRowEntity createInvoiceRowEntity(int id, InvoiceEntity invoiceEntity) {
		final var invoiceRowEntity = InvoiceRowEntity.create()
			.withAccountInformation(createAccountInformationEmbeddable())
			.withCostPerUnit(COST_PER_UNIT)
			.withId(id)
			.withInvoice(invoiceEntity)
			.withQuantity(QUANTITY)
			.withTotalAmount(COST_PER_UNIT * QUANTITY);

		return invoiceRowEntity.withDescriptions(List.of(
			createDescriptionEntity(1, invoiceRowEntity, STANDARD, DESCRIPTION)));
	}

	private static DescriptionEntity createDescriptionEntity(int id, InvoiceRowEntity invoiceRowEntity, DescriptionType type, String text) {
		return DescriptionEntity.create()
			.withId(id)
			.withInvoiceRow(invoiceRowEntity)
			.withText(text)
			.withType(type);
	}

	private static AccountInformationEmbeddable createAccountInformationEmbeddable() {
		return AccountInformationEmbeddable.create()
			.withAccuralKey(ACCURAL_KEY)
			.withActivity(ACTIVITY)
			.withArticle(ARTICLE)
			.withCostCenter(COST_CENTER)
			.withCounterpart(COUNTERPART)
			.withDepartment(DEPARTMENT)
			.withProject(PROJECT)
			.withSubaccount(SUBACCOUNT);
	}
}
