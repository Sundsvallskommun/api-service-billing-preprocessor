package se.sundsvall.billingpreprocessor.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AccountInformationTest {

	@Test
	void testBean() {
		assertThat(AccountInformation.class, allOf(
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
		final var amount = BigDecimal.valueOf(1234.56d);
		final var costCenter = "costCenter";
		final var counterpart = "counterpart";
		final var department = "department";
		final var article = "article";
		final var project = "project";
		final var subaccount = "subaccount";

		final var bean = AccountInformation.create()
			.withAccuralKey(accuralKey)
			.withActivity(activity)
			.withAmount(amount)
			.withCostCenter(costCenter)
			.withCounterpart(counterpart)
			.withDepartment(department)
			.withArticle(article)
			.withProject(project)
			.withSubaccount(subaccount);

		assertThat(bean.getAccuralKey()).isEqualTo(accuralKey);
		assertThat(bean.getActivity()).isEqualTo(activity);
		assertThat(bean.getAmount()).isEqualTo(amount);
		assertThat(bean.getCostCenter()).isEqualTo(costCenter);
		assertThat(bean.getCounterpart()).isEqualTo(counterpart);
		assertThat(bean.getDepartment()).isEqualTo(department);
		assertThat(bean.getArticle()).isEqualTo(article);
		assertThat(bean.getProject()).isEqualTo(project);
		assertThat(bean.getSubaccount()).isEqualTo(subaccount);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new AccountInformation()).hasAllNullFieldsOrProperties();
		assertThat(AccountInformation.create()).hasAllNullFieldsOrProperties();
	}
}
