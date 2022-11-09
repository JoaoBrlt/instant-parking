package fr.brilhante.joao.instant.parking.consumers.lille;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.brilhante.joao.instant.parking.config.JacksonConfig;
import fr.brilhante.joao.instant.parking.config.WebFluxConfig;
import fr.brilhante.joao.instant.parking.consumers.ParkingConsumerTestUtils;
import fr.brilhante.joao.instant.parking.consumers.lille.dtos.LilleParkingRecordDto;
import fr.brilhante.joao.instant.parking.consumers.lille.dtos.LilleParkingRecordFieldsDto;
import fr.brilhante.joao.instant.parking.consumers.lille.dtos.LilleParkingResponseDto;
import fr.brilhante.joao.instant.parking.consumers.lille.mappers.LilleParkingMapper;
import fr.brilhante.joao.instant.parking.models.Parking;
import fr.brilhante.joao.instant.parking.services.ParkingService;
import fr.brilhante.joao.instant.parking.utils.GeometryUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest({JacksonConfig.class, WebFluxConfig.class})
@ExtendWith(MockitoExtension.class)
class LilleParkingConsumerTest {

	private MockWebServer mockWebServer;

	@Autowired
	private WebClient webClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private ParkingService parkingService;

	@Mock
	private LilleParkingMapper parkingMapper;

	private LilleParkingConsumer parkingConsumer;

	private LilleParkingRecordDto parkingRecord;

	@BeforeEach
	void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

		String endpointUrl = mockWebServer.url("/api").toString();
		parkingConsumer = new LilleParkingConsumer(parkingService, parkingMapper, webClient, endpointUrl);

		parkingRecord = new LilleParkingRecordDto(
			"36577379d48fe69fcb537a6c942aad00de4166ad",
			new LilleParkingRecordFieldsDto(
				"Parking Rihour-Printemps",
				"Place Rihour",
				298,
				300,
				GeometryUtils.createPoint(3.06136, 50.63543)
			)
		);
	}

	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void queryParkingFacilities() throws InterruptedException, JsonProcessingException {
		LilleParkingResponseDto response = new LilleParkingResponseDto(List.of(parkingRecord));
		MockResponse mockResponse = ParkingConsumerTestUtils.createMockResponse(objectMapper, response);
		mockWebServer.enqueue(mockResponse);

		LilleParkingResponseDto actualResponse = parkingConsumer.queryParkingFacilities().block();
		assertEquals(response, actualResponse);

		RecordedRequest request = mockWebServer.takeRequest();
		assertEquals("GET", request.getMethod());
		assertEquals("/api", request.getPath());
	}

	@Test
	void saveParkingFacilities() {
		LilleParkingResponseDto response = new LilleParkingResponseDto(List.of(parkingRecord));

		Parking parking = new Parking();
		when(parkingMapper.toParking(parkingRecord)).thenReturn(parking);

		parkingConsumer.saveParkingFacilities(response);
		verify(parkingService).saveParkingFacility(parking);
	}
}
