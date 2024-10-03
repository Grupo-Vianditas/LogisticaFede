package ar.edu.utn.dds.k3003.RabbitMQConsumer.Interfaces;

public interface ErrorHandler {
    void handleError(Exception e);
}
