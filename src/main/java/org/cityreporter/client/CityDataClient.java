package org.cityreporter.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.cityreporter.configuration.CityReporterConfig;
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
    private final String configPath = "/tmp/config/";
    // One thread for task with old params, one for same task with new params
    private final ScheduledExecutorService schedulerW = Executors.newScheduledThreadPool(2);
    private final ScheduledExecutorService schedulerT = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> futureTaskW;
    private ScheduledFuture<?> futureTaskT;
    private Runnable taskW;
    private Runnable taskT;
    private final WeatherClient wc;
    private final TrafficClient tc;

    private CityReporterConfig config;
    private String SYSTEM_ID;
    private String URL_KSAM;
    private int PORT_OPENDLV;
    private String HOST_OPENDLV;

    private Map<String, Boolean> monitorsState;
    private Map<String, Integer> monitorsFreq;

    public CityDataClient(String configFile) {
	super();
	ObjectMapper mapper = new ObjectMapper();
	try {
	    String dataConfig = new String(Files.readAllBytes(Paths.get(this.configPath + configFile)));
	    this.config = mapper.readValue(dataConfig, CityReporterConfig.class);
	    this.PORT_OPENDLV = this.config.getPortOpenDLV();
	    this.HOST_OPENDLV = this.config.getHostOpenDLV();
	    this.SYSTEM_ID = this.config.getSystemId();
	    this.URL_KSAM = "http://" + this.config.getHostKsam() + ":" + this.config.getPortKsam() + "/"
		    + this.SYSTEM_ID + "/monitoringData";
	} catch (IOException e) {
	    e.printStackTrace();
	}

	this.wc = new WeatherClient(this.config.getUrlWeather(), this.config.isReplay());
	this.tc = new TrafficClient(this.config.getUrlTraffic(), this.config.isReplay(), this.config.isSimulation());
	this.monitorsState = new HashMap<>();
	this.monitorsState.put("heretraffic", true);
	this.monitorsState.put("openweathermap", true);
	this.monitorsFreq = new HashMap<>();
	this.monitorsFreq.put("heretraffic", 60000);
	this.monitorsFreq.put("openweathermap", 60000);

	/** Monitor weather every 60000 ms **/
	this.taskW = new Runnable() {
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
			    Arrays.asList(""));
		    // Arrays.asList("laneFollower"));
		    monData.setContext(Arrays.asList(contextVar));
		    // LOGGER.info(monData.toString());
		    postData(monData);
		}
	    }
	};
	this.futureTaskW = this.schedulerW.scheduleAtFixedRate(taskW, 0, this.monitorsFreq.get("openweathermap"),
		TimeUnit.MILLISECONDS);
	/*************************************/

	/** Monitor traffic every 1000 ms **/
	this.taskT = new Runnable() {
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
			    Arrays.asList(""));
		    // Arrays.asList("laneFollower"));
		    monData.setContext(Arrays.asList(contextVar));
		    // LOGGER.info(monData.toString());
		    postData(monData);
		    postTrafficDataToLaneFollower(monData);
		}
	    }
	};
	this.futureTaskT = this.schedulerT.scheduleAtFixedRate(taskT, 0, this.monitorsFreq.get("heretraffic"),
		TimeUnit.MILLISECONDS);
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
	    LOGGER.info("Post ksam: " + jsonMonData);
	} catch (JsonProcessingException e) {
	    e.printStackTrace();
	}
    }

    private void postTrafficDataToLaneFollower(MonitoringData monData) {
	String dataString = "TrafficFactor:"
		+ monData.getMonitors().get(0).getMeasurements().get(0).getMeasures().get(0).getValue() + "\0";
	try {
	    Socket socket = new Socket(this.HOST_OPENDLV, this.PORT_OPENDLV);
	    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
	    byte[] data = dataString.getBytes();
	    dos.write(data);
	    dos.close();
	    socket.close();
	    LOGGER.info("Post OpenDLV " + dataString);
	} catch (IOException e) {
	    LOGGER.error(e.toString());
	}
    }

    private void changeReadInterval(ScheduledFuture<?> futureTask, ScheduledExecutorService scheduledExecutorService,
	    Runnable task, long time) {
	if (time > 0) {
	    if (futureTask != null) {
		futureTask.cancel(true);
	    }

	    futureTask = scheduledExecutorService.scheduleAtFixedRate(task, 0, time, TimeUnit.SECONDS);
	}
    }

    public boolean adaptMonitors(String adaptation) {
	ObjectMapper mapper = new ObjectMapper();
	try {
	    MonitorAdaptation adapt = mapper.readValue(adaptation, MonitorAdaptation.class);
	    adapt.getMonitorsToAdd().forEach(m -> {
		monitorsState.put(m, true);
		LOGGER.info("Add monitor: " + m);

	    });
	    adapt.getMonitorsToRemove().forEach(m -> {
		LOGGER.info("Remove monitor: " + m);
		monitorsState.put(m, false);
	    });
	    adapt.getParamsToAdapt().keySet().forEach(p -> {
		if (p.equals("traffic-route")) {
		    LOGGER.info("Heretraffic change location parameter");
		    tc.adaptMontitorParameter("Route", "2");
		} else if (p.equals("traffic-frequency")) {
		    this.changeReadInterval(futureTaskT, schedulerT, taskT,
			    Long.valueOf(adapt.getParamsToAdapt().get(p)));
		}
	    });
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return true;
    }

    public WeatherClient getWc() {
	return wc;
    }

    public TrafficClient getTc() {
	return tc;
    }

    // public static void main(String args[]) throws IOException,
    // ClassNotFoundException {
    // new CityDataClient();
    // }
}
