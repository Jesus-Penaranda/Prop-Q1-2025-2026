package Tests.Unitarios; 

import dominio.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test unitario para la clase Textual
 */
@RunWith(MockitoJUnitRunner.class)
public class TextualTest 
{
    @Mock
    private Persona mockPersona;
    @Mock
    private Encuesta mockEncuesta;
    @Mock
    private Unica mockPreguntaTipo3; // Cualitativa ordenada
    @Mock
    private Unica mockPreguntaTipo4Unica; // Cualitativa no ordenada
    @Mock
    private Multiple mockPreguntaTipo4Multiple;
    @Mock
    private Libre mockPreguntaTipo4Libre;

    /**
     * Utilidad para crear Sets
     */
    private Set<String> setOf(String... items) {return new HashSet<>(Arrays.asList(items));}

    @Test
    public void testGetValorRespuestaUnica()
    {
        when(mockPreguntaTipo4Unica.getTipo()).thenReturn(4);
        Textual resposta = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Unica, "Valor de prova");
        assertEquals("Valor de prova", resposta.getValor());
    }

    @Test
    public void testGetValorRespuestaMultiple() 
    {
        Set<String> valors = setOf("A", "B");
        Textual resposta = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Multiple, valors);
        assertEquals(valors, resposta.getValor());
    }

    @Test
    public void testDistanceTipo3OrdenadaNormal() 
    {
        when(mockPreguntaTipo3.getTipo()).thenReturn(3);
        when(mockPreguntaTipo3.getNumModalitats()).thenReturn(5);
        when(mockPreguntaTipo3.getNumeral("Molt Mal")).thenReturn(0);
        when(mockPreguntaTipo3.getNumeral("Molt Be")).thenReturn(4);
        when(mockPreguntaTipo3.getNumeral("Normal")).thenReturn(2);

        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo3, "Molt Mal");
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo3, "Molt Be");
        double dist = respostaA.distance(respostaB);
        assertEquals(1.0, dist, 0.001);
        Textual respostaC = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo3, "Normal");
        double dist2 = respostaA.distance(respostaC);
        assertEquals(0.5, dist2, 0.001);
    }

    @Test
    public void testDistanceTipo3OrdenadaExtremoNulls() 
    {
        when(mockPreguntaTipo3.getTipo()).thenReturn(3);
        when(mockPreguntaTipo3.getNumModalitats()).thenReturn(5);
        when(mockPreguntaTipo3.getNumeral("Normal")).thenReturn(2);
        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo3, "Normal");
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo3, (String) null);
        Textual respostaC = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo3, (String) null);
        assertEquals(1.0, respostaA.distance(respostaB), 0.001); 
        assertEquals(0.0, respostaB.distance(respostaC), 0.001); 
    }

    @Test
    public void testDistanceTipo3OrdenadaExtremoDivCero() {
        when(mockPreguntaTipo3.getTipo()).thenReturn(3);
        when(mockPreguntaTipo3.getNumModalitats()).thenReturn(1);
        when(mockPreguntaTipo3.getNumeral("Unica")).thenReturn(0);
        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo3, "Unica");
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo3, "Unica");
        assertEquals(0.0, respostaA.distance(respostaB), 0.001);
    }

    @Test
    public void testDistanceTipo4Unica() 
    {
        when(mockPreguntaTipo4Unica.getTipo()).thenReturn(4);
        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Unica, "Rock");
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Unica, "Pop");
        Textual respostaC = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Unica, "Rock");
        assertEquals(1.0, respostaA.distance(respostaB), 0.001);
        assertEquals(0.0, respostaA.distance(respostaC), 0.001);
    }

    @Test
    public void testDistanceTipo4UnicaExtremoNulls() 
    {
        when(mockPreguntaTipo4Unica.getTipo()).thenReturn(4);
        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Unica, "Rock");
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Unica, (String) null);
        Textual respostaC = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Unica, (String) null);
        assertEquals(1.0, respostaA.distance(respostaB), 0.001);
        assertEquals(0.0, respostaB.distance(respostaC), 0.001);
    }

    @Test
    public void testDistanceTipo4MultipleJaccard() 
    {
        when(mockPreguntaTipo4Multiple.getTipo()).thenReturn(4);
        Set<String> setA = setOf("Rock", "Pop", "Jazz");
        Set<String> setB = setOf("Pop", "Jazz", "Classica");
        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Multiple, setA);
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Multiple, setB);
        assertEquals(0.5, respostaA.distance(respostaB), 0.001);
    }

    @Test
    public void testDistanceTipo4MultipleExtremoNulls() 
    {
        when(mockPreguntaTipo4Multiple.getTipo()).thenReturn(4);
        Set<String> setA = setOf("Rock", "Pop");
        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Multiple, setA);
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Multiple, (Set<String>) null);
        Textual respostaC = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Multiple, (Set<String>) null);
        assertEquals(1.0, respostaA.distance(respostaB), 0.001);
        assertEquals(0.0, respostaB.distance(respostaC), 0.001);
    }

    @Test
    public void testDistanceTipo4MultipleExtremoEmpty() 
    {
        when(mockPreguntaTipo4Multiple.getTipo()).thenReturn(4);
        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Multiple, new HashSet<String>());
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Multiple, new HashSet<String>());
        assertEquals(0.0, respostaA.distance(respostaB), 0.001);
    }

    @Test
    public void testDistanceTipo4Libre() 
    {
        when(mockPreguntaTipo4Libre.getTipo()).thenReturn(4);
        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Libre, "Un text llarg");
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Libre, "Un altre text diferent");
        assertEquals(0.0, respostaA.distance(respostaB), 0.001);
    }

    @Test
    public void testDistanceTipo4LibreExtremoNulls() 
    {
        when(mockPreguntaTipo4Libre.getTipo()).thenReturn(4);
        Textual respostaA = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Libre, "text 1");
        Textual respostaB = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Libre, (String) null);
        Textual respostaC = new Textual(mockPersona, mockEncuesta, mockPreguntaTipo4Libre, (String) null);
        assertEquals(1.0, respostaA.distance(respostaB), 0.001);
        assertEquals(0.0, respostaB.distance(respostaC), 0.001);
    }
}