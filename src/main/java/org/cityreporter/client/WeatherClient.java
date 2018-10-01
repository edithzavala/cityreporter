package org.cityreporter.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.cityreporter.model.ksampost.Measure;
import org.cityreporter.model.ksampost.Measurement;
import org.cityreporter.model.ksampost.RuntimeMonitorData;
import org.cityreporter.model.weather.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class WeatherClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherClient.class);

    private final String URL_WEATHER;
    private final boolean replay;

    private Iterator<String> linesW;
    private String city; // Could be an adaptive parameters for getting weather

    public WeatherClient(String url, boolean replay) {
	this.URL_WEATHER = url;
	this.replay = replay;
	if (replay) {
	    try {
		linesW = Files.lines(Paths.get("/tmp/weka/openweathermap-mainWeather_replay.txt"))
			.collect(Collectors.toList()).iterator();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	this.city = "Gothenburg";
    }

    public void adaptMontitorParameter(String parameter, String value) {
	this.city = value; // example of para meter adaptation
    }

    public RuntimeMonitorData getWeatherData() {
	String weather = "";
	if (replay) {
	    weather = linesW.hasNext() ? linesW.next().split(" ")[1] : "Rain";
	} else {
	    RestTemplate restTemplate = new RestTemplate();
	    WeatherResponse response = restTemplate.getForObject(URL_WEATHER, WeatherResponse.class);
	    weather = response.getWeather().get(0).getMain();
	}
	/** Temperature **/
	// Measure m_temp = new Measure();
	// m_temp.setmTimeStamp(monData.getTimeStamp());
	// Temperature is in Kelvin (K-273.15 = Celsius)
	// m_temp.setValue(String.valueOf(response.getMain().getTemp() - 273.15));
	//
	// Measurement mrmt_temp = new Measurement();
	// mrmt_temp.setVarId("temperature");
	// mrmt_temp.setMeasures(Arrays.asList(m_temp));
	/***************/

	/** Main weather ["Rain","Snow","Extreme","Clear","Clouds"] **/
	// Create a measure with TS and Value
	Measure m_state = new Measure();
	m_state.setmTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
	m_state.setValue(weather);
	// Add measure to corresponding measurement i.e., monitoring variable
	Measurement mrmt = new Measurement();
	mrmt.setVarId("mainWeather");
	mrmt.setMeasures(Arrays.asList(m_state));
	/***************/

	// Add measurements, i.e., monitoring variables to the weather monitor
	RuntimeMonitorData rmd = new RuntimeMonitorData();
	rmd.setMonitorId("openweathermap");
	rmd.setMeasurements(Arrays.asList(mrmt));

	return rmd;
    }
}
