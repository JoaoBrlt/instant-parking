package fr.brilhante.joao.instant.parking.dtos;

import javax.validation.constraints.NotNull;

public record ParkingSearchResultDto(
	@NotNull String id,
	@NotNull String consumerId,
	@NotNull String name,
	@NotNull String address,
	Integer availableSpots,
	@NotNull Integer totalSpots,
	@NotNull LocationDto location,
	@NotNull Double distance
) {
}
