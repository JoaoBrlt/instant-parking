package fr.brilhante.joao.instant.parking.consumers.poitiers;

import fr.brilhante.joao.instant.parking.consumers.poitiers.dtos.GPParkingAvailabilityResponseDto;
import fr.brilhante.joao.instant.parking.consumers.poitiers.dtos.GPParkingDataDto;
import fr.brilhante.joao.instant.parking.consumers.poitiers.dtos.GPParkingRecordDto;
import fr.brilhante.joao.instant.parking.consumers.poitiers.dtos.GPParkingResponseDto;
import fr.brilhante.joao.instant.parking.consumers.poitiers.mappers.GPParkingMapper;
import fr.brilhante.joao.instant.parking.models.Parking;
import fr.brilhante.joao.instant.parking.services.ParkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GPParkingConsumer {

	private final ParkingService parkingService;
	private final GPParkingMapper parkingMapper;
	private final WebClient webClient;
	private final String facilitiesUrl;
	private final String availabilityUrl;

	public GPParkingConsumer(
		ParkingService parkingService,
		GPParkingMapper parkingMapper,
		WebClient webClient,
		@Value("${parking.grand-poitiers.facilities-url}") String facilitiesUrl,
		@Value("${parking.grand-poitiers.availability-url}") String availabilityUrl
	) {
		this.parkingService = parkingService;
		this.parkingMapper = parkingMapper;
		this.webClient = webClient;
		this.facilitiesUrl = facilitiesUrl;
		this.availabilityUrl = availabilityUrl;
	}

	@Scheduled(fixedDelayString = "${parking.grand-poitiers.interval}")
	private void consumeParkingFacilities() {
		Mono
			.zip(
				queryParkingFacilities(),
				queryParkingAvailability(),
				GPParkingDataDto::new
			)
			.subscribe(this::saveParkingFacilities);
	}

	public Mono<GPParkingResponseDto> queryParkingFacilities() {
		return webClient
			.get()
			.uri(facilitiesUrl)
			.retrieve()
			.bodyToMono(GPParkingResponseDto.class);
	}

	public Mono<GPParkingAvailabilityResponseDto> queryParkingAvailability() {
		return webClient
			.get()
			.uri(availabilityUrl)
			.retrieve()
			.bodyToMono(GPParkingAvailabilityResponseDto.class);
	}

	public Integer getParkingAvailability(GPParkingRecordDto parkingRecord, GPParkingAvailabilityResponseDto availabilityResponse) {
		return availabilityResponse.records().stream()
			.filter(item -> parkingRecord.fields().nom().equals(item.fields().nom()))
			.findFirst()
			.map(availabilityRecord -> availabilityRecord.fields().places_restantes())
			.orElse(null);
	}

	public void saveParkingFacilities(GPParkingDataDto data) {
		for (GPParkingRecordDto parkingRecord : data.facilities().records()) {
			Parking parking = parkingMapper.toParking(parkingRecord);
			Integer availableSpots = getParkingAvailability(parkingRecord, data.availability());
			parking.setAvailableSpots(availableSpots);
			parkingService.saveParkingFacility(parking);
		}
		log.info("[Grand Poitiers] Consumed {} parking facilities.", data.facilities().records().size());
	}
}
