package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import retrofit2.Call;
import retrofit2.http.*;

import java.time.LocalDateTime;

public interface ViandasRetrofitClient {

  @GET("viandas/{qr}")
  Call<ViandaDTO> get(@Path("qr") String qr);

  @PATCH("viandas/{qr}/estado")
  Call<ViandaDTO> modificarEstado(
          @Path("qr") String qr,
          @Query("status") EstadoViandaEnum estado
  );

}
