package se.sundsvall.billingpreprocessor.service.util;

import static java.util.Optional.ofNullable;
public class StringUtil {
	private StringUtil() {}

	public static String removeHyphensFromNumericString(String value) {
		return ofNullable(value)
			.filter(s -> s.matches("[0-9-]+"))
			.map(s -> s.replaceAll("\\D", "")) // Replace all non digits with emtpy string
			.orElse(value);
	}
}
