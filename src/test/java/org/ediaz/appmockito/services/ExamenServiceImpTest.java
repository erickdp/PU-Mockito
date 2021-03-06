package org.ediaz.appmockito.services;

import org.ediaz.appmockito.Datos;
import org.ediaz.appmockito.models.Examen;
import org.ediaz.appmockito.repositories.ExamenRepository;
import org.ediaz.appmockito.repositories.ExamenRepositoryImp;
import org.ediaz.appmockito.repositories.PreguntaRepositoryImp;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
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
    private PreguntaRepositoryImp preguntaRepository;
    @InjectMocks
    private ExamenServiceImp service;

    //    Captor captura los argumentos para ser verificados
    @Captor
    ArgumentCaptor<Long> captor;

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
//        this.preguntaRepository = mock(PreguntaRepositoryImp.class);
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
//        Si le paso como argumento 1L entonces lanza la expecion
        when(preguntaRepository.findPreguntasPorExamenId(1L)).thenThrow(IllegalArgumentException.class);
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas("Analisis de datos");
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

    @Test
    void testCaptor() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Analisis de datos");

//        Toma el valor especifico que se envia como argumento, ya no debo de poner anyLong
        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());
        assertEquals(1L, captor.getValue());
    }

    //    El assertThrow solo sirve cuando se devuelve algo, cuando es void cambia y se usa doThrow
    @Test
    void testDoThrow() {
//        Si no se ejecuta guardarVarias la prueba falla
        var examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());
        assertThrows(IllegalArgumentException.class, () -> {
            service.guardarExamen(examen);
        });
    }

    //    Con do Answer se puede realizar algun evento para un determinad parametro enviado
    @Test
    @Disabled
    void testDoAnswer() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0); // Tomo el argumento que se pasa al metodo
            return id == 2L ? Datos.PREGUNTAS : Collections.emptyList(); // Solo si el ID del examen es 2 entonces devolvera preguntas
        }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        var examen = service.findExamenPorNombreConPreguntas("Marcos de desarrollo");
        assertAll(
                () -> assertEquals(3L, examen.getId()), // Para las primeras 2 pruebas
                () -> assertEquals("Marcos de desarrollo", examen.getNombre()),
                () -> assertEquals(5, examen.getPreguntas().size())
        );

        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    //    Segunda forma de hacer el metodo testGuardarExamen
    @Test
    @DisplayName("Prueba guardar Examen 2")
    void testGuardarExamen2() {
        var examenGuardar = Datos.EXAMEN;
        examenGuardar.setPreguntas(Datos.PREGUNTAS);
        doAnswer(new Answer<Examen>() {
            Long secuencia = 4L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(examenRepository).save(any(Examen.class));
        var examen = service.guardarExamen(examenGuardar);
        assertNotNull(examen.getId());
        assertEquals(4L, examen.getId());
        assertEquals("Desarrollo de sistemas", examen.getNombre());

        verify(examenRepository).save(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    //    Para poder simular parcialmente el objeto mock se puede usar doCallRealMethod
//    Esto permite llamar al metodo real y tratandolo en un entorno casi real
    @Test
    void doCallRealMethodTest() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        var examen = service.findExamenPorNombreConPreguntas("Analisis de datos");
        assertTrue(examen.getPreguntas().contains("integrales"));
    }

    //    Los espias ya no forman parte de los objetos mock o simulados, sus llamadas corresponden a metodo implementados reales
    @Test
    @Disabled
    void testSpy() {
        var preguntaRepository = spy(PreguntaRepositoryImp.class); // En esta ocasion se debe de hacer referencia a la clase que implementa
        var examenRepository = spy(ExamenRepositoryImp.class);
        var examenService = new ExamenServiceImp(examenRepository, preguntaRepository);

        // Si se quisiera simular valores con spy se debe de usar doReturn
        var preguntas = Arrays.asList("integrales");
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        var examen = examenService.findExamenPorNombreConPreguntas("Analisis de datos");
        assertEquals(1L, examen.getId());
        assertEquals("Analisis de datos", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size()); // falla aqui pues devuelve solo 1 elemento
    }

    @Test
    void ordenDeEjecicion() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        this.service.findExamenPorNombreConPreguntas("Analisis de datos");
        this.service.findExamenPorNombreConPreguntas("Seguridad de ti");

        var orden = inOrder(examenRepository, preguntaRepository);
        orden.verify(examenRepository).findAll();
        orden.verify(preguntaRepository).findPreguntasPorExamenId(1L);

        orden.verify(examenRepository).findAll();
        orden.verify(preguntaRepository).findPreguntasPorExamenId(2L);
    }

//    Tambien se puede definir el numero de veces que se deben de ejecutar los metodos
    @Test
    void testNumeroDeInvocaciones() {
        when(this.examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        this.service.findExamenPorNombreConPreguntas("Analisis de datos");

        verify(this.preguntaRepository, atLeast(1)).findPreguntasPorExamenId(1L); // Se define el numero de veces minimo que se puede ejecutar
        verify(this.preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(1L);
        verify(this.preguntaRepository, atMost(3)).findPreguntasPorExamenId(1L); // Maximo de veces que se puede ejecutar
        verify(this.preguntaRepository, atMostOnce()).findPreguntasPorExamenId(1L);
    }

    @Test
    void testNumeroDeInvocaciones2() {
        when(this.examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        this.service.findExamenPorNombreConPreguntas("Analisis de datos");

//        verify(this.preguntaRepository, atLeast(2)).findPreguntasPorExamenId(1L); // Se define el numero de veces minimo que se puede ejecutar, falla por que solo se llama 1 vez
        verify(this.preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(1L);
        verify(this.preguntaRepository, atMost(20)).findPreguntasPorExamenId(1L); // Maximo de veces que se puede ejecutar
        verify(this.preguntaRepository, atMostOnce()).findPreguntasPorExamenId(1L);
    }

    @Test
    void testNumeroDeInvocaciones3() {
        when(this.examenRepository.findAll()).thenReturn(Collections.emptyList());
        this.service.findExamenPorNombreConPreguntas("Analisis de datos");

        verify(this.preguntaRepository, never()).findPreguntasPorExamenId(1L); // Pasa porque nunca interactua con este metodo
        verify(this.examenRepository).findAll(); // No interactua con las preguntas pero si con los examenes
        verifyNoInteractions(this.preguntaRepository);
    }
}