package fr.brilhante.joao.instant.parking.projections;

public interface ParkingSearchResult {

	String getId();

	String getConsumerId();

	String getName();

	String getAddress();

	Integer getAvailableSpots();

	int getTotalSpots();

	String getLocation();

	double getDistance();
}
