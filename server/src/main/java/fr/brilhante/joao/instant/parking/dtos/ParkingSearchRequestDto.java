package fr.brilhante.joao.instant.parking.dtos;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record ParkingSearchRequestDto(@Valid @NotNull LocationDto location, Double radius) {
}
