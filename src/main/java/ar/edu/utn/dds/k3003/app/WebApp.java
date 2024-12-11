package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.controller.RutaController;
import ar.edu.utn.dds.k3003.controller.TrasladoController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import ar.edu.utn.dds.k3003.metrics.MetricsConfig;
import ar.edu.utn.dds.k3003.metrics.controllersCounters.RutasCounter;
import ar.edu.utn.dds.k3003.metrics.controllersCounters.TrasladosCounter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;

import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.Counter;

import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import ar.edu.utn.dds.k3003.RabbitMQConsumer.Implementations.DefaultErrorHandler;
import ar.edu.utn.dds.k3003.RabbitMQConsumer.Implementations.TemperaturaMessageHandler;
import ar.edu.utn.dds.k3003.RabbitMQConsumer.Interfaces.ErrorHandler;
import ar.edu.utn.dds.k3003.RabbitMQConsumer.Interfaces.MessageHandler;
import ar.edu.utn.dds.k3003.RabbitMQConsumer.RabbitMQCloudConsumer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

public class WebApp {

    private static final String TOKEN = "token123";

    public static void main(String[] args) {

        var env = System.getenv();

        // Variables de entorno
        var URL_VIANDAS = env.get("URL_VIANDAS");
        var URL_LOGISTICA = env.get("URL_LOGISTICA");
        var URL_HELADERAS = env.get("URL_HELADERAS");
        var URL_COLABORADORES = env.get("URL_COLABORADORES");




        var objectMapper = createObjectMapper();
        var fachada = new Fachada();

        MetricsConfig metricsConfig = new MetricsConfig();
        PrometheusMeterRegistry registry = metricsConfig.getRegistry();
        final var micrometerPlugin = new MicrometerPlugin(config -> config.registry = registry);

        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));

        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));


        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                configureObjectMapper(mapper);
            }));

            // Registra el plugin de métricas
            config.registerPlugin(micrometerPlugin);
        }).start(port);


        var rutaController = new RutaController(fachada, new RutasCounter(metricsConfig));
        var trasladosController = new TrasladoController(fachada, new TrasladosCounter(metricsConfig));

        app.post("/rutas", rutaController::agregar);
        app.get("/traslados/search/findByColaboradorId", trasladosController::trasladosColaborador);
        app.post("/traslados", trasladosController::asignar);
        app.post("/borrar", trasladosController::borrar);

        app.post("/traslados/{id}/depositar", trasladosController::depositar);
        app.post("/traslados/{id}/retirar", trasladosController::retirar);
        app.get("/traslados/{id}", trasladosController::obtener);

        app.patch("/traslados/{id}",trasladosController::modificar);

        app.get("/status", ctx -> ctx.status(HttpStatus.OK));


        // Controller metricas
        app.get("/metrics", ctx -> {
            var auth = ctx.header("Authorization");

            if (auth != null && auth.equals("Bearer " + TOKEN)) {
                ctx.contentType("text/plain; version=0.0.4")
                        .result(registry.scrape());
            } else {
                ctx.status(401).json("unauthorized access");
            }
        });

        app.get("/", ctx -> ctx.result("Hola, soy una API y no un easter egg."));



    }




    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }

    public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
    }
}
