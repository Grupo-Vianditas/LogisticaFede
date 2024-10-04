package ar.edu.utn.dds.k3003.metrics.controllersCounters;

import ar.edu.utn.dds.k3003.metrics.MetricsConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.core.instrument.Counter;


public class RutasCounter {

    //Contadores //todo : falta contar cuando falla
    private final Counter postSucessfulRutasCounter;

    public RutasCounter(MetricsConfig metricsConfig) {
        PrometheusMeterRegistry registry = metricsConfig.getRegistry();


        // Contadores para el endpoint POST /rutas
        postSucessfulRutasCounter = Counter.builder("requests_post_rutas")
                .tag("endpoint", "/rutas")
                .tag("status","successful")
                .tag("method", "POST")
                .description("Total successful POST requests to /rutas")
                .register(registry);

    }

    public void incrementSucessfulPostCounter() {
        postSucessfulRutasCounter.increment();
    }


}