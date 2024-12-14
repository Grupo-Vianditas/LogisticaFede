package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HeladerasRetrofitClient {
  @POST("/retiros")
  Call<Void> retirar(@Body RetiroDTO retiroDTO);

  @POST("/depositos")
  Call<Void> depositar(@Body ViandaDTO viandaDTO);
}