package Tests.Unitarios;

import dominio.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap; 

/**
 * Test unitario para la clase IniTrivial
 */
@RunWith(MockitoJUnitRunner.class)
public class IniTrivialTest 
{
    private IniTrivial ini;
    @Mock
    private Respuesta mockR1, mockR2, mockR3;
    private ArrayList<Respuesta> P1, P2, P3;
    private LinkedHashMap<String, ArrayList<Respuesta>> matrizOrdenada;


    @Before
    public void setUp() 
    {
        ini = new IniTrivial();

        //1- Crear los 3 puntos (cada uno es una lista de 1 Respuesta)
        P1 = new ArrayList<>(Arrays.asList(mockR1) );
        P2 = new ArrayList<>(Arrays.asList(mockR2) );
        P3 = new ArrayList<>(Arrays.asList(mockR3) );

        //2- Crear la matriz de datos ordenada
        matrizOrdenada = new LinkedHashMap<>();
        matrizOrdenada.put("Persona_1_Ana", P1);   
        matrizOrdenada.put("Persona_2_Juan", P2); 
        matrizOrdenada.put("Persona_3_Maria", P3); 
    }

    /**
     * Prueba el caso principal: k es menor que el total de puntos
     * Debe seleccionar solo los 2 primeros
     */
    @Test
    public void testIniCentroides_K_EsMenorQueTotal() 
    {

        ArrayList<ArrayList<Respuesta>> centroides = new ArrayList<>();
        int k = 2;
        ini.iniCentroides(k, centroides, matrizOrdenada);

        assertEquals("Debe haber 2 centroides seleccionados", 2, centroides.size());
        assertEquals("El primer centroide debe ser P1", P1, centroides.get(0));
        assertEquals("El segundo centroide debe ser P2", P2, centroides.get(1));
        assertFalse("No debe contener P3", centroides.contains(P3));
    }

    /**
     * Prueba el caso extremo: k es mayor que el total de puntos
     * Debe seleccionar todos los puntos disponibles
     */
    @Test
    public void testIniCentroides_K_EsMayorQueTotal()
     {

        ArrayList<ArrayList<Respuesta>> centroides = new ArrayList<>();
        int k = 5; 
        ini.iniCentroides(k, centroides, matrizOrdenada);
        assertEquals("Debe seleccionar todos los puntos (3)", 3, centroides.size());
        assertTrue("Debe contener P1", centroides.contains(P1));
        assertTrue("Debe contener P2", centroides.contains(P2));
        assertTrue("Debe contener P3", centroides.contains(P3));
    }

    /**
     * Prueba el caso extremo: la matriz de datos está vacía
     */
    @Test
    public void testIniCentroides_MatrizVacia() 
    {
        ArrayList<ArrayList<Respuesta>> centroides = new ArrayList<>();
        HashMap<String, ArrayList<Respuesta>> matrizVacia = new HashMap<>();
        int k = 2;
        ini.iniCentroides(k, centroides, matrizVacia);
        assertEquals("La lista de centroides debe estar vacía", 0, centroides.size());
    }

    /**
     * Prueba que el algoritmo crea copias de los puntos, no referencias
     */
    @Test
    public void testIniCentroides_CreaCopias() 
    {
        ArrayList<ArrayList<Respuesta>> centroides = new ArrayList<>();
        int k = 1;
        ini.iniCentroides(k, centroides, matrizOrdenada);
        assertEquals("Debe tener 1 centroide", 1, centroides.size());
        assertEquals("El contenido debe ser igual a P1", P1, centroides.get(0));
        assertNotSame("La instancia debe ser una copia, no el original", P1, centroides.get(0));
    }
}