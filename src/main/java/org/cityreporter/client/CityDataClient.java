package org.cityreporter.client;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.cityreporter.model.ksampost.MonitoringData;
import org.cityreporter.server.MonitorAdaptation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CityDataClient {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    private final ScheduledExecutorService schedulerW = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService schedulerT = Executors.newSingleThreadScheduledExecutor();
    private final WeatherClient wc;
    private final TrafficClient tc;
    private final int PORT_KSAM = 8090;
    private final String SYSTEM_ID = "openDlvMonitorv0";
    // private final RestTemplate REST_TEMPLATE = new RestTemplate();
    private final String URL_KSAM = "http://localhost:" + PORT_KSAM + "/" + SYSTEM_ID + "/monitoringData";

    private Map<String, Boolean> monitorsState;

    public CityDataClient() {
	super();
	this.wc = new WeatherClient();
	this.tc = new TrafficClient();
	this.monitorsState = new HashMap<>();
	this.monitorsState.put("heretraffic", true);
	this.monitorsState.put("openweathermap", true);

	/** Monitor weather every 60000 ms **/
	Runnable taskW = new Runnable() {
	    @Override
	    public void run() {
		if (monitorsState.get("openweathermap")) {
		    MonitoringData monData = new MonitoringData();
		    monData.setSystemId(SYSTEM_ID);
		    monData.setTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
		    monData.setMonitors(new ArrayList<>());
		    // Add monitors with their variables measures to the list of monitors
		    monData.getMonitors().add(wc.getWeatherData()); // weather monitor (freq= 60000 ms)
		    // Add context variables
		    Map.Entry<String, Object> contextVar = new AbstractMap.SimpleEntry<String, Object>("services",
			    Arrays.asList("laneFollower"));
		    monData.setContext(Arrays.asList(contextVar));
		    // LOGGER.info(monData.toString());
		    postData(monData);
		}
	    }
	};
	schedulerW.scheduleAtFixedRate(taskW, 5000, 60000, TimeUnit.MILLISECONDS);
	/*************************************/

	/** Monitor traffic every 1000 ms **/
	Runnable taskT = new Runnable() {
	    @Override
	    public void run() {
		if (monitorsState.get("heretraffic")) {
		    MonitoringData monData = new MonitoringData();
		    monData.setSystemId(SYSTEM_ID);
		    monData.setTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
		    monData.setMonitors(new ArrayList<>());
		    // Add monitors with their variables measures to the list of monitors
		    monData.getMonitors().add(tc.getTrafficData());// traffic monitor (freq= 1000 ms)
		    // Add context variables
		    Map.Entry<String, Object> contextVar = new AbstractMap.SimpleEntry<String, Object>("services",
			    Arrays.asList("laneFollower"));
		    monData.setContext(Arrays.asList(contextVar));
		    // LOGGER.info(monData.toString());
		    postData(monData);
		}
	    }
	};
	schedulerT.scheduleAtFixedRate(taskT, 5000, 1000, TimeUnit.MILLISECONDS);
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

    public WeatherClient getWc() {
	return wc;
    }

    public TrafficClient getTc() {
	return tc;
    }

    public boolean adaptMonitors(String adaptation) {
	ObjectMapper mapper = new ObjectMapper();
	try {
	    MonitorAdaptation adapt = mapper.readValue(adaptation, MonitorAdaptation.class);
	    adapt.getMonitorsToAdd().forEach(m -> {
		if (m.equals("heretraffic-trafficFactor_PATH2")) {
		    LOGGER.info("Heretraffic change location parameter");
		    tc.adaptMontitorParameter("Path", "2");
		} else {
		    monitorsState.put(m, true);
		    LOGGER.info("Add monitor: " + m);
		}
	    });
	    adapt.getMonitorsToRemove().forEach(m -> {
		LOGGER.info("Remove monitor: " + m);
		monitorsState.put(m, false);
	    });
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return true;
    }

    // public static void main(String args[]) throws IOException,
    // ClassNotFoundException {
    // new CityDataClient();
    // }
}
