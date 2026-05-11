package Tests.Unitarios;

import dominio.Unica;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class UnicaTest 
{
    @Test
    public void testNumericOptions_parsingAndValidation() 
    {
        List<String> opciones = Arrays.asList("1.5", "3.0", "2.25");
        Unica p = new Unica(1, "Pregunta num", null, 1, 1, opciones);

        // getters
        assertEquals(opciones, p.getOpciones());
        assertEquals(3, p.getNumModalitats());

        // Vmax y Vmin deben haberse calculado
        assertNotNull(p.getVmax());
        assertNotNull(p.getVmin());
        assertEquals(3.0, p.getVmax(), 1e-9);
        assertEquals(1.5, p.getVmin(), 1e-9);

        // numerales y validaciones
        assertEquals(Integer.valueOf(0), p.getNumeral("1.5"));
        assertEquals(Integer.valueOf(2), p.getNumeral("2.25"));
        assertNull(p.getNumeral("no-existe"));

        assertTrue(p.esRespuestaValida("1"));
        assertTrue(p.esRespuestaValida("2"));
        assertTrue(p.esRespuestaValida("3"));
        assertFalse(p.esRespuestaValida("0"));
        assertFalse(p.esRespuestaValida("4"));
        assertFalse(p.esRespuestaValida("abc"));
        assertFalse(p.esRespuestaValida(null));
    }

    @Test
    public void testNonNumericOptions_vminVmax_nullAndNumeral() 
    {
        List<String> opciones = Arrays.asList("A", "B");
        Unica p = new Unica(2, "Pregunta texto", null, 1, 1, opciones);
        assertNull(p.getVmax());
        assertNull(p.getVmin());

        assertEquals(2, p.getNumModalitats());
        assertEquals(Integer.valueOf(0), p.getNumeral("A"));
        assertEquals(Integer.valueOf(1), p.getNumeral("B"));
        assertNull(p.getNumeral("C"));
    }

    @Test
    public void testMostrarPregunta_outputsEnunciadoAndOptionsAndPrompt() 
    {
        List<String> opciones = Arrays.asList("Uno", "Dos");
        Unica p = new Unica(3, "¿Cuál?", null, 1, 1, opciones);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));
        try { p.mostrarPregunta(); } 
        finally { System.setOut(oldOut); }
        String salida = out.toString();
        assertTrue(salida.contains("¿Cuál?"));
        assertTrue(salida.contains("1. Uno"));
        assertTrue(salida.contains("2. Dos"));
        assertTrue(salida.contains("Introduce tu opción"));
    }
}