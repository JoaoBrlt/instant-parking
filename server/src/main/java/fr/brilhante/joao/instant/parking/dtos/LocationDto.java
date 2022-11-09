package fr.brilhante.joao.instant.parking.dtos;

import javax.validation.constraints.NotNull;

public record LocationDto(@NotNull Double latitude, @NotNull Double longitude) {
}
