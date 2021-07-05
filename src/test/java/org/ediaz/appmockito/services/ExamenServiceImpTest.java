package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;
import org.ediaz.appmockito.repositories.ExamenRepository;
import org.ediaz.appmockito.repositories.PreguntaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Para la inyeccion de dependencias con mock hay de 2 formas
//1.- Para esto se debe de tener la dependencia de mock con junit jupiter, al usar esta no es necesario el beforeEach
@ExtendWith(MockitoExtension.class)
class ExamenServiceImpTest {

    @Mock
    private ExamenRepository examenRepository;
    @Mock
    private PreguntaRepository preguntaRepository;
    @InjectMocks
    private ExamenServiceImp service;

    private TestInfo testInfo;
    private TestReporter testReporter;

    //    Befor each para inicializar las instancias
    @BeforeEach
    void beforeEach(TestInfo testInfo, TestReporter testReporter) {
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        this.testReporter.publishEntry("Prueba Unitaria ejecutandoce -> "
                .concat(testInfo.getTestMethod().orElseThrow().getName()));

//        2.- Habilitando las anotaciones mock para esta instancia
//        MockitoAnnotations.openMocks(this);
//        Lo que hace Mockito es crear una clase que implementa la ainterfaz al vuelo
//        Esto con el objetivo de evitar cambiar datos o agregar en una implementacion real
//        this.examenRepository = mock(ExamenRepository.class);
//        this.preguntaRepository = mock(PreguntaRepository.class);
//        this.service = new ExamenServiceImp(examenRepository, preguntaRepository);
    }

//    BDD Behavior Development Driver - desarrollo impulsado al comportamiento
    @Test
    void findExamenPorNombre() {
//        Given - correponden a las precondiciones al ambiente de prueba
//        Cada vez que se ejecute el metodo de findAll en la clase de service entonces retornara estos datos
//        Se llama una simulacion del objeto ExamenRepository.class
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
//        La simulacion solo se debe de hacer de metodos publicos no staticos final o private

//        When - Cuando ejecutamos el metodo a verificar
        var examen = service.findExamenPorNombre("Analisis de datos");

//        Then - Validacion de la ejecucion
        assertTrue(examen.isPresent()); //
        assertEquals(1L, examen.orElseThrow().getId()); // El api recomienda orElseThrow y no get, pero hacen lo mismo
        assertEquals("Analisis de datos", examen.orElseThrow().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
//        Given
        when(examenRepository.findAll()).thenReturn(Collections.emptyList());
//        When
        var examen = service.findExamenPorNombre("Analisis de datos");
//        Then
        assertFalse(examen.isPresent());
    }

    @Test
    @DisplayName("Encontrar examen y preguntas")
    void findExamenPorNombrePreguntas() {
//        Given
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES); // Se utilizan los datos de una clase de prueba
//        when(preguntaRepository.findPreguntasPorExamenId(1L)).thenReturn(Datos.PREGUNTAS); // Solo cuando haya el id 1 se traen los datos
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS); // Se puede usar anyLong para obtener de cualquier id
//        When
        var examen = service.findExamenPorNombreConPreguntas("Analisis de datos");
//        Then
        assertEquals(5, examen.getPreguntas().size());
    }

    @Test
    @Disabled
    void findExamenPorNombrePreguntasVerify() {
//        Given
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
//        When
        var examen = service.findExamenPorNombreConPreguntas("Analisis de datos");
//        Then
        assertEquals(5, examen.getPreguntas().size());
//        Para determinar si enrealidad se usa el metodo mediante mock se usa verify
        verify(examenRepository).findAll();
//        En este caso fuerzo la falla porque se llamo el metodo para 1L pero valido que se haya llamado con 2L
        verify(preguntaRepository).findPreguntasPorExamenId(2L);
    }

    @Test
    @DisplayName("Prueba guardar Examen")
    void testGuardarExamen() {
//        Given
        var examenGuardar = Datos.EXAMEN;
        examenGuardar.setPreguntas(Datos.PREGUNTAS);
//        Cuando guarde cualquier instanci de Examen retorna algo
//        Vamos a simular la asigancion de id como lo haria en un bd
        when(examenRepository.save(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 4L;
            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0); // el argumento es el objeto que se pasa al guardar
                examen.setId(secuencia++);
                return examen;
            }
        });
//        When
        var examen = service.guardarExamen(examenGuardar);
//        Then
        assertNotNull(examen.getId());
        assertEquals(4L, examen.getId());
        assertEquals("Desarrollo de sistemas", examen.getNombre());

        verify(examenRepository).save(any(Examen.class));
//        Si no se le pasa preguntas este metodo no se ejecuta y no pasa el test
        verify(preguntaRepository).guardarVarias(anyList());
    }

//    Manejo de excepciones que permite unas pruebas mas robustas


}