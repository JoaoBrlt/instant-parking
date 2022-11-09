package fr.brilhante.joao.instant.parking.consumers.poitiers.mappers;

import fr.brilhante.joao.instant.parking.consumers.poitiers.dtos.GPParkingRecordDto;
import fr.brilhante.joao.instant.parking.mappers.GeometryMapper;
import fr.brilhante.joao.instant.parking.models.Parking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = GeometryMapper.class)
public interface GPParkingMapper {

	@Mapping(target = "id", source = "recordid")
	@Mapping(target = "consumerId", constant = "GRAND_POITIERS")
	@Mapping(target = "name", source = "fields.nom")
	@Mapping(target = "address", source = "fields.adresse")
	@Mapping(target = "totalSpots", source = "fields.nb_places")
	@Mapping(target = "location", source = "geometry")
	Parking toParking(GPParkingRecordDto parkingRecord);
}
