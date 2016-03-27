package com.github.acme.microservices.web;

import com.github.acme.microservices.SensorConfigClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api-gateway")
public class ClientDepController {

    @Autowired
    private SensorConfigClient sensorConfigClient;

    @RequestMapping(method = RequestMethod.GET)
    public String sayHello(@RequestParam String name) {
        return String.format("Client %s was answered with: %s", name, sensorConfigClient.hello());
    }
}
