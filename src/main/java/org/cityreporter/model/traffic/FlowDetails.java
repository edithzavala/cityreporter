package org.cityreporter.model.traffic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowDetails {
    private List<TrafficFlow> CF;

    public List<TrafficFlow> getCF() {
	return CF;
    }

    public void setCF(List<TrafficFlow> cF) {
	CF = cF;
    }
}
