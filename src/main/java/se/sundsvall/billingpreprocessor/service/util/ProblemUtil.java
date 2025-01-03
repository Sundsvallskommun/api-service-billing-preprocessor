package se.sundsvall.billingpreprocessor.service.util;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import java.util.function.Supplier;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

public final class ProblemUtil {
	private ProblemUtil() {}

	public static Supplier<ThrowableProblem> createInternalServerErrorProblem(String message) {
		return createProblem(INTERNAL_SERVER_ERROR, message);
	}

	public static Supplier<ThrowableProblem> createProblem(Status status, String message) {
		return () -> Problem.valueOf(status, message);
	}
}
