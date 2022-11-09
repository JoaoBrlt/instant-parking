package fr.brilhante.joao.instant.parking.controllers;

import fr.brilhante.joao.instant.parking.dtos.ParkingDto;
import fr.brilhante.joao.instant.parking.dtos.ParkingSearchRequestDto;
import fr.brilhante.joao.instant.parking.dtos.ParkingSearchResultDto;
import fr.brilhante.joao.instant.parking.services.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("parking")
public class ParkingController {

	private final ParkingService parkingService;

	public ParkingController(ParkingService parkingService) {
		this.parkingService = parkingService;
	}

	/**
	 * Retrieves all the parking facilities.
	 * @param pageable The pagination options.
	 * @return All parking facilities.
	 */
	@GetMapping
	@Operation(summary = "Get all parking facilities")
	@ApiResponse(responseCode = "200", description = "The request was successfully completed")
	public Page<ParkingDto> getAllParkingFacilities(@ParameterObject Pageable pageable) {
		return parkingService.getAllParkingFacilities(pageable);
	}

	/**
	 * Searches for parking facilities near a given location.
	 * <p>
	 * This endpoint uses the POST method to secure sensitive user data such as their location.
	 * </p>
	 * @param request  The search request.
	 * @param pageable The pagination options.
	 * @return All parking facilities near the provided location.
	 */
	@PostMapping("searches")
	@Operation(summary = "Search for parking facilities near a given location")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "The request was successfully completed"),
		@ApiResponse(responseCode = "400", description = "The provided search request is invalid", content = @Content)
	})
	public Page<ParkingSearchResultDto> searchParkingFacilitiesNearLocation(
		@Valid @RequestBody ParkingSearchRequestDto request,
		@ParameterObject Pageable pageable
	) {
		return parkingService.searchParkingFacilitiesNearLocation(request, pageable);
	}
}
