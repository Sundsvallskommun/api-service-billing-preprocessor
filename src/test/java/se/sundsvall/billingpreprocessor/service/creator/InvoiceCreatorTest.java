package se.sundsvall.billingpreprocessor.service.creator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

@SpringBootTest(classes = Application.class, webEnvironment = MOCK)
@ActiveProfiles("junit")
class InvoiceCreatorTest {

	@Autowired
	List<InvoiceCreator> creators;

	@Test
	/**
	 * Validate that only one invoice creator implementation handles one explicit combination of type and category
	 */
	void noDuplicateCreatorExists() {
		final Map<Type, List<String>> allCombos = new HashMap<>();

		creators.forEach(creator -> {
			creator.getProcessableTypes().forEach(type -> {
				if (!allCombos.containsKey(type)) {
					allCombos.put(type, new ArrayList<>());
				}
				allCombos.get(type).addAll(creator.getProcessableCategories());
			});
		});

		// Evaluate that only one creator have the unique type/category combo
		allCombos.entrySet().forEach(entry -> {
			assertThat(entry.getValue())
				.withFailMessage("Two or more invoice creator implementations is overlapping in handled types and/or handled categories.")
				.containsOnlyOnceElementsOf(entry.getValue());
		});
	}
}
