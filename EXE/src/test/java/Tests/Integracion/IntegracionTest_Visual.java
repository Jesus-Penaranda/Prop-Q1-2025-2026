package Tests.Integracion;

import dominio.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Test de integración con datos heterogéneos y varias preguntas
 * Valida el clustering con datos heterogéneos (Edad +Ciudad + Hobbies)
 */
public class IntegracionTest_Visual 
{

    @Test
    public void testClusteringDetallado() throws Exception 
    {
        System.out.println("INICIO DEL TEST DE INTEGRACIÓN:");
        System.out.println("Paso 1: Configuración de la encuesta");
        ctrlDominio ctrl = new ctrlDominio();
        ctrl.login("TesterVisual", "1234"); // Login inventado para testear
        ArrayList<ctrlDominio.DatosPregunta> datos = new ArrayList<>();
        datos.add(new ctrlDominio.DatosPregunta("Edad", 1, 1, null)); // Numérica
        List<String> ciudades = Arrays.asList("Madrid", "Barcelona", "Valencia");
        datos.add(new ctrlDominio.DatosPregunta("Ciudad", 3, 3, ciudades)); // Unica (Textual)
        List<String> hobbies = Arrays.asList("Futbol", "Tenis", "Pintura", "Leer");
        datos.add(new ctrlDominio.DatosPregunta("Hobbies", 2, 4, hobbies)); // Multiple

        Integer idEncuesta = ctrl.crearEncuesta(datos);
        Encuesta enc = ctrl.getEncuesta(idEncuesta);
        ArrayList<Pregunta> pregs = enc.getPreguntes();
        System.out.println("Encuesta creada con ID: " + idEncuesta);
        System.out.println("Estructura de Preguntas:");
        System.out.printf("  %-5s %-20s %-15s\n", "ID", "Enunciado", "Tipo");
        System.out.println("  ---------------------------------------------");
        for (Pregunta p : pregs) {System.out.printf("  %-5d %-20s %-15s\n", p.getId(), p.getEnunciado(), p.getClass().getSimpleName());}
        System.out.println("Paso 2: Generación de perfiles de prueba");
        System.out.println("Objetivo: Crear dos grupos claramente diferenciados.");
        System.out.println("  - GRUPO A: Jóvenes (19-22), Madrid, Deportistas.");
        System.out.println("  - GRUPO B: Adultos (40-45), Barcelona, Culturales.");
        System.out.println("\nDatos inyectados:");
        System.out.printf("  %-10s %-10s %-15s %-20s\n", "PERSONA", "EDAD", "CIUDAD", "HOBBIES");
        System.out.println("  -------------------------------------------------------------");

        // Grupo A
        registrarPersonaVisual(ctrl, idEncuesta, enc, pregs, "A1", 20.0, "Madrid", new String[]{"Futbol", "Tenis"});
        registrarPersonaVisual(ctrl, idEncuesta, enc, pregs, "A2", 22.0, "Madrid", new String[]{"Futbol"});
        registrarPersonaVisual(ctrl, idEncuesta, enc, pregs, "A3", 19.0, "Madrid", new String[]{"Tenis", "Futbol"});

        // Grupo B
        registrarPersonaVisual(ctrl, idEncuesta, enc, pregs, "B1", 40.0, "Barcelona", new String[]{"Pintura", "Leer"});
        registrarPersonaVisual(ctrl, idEncuesta, enc, pregs, "B2", 42.0, "Barcelona", new String[]{"Leer"});
        registrarPersonaVisual(ctrl, idEncuesta, enc, pregs, "B3", 45.0, "Barcelona", new String[]{"Pintura"});
        System.out.println("  -------------------------------------------------------------");

        System.out.println("Paso 3: Ejecución del algoritmo K-means");
        System.out.println("Parámetros:");
        System.out.println("  - K (Clusters): 2");
        System.out.println("  - Inicialización: K-Means++");
        System.out.println("  - Distancia: Heterogénea (Euclídea + Hamming/Jaccard)");
        
        ctrl.analizarEncuesta(idEncuesta, 2, "K-MEANS", "K-MEANSPP", new HashSet<>());
        System.out.println(" - Algoritmo finalizado sin errores.");

        System.out.println("Paso 4: Verificación de resultados");

        ArrayList<Perfil> perfiles = ctrl.obtenerPerfiles(idEncuesta);
        List<Set<String>> clustersObtenidos = new ArrayList<>();
        for (Perfil p : perfiles) {clustersObtenidos.add(new HashSet<>(ctrl.obtenerListaIndividuos(p)));}

        // Definimos expectativas
        Set<String> esperadoA = new HashSet<>(Arrays.asList("A1", "A2", "A3"));
        Set<String> esperadoB = new HashSet<>(Arrays.asList("B1", "B2", "B3"));

        System.out.println("Comparación de los clusters:");
        
        boolean encontradoA = false;
        boolean encontradoB = false;

        int i = 1;
        for (Set<String> actual : clustersObtenidos) 
        {
            System.out.println("\n  CLUSTER " + i + " OBTENIDO: " + actual);
            if (actual.equals(esperadoA)) 
            {
                System.out.println("CORRECTO: Coincide con el GRUPO A (Jóvenes/Madrid).");
                encontradoA = true;
            } 
            else if (actual.equals(esperadoB)) 
            {
                System.out.println("CORRECTO: Coincide con el GRUPO B (Adultos/Barcelona).");
                encontradoB = true;
            } 
            else System.out.println("CORRECTO: No coincide con ningún grupo esperado.");
            i++;
        }

         System.out.println("Paso 5: Resumen de las assertions de JUnit");

        System.out.print("TEST 1: Número de clusters es 2: ");
        if (perfiles.size() == 2) System.out.println("OK"); else System.out.println("FALLO");
        assertEquals(2, perfiles.size());

        System.out.print("TEST 2: Grupo A encontrado completo: ");
        if (encontradoA) System.out.println("OK"); else System.out.println("FALLO");
        assertTrue("Falta el grupo de jóvenes", encontradoA);

        System.out.print("TEST 3: Grupo B encontrado completo: ");
        if (encontradoB) System.out.println("OK"); else System.out.println("FALLO");
        assertTrue("Falta el grupo de adultos", encontradoB);

        double calidad = ctrl.obtenerCalidadClustering(idEncuesta);
        System.out.printf("TEST 4: Calidad del Clustering (%.4f) > 0.5: ", calidad);
        if (calidad > 0.5) System.out.println("OK"); else System.out.println("FALLO (Calidad baja)");
        assertTrue(calidad > 0.5);

        System.out.println("Resultado: ÉXITO");
    }

    private void registrarPersonaVisual(ctrlDominio ctrl, Integer idE, Encuesta enc, ArrayList<Pregunta> pregs, String idPersona, Double edad, String ciudad, String[] hobbiesArr) throws Exception 
    {
        ArrayList<Respuesta> r = new ArrayList<>();
        Persona p = new Persona(idPersona);
        r.add(new Numerica(p, enc, pregs.get(0), edad));
        r.add(new Textual(p, enc, pregs.get(1), ciudad));
        Set<String> setHobbies = new HashSet<>(Arrays.asList(hobbiesArr));
        r.add(new Textual(p, enc, pregs.get(2), setHobbies));
        ctrl.responderEncuesta(idE, idPersona, r);
        System.out.printf("  %-10s %-10.1f %-15s %-20s\n", idPersona, edad, ciudad, Arrays.toString(hobbiesArr));
    }
}