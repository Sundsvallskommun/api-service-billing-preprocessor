package se.sundsvall.billingpreprocessor.service.util;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import java.util.function.Supplier;

import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

public class ProblemUtil {
	private ProblemUtil() {}

	public static Supplier<ThrowableProblem> createProblem(String message) {
		return () -> Problem.valueOf(INTERNAL_SERVER_ERROR, message);
	}
}
