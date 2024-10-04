package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Retiro;
import ar.edu.utn.dds.k3003.model.Traslado;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class RetiroDTORepository {

    private static AtomicLong seqId = new AtomicLong();
    private Collection<Retiro> retiros;

    public RetiroDTORepository() {
        this.retiros = new ArrayList<>();
    }

    public Retiro save(Retiro retiro) {
        if (Objects.isNull(retiro.getId())) {
            retiro.setId(seqId.getAndIncrement());
            this.retiros.add(retiro);
        }
        return retiro;
    }

    public Retiro findById(Long id) {
        Optional<Retiro> first = this.retiros.stream().filter(x -> x.getId().equals(id)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay una ruta de id: %s", id)
        ));
    }

    public Retiro findByRetiros(Long id) {
        Optional<Retiro> first = this.retiros.stream().filter(x -> x.getId().equals(id)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay un traslado de id: %s", id)
        ));
    }
}
