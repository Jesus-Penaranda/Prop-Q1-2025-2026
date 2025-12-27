package Tests.Unitarios;

import dominio.Pregunta;
import dominio.Encuesta;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitari para la clase abstracta Pregunta
 * Se usan subclases de prueba para implementar los metoodos abstractos
 */
public class PreguntaTest 
{
    // Subclase de prueba usando el constructor por defecto
    private static class TestPreguntaDefault extends Pregunta 
    {
        @Override
        public void mostrarPregunta() {} // Para el test
        @Override
        public boolean esRespuestaValida(String input) { return input != null && !input.trim().isEmpty(); }
    }

    // Subclase de prueba que permite usar el constructor protegido con param
    private static class TestPreguntaParam extends Pregunta 
    {
        public TestPreguntaParam(Integer id, String enun, Encuesta enc, Integer tipoP, Integer tipoR) { super(id, enun, enc, tipoP, tipoR);}
        @Override
        public void mostrarPregunta() {} // Para el test
        @Override
        public boolean esRespuestaValida(String input) { return input != null && input.length() > 0; }
    }

    @Test
    public void defaultConstructor_initialValuesAreNull() 
    {
        Pregunta p = new TestPreguntaDefault();
        assertNull(p.getId());
        assertNull(p.getEnunciado());
        assertNull(p.getIdEncuesta());
        assertNull(p.getTipo());
        assertNull(p.getTipoPregunta());
        assertFalse(p.esRespuestaValida(null));
        assertFalse(p.esRespuestaValida("   "));
        assertTrue(p.esRespuestaValida("respuesta"));
    }

    @Test
    public void paramConstructor_andGetters_workCorrectly() 
    {
        Encuesta mockEnc = mock(Encuesta.class);
        when(mockEnc.getId()).thenReturn(123);
        Pregunta p = new TestPreguntaParam(7, "Enunciado test", mockEnc, 2, 4);
        assertEquals(Integer.valueOf(7), p.getId());
        assertEquals("Enunciado test", p.getEnunciado());
        assertEquals(Integer.valueOf(123), p.getIdEncuesta());
        assertEquals(Integer.valueOf(4), p.getTipo());
        assertEquals(Integer.valueOf(2), p.getTipoPregunta());
    }

    @Test
    public void perteneceAEncuesta_behaviour() 
    {
        Encuesta mockEnc = mock(Encuesta.class);
        when(mockEnc.getId()).thenReturn(999);
        Pregunta p = new TestPreguntaDefault();
        assertFalse(p.perteneceAEncuesta("999"));
        p.setEncuesta(mockEnc);
        assertTrue(p.perteneceAEncuesta("999"));
        assertFalse(p.perteneceAEncuesta(null));
        assertFalse(p.perteneceAEncuesta("other"));
    }
}