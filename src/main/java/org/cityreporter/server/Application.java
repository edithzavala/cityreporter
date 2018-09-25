package org.cityreporter.server;

import org.cityreporter.client.CityDataClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static CityDataClient cityDataClient;

    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
	cityDataClient = new CityDataClient();
    }

}
