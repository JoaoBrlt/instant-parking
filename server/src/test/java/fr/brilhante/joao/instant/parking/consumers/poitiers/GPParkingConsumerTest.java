package fr.brilhante.joao.instant.parking.consumers.poitiers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.brilhante.joao.instant.parking.config.JacksonConfig;
import fr.brilhante.joao.instant.parking.config.WebFluxConfig;
import fr.brilhante.joao.instant.parking.consumers.ParkingConsumerTestUtils;
import fr.brilhante.joao.instant.parking.consumers.poitiers.dtos.*;
import fr.brilhante.joao.instant.parking.consumers.poitiers.mappers.GPParkingMapper;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest({JacksonConfig.class, WebFluxConfig.class})
@ExtendWith(MockitoExtension.class)
class GPParkingConsumerTest {

	private MockWebServer mockWebServer;

	@Autowired
	private WebClient webClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private ParkingService parkingService;

	@Mock
	private GPParkingMapper parkingMapper;

	private GPParkingConsumer parkingConsumer;

	private GPParkingRecordDto parkingRecord;

	private GPParkingAvailabilityRecordDto availabilityRecord;

	private GPParkingAvailabilityRecordDto availabilityRecord2;

	@BeforeEach
	void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

		String facilityUrl = mockWebServer.url("/facilities").toString();
		String availabilityUrl = mockWebServer.url("/availability").toString();
		parkingConsumer = new GPParkingConsumer(parkingService, parkingMapper, webClient, facilityUrl, availabilityUrl);

		parkingRecord = new GPParkingRecordDto(
			"e24d0474ce8282db403e8cbf61c94069c1b89460",
			new GPParkingRecordFieldsDto("PALAIS DE JUSTICE", "Boulevard De Lattre de Tassigny", 140),
			GeometryUtils.createPoint(0.35129, 46.58595)
		);
		availabilityRecord = new GPParkingAvailabilityRecordDto(
			"702c4b1c721b2d218f770e9dee44a26968bd36d8",
			new GPParkingAvailabilityRecordFieldsDto("PALAIS DE JUSTICE", 44)
		);
		availabilityRecord2 = new GPParkingAvailabilityRecordDto(
			"e1b963996f09dac9ec9345f9d012937935009e8a",
			new GPParkingAvailabilityRecordFieldsDto("ARRET MINUTE", 72)
		);
	}

	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void queryParkingFacilities() throws JsonProcessingException, InterruptedException {
		GPParkingResponseDto response = new GPParkingResponseDto(List.of(parkingRecord));
		MockResponse mockResponse = ParkingConsumerTestUtils.createMockResponse(objectMapper, response);
		mockWebServer.enqueue(mockResponse);

		GPParkingResponseDto actualResponse = parkingConsumer.queryParkingFacilities().block();
		assertEquals(response, actualResponse);

		RecordedRequest request = mockWebServer.takeRequest();
		assertEquals("GET", request.getMethod());
		assertEquals("/facilities", request.getPath());
	}

	@Test
	void queryParkingAvailability() throws JsonProcessingException, InterruptedException {
		GPParkingAvailabilityResponseDto response = new GPParkingAvailabilityResponseDto(List.of(availabilityRecord));
		MockResponse mockResponse = ParkingConsumerTestUtils.createMockResponse(objectMapper, response);
		mockWebServer.enqueue(mockResponse);

		GPParkingAvailabilityResponseDto actualResponse = parkingConsumer.queryParkingAvailability().block();
		assertEquals(response, actualResponse);

		RecordedRequest request = mockWebServer.takeRequest();
		assertEquals("GET", request.getMethod());
		assertEquals("/availability", request.getPath());
	}

	@Test
	void getParkingAvailability() {
		GPParkingAvailabilityResponseDto response = new GPParkingAvailabilityResponseDto(List.of(availabilityRecord, availabilityRecord2));

		Integer availableSpots = parkingConsumer.getParkingAvailability(parkingRecord, response);
		assertEquals(44, availableSpots);
	}

	@Test
	void getParkingAvailabilityWithNoRecordFound() {
		GPParkingAvailabilityResponseDto response = new GPParkingAvailabilityResponseDto(List.of(availabilityRecord2));

		Integer availableSpots = parkingConsumer.getParkingAvailability(parkingRecord, response);
		assertNull(availableSpots);
	}

	@Test
	void saveParkingFacilities() {
		GPParkingResponseDto facilitiesResponse = new GPParkingResponseDto(List.of(parkingRecord));
		GPParkingAvailabilityResponseDto availabilityResponse = new GPParkingAvailabilityResponseDto(List.of(availabilityRecord));
		GPParkingDataDto data = new GPParkingDataDto(facilitiesResponse, availabilityResponse);

		Parking parking = new Parking();
		when(parkingMapper.toParking(parkingRecord)).thenReturn(parking);

		parkingConsumer.saveParkingFacilities(data);

		Parking expectedParking = new Parking();
		expectedParking.setAvailableSpots(44);
		verify(parkingService).saveParkingFacility(expectedParking);
	}
}
