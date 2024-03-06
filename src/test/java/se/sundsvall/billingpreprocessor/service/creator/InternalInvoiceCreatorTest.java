package se.sundsvall.billingpreprocessor.service.creator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorConfig.INTERNAL_INVOICE_BUILDER;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.beanio.StreamFactory;
import org.beanio.builder.StreamBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.billingpreprocessor.integration.db.model.AccountInformationEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType;

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

	@Autowired
	private InternalInvoiceCreator creator;

	private static final StreamFactory FACTORY = StreamFactory.newInstance();

	@BeforeAll
	static void setUpFactory(@Qualifier(INTERNAL_INVOICE_BUILDER) StreamBuilder builder) {
		FACTORY.define(builder);
	}

	@Test
	void toBytesFromEmptyList() throws Exception {
		assertThat(creator.toBytes(Collections.emptyList())).isEmpty();
	}

	@Test
	void toBytesWhenInvoiceMissing() throws Exception {
		final var input = List.of(createbillingRecordEntity().withInvoice(null));
		final var e = assertThrows(ThrowableProblem.class, () -> creator.toBytes(input));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Invoice is not present");
	}

	@Test
	void toBytesFromEntity() throws Exception {
		final var result = creator.toBytes(List.of(createbillingRecordEntity()));

		final var expected = getResource("validation/expected_internal_format.txt")
			.replace("yyMMdd", LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")));

		assertThat(new String(result, StandardCharsets.UTF_8)).isEqualTo(expected);
	}

	private String getResource(final String fileName) throws IOException, URISyntaxException {
		return Files.readString(Paths.get(getClass().getClassLoader().getResource(fileName).toURI()), StandardCharsets.UTF_8);
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
