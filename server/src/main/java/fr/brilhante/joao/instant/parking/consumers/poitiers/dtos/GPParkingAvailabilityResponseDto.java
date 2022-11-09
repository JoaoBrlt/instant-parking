package fr.brilhante.joao.instant.parking.consumers.poitiers.dtos;

import java.util.List;

public record GPParkingAvailabilityResponseDto(List<GPParkingAvailabilityRecordDto> records) {
}
