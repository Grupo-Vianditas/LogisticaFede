package ar.edu.utn.dds.k3003.app;

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


    @Override
    public TrasladoDTO asignarTraslado(TrasladoDTO traslado) throws TrasladoNoAsignableException {
        /*try {
            // Intenta buscar la vianda por el QR
            ViandaDTO viandaDTO = fachadaViandas.buscarXQR(traslado.getQrVianda());

            // Intenta encontrar rutas válidas para el traslado
            List<Ruta> rutasPosibles = rutaRepository.findByHeladeras(traslado.getHeladeraOrigen(), traslado.getHeladeraDestino());

            if (rutasPosibles.isEmpty()) {
                throw new TrasladoNoAsignableException("noasignable");
            }

            Collections.shuffle(rutasPosibles);
            Ruta ruta = rutasPosibles.get(0);

            // Crea un nuevo traslado con la ruta seleccionada y otros detalles del traslado proporcionado
            //TrasladoDTO trasladoNuevo = new TrasladoDTO(traslado.getQrVianda(), EstadoTrasladoEnum.ASIGNADO, LocalDateTime.now(), traslado.getHeladeraOrigen(), traslado.getHeladeraDestino());

            Traslado trasladoNuevo = new Traslado(traslado.getQrVianda(), EstadoTrasladoEnum.ASIGNADO, LocalDateTime.now(), traslado.getHeladeraOrigen(),traslado.getHeladeraDestino());
            trasladoNuevo.setColaboradorId(14L);
            this.trasladoRepository.save(trasladoNuevo);

            // Asigna un colaborador (o lo que corresponda)

            // Aquí podrías guardar el traslado en la base de datos o realizar cualquier otra operación necesaria

            return trasladoMapper.map(trasladoNuevo);
        } catch (NoSuchElementException e) {
            // Si no se encuentra la vianda, arrojar la excepción correspondiente
            throw new NoSuchElementException("No se encontro el traslado");
        }
        */

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        rutaRepository.setEntityManager(entityManager);
        trasladoRepository.setEntityManager(entityManager);

        trasladoRepository.getEntityManager().getTransaction().begin();
        fachadaViandas.buscarXQR(traslado.getQrVianda());

        List<Ruta> rutasPosibles = this.rutaRepository.findByHeladeras(traslado.getHeladeraOrigen(), traslado.getHeladeraDestino());

        if (rutasPosibles.isEmpty()) {
            entityManager.getTransaction().rollback();
            entityManager.close();

            throw new TrasladoNoAsignableException("El traslado no es asignable, no tiene rutas posibles.");
        }


        Traslado trasladoNuevo = new Traslado(traslado, rutasPosibles.get(0));


        this.trasladoRepository.save(trasladoNuevo);

        trasladoRepository.getEntityManager().getTransaction().commit();
        trasladoRepository.getEntityManager().close();
        rutaRepository.getEntityManager().close();

        return trasladoMapper.map(trasladoNuevo);

    }


    @Override
    public List<TrasladoDTO> trasladosDeColaborador(Long colaboradorId, Integer mes, Integer anio) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        trasladoRepository.setEntityManager(entityManager);
        trasladoRepository.getEntityManager().getTransaction().begin();

        List<Traslado> trasladosDeColaborador = trasladoRepository.findByColaborador(colaboradorId);

        //List<Traslado> trasladosDeColaboradorPedidos = trasladosDeColaborador.stream().filter(x -> x.getFechaTraslado().getMonthValue() == mes
        //&& x.getFechaTraslado().getYear() == anio).toList();

        trasladoRepository.getEntityManager().getTransaction().commit();
        trasladoRepository.getEntityManager().close();


        return trasladosDeColaborador.stream().map(trasladoMapper::map).toList();

    }

    @Override
    public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {
        this.fachadaHeladeras = fachadaHeladeras;
    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {
        this.fachadaViandas = fachadaViandas;
    }

    public List<TrasladoDTO> traslados = new ArrayList<>();


    public List<ViandaDTO> viandas = new ArrayList<>();


    @Override
    public void trasladoRetirado(Long idTraslado) {

        TrasladoDTO traslado = this.buscarXId(idTraslado);

        Ruta rutaDeTraslado = new Ruta(traslado.getColaboradorId(), traslado.getHeladeraOrigen(), traslado.getHeladeraDestino());

        RetiroDTO retiroDTO = new RetiroDTO(traslado.getQrVianda(), "1", traslado.getHeladeraOrigen());

        fachadaHeladeras.retirar(retiroDTO);

        fachadaViandas.modificarEstado(traslado.getQrVianda(), EstadoViandaEnum.EN_TRASLADO);

        trasladoRepository.save(new Traslado(traslado.getQrVianda(),
                EstadoTrasladoEnum.EN_VIAJE,
                traslado.getFechaTraslado(),
                rutaDeTraslado,
                rutaDeTraslado.getHeladeraIdOrigen(),
                rutaDeTraslado.getHeladeraIdDestino()));

        /*
        Traslado trasladomap = this.trasladoRepository.actualizarTrasladoRetirado(idTraslado);
        traslado = trasladoMapper.map(trasladomap);

        //RetiroDTO heladeraRetirada = buscarRetiro(idTraslado);
        RetiroDTO retiroNuevo = new RetiroDTO(traslado.getQrVianda(), "444", LocalDateTime.now(), 444);
        retiroNuevo.setId(idTraslado);
        this.fachadaHeladeras.retirar(retiroNuevo);
        this.fachadaViandas.modificarEstado(traslado.getQrVianda(), EstadoViandaEnum.EN_TRASLADO);
         */
        //Vianda vianda = this.viandaRepository.buscarXQR(traslado.getQrVianda());
        //this.viandaRepository.save(vianda);

        //viandas.add(viandaDTO);
        //Vianda viandaBase = new Vianda(vianda.getCodigoQR(), vianda.getFechaElaboracion(),EstadoViandaEnum.EN_TRASLADO, vianda.getColaboradorId(),vianda.getHeladeraId());

    }

    @Override
    public void trasladoDepositado(Long idTraslado) {

        TrasladoDTO traslado = buscarXId(idTraslado);

        Ruta rutaDeTraslado = new Ruta(traslado.getColaboradorId(), traslado.getHeladeraOrigen(), traslado.getHeladeraDestino());

        fachadaHeladeras.depositar(traslado.getHeladeraDestino(), traslado.getQrVianda());

        fachadaViandas.modificarEstado(traslado.getQrVianda(), EstadoViandaEnum.DEPOSITADA);

        fachadaViandas.modificarHeladera(traslado.getQrVianda(), traslado.getHeladeraDestino());

        trasladoRepository.save(new Traslado(traslado.getQrVianda(),
                EstadoTrasladoEnum.ENTREGADO,
                traslado.getFechaTraslado(),
                rutaDeTraslado,
                rutaDeTraslado.getHeladeraIdOrigen(),
                rutaDeTraslado.getHeladeraIdDestino()));

        /*
        this.trasladoRepository.actualizarTrasladoDepositado(idTraslado);
        //RetiroDTO heladeraRetirada = buscarRetiro(idTraslado);

        RetiroDTO retiroNuevo = new RetiroDTO(traslado.getQrVianda(), "444", LocalDateTime.now(), 444);
        retiroNuevo.setId(idTraslado);
        this.fachadaHeladeras.depositar(retiroNuevo.getHeladeraId(), retiroNuevo.getQrVianda());
        this.fachadaViandas.modificarEstado(traslado.getQrVianda(), EstadoViandaEnum.DEPOSITADA);
        this.fachadaViandas.modificarHeladera(traslado.getQrVianda(),2);}

         */
    }

    @Override
    public TrasladoDTO buscarXId(Long aLong) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        trasladoRepository.setEntityManager(entityManager);
        trasladoRepository.getEntityManager().getTransaction().begin();
        Traslado trasladoBuscado = this.trasladoRepository.findById(aLong);
        trasladoRepository.getEntityManager().getTransaction().commit();
        trasladoRepository.getEntityManager().close();
        return trasladoMapper.map(trasladoBuscado);
    }

    public RetiroDTO buscarRetiro(Long codigo) {
        return retiroDTOMapper.map(this.retiroDTORepository.findByRetiros(codigo));
    }

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
}