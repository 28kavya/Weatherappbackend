package com.Weatherapp.Service;

import com.Weatherapp.dto.WeatherResponse;
import com.Weatherapp.Exception.CityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherResponse getWeather(String city) {
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric",
                    apiUrl, city, apiKey);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            List<Map<String, Object>> weatherList =
                    (List<Map<String, Object>>) response.get("weather");

            WeatherResponse wr = new WeatherResponse();
            wr.setCity(city);

            // Safe casting
            Object temp = main.get("temp");
            wr.setTemperature(temp instanceof Double ? (Double) temp :
                    ((Number) temp).doubleValue());

            Object humidity = main.get("humidity");
            wr.setHumidity(humidity instanceof Integer ? (Integer) humidity :
                    ((Number) humidity).intValue());

            wr.setDescription((String) weatherList.get(0).get("description"));

            return wr;

        } catch (HttpClientErrorException.NotFound e) {
            throw new CityNotFoundException("City not found: " + city);
        } catch (RestClientException e) {
            throw new RuntimeException("Error fetching weather data", e);
        }
    }
}