package org.cityreporter.model.ksampost;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RuntimeMonitorData {
    private String monitorId;
    private List<Measurement> measurements;

    public String getMonitorId() {
	return monitorId;
    }

    public void setMonitorId(String monitorId) {
	this.monitorId = monitorId;
    }

    public List<Measurement> getMeasurements() {
	return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
	this.measurements = measurements;
    }

}
