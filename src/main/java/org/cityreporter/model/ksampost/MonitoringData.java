package org.cityreporter.model.ksampost;

import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoringData {
    private String systemId;
    private long timeStamp;
    private List<Entry<String, Object>> context;
    private List<RuntimeMonitorData> monitors;

    public long getTimeStamp() {
	return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
	this.timeStamp = timeStamp;
    }

    public List<Entry<String, Object>> getContext() {
	return context;
    }

    public void setContext(List<Entry<String, Object>> context) {
	this.context = context;
    }

    public List<RuntimeMonitorData> getMonitors() {
	return monitors;
    }

    public void setMonitors(List<RuntimeMonitorData> monitors) {
	this.monitors = monitors;
    }

    public String getSystemId() {
	return systemId;
    }

    public void setSystemId(String systemId) {
	this.systemId = systemId;
    }

}
