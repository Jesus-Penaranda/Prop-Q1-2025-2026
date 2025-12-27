package Tests.Unitarios;

import dominio.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@RunWith(MockitoJUnitRunner.Silent.class)
public class IniKmeansPPTest 
{
    private IniKmeansPP ini;

    @Mock
    private Respuesta mockRespuestaP1;
    @Mock
    private Respuesta mockRespuestaP2;
    @Mock
    private Respuesta mockRespuestaP3;
    @Mock
    private Respuesta mockRespuestaP4;

    private ArrayList<Respuesta> P1, P2, P3, P4;
    private HashMap<String, ArrayList<Respuesta>> matrizDatos;

    private ArrayList<Respuesta> crearPunto(Respuesta r) {
        return new ArrayList<>(Arrays.asList(r));
    }

    @Before
    public void setUp() 
    {
        ini = new IniKmeansPP(12345); // Semilla fija
        P1 = crearPunto(mockRespuestaP1);
        P2 = crearPunto(mockRespuestaP2);
        P3 = crearPunto(mockRespuestaP3);
        P4 = crearPunto(mockRespuestaP4);
        matrizDatos = new HashMap<>();
        matrizDatos.put("ID_1", P1);
        matrizDatos.put("ID_2", P2);
        matrizDatos.put("ID_3", P3);
        matrizDatos.put("ID_4", P4);
    }

    @Test
    public void testIniCentroides_SeleccionaPuntosLejanos() 
    {
        // Distancias internas (cerca)
        configurarDistancia(mockRespuestaP1, mockRespuestaP2, 0.01);
        configurarDistancia(mockRespuestaP3, mockRespuestaP4, 0.01);

        // Distancias externas (lejos)
        configurarDistancia(mockRespuestaP1, mockRespuestaP3, 100.0);
        configurarDistancia(mockRespuestaP1, mockRespuestaP4, 100.0);
        configurarDistancia(mockRespuestaP2, mockRespuestaP3, 100.0);
        configurarDistancia(mockRespuestaP2, mockRespuestaP4, 100.0);

        ArrayList<ArrayList<Respuesta>> centroides = new ArrayList<>();
        ini.iniCentroides(2, centroides, matrizDatos);

        assertEquals("Debería haber 2 centroides", 2, centroides.size());

        ArrayList<Respuesta> c1 = centroides.get(0);
        ArrayList<Respuesta> c2 = centroides.get(1);
        
        boolean c1_GrupoA = c1.equals(P1) || c1.equals(P2);
        boolean c2_GrupoA = c2.equals(P1) || c2.equals(P2);

        assertTrue("Debería haber elegido uno de cada grupo distinto", c1_GrupoA != c2_GrupoA);
    }

    @Test
    public void testIniCentroides_PuntosIdenticos_EvitaBucleInfinito() 
    {
        when(mockRespuestaP1.distance(any())).thenReturn(0.0);
        when(mockRespuestaP2.distance(any())).thenReturn(0.0);
        when(mockRespuestaP3.distance(any())).thenReturn(0.0);
        when(mockRespuestaP4.distance(any())).thenReturn(0.0);

        ArrayList<ArrayList<Respuesta>> centroides = new ArrayList<>();
        int k = 3;
        
        ini.iniCentroides(k, centroides, matrizDatos);

        System.out.println("Debug - Centroides generados (Caso Idénticos): " + centroides.size());
        
        assertEquals("Si los puntos son iguales, debería duplicar hasta llegar a K", k, centroides.size());
    }

    /**
     * K mayor que el número de puntos.
     * Debería devolver todos los puntos disponibles.
     */
    @Test
    public void testIniCentroides_KMayorQuePuntos() 
    {
        ArrayList<ArrayList<Respuesta>> centroides = new ArrayList<>();
        int k = 10; // Tenemos solo 4 puntos
        
        ini.iniCentroides(k, centroides, matrizDatos);

        assertEquals("Si k > N, devuelve N centroides", 4, centroides.size());
    }

    private void configurarDistancia(Respuesta r1, Respuesta r2, double dist) 
    {
        when(r1.distance(r2)).thenReturn(dist);
        when(r2.distance(r1)).thenReturn(dist);
    }
}