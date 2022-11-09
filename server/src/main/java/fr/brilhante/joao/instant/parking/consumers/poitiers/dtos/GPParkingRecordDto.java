package fr.brilhante.joao.instant.parking.consumers.poitiers.dtos;

import org.locationtech.jts.geom.Geometry;

public record GPParkingRecordDto(String recordid, GPParkingRecordFieldsDto fields, Geometry geometry) {
}
