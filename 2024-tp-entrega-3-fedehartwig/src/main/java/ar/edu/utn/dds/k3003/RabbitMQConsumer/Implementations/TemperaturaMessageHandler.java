package ar.edu.utn.dds.k3003.RabbitMQConsumer.Implementations;

import ar.edu.utn.dds.k3003.RabbitMQConsumer.Interfaces.MessageHandler;
import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.util.NoSuchElementException;


public class TemperaturaMessageHandler implements MessageHandler {

    private Fachada fachada;

    public TemperaturaMessageHandler(Fachada fachada){
        this.fachada = fachada;
    }

    @Override
    public void handleMessage(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            TemperaturaDTO tempMessage = objectMapper.readValue(message, TemperaturaDTO.class);

            //fachada.temperatura(tempMessage);
        }catch (NoSuchElementException nsee){
            System.out.println("Error en TemperaturaMessageHandler : " + nsee);
        }catch (Exception e){
            System.out.println("Error en TemperaturaMessageHandler : " + e);

        }
    }

}
