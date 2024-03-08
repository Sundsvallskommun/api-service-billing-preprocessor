package se.sundsvall.billingpreprocessor.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createInternalServerErrorProblem;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createProblem;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Status;

class ProblemUtilTest {

	@Test
	void testCreateInternalServerErrorProblem() {
		final var message = RandomStringUtils.randomAlphabetic(10);
		final var problem = createInternalServerErrorProblem(message).get();

		assertThat(problem).isNotNull();
		assertThat(problem.getStatus()).isEqualTo(Status.INTERNAL_SERVER_ERROR);
		assertThat(problem.getMessage()).isEqualTo("Internal Server Error: %s".formatted(message));
	}

	@Test
	void testCreateProblem() {
		final var status = Status.I_AM_A_TEAPOT;
		final var message = RandomStringUtils.randomAlphabetic(10);
		final var problem = createProblem(status, message).get();

		assertThat(problem).isNotNull();
		assertThat(problem.getStatus()).isEqualTo(status);
		assertThat(problem.getMessage()).isEqualTo("%s: %s".formatted(status.getReasonPhrase(), message));
	}
}
