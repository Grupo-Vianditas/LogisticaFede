package ar.edu.utn.dds.k3003.model.logistica;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;


import java.time.LocalDateTime;
import java.util.*;

public class Logistica implements FachadaLogistica {


    @Override
    public RutaDTO agregar(RutaDTO ruta) {
        RutaDTO rutaNueva = new RutaDTO(ruta.getColaboradorId(), ruta.getHeladeraIdOrigen(), ruta.getHeladeraIdDestino());
        rutaNueva.setId(2L);

        return rutaNueva;
    }

    @Override
    public TrasladoDTO buscarXId(Long trasladoId) throws NoSuchElementException {
        return traslados.stream()
                .filter(traslado -> traslado.getId().equals(trasladoId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No se encontró un traslado con el ID: " + trasladoId));
    }

    @Override
    public TrasladoDTO asignarTraslado(TrasladoDTO traslado) throws TrasladoNoAsignableException {

        //El LocalDateTime es para actualizarla fecha a la del traslado
        TrasladoDTO trasladoNuevo = new TrasladoDTO(traslado.getQrVianda(),EstadoTrasladoEnum.ASIGNADO, LocalDateTime.now(), traslado.getHeladeraOrigen(),traslado.getHeladeraDestino());

        return trasladoNuevo;

    }

    @Override
    public List<TrasladoDTO> trasladosDeColaborador(Long id, Integer mes, Integer anio) {
        return List.of();
    }

    @Override
    public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {

    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {

    }

    public List<TrasladoDTO> traslados = new ArrayList<>();

    public TrasladoDTO buscarTrasladoPorID(Long id){
        return traslados.stream()
                .filter(traslado -> traslado.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No se encontró un traslado con el ID: " + id));
    }

    public List<ViandaDTO> viandas = new ArrayList<>();

    public ViandaDTO buscarViandaPorQR(String codigoQr){
        return viandas.stream()
                .filter(vianda -> vianda.getCodigoQR().equals(codigoQr))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No se encontró un traslado con el ID: " + codigoQr));
    }

    @Override
    public void trasladoRetirado(Long idTraslado) {
        TrasladoDTO traslado = buscarTrasladoPorID(idTraslado);


        //El LocalDateTime es para actualizarla fecha a la del traslado
        TrasladoDTO trasladoBase = new TrasladoDTO(traslado.getQrVianda(),EstadoTrasladoEnum.EN_VIAJE, LocalDateTime.now(), traslado.getHeladeraOrigen(),traslado.getHeladeraDestino());
        trasladoBase.setId(idTraslado);


        ViandaDTO viandaDTO = buscarViandaPorQR(traslado.getQrVianda());
        ViandaDTO viandaBase = new ViandaDTO(viandaDTO.getCodigoQR(), viandaDTO.getFechaElaboracion(),EstadoViandaEnum.EN_TRASLADO, viandaDTO.getColaboradorId(),viandaDTO.getHeladeraId());

        traslados.remove(traslado);
        traslados.add(trasladoBase);

        viandas.remove(viandaDTO);
        viandas.add(viandaBase);
    }

    @Override
    public void trasladoDepositado(Long idTraslado) {

        TrasladoDTO traslado = buscarTrasladoPorID(idTraslado);

        //El LocalDateTime es para actualizarla fecha a la del traslado
        TrasladoDTO trasladoBase = new TrasladoDTO(traslado.getQrVianda(),EstadoTrasladoEnum.ENTREGADO, LocalDateTime.now(), traslado.getHeladeraOrigen(),traslado.getHeladeraDestino());
        trasladoBase.setId(idTraslado);

        ViandaDTO viandaDTO = buscarViandaPorQR(traslado.getQrVianda());
        ViandaDTO viandaBase = new ViandaDTO(viandaDTO.getCodigoQR(), viandaDTO.getFechaElaboracion(),EstadoViandaEnum.RETIRADA, viandaDTO.getColaboradorId(),viandaDTO.getHeladeraId());

        traslados.remove(traslado);
        traslados.add(trasladoBase);

    }
}
