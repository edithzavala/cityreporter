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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrafficClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherClient.class);
    // INVALID KEY
    private final String URL_TARFFIC = "https://traffic.cit.api.here.com/traffic/6.2/flow.json?app_id=FGariHtsUJwg53I3Cj3o&app_code=s2S6hp1Y42Xv3iGN9sZYjw&prox=57.6891394,11.9875979,10";
    private final boolean replay = false;

    private Iterator<String> linesT;
    private int path;// Could be an adaptive parameters for getting weather

    public TrafficClient() {
	if (replay) {
	    try {
		linesT = Files.lines(Paths.get("/tmp/weka/heretraffic-trafficFactor_replay.txt"))
			.collect(Collectors.toList()).iterator();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	this.path = 1;
    }

    public void adaptMontitorParameter(String parameter, String value) {
	this.path = Integer.valueOf(value); // example of para meter adaptation
	LOGGER.info("Heretraffic location parameter changed");
    }

    public RuntimeMonitorData getTrafficData() {
	String trafficF = "";
	if (replay) {
	    trafficF = linesT.hasNext() ? linesT.next().split(" ")[1] : "0.0";
	} else {
	    // RestTemplate restTemplate = new RestTemplate();
	    // ResponseEntity<String> response = restTemplate.getForEntity(URL_TARFFIC,
	    // String.class);
	    //
	    // ObjectMapper ob = new ObjectMapper();
	    // ob.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	    // TrafficResponse tr = null;
	    // try {
	    // tr = ob.readValue(response.getBody(), TrafficResponse.class);
	    // trafficF = String.valueOf(
	    // tr.getRWS().get(0).getRW().get(0).getFIS().get(0).getFI().get(0).getCF().get(0).getJF());
	    // // LOGGER.info("Jam factor: "
	    // // +
	    // //
	    // tr.getRWS().get(0).getRW().get(0).getFIS().get(0).getFI().get(0).getCF().get(0).getJF());
	    // } catch (IOException e) {
	    // e.printStackTrace();
	    // }
	    switch (this.path) {
	    case 1:
		trafficF = "3.0";// PATH 1 (L)
		break;
	    case 2:
		trafficF = "2.0";// PATH 2 (S)
		break;
	    case 3:
		trafficF = "6.0";// PATH 3 (d)
		break;
	    default:
		break;
	    }
	}

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
