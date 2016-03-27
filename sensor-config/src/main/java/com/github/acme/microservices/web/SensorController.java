package com.github.acme.microservices.web;

import com.github.acme.microservices.model.vo.SensorVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    @RequestMapping(method = RequestMethod.POST)
    public String createSensor(final Principal principal,
                             final @Valid SensorVO sensor) {
        return "TODO: New sensor not yet created";
    }
}
