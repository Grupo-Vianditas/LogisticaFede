package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.metrics.controllersCounters.RutasCounter;
import ar.edu.utn.dds.k3003.metrics.controllersCounters.TrasladosCounter;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class TrasladoController {

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


  public void depositar(Context context) {
    var id = context.pathParamAsClass("id", Long.class).get();
    try {
      fachada.trasladoDepositado(id);
    } catch (Exception e) {
      System.out.println("Error al borrar la base de datos: " + e.getMessage());
    }
  }

  public void retirar(Context context) {
    var id = context.pathParamAsClass("id", Long.class).get();

    try {
      fachada.trasladoRetirado(id);
    } catch (Exception e) {
      System.out.println("Error al borrar la base de datos: " + e.getMessage());
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
