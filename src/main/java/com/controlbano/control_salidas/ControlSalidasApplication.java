package com.controlbano.control_salidas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ControlSalidasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlSalidasApplication.class, args);
	}

}
