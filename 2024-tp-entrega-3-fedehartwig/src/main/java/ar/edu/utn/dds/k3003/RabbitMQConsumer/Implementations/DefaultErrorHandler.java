package ar.edu.utn.dds.k3003.RabbitMQConsumer.Implementations;

import ar.edu.utn.dds.k3003.RabbitMQConsumer.Interfaces.ErrorHandler;

// Implementación concreta de ErrorHandler para manejar los errores
public class DefaultErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Exception e) {
        // Lógica para manejar el error
        System.err.println("Ocurrió un error durante el procesamiento: " + e.getMessage());

        // Aquí puedes agregar más lógica de manejo de errores, como:
        // - Enviar notificación de alerta
        // - Registrar el error en un log
        // - Reintentar el procesamiento, si es necesario
    }
}
