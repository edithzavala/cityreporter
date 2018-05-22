package org.cityreporter.client;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.cityreporter.model.ksampost.MonitoringData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CityDataClient {
    private final ScheduledExecutorService schedulerW = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService schedulerT = Executors.newSingleThreadScheduledExecutor();
    private final WeatherClient wc;
    private final TrafficClient tc;
    private final int PORT_KSAM = 8080;
    private final String SYSTEM_ID = "openDlvMonitorv0";
    // private final RestTemplate REST_TEMPLATE = new RestTemplate();
    private final String URL_KSAM = "http://localhost:" + PORT_KSAM + "/" + SYSTEM_ID + "/monitoringData";

    public CityDataClient() {
	super();
	this.wc = new WeatherClient();
	this.tc = new TrafficClient();
	/** Monitor weather every 60000 ms **/
	Runnable taskW = new Runnable() {
	    @Override
	    public void run() {
		MonitoringData monData = new MonitoringData();
		monData.setSystemId(SYSTEM_ID);
		monData.setTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
		monData.setMonitors(new ArrayList<>());
		// Add monitors with their variables measures to the list of monitors
		monData.getMonitors().add(wc.getWeatherData()); // weather monitor (freq= 60000 ms)
		postData(monData);
	    }
	};
	schedulerW.scheduleAtFixedRate(taskW, 0, 60000, TimeUnit.MILLISECONDS);
	/*************************************/
	/** Monitor traffic every 60000 ms **/
	Runnable taskT = new Runnable() {
	    @Override
	    public void run() {
		MonitoringData monData = new MonitoringData();
		monData.setSystemId(SYSTEM_ID);
		monData.setTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
		monData.setMonitors(new ArrayList<>());
		// Add monitors with their variables measures to the list of monitors
		monData.getMonitors().add(tc.getTrafficData());// traffic monitor (freq= 1000 ms)
		postData(monData);
	    }
	};
	schedulerW.scheduleAtFixedRate(taskT, 0, 1000, TimeUnit.MILLISECONDS);
	/*************************************/
    }

    private void postData(MonitoringData monData) {
	ObjectMapper mapper = new ObjectMapper();
	try {
	    String jsonMonData = mapper.writeValueAsString(monData);
	    HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.set("Content-Type", "application/json");
	    HttpEntity<String> httpEntity = new HttpEntity<String>(jsonMonData, httpHeaders);
	    RestTemplate restTemplate = new RestTemplate();
	    restTemplate.postForObject(URL_KSAM, httpEntity, String.class);
	} catch (JsonProcessingException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
	new CityDataClient();
    }
}
