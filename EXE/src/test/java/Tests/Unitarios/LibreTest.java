package Tests.Unitarios;
import dominio.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After; 
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test unitario para la clase Libre
 */
@RunWith(MockitoJUnitRunner.class)
public class LibreTest 
{
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Mock
    private Encuesta mockEncuesta;
    private Libre preguntaLibre;
    private String NEW_LINE; 
    @Before
    public void config() 
    {
        System.setOut(new PrintStream(outContent));
        NEW_LINE = System.lineSeparator();
        preguntaLibre = new Libre(1, "Que opinas de PROP?", mockEncuesta,4,1);
    }
    
    @After
    public void restoreStreams() {System.setOut(originalOut);}


    /**
     * Prueba el método "esRespuestaValida" con un caso de válido
     */
    @Test
    public void testEsRespuestaValidaConTextoNormal() 
    {
        String inputValido = "Es una asignatura genial.";
        boolean resultado = preguntaLibre.esRespuestaValida(inputValido);
        assertTrue("Una respuesta con texto normal debería ser válida", resultado);
        assertEquals("No debería imprimirse nada en caso de éxito", "", outContent.toString());
    }

    /**
     * Prueba el metodo "esRespuestaValida" con un valor null
     * Este es un caso extremo
     */
    @Test
    public void testEsRespuestaValidaConNull() 
    {
        String inputNull = null;
        String errorEsperado = "Error: La resposta no puede estar vacía." + NEW_LINE;
        boolean resultado = preguntaLibre.esRespuestaValida(inputNull);
        assertFalse("Una respuesta null no debería ser válida", resultado);
        assertEquals("Debería imprimirse un error si el input es null", errorEsperado, outContent.toString());
    }

    /**
     * Prueba el metodo "esRespuestaValida" con un String vacío ("")
     * Este es un caso extremo
     */
    @Test
    public void testEsRespuestaValidaConStringVacio() 
    {
        String inputVacio = "";
        String errorEsperado = "Error: La resposta no puede estar vacía." + NEW_LINE;
        boolean resultado = preguntaLibre.esRespuestaValida(inputVacio);
        assertFalse("Una respuesta vacía (\"\") no debería ser válida", resultado);
        assertEquals("Debería imprimirse un error si el input está vacío", errorEsperado, outContent.toString());
    }

    /**
     * Prueba el método "esRespuestaValida" con un String en blanco (" ")
     */
    @Test
    public void testEsRespuestaValidaConStringEnBlanco() 
    {
        String inputBlanco = "   ";
        String errorEsperado = "Error: La resposta no puede estar vacía." + NEW_LINE;
        boolean resultado = preguntaLibre.esRespuestaValida(inputBlanco);
        assertFalse("Una respuesta solo con espacios no debería ser válida", resultado);
        assertEquals("Debería imprimirse un error si el input está en blanco", errorEsperado, outContent.toString());
    }

    /**
     * Prueba que el método "mostrarPregunta" imprime el enunciado
     * y el prompt correctamente
     */
    @Test
    public void testMostrarPregunta() 
    {

        String salidaEsperada ="Que opinas de PROP?" + NEW_LINE +"Introduce tu respuesta: ";
        preguntaLibre.mostrarPregunta();
        assertEquals(salidaEsperada, outContent.toString());
    }
}