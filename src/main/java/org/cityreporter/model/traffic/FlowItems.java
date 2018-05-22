package org.cityreporter.model.traffic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowItems {
    private List<FlowItem> FIS;

    public List<FlowItem> getFIS() {
	return FIS;
    }

    public void setFIS(List<FlowItem> fIS) {
	FIS = fIS;
    }
}
