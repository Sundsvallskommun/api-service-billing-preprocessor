package se.sundsvall.billingpreprocessor.apptest;

import static java.util.List.of;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/CustomerInvoiceRecordsIT/", classes = Application.class)
class CustomerInvoiceRecordsIT extends AbstractAppTest {

	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";
	private static final List<String> CONTENT_TYPE_JSON = of(APPLICATION_JSON_VALUE);

	@Test
	void test01_createCustomerInvoiceRecordForExternalPrivatePerson() {
		final var location = setupCall()
			.withServicePath("/2281/billingrecords")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, of("^/2281/billingrecords/(.*)$"))
			.sendRequestAndVerifyResponse()
			.getResponseHeaders()
			.getLocation();

		// Execute get on location to verify saved values
		setupCall()
			.withServicePath(location.getPath())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_createCustomerInvoiceRecordForExternalOrganization() {
		final var location = setupCall()
			.withServicePath("/2281/billingrecords")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, of("^/2281/billingrecords/(.*)$"))
			.sendRequestAndVerifyResponse()
			.getResponseHeaders()
			.getLocation();

		// Execute get on location to verify saved values
		setupCall()
			.withServicePath(location.getPath())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_createCustomerInvoiceRecordForInternalInvoice() {
		final var location = setupCall()
			.withServicePath("/2281/billingrecords")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, of("^/2281/billingrecords/(.*)$"))
			.sendRequestAndVerifyResponse()
			.getResponseHeaders()
			.getLocation();

		// Execute get on location to verify saved values
		setupCall()
			.withServicePath(location.getPath())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
