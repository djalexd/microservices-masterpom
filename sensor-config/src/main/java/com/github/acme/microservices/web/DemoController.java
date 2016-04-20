package com.github.acme.microservices.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Value("${foo:default}")
    private String fooValue;

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping(method = RequestMethod.GET)
    public String getHello() {
        int size = discoveryClient.getInstances("sensor-config").size();
        return "Hello, world! This is " + fooValue + " and running with " + size + " instances";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/me")
    public String me() {
	    final StringBuilder sb = new StringBuilder();

	    discoveryClient.getInstances("sensor-config").forEach(c -> sb.append(c.getServiceId()).append(" -> ").append(c.getMetadata()).append("<br/>"));
        sb.append(discoveryClient.getLocalServiceInstance());

	    return sb.toString();
    }

	@RequestMapping(method = RequestMethod.GET, value = "/me2")
	public String me2() {
		final StringBuilder sb = new StringBuilder();

		discoveryClient.getInstances("sensor-config").forEach(c -> sb.append(c.getServiceId()).append(" -> ").append(c.getMetadata()).append("<br/>"));
		sb.append(discoveryClient.getLocalServiceInstance());

		return sb.toString();
	}

}
