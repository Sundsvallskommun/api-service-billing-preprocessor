package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Random;
import org.junit.jupiter.api.Test;

class InvoiceAccountingRowTest {

	@Test
	void testBean() {
		assertThat(InvoiceAccountingRow.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var accuralKey = "accuralKey";
		final var activity = "activity";
		final var costCenter = "costCenter";
		final var counterpart = "counterpart";
		final var object = "object";
		final var operation = "operation";
		final var project = "project";
		final var subaccount = "subaccount";
		final var totalAmount = new Random().nextFloat();

		final var bean = InvoiceAccountingRow.create()
			.withAccuralKey(accuralKey)
			.withActivity(activity)
			.withCostCenter(costCenter)
			.withCounterpart(counterpart)
			.withObject(object)
			.withOperation(operation)
			.withProject(project)
			.withSubAccount(subaccount)
			.withAmount(totalAmount);

		assertThat(bean.getAccuralKey()).isEqualTo(accuralKey);
		assertThat(bean.getActivity()).isEqualTo(activity);
		assertThat(bean.getCostCenter()).isEqualTo(costCenter);
		assertThat(bean.getCounterpart()).isEqualTo(counterpart);
		assertThat(bean.getObject()).isEqualTo(object);
		assertThat(bean.getOperation()).isEqualTo(operation);
		assertThat(bean.getProject()).isEqualTo(project);
		assertThat(bean.getSubAccount()).isEqualTo(subaccount);
		assertThat(bean.getAmount()).isEqualTo(totalAmount);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new InvoiceAccountingRow()).hasAllNullFieldsOrProperties();
		assertThat(InvoiceAccountingRow.create()).hasAllNullFieldsOrProperties();
	}
}
