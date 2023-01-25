package se.sundsvall.billingpreprocessor.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import org.junit.jupiter.api.Test;

class AccountInformationEmbeddableTest {

	@Test
	void testBean() {
		assertThat(AccountInformationEmbeddable.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {
		final var accuralKey = "accuralKey";
		final var activity = "activity";
		final var costCenter = "costCenter";
		final var counterpart = "counterpart";
		final var department = "department";
		final var article = "article";
		final var project = "project";
		final var subaccount = "subaccount";

		final var embeddable = AccountInformationEmbeddable.create()
			.withAccuralKey(accuralKey)
			.withActivity(activity)
			.withCostCenter(costCenter)
			.withCounterpart(counterpart)
			.withDepartment(department)
			.withArticle(article)
			.withProject(project)
			.withSubaccount(subaccount);

		assertThat(embeddable).hasNoNullFieldsOrProperties();
		assertThat(embeddable.getAccuralKey()).isEqualTo(accuralKey);
		assertThat(embeddable.getActivity()).isEqualTo(activity);
		assertThat(embeddable.getCostCenter()).isEqualTo(costCenter);
		assertThat(embeddable.getCounterpart()).isEqualTo(counterpart);
		assertThat(embeddable.getDepartment()).isEqualTo(department);
		assertThat(embeddable.getArticle()).isEqualTo(article);
		assertThat(embeddable.getProject()).isEqualTo(project);
		assertThat(embeddable.getSubaccount()).isEqualTo(subaccount);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(AccountInformationEmbeddable.create()).hasAllNullFieldsOrProperties();
		assertThat(new AccountInformationEmbeddable()).hasAllNullFieldsOrProperties();
	}
}
