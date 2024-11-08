package se.sundsvall.billingpreprocessor.apptest;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

/**
 * Billing Records IT tests.
 */
@WireMockAppTestSuite(files = "classpath:/BillingRecordsIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class BillingRecordsIT extends AbstractAppTest {

	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";
	private static final List<String> CONTENT_TYPE_JSON = of(APPLICATION_JSON_VALUE);

	@Autowired
	private BillingRecordRepository repository;

	@Test
	void test01_createExternalBillingRecord() {
		setupCall()
			.withServicePath("/2281/billingrecords")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, of("^/2281/billingrecords/(.*)$"))
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_readBillingRecordById() {
		setupCall()
			.withServicePath("/2281/billingrecords/1310ee8b-ecf9-4fe1-ab9d-f19153b19d06")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_readAllBillingRecords() {
		setupCall()
			.withServicePath("/2281/billingrecords")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_readBillingRecordsByFilter() {
		setupCall()
			.withServicePath("/2281/billingrecords?filter=category : 'ISYCASE' and status : 'APPROVED'")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_updateBillingRecord() {
		setupCall()
			.withServicePath("/2281/billingrecords/83e4d599-5b4d-431c-8ebc-81192e9401ee")
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_deleteBillingRecordInNonDeletableState() {
		final var id = "1310ee8b-ecf9-4fe1-ab9d-f19153b19d06";
		assertThat(repository.existsById(id)).isTrue();

		setupCall()
			.withServicePath("/2281/billingrecords/" + id)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(METHOD_NOT_ALLOWED)
			.withExpectedResponseHeader(CONTENT_TYPE, of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		assertThat(repository.existsById(id)).isTrue();
	}

	@Test
	void test07_deleteBillingRecord() {
		final var id = "83e4d599-5b4d-431c-8ebc-81192e9401ee";
		assertThat(repository.existsById(id)).isTrue();

		setupCall()
			.withServicePath("/2281/billingrecords/" + id)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.withExpectedResponseBodyIsNull()
			.sendRequestAndVerifyResponse();

		assertThat(repository.existsById(id)).isFalse();
	}

	@Test
	void test08_createBillingRecords() {
		setupCall()
			.withServicePath("/2281/billingrecords/batch")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, of("^/2281/billingrecords/$"))
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_createInternalBillingRecord() {
		setupCall()
			.withServicePath("/2281/billingrecords")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(LOCATION, of("^/2281/billingrecords/(.*)$"))
			.sendRequestAndVerifyResponse();
	}
}
