package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.clients.ViandasRetrofitClient;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.metrics.controllersCounters.RutasCounter;
import ar.edu.utn.dds.k3003.metrics.controllersCounters.TrasladosCounter;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import retrofit2.Response;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;


public class TrasladoController {

  ViandasRetrofitClient viandasRetrofitClient;

  private final Fachada fachada;
  private TrasladosCounter trasladosCounter;

  public TrasladoController(Fachada fachada, TrasladosCounter trasladosCounter) {
    this.fachada = fachada;
    this.trasladosCounter = trasladosCounter;
  }

  public void asignar(Context context) {
    try {
      var trasladoDTO = context.bodyAsClass(TrasladoDTO.class);
      var trasladoDTORta = this.fachada.asignarTraslado(trasladoDTO);
      context.json(trasladoDTORta);
      context.status(HttpStatus.CREATED);
      trasladosCounter.incrementSuccessfulPostCounter();

    } catch (TrasladoNoAsignableException | NoSuchElementException e) {
      context.result(e.getLocalizedMessage());
      context.status(HttpStatus.BAD_REQUEST);
      trasladosCounter.incrementFailedPostCounter();
    }
  }

  public void obtener(Context context) {
    var id = context.pathParamAsClass("id", Long.class).get();
    try {
      var trasladoDTO = this.fachada.buscarXId(id);
      context.json(trasladoDTO);
      trasladosCounter.incrementSuccessfulGetCounter();
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
      trasladosCounter.incrementFailedGetCounter();
    }
  }

  public void borrar(Context context) {
    try {
      fachada.borrar();
    } catch (Exception e) {
      System.out.println("Error al borrar la base de datos: " + e.getMessage());
    }
  }

  public void modificar(Context context) {

    var id = context.pathParamAsClass("id", Long.class).get();
    try {
      TrasladoDTO trasladoDTO = context.bodyAsClass(TrasladoDTO.class);
      trasladoDTO.setId(id);
      String estado = trasladoDTO.getStatus().toString();

      // Reviso que sea un estado valido, sino devuelvo una excepcion
      switch (estado) {
        case "EN_VIAJE":
          this.fachada.trasladoRetirado(id);
          break;
        case "ENTREGADO":
          this.fachada.trasladoDepositado(id);
          break;
        default:
          context.result("Solo se puede cambiar el estado de un traslado a EN_VIAJE o ENTREGADO.");
          context.status(HttpStatus.BAD_REQUEST);
      }
    }
    catch(NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }

  }

  public void retirar(Context context) {
    var id = context.pathParamAsClass("id", Long.class).get();
    try {
      TrasladoDTO trasladoDTO = context.bodyAsClass(TrasladoDTO.class);
      trasladoDTO.setId(id);

      String estado = trasladoDTO.getStatus().toString();

      // Verifico que el estado sea EN_VIAJE
      if ("EN_VIAJE".equals(estado)) {
        this.fachada.trasladoRetirado(id);
        context.result("El traslado ha sido marcado como EN_VIAJE.");
        context.status(HttpStatus.OK);
      } else {
        context.result("El estado debe ser EN_VIAJE para realizar el retiro.");
        context.status(HttpStatus.BAD_REQUEST);
      }
    } catch (NoSuchElementException ex) {
      context.result("No se encontró el traslado con ID: " + id);
      context.status(HttpStatus.NOT_FOUND);
    }
  }

  public void depositar(Context context) {
    var id = context.pathParamAsClass("id", Long.class).get();
    try {
      TrasladoDTO trasladoDTO = context.bodyAsClass(TrasladoDTO.class);
      trasladoDTO.setId(id);

      String estado = trasladoDTO.getStatus().toString();

      // Verifico que el estado sea ENTREGADO
      if ("ENTREGADO".equals(estado)) {
        this.fachada.trasladoDepositado(id);
        context.result("El traslado ha sido marcado como ENTREGADO.");
        context.status(HttpStatus.OK);
      } else {
        context.result("El estado debe ser ENTREGADO para realizar el depósito.");
        context.status(HttpStatus.BAD_REQUEST);
      }
    } catch (NoSuchElementException ex) {
      context.result("No se encontró el traslado con ID: " + id);
      context.status(HttpStatus.NOT_FOUND);
    }
  }


  public void trasladosColaborador(Context context){
    var id = context.queryParamAsClass("colaboradorId", Long.class).get();
    try {
      var listaDeTraslados = this.fachada.trasladosDeColaborador(id, LocalDateTime.now().getMonthValue(), LocalDateTime.now().getYear());
      context.json(listaDeTraslados);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }

}
