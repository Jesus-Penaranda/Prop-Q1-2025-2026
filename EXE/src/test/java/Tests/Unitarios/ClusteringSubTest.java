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

/**
 * Test Unitario para la clase ClusteringSub.
 * Se usa Mockito para aislar la clase de sus dependencias (Cjt_Respuestas, IniKmeansPP, y Respuesta).
 */
@RunWith(MockitoJUnitRunner.class)
public class ClusteringSubTest {


    private ClusteringSub clustering;
    @Mock
    private Cjt_Respuestas mockRespuestas;
    @Mock
    private IniKmeansPP mockIni;
    @Mock
    private Respuesta mockR_A1, mockR_A2; // Punto A (Ana)
    @Mock
    private Respuesta mockR_B1, mockR_B2; // Punto B (Juan)
    @Mock
    private Respuesta mockR_C1, mockR_C2; // Punto C (Maria)
    @Mock
    private Respuesta mockC1_R1, mockC1_R2; // Centroide  1
    @Mock
    private Respuesta mockC2_R1, mockC2_R2; // Centroide 2
    private HashMap<String, ArrayList<Respuesta>> matrizSimulada;
    private ArrayList<Respuesta> puntoA, puntoB, puntoC;
    private ArrayList<Respuesta> centroide1, centroide2;

    /**
     * Método de ayuda para crear un punto de 2 dimensiones.
     */
    private ArrayList<Respuesta> creaPunto(Respuesta r1, Respuesta r2) { return new ArrayList<>(Arrays.asList(r1, r2));}

    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() 
    {
        // 1- Crear los puntos de datos (lo ciudadanos)
        puntoA = creaPunto(mockR_A1, mockR_A2);
        puntoB = creaPunto(mockR_B1, mockR_B2);
        puntoC = creaPunto(mockR_C1, mockR_C2);

        // 2- Crear la matriz de respuestas
        matrizSimulada = new HashMap<>();
        matrizSimulada.put("Ana", puntoA);
        matrizSimulada.put("Juan", puntoB);
        matrizSimulada.put("Maria", puntoC);
        
        // 3- Crear centroides inicialess
        centroide1 = creaPunto(mockC1_R1, mockC1_R2);
        centroide2 = creaPunto(mockC2_R1, mockC2_R2);

        when(mockRespuestas.getMatriz()).thenReturn(matrizSimulada);


        doAnswer(invocation -> 
        {
            ArrayList<ArrayList<Respuesta>> centroidesVacios = invocation.getArgument(1);
            centroidesVacios.add(centroide1);
            centroidesVacios.add(centroide2);
            return null;
        }).when(mockIni).iniCentroides(eq(2), any(), eq(matrizSimulada));

        // 4- Simular las distancias 
        // Forzamos que Ana y Juan pertenezcan al Cluster 1, Maria al Cluster 2
        // Distancias de Ana 0.1 a C1, 0.9 a C2 -> (a C1)
        when(mockR_A1.distance(mockC1_R1)).thenReturn(0.1);
        when(mockR_A2.distance(mockC1_R2)).thenReturn(0.1); // Total C1: 0.2/2 = 0.1
        when(mockR_A1.distance(mockC2_R1)).thenReturn(0.9);
        when(mockR_A2.distance(mockC2_R2)).thenReturn(0.9); // Total C2: 1.8/2 = 0.9
        
        // Distancias de Juan: 0.2 a C1, 0.8 a C2 -> (Irá a C1)
        when(mockR_B1.distance(mockC1_R1)).thenReturn(0.2);
        when(mockR_B2.distance(mockC1_R2)).thenReturn(0.2); // Total C1: 0.4/2 = 0.2
        when(mockR_B1.distance(mockC2_R1)).thenReturn(0.8);
        when(mockR_B2.distance(mockC2_R2)).thenReturn(0.8); // Total C2: 1.6/2 = 0.8

        // Distancias de Maria: 0.7 a C1, 0.3 a C2 -> (Irá a C2)
        when(mockR_C1.distance(mockC1_R1)).thenReturn(0.7);
        when(mockR_C2.distance(mockC1_R2)).thenReturn(0.7); // Total C1: 1.4/2 = 0.7
        when(mockR_C1.distance(mockC2_R1)).thenReturn(0.3);
        when(mockR_C2.distance(mockC2_R2)).thenReturn(0.3); // Total C2: 0.6/2 = 0.3

        // 6. Simular distancias para evaluaCalidad (Silhouette)
        // a(i) -> distancias internas (Ana-Juan)
        when(mockR_A1.distance(mockR_B1)).thenReturn(0.1); // Ana- Juan (cerca)
        when(mockR_A2.distance(mockR_B2)).thenReturn(0.1);
        when(mockR_B1.distance(mockR_A1)).thenReturn(0.1); // Juan- Ana (cerca)
        when(mockR_B2.distance(mockR_A2)).thenReturn(0.1);
        
        // b(i) -> distancias externas (Ana-Maria, Juan-Maria)
        when(mockR_A1.distance(mockR_C1)).thenReturn(0.9); // Ana- Maria (lejos)
        when(mockR_A2.distance(mockR_C2)).thenReturn(0.9);
        when(mockR_C1.distance(mockR_A1)).thenReturn(0.9); // Maria- Ana (lejos)
        when(mockR_C2.distance(mockR_A2)).thenReturn(0.9);
        
        when(mockR_B1.distance(mockR_C1)).thenReturn(0.8); // Juan- Maria (lejos)
        when(mockR_B2.distance(mockR_C2)).thenReturn(0.8);
        when(mockR_C1.distance(mockR_B1)).thenReturn(0.8); // Maria- Juan (lejos)
        when(mockR_C2.distance(mockR_B2)).thenReturn(0.8);
    }

    /**
     * Prueba el estado inicial del objeto justo después de la constructora
     */
    @Test
    public void testConstructoraEIncializacion() 
    {
        clustering = new ClusteringSub(mockRespuestas, mockIni, 2);
        assertEquals(2, clustering.getK());
        assertNotNull(clustering.getCentroides());
        assertEquals(0, clustering.getCentroides().size());
        assertNotNull(clustering.getInfoAsignaciones());
        assertEquals(0, clustering.getInfoAsignaciones().size());
        assertTrue(Double.isNaN(clustering.getIndiceCalidad()));
    }

    /**
     * Prueba la ejecución completa de iniciaAlgoritmo()
     */
    @Test
    public void testIniciaAlgoritmo() 
    {
        clustering = new ClusteringSub(mockRespuestas, mockIni, 2);
        clustering.iniciaAlgoritmo();
        ArrayList<ArrayList<String>> asignaciones = clustering.getInfoAsignaciones();
        assertNotNull(asignaciones);
        assertEquals(2, asignaciones.size());
        assertEquals(2, asignaciones.get(0).size());
        assertTrue(asignaciones.get(0).contains("Ana"));
        assertTrue(asignaciones.get(0).contains("Juan"));
        assertEquals(1, asignaciones.get(1).size());
        assertTrue(asignaciones.get(1).contains("Maria"));
        ArrayList<ArrayList<Respuesta>> centroides = clustering.getCentroides();
        assertEquals(2, centroides.size());
        assertSame(centroide1, centroides.get(0));
        assertSame(centroide2, centroides.get(1));
        assertEquals(0.921, clustering.getIndiceCalidad(), 0.001);
    }
    
    /**
     * Prueba el caso extremo de evaluaCalidad cuando k = 1 (un solo cluster)
     */
    @Test
    public void testIniciaAlgoritmoConUnCluster() 
    {

        clustering = new ClusteringSub(mockRespuestas, mockIni, 1); 
        when(mockRespuestas.getMatriz()).thenReturn(matrizSimulada);
        doAnswer(invocation -> 
        {
            ArrayList<ArrayList<Respuesta>> centroidesVacios = invocation.getArgument(1);
            centroidesVacios.add(centroide1);
            return null;
        }).when(mockIni).iniCentroides(eq(1), any(), eq(matrizSimulada));

        when(mockR_A1.distance(mockC1_R1)).thenReturn(0.1);
        when(mockR_A2.distance(mockC1_R2)).thenReturn(0.1);
        when(mockR_B1.distance(mockC1_R1)).thenReturn(0.2);
        when(mockR_B2.distance(mockC1_R2)).thenReturn(0.2);
        when(mockR_C1.distance(mockC1_R1)).thenReturn(0.3);
        when(mockR_C2.distance(mockC1_R2)).thenReturn(0.3);
        clustering.iniciaAlgoritmo();
        assertEquals(3, clustering.getInfoAsignaciones().get(0).size());
        assertEquals(0.0, clustering.getIndiceCalidad(), 0.001);
    }

    /**
     * Prueba el caso extremo de evaluaCalidad cuando no hay puntos
     */
    @Test
    public void testIniciaAlgoritmoConCeroPuntos() 
    {
        clustering = new ClusteringSub(mockRespuestas, mockIni, 2);
        HashMap<String, ArrayList<Respuesta>> matrizVacia = new HashMap<>();
        when(mockRespuestas.getMatriz()).thenReturn(matrizVacia);
        doAnswer(invocation -> 
        {
            ArrayList<ArrayList<Respuesta>> centroidesVacios = invocation.getArgument(1);
            centroidesVacios.add(centroide1);
            centroidesVacios.add(centroide2);
            return null;
        }).when(mockIni).iniCentroides(eq(2), any(), eq(matrizVacia));
        clustering.iniciaAlgoritmo();
        assertEquals(0, clustering.getInfoAsignaciones().get(0).size());
        assertEquals(0, clustering.getInfoAsignaciones().get(1).size());
        assertEquals(0.0, clustering.getIndiceCalidad(), 0.001);
    }
}