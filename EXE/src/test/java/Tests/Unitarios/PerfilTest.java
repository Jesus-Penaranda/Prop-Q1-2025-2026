package Tests.Unitarios;

import dominio.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

public class PerfilTest 
{

    @Test
    public void testEmptyAndParamConstructorCopiesListAndRepresentative() 
    {
        Perfil pEmpty = new Perfil();
        assertNotNull(pEmpty.getListaIndividuos());
        assertTrue(pEmpty.getListaIndividuos().isEmpty());
        assertNull(pEmpty.getRepresentante());

        ArrayList<String> lista = new ArrayList<>();
        lista.add("X");
        lista.add("Y");
        Perfil p = new Perfil(lista, "Y");

        lista.add("Z");
        ArrayList<String> perfilLista = p.getListaIndividuos();
        assertEquals(2, perfilLista.size());
        assertTrue(perfilLista.contains("X"));
        assertTrue(perfilLista.contains("Y"));
        assertEquals("Y", p.getRepresentante());
    }

    @Test
    public void testConstructorSelectsNearestRepresentative() 
    {
        ArrayList<String> individuos = new ArrayList<>();
        individuos.add("A");
        individuos.add("B");
        individuos.add("C");
        Cjt_Respuestas mockResps = mock(Cjt_Respuestas.class);
        Respuesta respA = mock(Respuesta.class);
        Respuesta respB = mock(Respuesta.class);
        Respuesta respC = mock(Respuesta.class);
        Respuesta centro = mock(Respuesta.class);
        Pregunta mockPregunta = mock(Pregunta.class);
        when(mockPregunta.getEnunciado()).thenReturn("Pregunta Dummy");

        when(respA.getPregunta()).thenReturn(mockPregunta);
        when(respB.getPregunta()).thenReturn(mockPregunta);
        when(respC.getPregunta()).thenReturn(mockPregunta);
        when(centro.getPregunta()).thenReturn(mockPregunta);

        ArrayList<Respuesta> listA = new ArrayList<>(); listA.add(respA);
        ArrayList<Respuesta> listB = new ArrayList<>(); listB.add(respB);
        ArrayList<Respuesta> listC = new ArrayList<>(); listC.add(respC);

        when(mockResps.getRespuestasPersona("A")).thenReturn(listA);
        when(mockResps.getRespuestasPersona("B")).thenReturn(listB);
        when(mockResps.getRespuestasPersona("C")).thenReturn(listC);
        when(respA.distance(centro)).thenReturn(5.0);
        when(respB.distance(centro)).thenReturn(2.0);
        when(respC.distance(centro)).thenReturn(3.0);

        ArrayList<Respuesta> centroide = new ArrayList<>();
        centroide.add(centro);
        
        Perfil perfil = new Perfil(individuos, centroide, mockResps);
        assertEquals(3, perfil.getListaIndividuos().size());
        assertEquals("B", perfil.getRepresentante());
    }
}