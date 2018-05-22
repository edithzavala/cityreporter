package org.cityreporter.model.ksampost;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Measure {
    private long mTimeStamp;
    private String value;

    public long getmTimeStamp() {
	return mTimeStamp;
    }

    public void setmTimeStamp(long mTimeStamp) {
	this.mTimeStamp = mTimeStamp;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

}
