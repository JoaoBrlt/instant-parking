package fr.brilhante.joao.instant.parking.repositories;

import fr.brilhante.joao.instant.parking.models.Parking;
import fr.brilhante.joao.instant.parking.models.ParkingId;
import fr.brilhante.joao.instant.parking.projections.ParkingSearchResult;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParkingRepository extends JpaRepository<Parking, ParkingId> {

	/**
	 * Queries all the parking facilities near a given position.
	 * <p>
	 * This method uses a native query to take advantage of the functions provided by PostGIS.
	 * Moreover, it uses a projection to retrieve the distance separating each parking from the provided location.
	 * </p>
	 * @param point    The location around which to search.
	 * @param radius   The search radius (in meters).
	 * @param pageable The pagination options.
	 * @return All parking facilities near the provided location ordered by distance.
	 */
	@Query(
		value = "SELECT id, consumer_id AS consumerId, name, address, available_spots AS availableSpots, " +
			"total_spots AS totalSpots, ST_AsText(location) AS location, ST_Distance(location, :point) AS distance " +
			"FROM parking WHERE ST_DWithin(location, :point, :radius) ORDER BY distance",
		countQuery = "SELECT COUNT(*) FROM parking WHERE ST_DWithin(location, :point, :radius)",
		nativeQuery = true
	)
	Page<ParkingSearchResult> findAllNearLocation(Point point, double radius, Pageable pageable);
}
