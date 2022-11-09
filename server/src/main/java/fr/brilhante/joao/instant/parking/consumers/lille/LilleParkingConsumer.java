package fr.brilhante.joao.instant.parking.consumers.lille;

import fr.brilhante.joao.instant.parking.consumers.lille.dtos.LilleParkingRecordDto;
import fr.brilhante.joao.instant.parking.consumers.lille.dtos.LilleParkingResponseDto;
import fr.brilhante.joao.instant.parking.consumers.lille.mappers.LilleParkingMapper;
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
public class LilleParkingConsumer {

	private final ParkingService parkingService;
	private final LilleParkingMapper parkingMapper;
	private final WebClient webClient;
	private final String endpointUrl;

	public LilleParkingConsumer(
		ParkingService parkingService,
		LilleParkingMapper parkingMapper,
		WebClient webClient,
		@Value("${parking.lille.url}") String endpointUrl
	) {
		this.parkingService = parkingService;
		this.parkingMapper = parkingMapper;
		this.webClient = webClient;
		this.endpointUrl = endpointUrl;
	}

	@Scheduled(fixedDelayString = "${parking.lille.interval}")
	private void consumeParkingFacilities() {
		queryParkingFacilities().subscribe(this::saveParkingFacilities);
	}

	public Mono<LilleParkingResponseDto> queryParkingFacilities() {
		return webClient
			.get()
			.uri(endpointUrl)
			.retrieve()
			.bodyToMono(LilleParkingResponseDto.class);
	}

	public void saveParkingFacilities(LilleParkingResponseDto response) {
		for (LilleParkingRecordDto parkingRecord : response.records()) {
			Parking parking = parkingMapper.toParking(parkingRecord);
			parkingService.saveParkingFacility(parking);
		}
		log.info("[Lille] Consumed {} parking facilities.", response.records().size());
	}
}
