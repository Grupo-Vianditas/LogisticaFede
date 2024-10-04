package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;
import ar.edu.utn.dds.k3003.model.Vianda;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ViandaRepository {

    private static AtomicLong seqId = new AtomicLong();
    private Collection<Vianda> viandas;

    public ViandaRepository(){
        this.viandas = new ArrayList<>();
    }

    public Vianda save(Vianda vianda) {
        if (Objects.isNull(vianda.getId())) {
            vianda.setId(seqId.getAndIncrement());
            this.viandas.add(vianda);
        }
        return vianda;
    }

    public Vianda buscarXQR(String codigoQr) throws NoSuchElementException {
        Optional<Vianda> first = this.viandas.stream().filter(x -> x.getCodigoQR().equals(codigoQr)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay un vianda con codigo qr: %s", codigoQr)
        ));
    }

    public Vianda findById(String id) {
        Optional<Vianda> first = this.viandas.stream().filter(x -> x.getCodigoQR().equals(id)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay un traslado de id: %s", id)
        ));
    }

}
