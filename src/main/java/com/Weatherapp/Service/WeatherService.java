package com.Weatherapp.Service;

import com.Weatherapp.dto.WeatherResponse;
import com.Weatherapp.Exception.CityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherResponse getWeather(String city) {
        try {
            String url = apiUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric";

            Map response = restTemplate.getForObject(url, Map.class);

            Map main = (Map) response.get("main");
            List<Map> weatherList = (List<Map>) response.get("weather");

            WeatherResponse wr = new WeatherResponse();
            wr.setCity(city);
            wr.setTemperature((Double) main.get("temp"));
            wr.setHumidity((Integer) main.get("humidity"));
            wr.setDescription((String) weatherList.get(0).get("description"));

            return wr;
        } catch (Exception e) {
            throw new CityNotFoundException("City not found: " + city);
        }
    }
}
