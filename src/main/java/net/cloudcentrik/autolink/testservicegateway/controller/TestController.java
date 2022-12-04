package net.cloudcentrik.autolink.testservicegateway.controller;

import net.cloudcentrik.autolink.testservicegateway.model.Greeting;
import net.cloudcentrik.autolink.testservicegateway.service.HttpTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @Autowired
    private HttpTestService httpTestService;

    @GetMapping("/test")
    public Mono<Greeting> test() {
        return httpTestService.callTest("start");
    }

    @GetMapping("/test-cb")
    public Mono<Greeting> testWithCB() {
        return httpTestService.callTestWithCircuitBreaker("start");
    }


    @RequestMapping("/hello")
    public Mono<String> hello(@RequestParam(value = "name", defaultValue = "John") String name) {
        return WebClient.builder()
                .build().get().uri("http://test-service/greetings")
                .retrieve().bodyToMono(String.class)
                .map(greeting -> String.format("%s, %s!", greeting, name));
    }
}
