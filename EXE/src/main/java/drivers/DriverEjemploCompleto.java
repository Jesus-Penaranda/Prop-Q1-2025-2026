package drivers;

import dominio.*;
import java.util.*;

/**
 * Driver autoejecutable
 * Ejecuta un caso de uso completo automáticamente sin necesidad de intervención del usuario
 */
public class DriverEjemploCompleto 
{

    public static void main(String[] args) 
    {
        try 
        {
            System.out.println("\n");
            System.out.println("  DRIVER AUTOMÁTICO: DEMOSTRACIÓN DE INTEGRACIÓN");
            System.out.println("\n");

            System.out.println("\nPaso 1: Creando Encuesta 'Estilo de Vida'...");
            ctrlDominio ctrl = new ctrlDominio();
            ctrl.login("TesterVisual", "1234");
            ArrayList<ctrlDominio.DatosPregunta> datos = new ArrayList<>();
            datos.add(new ctrlDominio.DatosPregunta("Edad", 1, 1, null));
            datos.add(new ctrlDominio.DatosPregunta("Ciudad", 3, 3, Arrays.asList("Madrid", "BCN", "Valencia")));
            datos.add(new ctrlDominio.DatosPregunta("Hobbies", 2, 4, Arrays.asList("Futbol", "Cine", "Leer")));
            
            Integer id = ctrl.crearEncuesta(datos);
            System.out.println("Encuesta creada con ID: " + id);
            System.out.println("\nPaso 2: Generando 4 usuarios simulados...");
            registrar(ctrl, id, "UserA", 20.0, "Madrid", new String[]{"Futbol"});
            registrar(ctrl, id, "UserB", 21.0, "Madrid", new String[]{"Futbol"});
            registrar(ctrl, id, "UserC", 60.0, "BCN", new String[]{"Leer"});
            registrar(ctrl, id, "UserD", 62.0, "BCN", new String[]{"Leer"});
            System.out.println(" - Usuarios registrados: A y B (Jóvenes), C y D (Mayores).");

            System.out.println("\nPaso 3: Probando Persistencia (Guardar - Borrar la memoria - Cargar)");
            ctrl.guardarEncuesta(id);
            ctrl.guardarRespuestas(id);
            
            ctrl = new ctrlDominio(); 
            ctrl.importarEncuesta(id);
            ctrl.importarRespuestas(id);
            System.out.println(" - Datos recuperados del disco correctamente.");

            System.out.println("\nPaso 4: Ejecutando K-Means (K=2) Ignorando la pregunta con enunciado'Ciudad'...");
            HashSet<String> ignorar = new HashSet<>();
            ignorar.add("Ciudad");
            
            ctrl.analizarEncuesta(id, 2, "K-MEANS", "K-MEANSPP", ignorar);
            
            System.out.println("\nPaso 5: Resultados Obtenidos:");
            ArrayList<Perfil> perfiles = ctrl.obtenerPerfiles(id);
            
            int i = 1;
            for(Perfil p : perfiles) 
            {
                System.out.println("Cluster " + i + ": " + ctrl.obtenerListaIndividuos(p));
                System.out.println("   Representante: " + ctrl.obtenerRepresentante(p));
                i++;
            }
            
            double calidad = ctrl.obtenerCalidadClustering(id);
            System.out.printf("\nCalidad del Clustering %.4f\n", calidad);
            
            if (calidad > 0.5) System.out.println("Resultado: ÉXITO");
            else System.out.println("Resultado: ATENCIÓN (Calidad mala)");

        } 
        catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void registrar(ctrlDominio ctrl, Integer id, String per, Double edad, String ciu, String[] hob) throws Exception 
    {
        Encuesta enc = ctrl.getEncuesta(id);
        ArrayList<Pregunta> pg = enc.getPreguntes();
        ArrayList<Respuesta> r = new ArrayList<>();
        r.add(new Numerica(new Persona(per), enc, pg.get(0), edad));
        r.add(new Textual(new Persona(per), enc, pg.get(1), ciu));
        r.add(new Textual(new Persona(per), enc, pg.get(2), new HashSet<>(Arrays.asList(hob))));
        ctrl.responderEncuesta(id, per, r);
    }
}