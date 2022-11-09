package fr.brilhante.joao.instant.parking.mappers;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.mapstruct.Mapper;

@Mapper
public class GeometryMapper {

	public Point toPoint(Geometry geometry) {
		if (geometry.getGeometryType().equals(Geometry.TYPENAME_POINT)) {
			return (Point) geometry;
		}
		throw new IllegalArgumentException("The provided geometry is not a point.");
	}

	public Point toPoint(String wellKnownText) throws ParseException {
		WKTReader reader = new WKTReader();
		return toPoint(reader.read(wellKnownText));
	}
}
