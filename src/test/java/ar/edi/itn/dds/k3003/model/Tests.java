package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.model.Heladera;
import org.junit.jupiter.api.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.desktop.QuitResponse;
import java.time.LocalDateTime;
import java.util.*;
import ar.edu.utn.dds.k3003.tests.TestTP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

@ExtendWith({MockitoExtension.class})
public class Tests{
    private static final String QR_VIANDA = "123";
    private static final int HELADERA_ORIGEN = 1;
    private static final int HELADERA_DESTINO = 2;
    private TrasladoRepository trasladoRepository;

    @Mock
    FachadaViandas fachadaViandas;
    @Mock
    FachadaHeladeras fachadaHeladeras;

    FachadaLogistica instancia;
    public Tests() {
        this.trasladoRepository = new TrasladoRepository();

    }
    @BeforeEach
    void setUp() throws Throwable {

        try {
            this.instancia = new Fachada();
            this.instancia.setHeladerasProxy(this.fachadaHeladeras);
            this.instancia.setViandasProxy(this.fachadaViandas);
        } catch (Throwable var2) {
            Throwable $ex = var2;
            throw $ex;
        }
    }

}
