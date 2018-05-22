package org.cityreporter.model.weather;

import java.util.List;

public class WeatherResponse {
    private List<Weather> weather;
    private WeatherDetails main;

    public List<Weather> getWeather() {
	return weather;
    }

    public void setWeather(List<Weather> weather) {
	this.weather = weather;
    }

    public WeatherDetails getMain() {
	return main;
    }

    public void setMain(WeatherDetails main) {
	this.main = main;
    }

}
