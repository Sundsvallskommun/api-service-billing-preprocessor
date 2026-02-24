package se.sundsvall.billingpreprocessor.service.util;

import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public final class ProblemUtil {
	private ProblemUtil() {}

	public static Supplier<ThrowableProblem> createInternalServerErrorProblem(String message) {
		return createProblem(INTERNAL_SERVER_ERROR, message);
	}

	public static Supplier<ThrowableProblem> createProblem(HttpStatus status, String message) {
		return () -> Problem.valueOf(status, message);
	}
}
