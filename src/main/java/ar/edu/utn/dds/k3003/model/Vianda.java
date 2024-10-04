package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor

public class Vianda {
    private Long id;
    private final String codigoQR;
    private final LocalDateTime fechaElaboracion;
    private final EstadoViandaEnum estado;
    private final Long colaboradorId;
    private final Integer heladeraId;

    public Vianda(String codigoQR, LocalDateTime fechaElaboracion, EstadoViandaEnum estado, Long colaboradorId, Integer heladeraId) {
        this.codigoQR = codigoQR;
        this.fechaElaboracion = fechaElaboracion;
        this.estado = estado;
        this.colaboradorId = colaboradorId;
        this.heladeraId = heladeraId;
    }
}
