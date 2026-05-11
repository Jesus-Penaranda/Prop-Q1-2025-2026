package drivers;

import dominio.*;
import java.util.*;

/**
 * Driver Avanzado: Comparativa de Algoritmos
 * Genera una encuesta compleja (Numérica + Única + Múltiple).
 * Calcula automáticamente la K óptima (Método Elbow).
 * Ejecuta K-Means vs K-Medoides y compara su Calidad (Silhouette).
 */
public class DriverComparativa 
{
    public static void main(String[] args) 
    {
        try 
        {
            System.out.println("============================================================");
            System.out.println("  DRIVER AVANZADO: BÚSQUEDA DE K ÓPTIMA Y COMPARATIVA");
            System.out.println("============================================================");

            System.out.println("\n[PASO 1] Inicializando Entorno y Login...");
            ctrlDominio ctrl = new ctrlDominio();
            ctrl.login("AdminComparativa", "admin123");

            System.out.println("\n[PASO 2] Creando Encuesta 'Perfil Tecnológico'...");
            ArrayList<ctrlDominio.DatosPregunta> datos = new ArrayList<>();
            
            datos.add(new ctrlDominio.DatosPregunta("Años Experiencia", 1, 1, null));
            List<String> roles = Arrays.asList("Backend", "Frontend", "DataScientist", "Manager");
            datos.add(new ctrlDominio.DatosPregunta("Rol Actual", 3, 3, roles));
            List<String> stack = Arrays.asList("Java", "Python", "React", "SQL", "Excel", "PowerBI");
            datos.add(new ctrlDominio.DatosPregunta("Tecnologías", 2, 4, stack));
            Integer id = ctrl.crearEncuesta(datos);
            System.out.println(" -> Encuesta creada con ID: " + id);

            System.out.println("\n[PASO 3] Inyectando datos simulados (3 Perfiles Claros)...");

            registrar(ctrl, id, "Dev_Sr_1", 8.0, "Backend", new String[]{"Java", "SQL"});
            registrar(ctrl, id, "Dev_Sr_2", 10.0, "Backend", new String[]{"Java", "SQL"});
            registrar(ctrl, id, "Dev_Sr_3", 7.5, "Backend", new String[]{"Java"});
            registrar(ctrl, id, "Data_Jr_1", 1.0, "DataScientist", new String[]{"Python", "Excel"});
            registrar(ctrl, id, "Data_Jr_2", 2.0, "DataScientist", new String[]{"Python", "PowerBI"});
            registrar(ctrl, id, "Data_Jr_3", 1.5, "DataScientist", new String[]{"Python", "SQL"});
            registrar(ctrl, id, "Jefe_1", 15.0, "Manager", new String[]{"Excel", "PowerBI"});
            registrar(ctrl, id, "Jefe_2", 20.0, "Manager", new String[]{"Excel"});

            System.out.println(" -> 8 Usuarios registrados en 3 grupos teóricos.");

            System.out.println("\n[PASO 4] Calculando K Óptima...");
            Map<Integer, Double> resultadosElbow = ctrl.calcularElbow(id, 5, "K-MEANS", "K-MEANSPP", new HashSet<>());
            int kOptima = resultadosElbow.get(-1).intValue();
            System.out.println(" -> El sistema sugiere que la K Óptima es: " + kOptima);
            if (kOptima < 2) kOptima = 2;
            System.out.println("\n[PASO 5] Batalla de Algoritmos con k = " + kOptima);
            System.out.println("------------------------------------------------------------");
            
            System.out.print(" Ejecutando K-MEANS... ");
            ctrl.analizarEncuesta(id, kOptima, "K-MEANS", "K-MEANSPP", new HashSet<>());
            double calidadMeans = ctrl.obtenerCalidadClustering(id);
            System.out.printf("Hecho. Calidad (Silhouette): %.4f\n", calidadMeans);
            imprimirResumenClusters(ctrl, id);
            System.out.println("------------------------------------------------------------");
            System.out.print(" Ejecutando K-MEDOIDES... ");
            ctrl.analizarEncuesta(id, kOptima, "K-MEDOIDES", "TRIVIAL", new HashSet<>());
            double calidadMedoides = ctrl.obtenerCalidadClustering(id);
            System.out.printf("Hecho. Calidad (Silhouette): %.4f\n", calidadMedoides);
            imprimirResumenClusters(ctrl, id);

            System.out.println("============================================================");
            System.out.println("  CONCLUSIÓN FINAL");
            System.out.println("============================================================");
            System.out.printf("  K-Means:    %.4f\n", calidadMeans);
            System.out.printf("  K-Medoides: %.4f\n", calidadMedoides);
            System.out.println("------------------------------------------------------------");
            
            if (calidadMedoides > calidadMeans) System.out.println("  GANADOR: K-MEDOIDES (Mejor adaptación a variables mixtas/outliers)");
            else if (calidadMeans > calidadMedoides) System.out.println("  GANADOR: K-MEANS (Mejor separación compacta)");
            else System.out.println("  EMPATE TÉCNICO");
            System.out.println("============================================================");

        } 
        catch (Exception e) 
        {
            System.out.println("\n!!! ERROR CRÍTICO EN EL DRIVER !!!");
            System.out.println("Causa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void registrar(ctrlDominio ctrl, Integer id, String per, Double exp, String rol, String[] tech) throws Exception 
    {
        Encuesta enc = ctrl.getEncuesta(id);
        ArrayList<Pregunta> pg = enc.getPreguntes();
        ArrayList<Respuesta> r = new ArrayList<>();
        r.add(new Numerica(new Persona(per), enc, pg.get(0), exp));
        r.add(new Textual(new Persona(per), enc, pg.get(1), rol));
        Set<String> setTech = new HashSet<>(Arrays.asList(tech));
        r.add(new Textual(new Persona(per), enc, pg.get(2), setTech));
        ctrl.responderEncuesta(id, per, r);
    }
    
    private static void imprimirResumenClusters(ctrlDominio ctrl, Integer id) throws Exception 
    {
        ArrayList<Perfil> perfiles = ctrl.obtenerPerfiles(id);
        int i = 1;
        for (Perfil p : perfiles) 
        {
            System.out.println("   Cluster " + i + ": " + ctrl.obtenerListaIndividuos(p) + " [Rep: " + p.getRepresentante() + "]");
            i++;
        }
    }
}