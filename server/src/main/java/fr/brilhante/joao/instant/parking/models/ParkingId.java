package fr.brilhante.joao.instant.parking.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ParkingId implements Serializable {

	private String id;

	private String consumerId;
}
