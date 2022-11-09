package fr.brilhante.joao.instant.parking.models;

import lombok.Data;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;

@Data
@Entity
@IdClass(ParkingId.class)
public class Parking {

	@Id
	private String id;

	@Id
	private String consumerId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String address;

	private Integer availableSpots;

	@Column(nullable = false)
	private int totalSpots;

	@Basic
	@Column(columnDefinition = "geography", nullable = false)
	private Point location;
}
