package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;
import org.ediaz.appmockito.repositories.ExamenRepository;
import org.ediaz.appmockito.repositories.PreguntaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
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
    @Test
    void testManejoException() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenThrow(IllegalArgumentException.class);
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas("Desarrollo de sistemas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

//    Uso de Argument matcher que permite complementar ademas de verificar que el metodo se ejecuta
//    un valor especifico

    @Test
    void testArgumentMatchers() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Analisis de datos");

        verify(examenRepository).findAll();
//        Aqui se usa argThat de argument Matchers para validar un valor especifico se puede usar eq()
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(1L)));
    }

    @Test
    void testArgumentMatchers2() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVO);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Analisis de datos");

        verify(examenRepository).findAll();
//        Se puede hacer sin la clase pero no se puede agregar un mensaje
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(
                (argumento) -> argumento != null && argumento >= 1L)
        );
    }

    //    Este test esta forzado a fallar para demostrar la validacion de argThat
    @Test
    void testArgumentMatchers3() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVO);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Analisis de datos");

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgumentMatchers()));
    }

    //    Se puede crear una clase al vuelo para usar ArgumentMatchers permitiendo agregar un mensaje personalizado
    public static class MiArgumentMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return this.argument != null && this.argument >= 1L;
        }

        @Override
        public String toString() {
            return "El argumento a fallado : " + this.argument;
        }
    }
}