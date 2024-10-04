package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Retiro {
    private Long id;
    private final String qrVianda;
    private final String tarjeta;
    private final LocalDateTime fechaRetiro;
    private final Integer heladeraId;

    public Retiro(String qrVianda, String tarjeta, Integer heladeraId) {
        this.qrVianda = qrVianda;
        this.tarjeta = tarjeta;
        this.heladeraId = heladeraId;
        this.fechaRetiro = LocalDateTime.now();
    }

}
