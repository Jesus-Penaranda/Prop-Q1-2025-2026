package Tests.Unitarios;

import dominio.Multiple;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * Test unitario para la clase Libre
 */
public class MultipleTest 
{

    @Test
    public void testGetOpcionesAndNumModalitats() 
    {
        List<String> opciones = Arrays.asList("Op1", "Op2", "Op3");
        Multiple p = new Multiple(1, "Enunciado", null, 1, 1, opciones);
        assertEquals(opciones, p.getOpciones());
        assertEquals(3, p.getNumModalitats());
    }

    @Test
    public void testEsRespuestaValida_validInputs() 
    {
        List<String> opciones = Arrays.asList("A", "B", "C");
        Multiple p = new Multiple(1, "Q", null, 1, 1, opciones);
        assertTrue(p.esRespuestaValida("1"));
        assertTrue(p.esRespuestaValida("2"));
        assertTrue(p.esRespuestaValida("1,3"));
        assertTrue(p.esRespuestaValida(" 1 , 2 "));
    }

    @Test
    public void testEsRespuestaValida_invalidInputs() 
    {
        List<String> opciones = Arrays.asList("A", "B", "C");
        Multiple p = new Multiple(1, "Q", null, 1, 1, opciones);
        assertFalse(p.esRespuestaValida(null));
        assertFalse(p.esRespuestaValida(""));
        assertFalse(p.esRespuestaValida("   "));
        assertFalse(p.esRespuestaValida("a,b"));
        assertFalse(p.esRespuestaValida("0")); 
        assertFalse(p.esRespuestaValida("4"));
        assertFalse(p.esRespuestaValida("1,5"));
    }

    @Test
    public void testMostrarPregunta_printsEnunciadoAndOptions() 
    {
        List<String> opciones = Arrays.asList("Uno", "Dos");
        Multiple p = new Multiple(10, "Pregunta test", null, 1, 1, opciones);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        try {p.mostrarPregunta();} 
        finally {System.setOut(originalOut);}
        String printed = out.toString();
        assertTrue(printed.contains("Pregunta test"));
        assertTrue(printed.contains("1. Uno"));
        assertTrue(printed.contains("2. Dos"));
    }

    @Test
    public void testGetTipoPregunta()
     {
        Multiple p = new Multiple(2, "E", null, 1, 1, Arrays.asList("x"));
        assertEquals(Integer.valueOf(2), p.getTipoPregunta());
    }
}