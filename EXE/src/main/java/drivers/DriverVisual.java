package drivers;

import dominio.*;
import dominio.ctrlDominio.DatosPregunta;
import java.util.*;

public class DriverVisual 
{

    private static ctrlDominio ctrl = new ctrlDominio();
    private static Scanner scanner = new Scanner(System.in);
    private static Integer idEncuestaActual = null;
    public static void main(String[] args) 
    {
        boolean salir = false;
        while (!salir) 
            {
            System.out.println("\nMenú");
            System.out.println("  1- Crear Encuesta de Prueba (Genera una encuesta )");
            System.out.println("  2- Responder Encuesta Manualmente");
            System.out.println("  3- Ejecutar Algoritmo (K-Means)");
            System.out.println("  4- Visualizar Informe de Resultados");
            System.out.println("  5- Salir");
            System.out.println("....");
            System.out.print("Seleccione una opción: ");
            try 
            {
                String input = scanner.nextLine();
                if (input.isEmpty()) continue;
                int opcion = Integer.parseInt(input);
                switch (opcion) 
                {
                    case 1: crearEncuestaPrueba(); break;
                    case 2: responderEncuesta(); break;
                    case 3: ejecutarClustering(); break;
                    case 4: verResultados(); break;
                    case 5: salir = true; break;
                    default: System.out.println("Error: Opción no inválida");
                }
            } 
            catch (Exception e) {System.out.println("Error " + e.getMessage());}
        }
    }

    private static void crearEncuestaPrueba() throws Exception 
    {
        ArrayList<DatosPregunta> preguntas = new ArrayList<>();
        preguntas.add(new DatosPregunta("Edad", 1, 1, null));
        preguntas.add(new DatosPregunta("Nivel de Estudios", 3, 3, Arrays.asList("Primaria", "Secundaria", "Universidad")));
        preguntas.add(new DatosPregunta("Temas de interés", 2, 4, Arrays.asList("Ciencia", "Arte", "Deporte", "Política")));

        idEncuestaActual = ctrl.crearEncuesta(preguntas);
        System.out.println("Encuesta creada con ID: " + idEncuestaActual);
        System.out.println("Se han configurado 3 preguntas (Numérica, Cualitativa Ordenada (Primaria < Secundaria< Universidad), Múltiple)");
    }

    private static void responderEncuesta() throws Exception 
    {
        if (idEncuestaActual == null) 
        {
            System.out.println("Primero debes crear una encuesta");
            return;
        }

        System.out.print("\nIntroduzca tu identificador como persona (por ejemplo: nombre, DNI etc...): ");
        String idPersona = scanner.nextLine();
        if (idPersona.isEmpty()) return;
        Encuesta enc = ctrl.getEncuesta(idEncuestaActual);
        ArrayList<Respuesta> respuestas = new ArrayList<>();
        ArrayList<Pregunta> pregs = enc.getPreguntes();
        System.out.println("\nFormulario de Respuesta");
        System.out.println("1- " + pregs.get(0).getEnunciado());
        System.out.print("Respuesta (Número): ");
        double edad = Double.parseDouble(scanner.nextLine());
        respuestas.add(new Numerica(new Persona(idPersona), enc, pregs.get(0), edad));
        System.out.println("2- " + pregs.get(1).getEnunciado());
        System.out.println("   Opciones: " + ((Unica)pregs.get(1)).getOpciones());
        System.out.print("Respuesta (Texto exacto): ");
        String est = scanner.nextLine();
        respuestas.add(new Textual(new Persona(idPersona), enc, pregs.get(1), est));
        System.out.println("3- " + pregs.get(2).getEnunciado());
        System.out.println("   Opciones: " + ((Multiple)pregs.get(2)).getOpciones());
        System.out.print("Respuesta (Separada por comas, por ejemplo: Ciencia,Arte): ");
        String linea = scanner.nextLine();
        Set<String> setInt = new HashSet<>(Arrays.asList(linea.split(",")));
        respuestas.add(new Textual(new Persona(idPersona), enc, pregs.get(2), setInt));
        ctrl.responderEncuesta(idEncuestaActual, idPersona, respuestas);
        System.out.println("Respuesta guardada en el sistema");
    }

    private static void ejecutarClustering() throws Exception 
    {
        if (idEncuestaActual == null) 
        {
            System.out.println("Error: No hay datos cargados");
            return;
        }
        Encuesta enc = ctrl.getEncuesta(idEncuestaActual);
        System.out.println("\nPreguntas disponibles en la encuesta:");
        for(Pregunta p : enc.getPreguntes()) System.out.println(" - " + p.getEnunciado());
        System.out.println("\nDeseas ignorar alguna pregunta para el clustering?");
        System.out.print("Escriba los enunciados exactos separados por comas (o pulse Enter para ninguna): ");
        String inputIgnorar = scanner.nextLine();
        
        HashSet<String> preguntasIgnorar = new HashSet<>();
        if (!inputIgnorar.trim().isEmpty()) 
        {
            String[] partes = inputIgnorar.split(",");
            for(String p : partes) preguntasIgnorar.add(p.trim());
            System.out.println("Se ignorarán las siguientes preguntas: " + preguntasIgnorar);
        }
        System.out.print("Introduce el número de clusters deseado (K): ");
        int k = Integer.parseInt(scanner.nextLine());
        System.out.println("Ejecutando K-Means..........");
        ctrl.analizarEncuesta(idEncuestaActual, k, "K-MEANS", "K-MEANSPP", preguntasIgnorar);
        System.out.println("Clustering finalizado, pulse la opción número 4 para ver los resultados");
    }

    private static void verResultados() throws Exception 
    {
        if (idEncuestaActual == null || !ctrl.tieneResultadosClustering(idEncuestaActual)) 
        {
            System.out.println("Error: No hay resultados disponibles. Ejecute el algoritmo primero.");
            return;
        }

        ArrayList<Perfil> perfiles = ctrl.obtenerPerfiles(idEncuestaActual);
        double calidad = ctrl.obtenerCalidadClustering(idEncuestaActual);
        System.out.println("\n................");
        System.out.println("    RESULTADOS");
        System.out.println("................\")");
        int i = 1;
        for (Perfil p : perfiles) 
        {
            System.out.println("\nCLUSTER " + i);
            System.out.println("  .....");
            System.out.println("  Representante del cluster   : " + ctrl.obtenerRepresentante(p));
            System.out.println("  Tamaño del grupo    : " + ctrl.obtenerListaIndividuos(p).size() + " personas");
            System.out.println("  Miembros           : " + ctrl.obtenerListaIndividuos(p));
            i++;
        }

        System.out.println("\n...................");
        System.out.printf("CALIDAD DEL CLUSTERING: %.4f\n", calidad);
        System.out.println(".....................");
    }
}