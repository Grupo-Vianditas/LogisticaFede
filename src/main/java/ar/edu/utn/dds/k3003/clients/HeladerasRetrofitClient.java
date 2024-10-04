package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HeladerasRetrofitClient {
  @GET("retiros")
  Call<RetiroDTO> retirar();
}