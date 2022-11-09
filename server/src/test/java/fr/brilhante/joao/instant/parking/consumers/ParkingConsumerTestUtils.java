package fr.brilhante.joao.instant.parking.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;

public class ParkingConsumerTestUtils {

	private ParkingConsumerTestUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static MockResponse createMockResponse(ObjectMapper mapper, Object body) throws JsonProcessingException {
		MockResponse mockResponse = new MockResponse();
		mockResponse.setHeader("Content-Type", "application/json");
		mockResponse.setBody(mapper.writeValueAsString(body));
		return mockResponse;
	}
}
