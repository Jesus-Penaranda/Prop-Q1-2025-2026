package Tests.Integracion;

import dominio.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Test de integración que usa ctrlDominio para simular un uso real:
 */
public class IntegracionTest_Edades 
{
    @Test
    public void Agrupacion_Kmeans_Edad() throws Exception 
    {

        ctrlDominio ctrl = new ctrlDominio();
        ctrl.login("TesterVisual", "1234");
        ArrayList<ctrlDominio.DatosPregunta> dp = new ArrayList<>();
        dp.add(new ctrlDominio.DatosPregunta("Edad", 1, 1, new ArrayList<>())); //Libre, tipo respuesta 1 (numerica)
        Integer idEncuesta = ctrl.crearEncuesta(dp);
        Encuesta enc = ctrl.getEncuesta(idEncuesta);
        System.out.println();
        ArrayList<Pregunta> preg = enc.getPreguntes();
        Pregunta edad = preg.get(0);

        String[] ids = new String[]{"A","B","C","D","E","F"};
        double[] valores = new double[]{18,27,21,78,81,67};

        System.out.println("ENCUESTA");
        System.out.println("ID_Pregunta,Enunciado,Tipo_Pregunta,Tipo_Respuesta,Opciones");
        for (int i = 0; i < preg.size(); i++) 
        {
            Pregunta p = preg.get(i);
            System.out.println(p.getId() + "," + p.getEnunciado() + "," + p.getTipo() + "," + "" + ",");
        }
        System.out.println("=======================");

        System.out.println("RESPUESTAS");
        System.out.print("Persona_ID");
        for (Pregunta p : preg) System.out.print("," + p.getEnunciado());
        System.out.println();
        for (int i = 0; i < ids.length; i++) 
        {
            System.out.print(ids[i]);
            for (int q = 0; q < preg.size(); q++) 
            {
                double val = (q == 0) ? valores[i] : Double.NaN;
                System.out.print("," + val);
            }
            System.out.println();
        }
        System.out.println("========================");
        System.out.println("EXPECTATIVA DEL TEST: se esperan dos clusters - [A,B,C] (jóvenes) y [D,E,F] (mayores)");
        System.out.println();

        for (int i = 0; i < ids.length; i++) 
        {
            Persona persona = new Persona(ids[i]);
            Numerica r = new Numerica(persona, enc, edad, valores[i]);
            ArrayList<Respuesta> respuestasPersona = new ArrayList<>();
            respuestasPersona.add(r);
            ctrl.responderEncuesta(idEncuesta, ids[i], respuestasPersona);
        }

        ctrl.analizarEncuesta(idEncuesta, 2, "K-MEANS", "K-MEANSPP", new HashSet<>());

        ArrayList<Perfil> perfiles = ctrl.obtenerPerfiles(idEncuesta);
        List<Set<String>> clusters = new ArrayList<>();
        for (Perfil p : perfiles) 
        {
            ArrayList<String> lista = ctrl.obtenerListaIndividuos(p);
            clusters.add(lista == null ? Collections.emptySet() : new HashSet<>(lista));
        }

        Set<String> esperadosJovenes = new HashSet<>(Arrays.asList("A","B","C"));
        Set<String> esperadosMayores = new HashSet<>(Arrays.asList("D","E","F"));
        List<Set<String>> clusters_esperados = Arrays.asList(esperadosJovenes, esperadosMayores);

        System.out.println("=== Clusters Esperados ===");
        for (int i = 0; i < clusters_esperados.size(); i++) System.out.println("Expected " + (i+1) + ": " + clusters_esperados.get(i));
        System.out.println("=== Clusters Calculados ===");
        for (int i = 0; i < clusters.size(); i++) System.out.println("Actual " + (i+1) + ": " + clusters.get(i));

         for (Set<String> exp : clusters_esperados) 
        {
            boolean encontrado = clusters.stream().anyMatch(c -> c.equals(exp));
            assertTrue("Se esperaba encontrar el cluster " + exp + " en los resultados", encontrado);
        }
        System.out.println("Verificación: todos los clusters esperados encontrados — TEST PASADO");
    }
}