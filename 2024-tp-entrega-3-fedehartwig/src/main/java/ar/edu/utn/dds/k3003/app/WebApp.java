package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.controller.RutaController;
import ar.edu.utn.dds.k3003.controller.TrasladoController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
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

        // Cosas de Migue para grafana

        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        // agregar aquí cualquier tag que aplique a todas las métrivas de la app
        // (e.g. EC2 region, stack, instance id, server group)
        registry.config().commonTags("app", "metrics-sample");

        // agregamos a nuestro reigstro de métricas todo lo relacionado a infra/tech
        // de la instancia y JVM
        try (var jvmGcMetrics = new JvmGcMetrics();
             var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
            jvmGcMetrics.bindTo(registry);
            jvmHeapPressureMetrics.bindTo(registry);
        }
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);


        Counter heladerasPostCounter = Counter.builder("requests_total")
                .tag("endpoint", "/heladeras")
                .tag("method", "POST")
                .description("Total POST requests to /heladeras")
                .register(registry);

        Counter heladerasGetCounter = Counter.builder("requests_total")
                .tag("endpoint", "/heladeras/{heladeraId}")
                .tag("method", "GET")
                .description("Total GET requests to /heladeras/{heladeraId}")
                .register(registry);

        Counter temperaturasPostCounter = Counter.builder("requests_total")
                .tag("endpoint", "/temperaturas")
                .tag("method", "POST")
                .description("Total POST requests to /temperaturas")
                .register(registry);



        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                configureObjectMapper(mapper);
            }));
        }).start(port);

        var rutaController = new RutaController(fachada);
        var trasladosController = new TrasladoController(fachada);


        app.post("/rutas", rutaController::agregar);
        app.get("/traslados/search/findByColaboradorId", trasladosController::trasladosColaborador);
        app.post("/traslados", trasladosController::asignar);
        app.post("/borrar", trasladosController::borrar);
        app.get("/traslados/{id}", trasladosController::obtener);

        // Controller metricas
        app.get("/metrics", ctx -> {
                    // chequear el header de authorization y chequear el token bearer
                    // configurado
                    var auth = ctx.header("Authorization");

                    if (auth != null && auth.intern() == "Bearer " + TOKEN) {
                        ctx.contentType("text/plain; version=0.0.4")
                                .result(registry.scrape());
                    } else {
                        // si el token no es el apropiado, devolver error,
                        // desautorizado
                        // este paso es necesario para que Grafana online
                        // permita el acceso
                        ctx.status(401).json("unauthorized access");
                    }
                }
        );


        app.get("/", ctx -> ctx.result("Hola, soy una API y no un easter egg."));

        // Seteo la jodita del conejo
        MessageHandler messageHandler = new TemperaturaMessageHandler(fachada);
        ErrorHandler errorHandler = new DefaultErrorHandler();

        RabbitMQCloudConsumer consumer = new RabbitMQCloudConsumer(
                env.get("QUEUE_NAME"),
                env.get("QUEUE_HOST"),
                env.get("QUEUE_USERNAME"),
                env.get("QUEUE_PASSWORD"),
                messageHandler,
                errorHandler
        );

        try {
            // Iniciar conexión y consumir mensajes
            consumer.iniciarConexion();
            consumer.consumirMensajes();

            // Mantener la aplicación en ejecución
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            System.err.println("Error inicializando consumidor: " + e.getMessage());
        } finally {
            try {
                consumer.cerrarConexion();
            } catch (IOException | TimeoutException e) {
                System.err.println("Error cerrando conexión: " + e.getMessage());
            }
        }


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
