package ar.edu.utn.dds.k3003.RabbitMQConsumer.Implementations;

import ar.edu.utn.dds.k3003.RabbitMQConsumer.Interfaces.MessageHandler;

public class DefaultMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(String message) throws Exception {

        System.out.println("Mensaje recibido : " + message);

    }
}

