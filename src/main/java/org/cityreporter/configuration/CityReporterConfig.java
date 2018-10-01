package org.cityreporter.configuration;

public class CityReporterConfig {

    private String hostOpenDLV;
    private int portOpenDLV;
    private String hostKsam;
    private int portKsam;
    private String systemId;
    private String urlTraffic;
    private String urlWeather;
    private boolean replay;
    private boolean simulation;

    public String getHostOpenDLV() {
	return hostOpenDLV;
    }

    public void setHostOpenDLV(String hostOpenDLV) {
	this.hostOpenDLV = hostOpenDLV;
    }

    public int getPortOpenDLV() {
	return portOpenDLV;
    }

    public void setPortOpenDLV(int portOpenDLV) {
	this.portOpenDLV = portOpenDLV;
    }

    public int getPortKsam() {
	return portKsam;
    }

    public void setPortKsam(int portKsam) {
	this.portKsam = portKsam;
    }

    public String getHostKsam() {
	return hostKsam;
    }

    public void setHostKsam(String hostKsam) {
	this.hostKsam = hostKsam;
    }

    public String getSystemId() {
	return systemId;
    }

    public void setSystemId(String systemId) {
	this.systemId = systemId;
    }

    public String getUrlTraffic() {
	return urlTraffic;
    }

    public void setUrlTraffic(String urlTraffic) {
	this.urlTraffic = urlTraffic;
    }

    public String getUrlWeather() {
	return urlWeather;
    }

    public void setUrlWeather(String urlWeather) {
	this.urlWeather = urlWeather;
    }

    public boolean isReplay() {
	return replay;
    }

    public void setReplay(boolean replay) {
	this.replay = replay;
    }

    public boolean isSimulation() {
	return simulation;
    }

    public void setSimulation(boolean simulation) {
	this.simulation = simulation;
    }

}
