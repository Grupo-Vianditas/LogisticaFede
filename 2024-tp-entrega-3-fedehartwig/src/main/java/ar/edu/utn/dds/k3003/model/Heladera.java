package ar.edu.utn.dds.k3003.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class Heladera {
    private Integer id;
    private final String nombre;

    public Heladera(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
