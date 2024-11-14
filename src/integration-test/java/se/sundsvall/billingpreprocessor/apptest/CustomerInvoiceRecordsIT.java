package se.sundsvall.billingpreprocessor.apptest;

import static java.util.List.of;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;

import org.junit.jupiter.api.Test;

import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/CustomerInvoiceRecordsIT/", classes = Application.class)
class CustomerInvoiceRecordsIT extends AbstractAppTest {

	private static final String REQUEST_FILE = "request.json";
	
	@Test
	void test01_createCustomerInvoiceRecordForExternalPrivatePerson() {
		setupCall()
			.withServicePath("/2281/billingrecords")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, of("^/2281/billingrecords/(.*)$"))
			.sendRequestAndVerifyResponse();
	}
	
	@Test
	void test02_createCustomerInvoiceRecordForExternalOrganization() {
		setupCall()
			.withServicePath("/2281/billingrecords")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, of("^/2281/billingrecords/(.*)$"))
			.sendRequestAndVerifyResponse();
	}
	
	@Test
	void test03_createCustomerInvoiceRecordForInternalInvoice() {
		setupCall()
			.withServicePath("/2281/billingrecords")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, of("^/2281/billingrecords/(.*)$"))
			.sendRequestAndVerifyResponse();
	}
}
