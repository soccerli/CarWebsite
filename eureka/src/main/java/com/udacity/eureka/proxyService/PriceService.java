package com.udacity.eureka.proxyService;


import com.netflix.discovery.shared.Applications;
import com.udacity.eureka.proxyController.ProxyAsConsumer;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PriceService {

    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate rt;
    private final Logger logger= LoggerFactory.getLogger(PriceService.class);

    //@Value("${pricing.service.name}")
    //private String serviceName;
    @Value("${pricing.service.name.keyword}")
    private String serviceNameKeywd;

    @Autowired
    DiscoveryClient discoveryClient;

    public PriceService(LoadBalancerClient loadBalancerClient, RestTemplate rt, DiscoveryClient discoveryClient) {
        this.loadBalancerClient = loadBalancerClient;
        this.rt = rt;
        this.discoveryClient=discoveryClient;
    }

    public String discoverService(){
        List<String> names=discoveryClient.getServices();
        String discoveredServiceName=null;
        for(String name:names) {
            logger.debug("discovered serviceName="+name);
            if(name.contains(serviceNameKeywd)) discoveredServiceName=name;
        }
        return discoveredServiceName;
    }

    public java.lang.String getEndPoint() {
        ServiceInstance serviceInstance=null;
        String discoveredSvcName=discoverService();
        try {
             logger.debug("serviceName="+discoveredSvcName);
             serviceInstance = loadBalancerClient.choose(discoveredSvcName);
             if(serviceInstance==null) throw(new Exception("can't find Price service"));
        }catch (Exception a){
            logger.debug(a.toString());
            return null;
        }
        return serviceInstance.getUri().toString();
    }

    public String getPriceById(String id){
        String targetUrl=getEndPoint()+"/prices/"+id;
        logger.debug("target url="+targetUrl);
        String response=null;
        try {
            response=rt.getForObject(targetUrl, String.class);
        }catch(Exception a){
            logger.debug(a.toString());
            return null;
        }
        return response;
    }

    public String getAllPrices(){
        String targetUrl=getEndPoint()+"/prices/";
        logger.debug("target url="+targetUrl);
        String response=null;
        try {
           response = rt.getForObject(targetUrl, String.class);
        }catch (Exception a){
            logger.debug(a.toString());
            return null;
        }
        return response;

    }
}
