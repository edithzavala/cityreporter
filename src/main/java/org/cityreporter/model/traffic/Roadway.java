package org.cityreporter.model.traffic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Roadway {
    private List<FlowItems> RW;

    public List<FlowItems> getRW() {
	return RW;
    }

    public void setRW(List<FlowItems> rW) {
	RW = rW;
    }

}
