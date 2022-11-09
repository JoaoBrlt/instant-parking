package fr.brilhante.joao.instant.parking.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GeometryUtils {

	private GeometryUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static Point createPoint(double x, double y) {
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate coordinate = new Coordinate(x, y);
		return geometryFactory.createPoint(coordinate);
	}
}
