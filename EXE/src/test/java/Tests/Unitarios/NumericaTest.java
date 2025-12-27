package Tests.Unitarios;

import dominio.*;
import org.junit.Test;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para la clase Numerica
 */
public class NumericaTest 
{

    @Test
    public void testGetValor_UnicaYLibre() {

        Persona p = mock(Persona.class);
        Encuesta e = mock(Encuesta.class);
        Unica preguntaUnica = mock(Unica.class);
        Numerica nU = new Numerica(p, e, preguntaUnica, 5.0);
        assertEquals(5.0, nU.getValor());

        Libre preguntaLibre = mock(Libre.class);
        Numerica nL = new Numerica(p, e, preguntaLibre, 3.14);
        assertEquals(3.14, nL.getValor());
    }

    @Test
    public void testGetValor_Multiple() 
    {
        Persona p = mock(Persona.class);
        Encuesta e = mock(Encuesta.class);

        Multiple preguntaMultiple = mock(Multiple.class);
        Set<Double> conjunto = new HashSet<>(Arrays.asList(1.0, 2.0));
        Numerica nm = new Numerica(p, e, preguntaMultiple, conjunto);
        Object val = nm.getValor();
        assertTrue(val instanceof Set);

        assertEquals(conjunto, val);
    }

    @Test
    public void testSetters() 
    {
        Persona p = mock(Persona.class);
        Encuesta e = mock(Encuesta.class);
        Unica u = mock(Unica.class);
        when(u.getVmax()).thenReturn(5.0);
        when(u.getVmin()).thenReturn(0.0);
        Numerica n = new Numerica(p, e, u);
        n.setValorUnic(4.0);
        assertEquals(4.0, n.getValor());
        Set<Double> set = new HashSet<>(Arrays.asList(1.0, 2.0));
        Multiple m = mock(Multiple.class);
        Numerica nm = new Numerica(p, e, m);
        nm.setValorsMultiples(set);
        assertEquals(set, nm.getValor());
    }
}