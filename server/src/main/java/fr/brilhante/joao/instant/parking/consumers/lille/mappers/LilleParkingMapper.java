package fr.brilhante.joao.instant.parking.consumers.lille.mappers;

import fr.brilhante.joao.instant.parking.consumers.lille.dtos.LilleParkingRecordDto;
import fr.brilhante.joao.instant.parking.mappers.GeometryMapper;
import fr.brilhante.joao.instant.parking.models.Parking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = GeometryMapper.class)
public interface LilleParkingMapper {

	@Mapping(target = "id", source = "recordid")
	@Mapping(target = "consumerId", constant = "LILLE")
	@Mapping(target = "name", source = "fields.libelle")
	@Mapping(target = "address", source = "fields.adresse")
	@Mapping(target = "availableSpots", source = "fields.dispo")
	@Mapping(target = "totalSpots", source = "fields.max")
	@Mapping(target = "location", source = "fields.geometry")
	Parking toParking(LilleParkingRecordDto parkingRecord);
}
