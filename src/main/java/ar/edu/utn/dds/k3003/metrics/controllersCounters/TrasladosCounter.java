package ar.edu.utn.dds.k3003.metrics.controllersCounters;

import ar.edu.utn.dds.k3003.metrics.MetricsConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

public class TrasladosCounter {

    //Contadores
    private final Counter postSuccessfulTrasladosCounter;
    private final Counter getSuccessfulTrasladosCounter;

    private final Counter postFailedTrasladosCounter;
    private final Counter getFailedTrasladosCounter;

    public TrasladosCounter(MetricsConfig metricsConfig) {
        PrometheusMeterRegistry registry = metricsConfig.getRegistry();


        // Contadores para el endpoint POST /traslados
        postSuccessfulTrasladosCounter = Counter.builder("requests_post_traslados")
                .tag("endpoint", "/traslados")
                .tag("status","successful")
                .tag("method", "POST")
                .description("Total successful POST requests to /traslados")
                .register(registry);

        postFailedTrasladosCounter = Counter.builder("requests_post_traslados")
                .tag("endpoint", "/traslados")
                .tag("status","failed")
                .tag("method", "POST")
                .description("Total failed POST requests to /traslados")
                .register(registry);


        // Contadores para el endpoint GET /traslados/{Id}
        getSuccessfulTrasladosCounter = Counter.builder("requests_get_traslados")
                .tag("endpoint", "/traslados/{Id}")
                .tag("status","successful")
                .tag("method", "GET")
                .description("Total successful GET requests to /traslados/{Id}")
                .register(registry);

        getFailedTrasladosCounter = Counter.builder("requests_get_traslados")
                .tag("endpoint", "/traslados/{Id}")
                .tag("status","failed")
                .tag("method", "GET")
                .description("Total failed GET requests to /traslados/{Id}")
                .register(registry);

    }

    public void incrementSuccessfulPostCounter() {
        postSuccessfulTrasladosCounter.increment();
    }

    public void incrementSuccessfulGetCounter() {
        getSuccessfulTrasladosCounter.increment();
    }
    public void incrementFailedPostCounter() {
        postFailedTrasladosCounter.increment();
    }
    public void incrementFailedGetCounter() {
        getFailedTrasladosCounter.increment();
    }


}