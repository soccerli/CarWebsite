package com.udacity.eureka.proxyController;

import com.google.gson.Gson;
import com.udacity.eureka.proxyService.PriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.ApplicationPath;

@Controller
public class ProxyAsConsumer {
    @Autowired
    private PriceService priceService;

    //private final GsonJsonParser gon = new GsonJsonParser();
    private final Gson gon= new Gson();


    public ProxyAsConsumer(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/prices/{id}")
    public ResponseEntity<?> getPrice(@PathVariable("id") String id){
       String response= priceService.getPriceById(id);
       if(response==null||response.length()==0) return ResponseEntity.notFound().build();

       //return ResponseEntity.ok(gon.toJson(response));
       //return ResponseEntity.ok(response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @GetMapping("/prices")
    public ResponseEntity<?> getAllPrices(){
        String response = priceService.getAllPrices();
        if(response==null||response.length()==0) return ResponseEntity.notFound().build();

       // return ResponseEntity.ok(gon.toJson(response));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }
}
