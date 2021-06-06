package com.udacity.vehicles.client.prices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PriceClientRT {

    @Autowired
    RestTemplate rt;

    private final Logger logger=LoggerFactory.getLogger(PriceClientRT.class);
    public PriceClientRT(RestTemplate rt) {
        this.rt = rt;
    }

   // @Value("${pricing.endpoint}")
    @Value("${eureka.endpoint}")
    private String baseUrl;

    public String getPrice(String id){
        logger.debug("baseUrl="+baseUrl);
        Price price=rt.getForObject(baseUrl+"/prices/"+id,Price.class);
        return String.format("%s %s",price.getCurrency(), price.getPrice());
    }
}
