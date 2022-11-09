package fr.brilhante.joao.instant.parking.projections;

import lombok.Data;

@Data
public class ParkingSearchResultImpl implements ParkingSearchResult {

	private String id;

	private String consumerId;

	private String name;

	private String address;

	private Integer availableSpots;

	private int totalSpots;

	private String location;

	private double distance;
}
