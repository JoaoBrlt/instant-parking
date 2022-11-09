package fr.brilhante.joao.instant.parking.services;

import fr.brilhante.joao.instant.parking.dtos.LocationDto;
import fr.brilhante.joao.instant.parking.dtos.ParkingDto;
import fr.brilhante.joao.instant.parking.dtos.ParkingSearchRequestDto;
import fr.brilhante.joao.instant.parking.dtos.ParkingSearchResultDto;
import fr.brilhante.joao.instant.parking.mappers.ParkingMapper;
import fr.brilhante.joao.instant.parking.models.Parking;
import fr.brilhante.joao.instant.parking.projections.ParkingSearchResult;
import fr.brilhante.joao.instant.parking.projections.ParkingSearchResultImpl;
import fr.brilhante.joao.instant.parking.repositories.ParkingRepository;
import fr.brilhante.joao.instant.parking.utils.GeometryUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

	@Mock
	private ParkingRepository parkingRepository;

	@Mock
	private ParkingMapper parkingMapper;

	private double defaultSearchRadius;

	private ParkingService parkingService;

	private ParkingDto parkingDto;

	private ParkingSearchResultDto parkingResultDto;

	@BeforeEach
	void init() {
		defaultSearchRadius = 500.0;
		parkingService = new ParkingService(parkingRepository, parkingMapper, defaultSearchRadius);

		parkingDto = new ParkingDto(
			"1",
			"GRAND_POITIERS",
			"HOTEL DE VILLE",
			"22 rue Carnot",
			240,
			625,
			new LocationDto(46.57932, 0.33855)
		);
		parkingResultDto = new ParkingSearchResultDto(
			"1",
			"GRAND_POITIERS",
			"HOTEL DE VILLE",
			"22 rue Carnot",
			240,
			625,
			new LocationDto(46.57932, 0.33855),
			105.25
		);
	}

	@Test
	void saveParkingFacility() {
		Parking parking = new Parking();

		parkingService.saveParkingFacility(parking);

		verify(parkingRepository).save(parking);
	}

	@Test
	void getAllParkingFacilities() {
		Parking parking = new Parking();
		Pageable pageable = PageRequest.of(0, 20);

		Page<Parking> parkingPage = new PageImpl<>(List.of(parking));
		when(parkingRepository.findAll(any(Pageable.class))).thenReturn(parkingPage);

		when(parkingMapper.toDto(parking)).thenReturn(parkingDto);

		Page<ParkingDto> actualParkingPage = parkingService.getAllParkingFacilities(pageable);
		assertEquals(1, actualParkingPage.getSize());
		assertEquals(parkingDto, actualParkingPage.getContent().get(0));
	}

	@Test
	void searchParkingFacilitiesNearLocation() {
		LocationDto location = new LocationDto(46.57932, 0.33855);
		ParkingSearchRequestDto searchRequest = new ParkingSearchRequestDto(location, 200.0);
		Pageable pageable = PageRequest.of(0, 20);

		Point expectedPoint = GeometryUtils.createPoint(0.33855, 46.57932);
		ParkingSearchResultImpl parkingResult = new ParkingSearchResultImpl();
		Page<ParkingSearchResult> parkingResultPage = new PageImpl<>(List.of(parkingResult));
		when(parkingRepository.findAllNearLocation(eq(expectedPoint), eq(200.0), any(Pageable.class))).thenReturn(parkingResultPage);

		when(parkingMapper.toDto(parkingResult)).thenReturn(parkingResultDto);

		Page<ParkingSearchResultDto> actualParkingResultPage = parkingService.searchParkingFacilitiesNearLocation(searchRequest, pageable);
		assertEquals(1, actualParkingResultPage.getSize());
		assertEquals(parkingResultDto, actualParkingResultPage.getContent().get(0));
	}

	@Test
	void searchParkingFacilitiesNearLocationWithNoRadius() {
		LocationDto location = new LocationDto(46.57932, 0.33855);
		ParkingSearchRequestDto searchRequest = new ParkingSearchRequestDto(location, null);
		Pageable pageable = PageRequest.of(0, 20);

		Point expectedPoint = GeometryUtils.createPoint(0.33855, 46.57932);
		ParkingSearchResultImpl parkingResult = new ParkingSearchResultImpl();
		Page<ParkingSearchResult> parkingResultPage = new PageImpl<>(List.of(parkingResult));
		when(parkingRepository.findAllNearLocation(eq(expectedPoint), eq(defaultSearchRadius), any(Pageable.class))).thenReturn(parkingResultPage);

		when(parkingMapper.toDto(parkingResult)).thenReturn(parkingResultDto);

		Page<ParkingSearchResultDto> actualParkingResultPage = parkingService.searchParkingFacilitiesNearLocation(searchRequest, pageable);
		assertEquals(1, actualParkingResultPage.getSize());
		assertEquals(parkingResultDto, actualParkingResultPage.getContent().get(0));
	}
}
