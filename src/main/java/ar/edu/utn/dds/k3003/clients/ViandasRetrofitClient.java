package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import retrofit2.Call;
import retrofit2.http.*;

public interface ViandasRetrofitClient {

  @GET("viandas/{qr}")
  Call<ViandaDTO> get(@Path("qr") String qr);

  @PATCH("viandas/{id}")
  Call <ViandaDTO> patch(@Path("id") String viandaID, @Query ("heladeraID") String heladeraID2);

}
