package com.example.demo.controller;

import com.example.demo.service.DeliveryStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = "*")
public class DeliveryStreamController {

    @Autowired
    private DeliveryStreamService deliveryStreamService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return deliveryStreamService.subscribe();
    }
}


