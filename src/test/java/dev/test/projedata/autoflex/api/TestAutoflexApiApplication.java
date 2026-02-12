package dev.test.projedata.autoflex.api;

import org.springframework.boot.SpringApplication;

public class TestAutoflexApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(AutoflexApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
