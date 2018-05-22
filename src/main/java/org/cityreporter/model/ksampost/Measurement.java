package org.cityreporter.model.ksampost;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Measurement {
    private String varId;
    private List<Measure> measures;

    public String getVarId() {
	return varId;
    }

    public void setVarId(String varId) {
	this.varId = varId;
    }

    public List<Measure> getMeasures() {
	return measures;
    }

    public void setMeasures(List<Measure> measures) {
	this.measures = measures;
    }

}
