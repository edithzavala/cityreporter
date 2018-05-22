package org.cityreporter.model.traffic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowItem {
    private List<FlowDetails> FI;

    public List<FlowDetails> getFI() {
	return FI;
    }

    public void setFI(List<FlowDetails> fI) {
	FI = fI;
    }

}
