package fr.brilhante.joao.instant.parking.mappers;

import fr.brilhante.joao.instant.parking.dtos.ParkingDto;
import fr.brilhante.joao.instant.parking.dtos.ParkingSearchResultDto;
import fr.brilhante.joao.instant.parking.models.Parking;
import fr.brilhante.joao.instant.parking.projections.ParkingSearchResult;
import org.mapstruct.Mapper;

@Mapper(uses = {GeometryMapper.class, LocationMapper.class})
public interface ParkingMapper {

	ParkingDto toDto(Parking parking);

	ParkingSearchResultDto toDto(ParkingSearchResult result);
}
