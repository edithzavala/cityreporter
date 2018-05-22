package org.cityreporter.client;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

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
    private String URL_TARFFIC = "";

    public TrafficClient() {
    }

    public RuntimeMonitorData getTrafficData() {
	RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<String> response = restTemplate.getForEntity(URL_TARFFIC, String.class);

	ObjectMapper ob = new ObjectMapper();
	ob.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	TrafficResponse tr = null;
	RuntimeMonitorData rmd = null;
	try {
	    tr = ob.readValue(response.getBody(), TrafficResponse.class);

	    // LOGGER.info("Jam factor: "
	    // +
	    // tr.getRWS().get(0).getRW().get(0).getFIS().get(0).getFI().get(0).getCF().get(0).getJF());
	    /** Traffic Jam Factor **/
	    // Create a measure with TS and Value
	    Measure m = new Measure();
	    m.setmTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
	    m.setValue(String
		    .valueOf(tr.getRWS().get(0).getRW().get(0).getFIS().get(0).getFI().get(0).getCF().get(0).getJF()));
	    // Add measure to corresponding measurement i.e., monitoring variable
	    Measurement mrmt = new Measurement();
	    mrmt.setVarId("trafficFactor");
	    mrmt.setMeasures(Arrays.asList(m));
	    /***************/

	    // Add measurements, i.e., monitoring variables to the weather monitor
	    rmd = new RuntimeMonitorData();
	    rmd.setMonitorId("heretraffic");
	    rmd.setMeasurements(Arrays.asList(mrmt));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return rmd;
    }
}
