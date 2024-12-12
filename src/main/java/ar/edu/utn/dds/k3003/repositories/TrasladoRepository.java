package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrasladoRepository {

  private static AtomicLong seqId = new AtomicLong();

  private EntityManager entityManager;

  private Collection<Traslado> traslados;

  public TrasladoRepository(EntityManager entityManager) {
    super();
    this.entityManager = entityManager;
  }

  public TrasladoRepository() {
    this.traslados = new ArrayList<>();
  }

  public Traslado save(Traslado traslado) {
    if (Objects.isNull(traslado.getId())) {
      entityManager.getTransaction()
          .begin();
      entityManager.persist(traslado);

      entityManager.getTransaction()
          .commit();
    }

    return traslado;
  }

  public void modificarEstado(
      Long idTraslado,
      EstadoTrasladoEnum estado
  ) {

    entityManager.getTransaction()
        .begin();
    Traslado traslado = this.findById(idTraslado);
    traslado.setEstado(estado);
    entityManager.merge(traslado);
    entityManager.getTransaction()
        .commit();
  }

  public Traslado findById(Long id) {

    Traslado traslado = entityManager.find(Traslado.class, id);

    if (Objects.isNull(traslado)) {
      throw new NoSuchElementException(String.format("No hay una ruta de id: %s", id));
    }

    return traslado;
  }

  public List<Traslado> findByColaborador(
      Long colaboradorId,
      Integer mes,
      Integer anio
  ) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Traslado> criteriaQuery = criteriaBuilder.createQuery(Traslado.class);
    Root<Traslado> root = criteriaQuery.from(Traslado.class);

    Predicate colaborador_id = criteriaBuilder.equal(root.get("colaboradorId"), colaboradorId);

    Predicate fecha_mes = (Objects.nonNull(mes)) ? criteriaBuilder.equal(
        criteriaBuilder.function("MONTH", Integer.class, root.get("fechaTraslado")), mes) : null;
    Predicate fecha_anio = (Objects.nonNull(anio)) ? criteriaBuilder.equal(
        criteriaBuilder.function("YEAR", Integer.class, root.get("fechaTraslado")), anio) : null;

    Predicate finalPredicate = colaborador_id;
    if (Objects.nonNull(fecha_mes)) {
      finalPredicate = criteriaBuilder.and(finalPredicate, fecha_mes);
    }
    if (Objects.nonNull(fecha_anio)) {
      finalPredicate = criteriaBuilder.and(finalPredicate, fecha_anio);
    }

    criteriaQuery.select(root)
        .where(finalPredicate);

    return entityManager.createQuery(criteriaQuery)
        .getResultList();
  }

  public List<Traslado> findByRuta(Ruta ruta) {

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Traslado> criteriaQuery = criteriaBuilder.createQuery(Traslado.class);
    Root<Traslado> root = criteriaQuery.from(Traslado.class);

    Predicate ruta_query = criteriaBuilder.equal(root.get("ruta"), ruta);

    criteriaQuery.select(root)
        .where(criteriaBuilder.and(ruta_query));

    return entityManager.createQuery(criteriaQuery)
        .getResultList();
  }

    /*
    public Traslado actualizarTrasladoRetirado(Long id){
        Traslado trasladoActualizar = findById(id);
        Traslado trasladoBase = new Traslado(trasladoActualizar.getQrVianda(), EstadoTrasladoEnum
        .EN_VIAJE, LocalDateTime.now(), trasladoActualizar.getHeladeraOrigen(),
        trasladoActualizar.getHeladeraDestino());
        Traslado trasladoGuardar= this.save(trasladoBase);
        trasladoGuardar.setId(id);

        return trasladoBase;
    }

    public Traslado actualizarTrasladoDepositado(Long id){
        Traslado trasladoActualizar = findById(id);
        Traslado trasladoBase = new Traslado(trasladoActualizar.getQrVianda(), EstadoTrasladoEnum
        .ENTREGADO, LocalDateTime.now(), trasladoActualizar.getHeladeraOrigen(),
        trasladoActualizar.getHeladeraDestino());
        Traslado trasladoGuardar= this.save(trasladoBase);

        trasladoGuardar.setId(id);
        return trasladoBase;
    }
    */
}
