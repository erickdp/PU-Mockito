package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;
import org.ediaz.appmockito.repositories.ExamenRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ExamenServiceImpTest {

    @Test
    void findExamenPorNombre() {
        // Lo que hace Mockito es crear una clase que implementa la ainterfaz al vuelo
//        Esto con el objetivo de evitar cambiar datos o agregar en una implementacion real
        var repository = mock(ExamenRepository.class);
        var service = new ExamenServiceImp(repository);
        var datos = Arrays.asList(
                new Examen(1L, "Analisis de datos"),
                new Examen(2L, "Seguridad de ti"),
                new Examen(3L, "Marcos de desarrollo"));
//        Cada vez que se ejecute el metodo de findAll en la clase de service entonces retornara estos datos
//        Se llama una simulacion del objeto ExamenRepository.class
        when(repository.findAll()).thenReturn(datos);
//        La simulacion solo se debe de hacer de metodos publicos no staticos final o private

        var examen = service.findExamenPorNombre("Analisis de datos");

        assertTrue(examen.isPresent()); //
        assertEquals(1L, examen.orElseThrow().getId()); // El api recomienda orElseThrow y no get, pero hacen lo mismo
        assertEquals("Analisis de datos", examen.orElseThrow().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        var repository = mock(ExamenRepository.class);
        var service = new ExamenServiceImp(repository);
        List<Examen> datos = Collections.emptyList();
        when(repository.findAll()).thenReturn(datos);

        var examen = service.findExamenPorNombre("Analisis de datos");

        assertTrue(examen.isPresent()); //
        assertEquals(1L, examen.orElseThrow().getId()); // El api recomienda orElseThrow y no get, pero hacen lo mismo
        assertEquals("Analisis de datos", examen.orElseThrow().getNombre());
    }
}