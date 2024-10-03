package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import javax.persistence.EntityManagerFactory;

public class RutaController {
  private EntityManagerFactory entityManagerFactory;

  private final Fachada fachada;

  public RutaController(Fachada fachada) {
    this.fachada = fachada;
  }

  public void agregar(Context context) {

    var rutaDTO = context.bodyAsClass(RutaDTO.class);
    var rutaDTORta = this.fachada.agregar(rutaDTO);
    context.json(rutaDTORta);
    context.status(HttpStatus.CREATED);
  }
}
