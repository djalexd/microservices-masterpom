package com.github.acme.microservices;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("sensor-config")
public interface SensorConfigClient {

    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    String hello();
}
