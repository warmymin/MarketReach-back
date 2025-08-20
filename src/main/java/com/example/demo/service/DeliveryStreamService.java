package com.example.demo.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DeliveryStreamService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("OK")
                    .reconnectTime(3000)
                    .comment("connected"));
        } catch (IOException ignored) {}
        return emitter;
    }

    public void publish(Object payload) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("delivery")
                        .data(payload, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }
}


