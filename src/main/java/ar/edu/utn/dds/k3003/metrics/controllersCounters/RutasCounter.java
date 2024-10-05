package ar.edu.utn.dds.k3003.metrics.controllersCounters;

import ar.edu.utn.dds.k3003.metrics.MetricsConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.core.instrument.Counter;


public class RutasCounter {

    //Contadores //TODO: falta agregar una esxepcion de cuando falla en agregar una ruta en
                // TODO: RutaController para asi poder medirlo agregando postFailedRutasCounter como contador.
    private final Counter postSuccessfulRutasCounter;
    private final Counter postFailedRutasCounter;

    public RutasCounter(MetricsConfig metricsConfig) {
        PrometheusMeterRegistry registry = metricsConfig.getRegistry();


        // Contadores para el endpoint POST /rutas
        postSuccessfulRutasCounter = Counter.builder("requests_post_rutas")
                .tag("endpoint", "/rutas")
                .tag("status","successful")
                .tag("method", "POST")
                .description("Total successful POST requests to /rutas")
                .register(registry);

        postFailedRutasCounter = Counter.builder("requests_post_rutas")
                .tag("endpoint", "/rutas")
                .tag("status","failed")
                .tag("method", "POST")
                .description("Total failed POST requests to /rutas")
                .register(registry);

    }

    public void incrementSuccessfulPostCounter() {
        postSuccessfulRutasCounter.increment();
    }
    public void incrementFailedPostCounter() {
        postFailedRutasCounter.increment();
    }


}