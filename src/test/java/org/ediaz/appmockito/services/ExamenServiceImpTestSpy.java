package org.ediaz.appmockito.services;

import org.ediaz.appmockito.repositories.ExamenRepositoryImp;
import org.ediaz.appmockito.repositories.PreguntaRepositoryImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImpTestSpy {

//    Para utilizar espias desde el inicio se necesita marcar las implementaciones con spy

    @Spy
    private ExamenRepositoryImp examenRepository;
    @Spy
    private PreguntaRepositoryImp preguntaRepository;
    @InjectMocks
    private ExamenServiceImp service;


//    Los espias ya no forman parte de los objetos mock o simulados, sus llamadas corresponden a metodo implementados reales
    @Test
    void testSpy() {
        // Si se quisiera simular valores con spy se debe de usar doReturn
        var preguntas = Arrays.asList("integrales");
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        var examen = service.findExamenPorNombreConPreguntas("Analisis de datos");
        assertEquals(1L, examen.getId());
        assertEquals("Analisis de datos", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size()); // falla aqui pues devuelve solo 1 elemento
    }
}