package fr.brilhante.joao.instant.parking.consumers.lille.dtos;

import org.locationtech.jts.geom.Geometry;

public record LilleParkingRecordFieldsDto(String libelle, String adresse, int dispo, int max, Geometry geometry) {
}
