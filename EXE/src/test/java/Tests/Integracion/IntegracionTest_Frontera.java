package Tests.Integracion;

import dominio.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Test de Integración: Análisis de Frontera en Clustering
 * Probamos valores extremos para cambiar un valor de cluster
 */
public class IntegracionTest_Frontera 
{

    @Test
    public void testComportamientoFrontera() throws Exception 
    {
        System.out.println("============================================================");
        System.out.println(" INICIO TEST DE FRONTERA VISUAL (Valores como IDs)");
        System.out.println("============================================================");

        ctrlDominio ctrl = new ctrlDominio();
        ctrl.login("TesterFrontera", "1234");
        ArrayList<ctrlDominio.DatosPregunta> datos = new ArrayList<>();
        datos.add(new ctrlDominio.DatosPregunta("Valor", 1, 1, new ArrayList<>())); 
        Integer idEncuesta = ctrl.crearEncuesta(datos);
        Encuesta enc = ctrl.getEncuesta(idEncuesta);
        Pregunta pregunta = enc.getPreguntes().get(0);
        double[] Extremos = {0.0, 0.1, 0.9, 1.0};
        for(double val : Extremos) 
        {
            String idVisual = String.valueOf(val);
            registrarRespuesta(ctrl, idEncuesta, enc, pregunta, idVisual, val);
        }
        double[] casosPrueba = {0.5, 0.4, 0.6}; 
        
        System.out.println("Extremos fijos: [0.0, 0.1] y [0.9, 1.0]");

        for (double valorVariable : casosPrueba) 
        {
            String idVariable = String.valueOf(valorVariable);
            
            System.out.println("\n------------------------------------------------------------");
            System.out.printf(" ESCENARIO: Añadimos valor %.1f \n", valorVariable);
            System.out.println("------------------------------------------------------------");

            registrarRespuesta(ctrl, idEncuesta, enc, pregunta, idVariable, valorVariable);
            System.out.print("--> Calculando K-Means (k = 2)... ");
            ctrl.analizarEncuesta(idEncuesta, 2, "K-MEANS", "K-MEANSPP", new HashSet<>());
            System.out.println("Hecho.");

            double calidad = ctrl.obtenerCalidadClustering(idEncuesta);
            System.out.printf("-> Calidad del Clustering: %.4f \n", + calidad);

            ArrayList<Perfil> perfiles = ctrl.obtenerPerfiles(idEncuesta);
            List<Set<String>> clusters = new ArrayList<>();
            
            System.out.println("[CLUSTERS RESULTANTES]");
            int i = 1;
            Set<String> clusterBajo = null;
            Set<String> clusterAlto = null;

            for (Perfil p : perfiles) 
            {
                Set<String> miembros = new HashSet<>(ctrl.obtenerListaIndividuos(p));
                clusters.add(miembros);
                System.out.println("Cluster " + i + ": " + miembros.toString());
                
                if (miembros.contains("0.0")) clusterBajo = miembros;
                if (miembros.contains("1.0")) clusterAlto = miembros;
                i++;
            }
            analizarLogica(valorVariable, idVariable, clusterBajo, clusterAlto);
        }
        
        System.out.println("\n============================================================");
        System.out.println(" TEST FINALIZADO");
        System.out.println("============================================================");
    }

    private void registrarRespuesta(ctrlDominio ctrl, Integer idE, Encuesta enc, Pregunta p, String idPersona, Double val) throws Exception 
    {
        ArrayList<Respuesta> r = new ArrayList<>();
        r.add(new Numerica(new Persona(idPersona), enc, p, val));
        ctrl.responderEncuesta(idE, idPersona, r);
    }

    private void analizarLogica(double val, String id, Set<String> bajo, Set<String> alto) 
    {
        if (val == 0.4) 
        {
            boolean exito = bajo != null && bajo.contains(id);
            System.out.println("-> VERIFICACIÓN (0.4): ");
            if (exito) System.out.println("CORRECTO. " + id + " está en el grupo bajo " + bajo);
            else System.out.println("FALLO. " + id + " debería estar con 0.0 y 0.1");
            assertTrue(exito);
        }
        else if (val == 0.6) 
        {
            boolean exito = alto != null && alto.contains(id);
            System.out.println("--> VERIFICACIÓN (0.6): ");
            if (exito) System.out.println("CORRECTO. " + id + " está en el grupo alto " + alto);
            else System.out.println("FALLO. " + id + " debería estar con 0.9 y 1.0");
            assertTrue(exito);
        }
        else if (val == 0.5) 
        {
            System.out.println("-> OBSERVACIÓN (0.5): ");
            if (bajo != null && bajo.contains(id)) System.out.println("Se agrupó con los BAJOS." );
            else System.out.println("Se agrupó con los ALTOS.");
        }
    }
}