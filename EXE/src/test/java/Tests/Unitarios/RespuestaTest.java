package Tests.Unitarios;

import dominio.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * Tests unitari para la clase abstracta Respuesta
 * Se usan subclases de prueba para implementar los metoodos abstractos
 */
public class RespuestaTest 
{

    // Pequeña implementación de prueba de Respuesta
    private static class TestRespuesta extends Respuesta 
    {
        private final Object valor;
        public TestRespuesta(Persona persona_as, Encuesta encuesta_as, Pregunta pregunta_as, Object valor) 
        {
            super(persona_as, encuesta_as, pregunta_as);
            this.valor = valor;
        }

        @Override
        public Object getValor() { return valor;}

        /**
         * Implementación simple para pruebas: si ambos valores son iguales  (0.0),
         * si alguno es null y otro no (1.0), si distintos entonces vale 1.0
         */
        @Override
        public double distance(Respuesta r) 
        {
            if (r == null) return 1.0;
            Object other = r.getValor();
            if (this.valor == null && other == null) return 0.0;
            if (this.valor == null || other == null) return 1.0;
            return this.valor.equals(other) ? 0.0 : 1.0;
        }
    }

    @Test
    public void constructorAndGetters_returnProvidedReferences() 
    {
        Persona persona = mock(Persona.class);
        Encuesta encuesta = mock(Encuesta.class);
        Pregunta pregunta = mock(Pregunta.class);
        TestRespuesta tr = new TestRespuesta(persona, encuesta, pregunta, "valorX");

        assertSame("Persona debe ser la misma instancia pasada al constructor", persona, tr.getPersona());
        assertSame("Encuesta debe ser la misma instancia pasada al constructor", encuesta, tr.getEncuesta());
        assertSame("Pregunta debe ser la misma instancia pasada al constructor", pregunta, tr.getPregunta());
        assertEquals("valorX", tr.getValor());
    }

    @Test
    public void distance_behaviour_equalsAndNulls() 
    {
        Persona persona = mock(Persona.class);
        Encuesta encuesta = mock(Encuesta.class);
        Pregunta pregunta = mock(Pregunta.class);

        TestRespuesta a = new TestRespuesta(persona, encuesta, pregunta, "X");
        TestRespuesta b = new TestRespuesta(persona, encuesta, pregunta, "X");
        TestRespuesta c = new TestRespuesta(persona, encuesta, pregunta, "Y");
        TestRespuesta n1 = new TestRespuesta(persona, encuesta, pregunta, null);
        TestRespuesta n2 = new TestRespuesta(persona, encuesta, pregunta, null);

        assertEquals(0.0, a.distance(b), 0.0);
        assertEquals(1.0, a.distance(c), 0.0);
        assertEquals(1.0, a.distance(n1), 0.0);
        assertEquals(0.0, n1.distance(n2), 0.0);
    }
}