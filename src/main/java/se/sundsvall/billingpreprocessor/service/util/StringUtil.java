package se.sundsvall.billingpreprocessor.service.util;

import static java.util.Optional.ofNullable;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
	private StringUtil() {}

	public static String formatLegalId(String value) {
		return ofNullable(value)
			.map(StringUtil::removeHyphensFromNumericString)
			.map(String::trim)
			.map(s -> StringUtils.right(s, 10)) // Trim away century part
			.orElse(value);

	}

	private static String removeHyphensFromNumericString(String value) {
		return ofNullable(value)
			.filter(s -> s.matches("[0-9-]+"))
			.map(s -> s.replaceAll("\\D", "")) // Replace all non digits with emtpy string
			.orElse(null);
	}
}
