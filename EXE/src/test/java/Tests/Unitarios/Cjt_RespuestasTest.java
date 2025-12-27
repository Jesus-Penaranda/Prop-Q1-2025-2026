package Tests.Unitarios;
import dominio.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import static org.mockito.Mockito.*;

public class Cjt_RespuestasTest 
{

    @Test
    public void testAddRespAndGetRespuestasPersona() 
    {
        Cjt_Respuestas cjt = new Cjt_Respuestas();
        Persona persona = mock(Persona.class);
        Encuesta encuesta = mock(Encuesta.class);
        Pregunta pregunta = mock(Pregunta.class);

        Respuesta r1 = new Textual(persona, encuesta, pregunta, "Hola");
        Respuesta r2 = new Textual(persona, encuesta, pregunta, "Adios");

        cjt.add_resp(r1, "P1");
        cjt.add_resp(r2, "P1");

        ArrayList<Respuesta> respuestas = cjt.getRespuestasPersona("P1");
        assertEquals(2, respuestas.size());
        assertEquals("Hola", respuestas.get(0).getValor());
        assertEquals("Adios", respuestas.get(1).getValor());
    }

    @Test
    public void testAddAllRespAndGetPersonas() 
    {
        Cjt_Respuestas cjt = new Cjt_Respuestas();
        Persona persona = mock(Persona.class);
        Encuesta encuesta = mock(Encuesta.class);
        Pregunta pregunta = mock(Pregunta.class);
        ArrayList<Respuesta> lista = new ArrayList<>();
        lista.add(new Textual(persona, encuesta, pregunta, "Uno"));
        lista.add(new Textual(persona, encuesta, pregunta, "Dos"));
        cjt.add_all_resp("P2", lista);
        ArrayList<String> personas = cjt.getPersonas();
        assertTrue(personas.contains("P2"));
    }

    @Test
    public void testGetMatrizIgnoresNoPreg() 
    {
        Cjt_Respuestas cjt = new Cjt_Respuestas();
        Persona persona = mock(Persona.class);
        Encuesta encuesta = mock(Encuesta.class);
        Pregunta pregunta = mock(Pregunta.class);
        when(pregunta.getEnunciado()).thenReturn("PreguntaIgnorada");
        Respuesta r = new Textual(persona, encuesta, pregunta, "Valor");
        cjt.add_resp(r, "P3");
        HashSet<String> noPreg = new HashSet<>();
        noPreg.add("PreguntaIgnorada");
        cjt.addNoPreg(noPreg);
        HashMap<String, ArrayList<Respuesta>> matriz = cjt.getMatriz();
        assertTrue(matriz.get("P3").isEmpty());
    }
}