package ar.edu.utn.dds.k3003.RabbitMQConsumer;

import ar.edu.utn.dds.k3003.RabbitMQConsumer.Interfaces.ErrorHandler;
import ar.edu.utn.dds.k3003.RabbitMQConsumer.Interfaces.MessageHandler;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQCloudConsumer {

    private final String queueName;
    private final ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private final MessageHandler messageHandler;
    private final ErrorHandler errorHandler;

    public RabbitMQCloudConsumer(String queueName, String host, String username, String password,
                                 MessageHandler messageHandler, ErrorHandler errorHandler) {
        this.queueName = queueName;
        this.messageHandler = messageHandler;
        this.errorHandler = errorHandler;

        // Configurar la fábrica de conexiones
        this.factory = new ConnectionFactory();
        this.factory.setHost(host);
        this.factory.setUsername(username);
        this.factory.setPassword(password);
        this.factory.setVirtualHost(username); // El username es el mismo para el VH
        this.factory.setAutomaticRecoveryEnabled(true);  // Habilitar reconexión automática
        this.factory.setNetworkRecoveryInterval(5000);   // Intentar reconectar cada 5 segundos
    }

    // Iniciar la conexión
    public void iniciarConexion() throws IOException, TimeoutException {
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        channel.queueDeclare(queueName, true, false, false, null);
        channel.basicQos(1);  // Limitar a 1 mensaje por consumidor
        System.out.println("Conexión a RabbitMQ exitosa. Esperando mensajes...");
    }

    // Iniciar el consumo de mensajes
    public void consumirMensajes() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            try {
                // Procesar el mensaje usando el handler inyectado
                messageHandler.handleMessage(message);

                // Confirmar el mensaje
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                errorHandler.handleError(e);  // Manejar el error usando el handler inyectado

                // Reinsertar el mensaje en la cola
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
            }
        };

        // Escuchar la cola sin auto-acknowledge
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
    }

    // Cerrar la conexión y el canal
    public void cerrarConexion() throws IOException, TimeoutException {
        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}