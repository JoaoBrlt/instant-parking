package fr.brilhante.joao.instant.parking.mappers;

import fr.brilhante.joao.instant.parking.dtos.LocationDto;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;

@Mapper
public class LocationMapper {

	public LocationDto toLocation(Point point) {
		return new LocationDto(point.getY(), point.getX());
	}
}
