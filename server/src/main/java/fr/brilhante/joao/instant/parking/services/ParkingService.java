package fr.brilhante.joao.instant.parking.services;

import fr.brilhante.joao.instant.parking.dtos.LocationDto;
import fr.brilhante.joao.instant.parking.dtos.ParkingDto;
import fr.brilhante.joao.instant.parking.dtos.ParkingSearchRequestDto;
import fr.brilhante.joao.instant.parking.dtos.ParkingSearchResultDto;
import fr.brilhante.joao.instant.parking.mappers.ParkingMapper;
import fr.brilhante.joao.instant.parking.models.Parking;
import fr.brilhante.joao.instant.parking.repositories.ParkingRepository;
import fr.brilhante.joao.instant.parking.utils.GeometryUtils;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParkingService {

	private final ParkingRepository parkingRepository;
	private final ParkingMapper parkingMapper;
	private final double defaultSearchRadius;

	public ParkingService(
		ParkingRepository parkingRepository,
		ParkingMapper parkingMapper,
		@Value("${parking.defaults.search-radius}")
		double defaultSearchRadius
	) {
		this.parkingRepository = parkingRepository;
		this.parkingMapper = parkingMapper;
		this.defaultSearchRadius = defaultSearchRadius;
	}

	/**
	 * Saves a parking facility.
	 * @param parking The parking facility to save.
	 */
	public void saveParkingFacility(Parking parking) {
		parkingRepository.save(parking);
	}

	/**
	 * Retrieves all the parking facilities.
	 * @param pageable The pagination options.
	 * @return All parking facilities.
	 */
	public Page<ParkingDto> getAllParkingFacilities(Pageable pageable) {
		return parkingRepository.findAll(pageable).map(parkingMapper::toDto);
	}

	/**
	 * Searches for parking facilities near a given location.
	 * @param request  The search request.
	 * @param pageable The pagination options.
	 * @return All parking facilities near the provided location.
	 */
	public Page<ParkingSearchResultDto> searchParkingFacilitiesNearLocation(
		ParkingSearchRequestDto request,
		Pageable pageable
	) {
		LocationDto location = request.location();
		Point point = GeometryUtils.createPoint(location.longitude(), location.latitude());
		double radius = Optional.ofNullable(request.radius()).orElse(defaultSearchRadius);
		return parkingRepository.findAllNearLocation(point, radius, pageable).map(parkingMapper::toDto);
	}
}
