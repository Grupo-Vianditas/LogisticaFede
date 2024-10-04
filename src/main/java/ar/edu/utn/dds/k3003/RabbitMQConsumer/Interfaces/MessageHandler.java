package ar.edu.utn.dds.k3003.RabbitMQConsumer.Interfaces;

public interface MessageHandler {

    void handleMessage(String message) throws Exception;
}
