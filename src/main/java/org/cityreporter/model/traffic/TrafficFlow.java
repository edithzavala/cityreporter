package org.cityreporter.model.traffic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficFlow {
    private double SU; // Average Speed Uncut (SU) for the road segment
    private double JF; // Jam Factor

    public double getSU() {
	return SU;
    }

    public void setSU(double sU) {
	SU = sU;
    }

    public double getJF() {
	return JF;
    }

    public void setJF(double jF) {
	JF = jF;
    }

}
