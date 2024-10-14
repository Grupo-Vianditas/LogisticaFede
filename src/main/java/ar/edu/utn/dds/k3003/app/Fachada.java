package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.ViandasRetrofitClient;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.*;
import lombok.Getter;
import lombok.Setter;

import java.awt.desktop.QuitResponse;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.utn.dds.k3003.repositories.auxiliar.PersistenceUtils.createEntityManagerFactory;

import javax.persistence.EntityManager;
@Setter
@Getter

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaLogistica {

    private final static EntityManagerFactory entityManagerFactory = createEntityManagerFactory();

    private final RutaRepository rutaRepository;
    private final RutaMapper rutaMapper;
    private final HeladeraRepository heladeraRepository;
    private final HeladeraMapper heladeraMapper;
    private final TrasladoRepository trasladoRepository;
    private final TrasladoMapper trasladoMapper;
    private final RetiroDTOMapper retiroDTOMapper;
    private final RetiroDTORepository retiroDTORepository;
    private final ViandaMapper viandaMapper;
    private final ViandaRepository viandaRepository;
    private FachadaViandas fachadaViandas;
    private FachadaHeladeras fachadaHeladeras;

    public Fachada() {
        this.rutaRepository = new RutaRepository();
        this.rutaMapper = new RutaMapper();
        this.trasladoMapper = new TrasladoMapper();
        this.trasladoRepository = new TrasladoRepository();
        this.heladeraMapper = new HeladeraMapper();
        this.heladeraRepository = new HeladeraRepository();
        this.retiroDTOMapper = new RetiroDTOMapper();
        this.retiroDTORepository = new RetiroDTORepository();
        this.viandaMapper = new ViandaMapper();
        this.viandaRepository = new ViandaRepository();
    }


    @Override
    public RutaDTO agregar(RutaDTO rutaDTO) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        rutaRepository.setEntityManager(entityManager);
        rutaRepository.getEntityManager().getTransaction().begin();

        Ruta ruta = new Ruta(rutaDTO.getColaboradorId(), rutaDTO.getHeladeraIdOrigen(), rutaDTO.getHeladeraIdDestino());
        ruta = this.rutaRepository.save(ruta);

        rutaRepository.getEntityManager().getTransaction().commit();
        rutaRepository.getEntityManager().close();


        return rutaMapper.map(ruta);
    }


    public Ruta buscarRutaXOrigenYDestino(Integer Origen, Integer Destino) throws NoSuchElementException {
        List<Ruta> rutas = this.rutaRepository.findByHeladeras(Origen, Destino);

        if (rutas.isEmpty()) {
            throw new NoSuchElementException("No hay ninguna ruta cargada para la heladeraOrigen " + Origen + " hacia la heladeraDestino " + Destino);
        }

        if (rutas.size() > 1) {
            // Selecciono una ruta al azar de las disponibles
            Random rand = new Random();
            return rutas.get(rand.nextInt(rutas.size()));
        }

        // Si solo hay una ruta, la devuelvo
        return rutas.get(0);
    }

    @Override
    public TrasladoDTO asignarTraslado(TrasladoDTO trasladoDTO) throws TrasladoNoAsignableException, NoSuchElementException {
        Ruta ruta;

        // Si no se encuentra la vianda, se lanza una excepción NoSuchElementException
        ViandaDTO vianda = this.fachadaViandas.buscarXQR(trasladoDTO.getQrVianda());

        // Si no se encuentra una ruta para ese origen y destino, se lanza una excepción TrasladoNoAsignableException
        try {
            ruta = buscarRutaXOrigenYDestino(trasladoDTO.getHeladeraOrigen(), trasladoDTO.getHeladeraDestino());
        } catch (NoSuchElementException e) {
            throw new TrasladoNoAsignableException(e.getLocalizedMessage());
        }

        // Si ya existe un traslado para esa vianda y esa ruta, se lanza una excepción TrasladoNoAsignableException. Lo dejo comentado porque rompe un test, pero esto es necesario tenerlo en cuenta porque sino se pueden añadir muchos traslados iguales y no tiene sentido.

        if (!this.trasladoRepository.findByRuta(ruta).isEmpty()) {
            throw new TrasladoNoAsignableException("Ya existe un traslado para la vianda " + trasladoDTO.getQrVianda() + " con el id de ruta " + ruta.getId() + " desde la heladera origen " + ruta.getHeladeraIdOrigen() + " hacia la heladera destino " + ruta.getHeladeraIdDestino() +" asignado al colaborador " + ruta.getColaboradorId() + ".");
        }

        // Si tanto la ruta como la vianda existen, procedo a crear y guardar el traslado
        Traslado traslado = this.trasladoRepository.save(
                new Traslado(
                        trasladoDTO.getQrVianda(),
                        ruta,
                        EstadoTrasladoEnum.ASIGNADO,
                        trasladoDTO.getFechaTraslado()
                )
        );

        // Creo un DTO con la información del traslado
        TrasladoDTO traslado_dto = new TrasladoDTO(
                traslado.getQrVianda(),
                traslado.getEstado(),
                traslado.getFechaTraslado(),
                traslado.getRuta().getHeladeraIdOrigen(),
                traslado.getRuta().getHeladeraIdDestino()
        );

        // Asigno el id del colaborador que está en la ruta al trasladoDTO
        traslado_dto.setColaboradorId(ruta.getColaboradorId());

        // Asigno el id del traslado generado al DTO
        traslado_dto.setId(traslado.getId());

        // Retorna un DTO con la información del traslado completa
        return traslado_dto;
    }



    @Override
    public List<TrasladoDTO> trasladosDeColaborador(Long colaboradorId, Integer mes, Integer anio) {
        List<Traslado> traslados = this.trasladoRepository.findByColaborador(colaboradorId, mes, anio);
        return traslados.stream()
                .map(this.trasladoMapper::map)
                .collect(Collectors.toList());


    }


    @Override
    public void trasladoRetirado(Long idTraslado) {

        Traslado traslado = this.trasladoRepository.findById(idTraslado);
        ViandaDTO vianda = this.fachadaViandas.buscarXQR(traslado.getQrVianda());

        RetiroDTO retiro = new RetiroDTO(
                vianda.getCodigoQR(),
                "123456789",
                LocalDateTime.now(),
                traslado.getRuta().getHeladeraIdOrigen()
        );

        fachadaHeladeras.retirar(retiro);

        // Modifico los estados de la vianda y el traslado. Desprecio los retornos
        fachadaViandas.modificarEstado(vianda.getCodigoQR(), EstadoViandaEnum.EN_TRASLADO);
        this.trasladoRepository.modificarEstado(traslado.getId(), EstadoTrasladoEnum.EN_VIAJE);

    }

    @Override
    public void trasladoDepositado(Long idTraslado) {

        Traslado traslado = this.trasladoRepository.findById(idTraslado);
        ViandaDTO vianda = this.fachadaViandas.buscarXQR(traslado.getQrVianda());

        fachadaViandas.modificarHeladera(vianda.getCodigoQR(), traslado.getRuta().getHeladeraIdDestino());

        // Modifico los estados de la vianda y el traslado. Desprecio los retornos
        fachadaViandas.modificarEstado(vianda.getCodigoQR(), EstadoViandaEnum.DEPOSITADA);
        this.trasladoRepository.modificarEstado(traslado.getId(), EstadoTrasladoEnum.ENTREGADO);

    }

    @Override
    public TrasladoDTO buscarXId(Long idTraslado) {
        Traslado traslado = this.trasladoRepository.findById(idTraslado);
        TrasladoDTO trasladoDTO= new TrasladoDTO(traslado.getQrVianda(), traslado.getEstado(), traslado.getFechaTraslado(), traslado.getRuta().getHeladeraIdOrigen(), traslado.getRuta().getHeladeraIdDestino());
        trasladoDTO.setColaboradorId(traslado.getRuta().getColaboradorId());
        trasladoDTO.setId(traslado.getId());
        return trasladoDTO;
    }

//    public RetiroDTO buscarRetiro(Long codigo) {
//        return retiroDTOMapper.map(this.retiroDTORepository.findByRetiros(codigo));
//    }

    public ViandaDTO buscarVianda(String qr) {
        return viandaMapper.map(this.viandaRepository.findById(qr));
    }


    public void borrar() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("TRUNCATE TABLE Ruta").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE Traslado").executeUpdate();
        entityManager.getTransaction().commit();


    }

    @Override
    public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {
        this.fachadaHeladeras = fachadaHeladeras;
    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {
        this.fachadaViandas = fachadaViandas;
    }
}