package fr.brilhante.joao.instant.parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InstantParkingApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstantParkingApplication.class, args);
	}
}
