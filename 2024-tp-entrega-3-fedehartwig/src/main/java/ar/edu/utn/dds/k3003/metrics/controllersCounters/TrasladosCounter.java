package ar.edu.utn.dds.k3003.metrics.controllersCounters;

import ar.edu.utn.dds.k3003.metrics.MetricsConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

public class TrasladosCounter {

    //Contadores
    private final Counter postSucessfulTrasladosCounter;
    private final Counter getSucessfulTrasladosCounter;

    public TrasladosCounter(MetricsConfig metricsConfig) {
        PrometheusMeterRegistry registry = metricsConfig.getRegistry();


        // Contadores para el endpoint POST /traslados
        postSucessfulTrasladosCounter = Counter.builder("requests_post_traslados")
                .tag("endpoint", "/traslados")
                .tag("status","successful")
                .tag("method", "POST")
                .description("Total successful POST requests to /traslados")
                .register(registry);


        // Contadores para el endpoint GET /traslados/{Id}
        getSucessfulTrasladosCounter = Counter.builder("requests_get_traslados")
                .tag("endpoint", "/traslados/{Id}")
                .tag("status","successful")
                .tag("method", "GET")
                .description("Total successful GET requests to /traslados/{Id}")
                .register(registry);

    }

    public void incrementSucessfulPostCounter() {
        postSucessfulTrasladosCounter.increment();
    }

    public void incrementSucessfulGetCounter() {
        getSucessfulTrasladosCounter.increment();
    }
}