package net.cloudcentrik.autolink.testservicegateway.service;

import lombok.extern.log4j.Log4j2;
import net.cloudcentrik.autolink.testservicegateway.model.Greeting;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
//@Service
@Component
public class HttpTestService {

    private final WebClient webClient;
    private final ReactiveCircuitBreaker testServiceCircuitBreaker;

    public HttpTestService(WebClient webClient,ReactiveCircuitBreakerFactory circuitBreakerFactory){
        this.testServiceCircuitBreaker = circuitBreakerFactory.create("test-cb");
        this.webClient = webClient;
    }

    public Mono<Greeting> callTest(String path) {
        return call(webClient,"http://test-service/"+path);
    }

    public Mono<Greeting> callTestWithCircuitBreaker(String path) {
        return callWithCircuitBreaker(webClient,"http://test-service/"+path);
    }

    Mono<Greeting> call(WebClient http, String url) {
        log.info("****************************************************** call: {}",url);
        return http
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Greeting.class);
    }


    Mono<Greeting> callWithCircuitBreaker(WebClient http, String url) {
        log.info("****************************************************** callWithCircuitBreaker: {}",url);
        return this.testServiceCircuitBreaker.run(
                http.get().uri(url).retrieve().bodyToMono(Greeting.class),
                throwable -> {
                    log.warn("Error making request to book service", throwable);
                    return Mono.just(new Greeting("test-service is not active at this moment!!!"));
                });
    }

}
