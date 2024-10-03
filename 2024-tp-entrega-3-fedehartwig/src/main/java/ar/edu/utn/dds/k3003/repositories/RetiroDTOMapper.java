package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.model.Retiro;
import ar.edu.utn.dds.k3003.model.Ruta;

public class RetiroDTOMapper {

    public RetiroDTO map(Retiro retiro){
        RetiroDTO retiroDTO = new RetiroDTO(retiro.getQrVianda(),retiro.getTarjeta(),retiro.getHeladeraId());
        retiroDTO.setId(retiro.getId());
        return retiroDTO;
    }
}
