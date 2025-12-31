package Tests.Unitarios;

import dominio.Encuesta;
import dominio.Pregunta;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test unitario para la clase encuesta
 */
@RunWith(MockitoJUnitRunner.class)
public class EncuestaTest 
{
    @Mock
    private Pregunta mockPregunta1;
    @Mock
    private Pregunta mockPregunta2;
    private Encuesta encuesta;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private String nueva_linea;

    /**
     * Configuración inicial
     */
    @Before
    public void setUp() 
    {
        System.setOut(new PrintStream(outContent));
        nueva_linea = System.lineSeparator();
        encuesta = new Encuesta(100);
        when(mockPregunta1.getId()).thenReturn(10);
        when(mockPregunta1.getEnunciado()).thenReturn("Enunciado Pregunta 1");
        when(mockPregunta2.getId()).thenReturn(20);
        when(mockPregunta2.getEnunciado()).thenReturn("Enunciado Pregunta 2");
    }

    /**
     * Restaura la salida de consola después de cada test
     */
    @After
    public void restoreStreams() { System.setOut(originalOut);}

    // Tests
    /**
     * Prueba la constructora vacía
     */
    @Test
    public void testConstructorVacio() 
    {
        Encuesta e = new Encuesta();
        assertNull("El ID de un constructor vacío debe ser null", e.getId());
        assertEquals("Debe tener 0 preguntas", 0, e.getNumPreguntas());
        assertEquals("El nextIdP debe empezar en 1", Integer.valueOf(1), e.nextIdP());
    }

    /**
     * Prueba la constructora con identificador
     */
    @Test
    public void testConstructorConId() 
    {
        Encuesta e = new Encuesta(50);
        assertEquals("El ID debe ser 50", Integer.valueOf(50), e.getId());
        assertEquals("Debe tener 0 preguntas", 0, e.getNumPreguntas());
    }

    /**
     * Prueba que la constructora con lista crea una COPIA de la lista original
     */
    @Test
    public void testConstructorConListaCreaCopia() 
    {
        ArrayList<Pregunta> listaOriginal = new ArrayList<>(Arrays.asList(mockPregunta1));
        Encuesta e = new Encuesta(listaOriginal, 200);
        assertEquals("Debe tener 1 pregunta", 1, e.getNumPreguntas());
        // Modificar la lista original
        listaOriginal.add(mockPregunta2);
        // Verificamos que la encuesta e no ha canviado
        assertEquals("La encuesta no debe cambiar si la lista original se modifica", 1, e.getNumPreguntas());
    }

    /**
     * Prueba añadir una sola pregunta con setPregunta
     */
    @Test
    public void testSetPregunta() 
    {
        encuesta.setPregunta(mockPregunta1);
        assertEquals("El número de preguntas debe ser 1", 1, encuesta.getNumPreguntas());
        assertSame("La pregunta añadida debe ser la correcta", mockPregunta1, encuesta.getPregunta(0));
    }

    /**
     * Prueba añadir una lista de preguntas con setPreguntas
     */
    @Test
    public void testSetPreguntas() 
    {
        ArrayList<Pregunta> lista = new ArrayList<>(Arrays.asList(mockPregunta1, mockPregunta2));
        encuesta.setPreguntas(lista);
        assertEquals("El número de preguntas debe ser 2", 2, encuesta.getNumPreguntas());
        assertSame("La segunda pregunta debe ser la correcta", mockPregunta2, encuesta.getPregunta(1));
    }

    /**
     * Prueba que el contador nextIdP funciona e incrementa correctamente
     */
    @Test
    public void testNextIdP() 
    {
        assertEquals("La primera llamada debe devolver 1", Integer.valueOf(1), encuesta.nextIdP());
        assertEquals("La segunda llamada debe devolver 2", Integer.valueOf(2), encuesta.nextIdP());
        assertEquals("La tercera llamada debe devolver 3", Integer.valueOf(3), encuesta.nextIdP());
    }

    /**
     * Prueba que eliminarPregunta elimina la pregunta correcta
     */
    @Test
    public void testEliminarPregunta_Exito() throws Exception 
    {
        encuesta.setPregunta(mockPregunta1); // id 10
        encuesta.setPregunta(mockPregunta2); // id 20
        assertEquals("Inicialmente debe tener 2 preguntas", 2, encuesta.getNumPreguntas());
        encuesta.eliminarPregunta(10);
        assertEquals("Debe quedar 1 pregunta", 1, encuesta.getNumPreguntas());
        assertSame("La pregunta restante debe ser la 2 (id 20)", mockPregunta2, encuesta.getPregunta(0));
    }

    /**
     * Prueba que eliminarPregunta lanza una Excepción si el id no existe.
     */
    @Test(expected = Exception.class)
    public void testEliminarPregunta_Fallo_LanzaException() throws Exception 
    {
        encuesta.setPregunta(mockPregunta1); // Solo existe id 10
        encuesta.eliminarPregunta(99); // id 99 no existe
    }

    /**
     * Prueba que print_preguntas imprime los enunciados correctos en la consola
     */
    @Test
    public void testPrintPreguntas() 
    {
        encuesta.setPregunta(mockPregunta1);
        encuesta.setPregunta(mockPregunta2);
        encuesta.print_preguntas();
        String expectedOutput = "Enunciado Pregunta 1" + nueva_linea + "Enunciado Pregunta 2" + nueva_linea;
        assertEquals(expectedOutput, outContent.toString());
    }

    /**
     * Prueba que getPreguntes devuelve la lista interna
     */
    @Test
    public void testGetPreguntesDevuelveListaInterna() 
    {
        encuesta.setPregunta(mockPregunta1);
        ArrayList<Pregunta> listaInterna = encuesta.getPreguntes();
        assertNotNull(listaInterna);
        assertEquals(1, listaInterna.size());
        listaInterna.add(mockPregunta2);
        assertEquals("Modificar la lista devuelta debe modificar la encuesta", 2, encuesta.getNumPreguntas());
    }
}