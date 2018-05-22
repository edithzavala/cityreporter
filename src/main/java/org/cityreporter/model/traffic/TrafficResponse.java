package org.cityreporter.model.traffic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficResponse {
    private List<Roadway> RWS;

    public List<Roadway> getRWS() {
	return RWS;
    }

    public void setRWS(List<Roadway> rWS) {
	RWS = rWS;
    }
}
