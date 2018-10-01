package org.cityreporter.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.cityreporter.model.ksampost.Measure;
import org.cityreporter.model.ksampost.Measurement;
import org.cityreporter.model.ksampost.RuntimeMonitorData;
import org.cityreporter.model.traffic.TrafficResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TrafficClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherClient.class);
    private final String URL_TARFFIC;
    private final boolean isReplay;
    private final boolean isSimulation; // or fake data

    private Iterator<String> linesT;
    private final Map<String, String> routeLatLon;
    private final int instances;
    private String routeNo;

    public TrafficClient(String url, boolean replay, boolean simulation) {
	this.isReplay = replay;
	this.isSimulation = simulation;
	this.URL_TARFFIC = url;
	if (isReplay) {
	    try {
		linesT = Files.lines(Paths.get("/tmp/weka/heretraffic-trafficFactor_replay.txt"))
			.collect(Collectors.toList()).iterator();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	this.instances = 10;
	this.routeNo = "1";
	this.routeLatLon = new HashMap<>();
	this.routeLatLon.put("1", "57.685789,11.9814767");
	this.routeLatLon.put("2", "57.6856601,11.980733");
	this.routeLatLon.put("3", "57.6858606,11.9814408");
	// LOGGER.info(
	// "Heretraffic get traffic for route: " + this.routeNo + " (" +
	// this.routeLatLon.get(this.routeNo) + ")");
    }

    public void adaptMontitorParameter(String parameter, String value) {
	this.routeNo = value;
	// LOGGER.info("Heretraffic location parameter changed to path: " + this.routeNo
	// + " ("
	// + this.routeLatLon.get(this.routeNo) + ")");
    }

    public RuntimeMonitorData getTrafficData() {
	String trafficF = "";
	if (isReplay) {
	    trafficF = linesT.hasNext() ? linesT.next().split(" ")[1] : "0.0";
	} else {
	    // LOGGER.info("Heretraffic get location from path: " + this.routeNo + " ("
	    // + this.routeLatLon.get(this.routeNo) + ")");
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> response = restTemplate.getForEntity(
		    URL_TARFFIC + this.routeLatLon.get(this.routeNo) + "," + this.instances, String.class);

	    if (isSimulation) {
		switch (this.routeNo) {
		case "1":
		    trafficF = "6.0";// PATH 1 (L)
		    break;
		case "2":
		    trafficF = "2.0";// PATH 2 (S)
		    break;
		case "3":
		    trafficF = "6.0";// PATH 3 (d)
		    break;
		default:
		    break;
		}
	    } else {

		ObjectMapper ob = new ObjectMapper();
		ob.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		TrafficResponse tr = null;

		try {
		    tr = ob.readValue(response.getBody(), TrafficResponse.class);
		    trafficF = String.valueOf(
			    tr.getRWS().get(0).getRW().get(0).getFIS().get(0).getFI().get(0).getCF().get(0).getJF());
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }

	}

	LOGGER.info("Traffic Jam Factor: " + trafficF);

	RuntimeMonitorData rmd = null;
	/** Traffic Jam Factor **/
	// Create a measure with TS and Value
	Measure m = new Measure();
	m.setmTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
	m.setValue(trafficF);
	// Add measure to corresponding measurement i.e., monitoring variable
	Measurement mrmt = new Measurement();
	mrmt.setVarId("trafficFactor");
	mrmt.setMeasures(Arrays.asList(m));
	/***************/

	// Add measurements, i.e., monitoring variables to the weather monitor
	rmd = new RuntimeMonitorData();
	rmd.setMonitorId("heretraffic");
	rmd.setMeasurements(Arrays.asList(mrmt));

	return rmd;
    }
}
