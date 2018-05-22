package org.cityreporter.client;

import java.sql.Timestamp;
import java.util.Arrays;

import org.cityreporter.model.ksampost.Measure;
import org.cityreporter.model.ksampost.Measurement;
import org.cityreporter.model.ksampost.RuntimeMonitorData;
import org.cityreporter.model.weather.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class WeatherClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherClient.class);
    // private final String URL_WEATHER =
    // "http://api.openweathermap.org/data/2.5/weather?q=Gothenburg&APPID=95852c5d692034ab82e49904bc20fa86";
    // private final String URL_WEATHER =
    // "http://api.openweathermap.org/data/2.5/forecast?q=Gothenburg&APPID=95852c5d692034ab82e49904bc20fa86";
    private final String URL_WEATHER = "http://api.openweathermap.org/data/2.5/weather?lat=57,773106&lon=12,768874&APPID=95852c5d692034ab82e49904bc20fa86";

    public WeatherClient() {
    }

    public RuntimeMonitorData getWeatherData() {
	RestTemplate restTemplate = new RestTemplate();
	WeatherResponse response = restTemplate.getForObject(URL_WEATHER, WeatherResponse.class);

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
	m_state.setValue(response.getWeather().get(0).getMain());
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
