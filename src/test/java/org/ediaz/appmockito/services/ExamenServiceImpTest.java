package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;
import org.ediaz.appmockito.repositories.ExamenRepository;
import org.ediaz.appmockito.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamenServiceImpTest {

    private ExamenRepository repository;
    private ExamenServiceImp service;
    private PreguntaRepository preguntaRepository;

    //    Befor each para inicializar las instancias
    @BeforeEach
    void beforeEach() {
//        Lo que hace Mockito es crear una clase que implementa la ainterfaz al vuelo
//        Esto con el objetivo de evitar cambiar datos o agregar en una implementacion real
        this.repository = mock(ExamenRepository.class);
        this.preguntaRepository = mock(PreguntaRepository.class);
        this.service = new ExamenServiceImp(repository, preguntaRepository);
    }

    @Test
    void findExamenPorNombre() {
//        Cada vez que se ejecute el metodo de findAll en la clase de service entonces retornara estos datos
//        Se llama una simulacion del objeto ExamenRepository.class
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
//        La simulacion solo se debe de hacer de metodos publicos no staticos final o private

        var examen = service.findExamenPorNombre("Analisis de datos");

        assertTrue(examen.isPresent()); //
        assertEquals(1L, examen.orElseThrow().getId()); // El api recomienda orElseThrow y no get, pero hacen lo mismo
        assertEquals("Analisis de datos", examen.orElseThrow().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        var examen = service.findExamenPorNombre("Analisis de datos");

        assertFalse(examen.isPresent());
    }

    @Test
    void findExamenPorNombrePreguntas() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES); // Se utilizan los datos de una clase de prueba
        when(preguntaRepository.findPreguntasPorExamenId(1L)).thenReturn(Datos.PREGUNTAS); // Solo cuando haya el id 1 se traen los datos
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS); Se puede usar anyLong para obtener de cualquier id
        var examen = service.findExamenPorNombreConPreguntas("Analisis de datos");
        assertEquals(5, examen.getPreguntas().size());
    }
}