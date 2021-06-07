package com.udacity.vehicles.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.vehicles.domain.car.Car;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class RestClientForTest {
    private final String baseUrl="http://localhost:";
    private final RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders header=new HttpHeaders();
    private JSONObject jsonObject=new JSONObject();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger=  LoggerFactory.getLogger(RestClientForTest.class);

    @PostConstruct
    private void afterConstruct(){
        header.setContentType(MediaType.APPLICATION_JSON);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String carToJsonString(Car car) throws JsonProcessingException {
        return objectMapper.writeValueAsString(car);
    }
    public Car JsonStringToCar(String jsonInString) throws IOException {
        return objectMapper.readValue(jsonInString,Car.class);
    }

    //Post request
    public String create(int port,String uri,String jsonInString) throws JsonProcessingException {
        HttpEntity<String> request=new HttpEntity<String >(jsonInString,header);
        return restTemplate.postForObject(baseUrl+port+uri,request,String.class);

    }

    //put reqeust
    public ResponseEntity<String>  update(int port,String uri,String jsonInString) throws JsonProcessingException, URISyntaxException {
        HttpEntity<String> request=new HttpEntity<String >(jsonInString,header);
        return restTemplate.exchange(new URI(baseUrl+port+uri), HttpMethod.PUT,request,String.class);
    }

    //get request
    public String get(int port,String uri) throws Exception{
        return restTemplate.getForObject(baseUrl+port+uri,String.class);
    }

    //delete request
    public ResponseEntity<String> delete(int port, String uri) {
        return restTemplate.exchange(baseUrl+port+uri,HttpMethod.DELETE,null,String.class,(Object) uri);
    }

}
