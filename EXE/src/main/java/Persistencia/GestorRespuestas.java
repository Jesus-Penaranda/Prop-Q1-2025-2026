package Persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import dominio.*;

public class GestorRespuestas 
{
    
    private final String PATH_DIRECTORIO = "Datos/Respuestas/";

    /**
     * Constructor del Gestor
     * Se asegura de que el directorio "Datos/Respuestas" exista
     */
    public GestorRespuestas() 
    {
        File directorioDatos = new File(PATH_DIRECTORIO);
        if (!directorioDatos.exists()) directorioDatos.mkdirs();
    }

    /**
     * Genera la ruta del archivo CSV para una encuesta específica
     */
    private String getPathFichero(Integer idEncuesta) { return PATH_DIRECTORIO + "respuestas_encuesta_" + idEncuesta + ".csv";}

    /**
     * Guarda todas las respuestas de una encuesta en su archivo CSV correspondiente
     * @param cjt El conjunto de respuestas a guardar
     * @param encuesta La encuesta a la que pertenecen las respuestas
     */
    public void guardar(Cjt_Respuestas cjt, Encuesta encuesta) throws IOException 
    {
        String path = getPathFichero(encuesta.getId());
        try (BufferedWriter escritura = new BufferedWriter(new FileWriter(path, false))) 
        {
            // Escribir cabecera
            escritura.write("Persona_ID");
            ArrayList<Pregunta> preguntas = encuesta.getPreguntes();
            for (Pregunta p : preguntas) escritura.write("," + p.getEnunciado());
            escritura.newLine();
            // Escribir una fila por cada persona
            ArrayList<String> personas_id = cjt.getPersonas();
            for (String id : personas_id) 
            {
                escritura.write(id);
                ArrayList<Respuesta> respuestas = cjt.getRespuestasPersona(id);
                for (Respuesta r : respuestas) 
                {
                    escritura.write(",");
                    escritura.write(convertirRespuestaCSV(r.getValor()));
                }
                escritura.newLine();
            }
        }
    }

    /**
     * Carga todas las respuestas de una encuesta desde su archivo CSV
     * @param encuesta La encuesta cuyas respuestas se van a cargar
     * @return Un conjunto de respuestas cargadas
     */
    public Cjt_Respuestas cargar(Encuesta encuesta) throws IOException 
    {
        String path = getPathFichero(encuesta.getId());
        File fichero = new File(path);
        Cjt_Respuestas cjt = new Cjt_Respuestas();
        if (!fichero.exists()) return cjt; // Devuelve conjunto vacío si no existe el archivo
        try (BufferedReader reader = new BufferedReader(new FileReader(fichero))) 
        {
            String linea;
            reader.readLine(); // Saltar cabecera
            ArrayList<Pregunta> preguntas = encuesta.getPreguntes();
            while ((linea = reader.readLine()) != null) 
            {
                if (linea.trim().isEmpty()) continue; // Miramos si esta vacio
                String[] campos = linea.split(",", -1); // -1 para mantener las respuestas vacias
                String idPersona = campos[0];
                Persona p = new Persona(idPersona);
                ArrayList<Respuesta> respuestasPersona = new ArrayList<Respuesta>();
                for (int i = 1; i < campos.length; i++) 
                {
                    // Protección por si el CSV tiene más columnas que preguntas en la encuesta
                    if (i - 1 >= preguntas.size()) break;
                    
                    Pregunta preg = preguntas.get(i - 1);
                    String valorString = campos[i];
                    Respuesta r = convertirCSVRespuesta(valorString, preg, p, encuesta);
                    respuestasPersona.add(r);
                }
                cjt.add_all_resp(p.getId(), respuestasPersona);
            }
        }
        return cjt;
    }

    /**
     * Convierte un valor de respuesta a formato CSV
     * - null o vacío - ""
     * - Multiple - "1:2:3"
     * - Otros - toString()
     */
    private String convertirRespuestaCSV(Object valor) 
    {
        if (valor == null) return ""; // No contestada
        if (valor instanceof Set) 
        {
            Set<?> conjunto = (Set<?>) valor;
            if (conjunto.isEmpty()) return ""; // No contestada
            StringBuilder sb = new StringBuilder(); // Para concatenar string en 1 solo objeto y no crear nuevos string innecesarios
            boolean primero = true;
            for (Object elem : conjunto) 
            {
                if (!primero) sb.append(":");
                sb.append(elem.toString());
                primero = false;
            }
            return sb.toString();
        }
        return valor.toString();
    }

    /**
     * Convierte una cadena string en formato CSV a un objeto Respuesta según el tipo de pregunta
     * CORREGIDO: Maneja correctamente Tipo 4 para preguntas Únicas y Múltiples.
     * @param valorCSV El valor en formato string del CSV
     * @param pregunta La pregunta
     * @param persona La persona que ha respondido
     * @param encuesta La encuesta
     * @return La Respuesta correspondiente
     */
    private Respuesta convertirCSVRespuesta(String valorCSV, Pregunta pregunta, Persona persona, Encuesta encuesta) 
    {
        Integer tipoR = pregunta.getTipo();
        
        // Caso respuesta vacía
        if (valorCSV == null || valorCSV.trim().isEmpty()) 
        {
            if (tipoR == 1 || tipoR == 2) return new Numerica(persona, encuesta, pregunta);
            else return new Textual(persona, encuesta, pregunta); 
        }

        // Tipo 1: Numérica Simple
        if (tipoR == 1) 
        { 
            try 
            {
                Double valor = Double.parseDouble(valorCSV);
                return new Numerica(persona, encuesta, pregunta, valor);
            } 
            catch (NumberFormatException e) 
            {
                System.err.println("Error convirtiendo valor numérico: " + valorCSV);
                return new Numerica(persona, encuesta, pregunta);
            }
        } 
        // Tipo 2: Numérica Múltiple
        else if (tipoR == 2) 
        { 
            Set<Double> valoresNum = new HashSet<>();
            String[] partesNum = valorCSV.split(":");
            for (String parte : partesNum) {
                try 
                {
                    valoresNum.add(Double.parseDouble(parte.trim()));
                } 
                catch (NumberFormatException e) 
                {
                    System.err.println("Error convirtiendo valor numérico multiple: " + parte);
                }
            }
            return new Numerica(persona, encuesta, pregunta, valoresNum);
        } 
        // Tipo 3: Textual Ordenada (Única)
        else if (tipoR == 3) 
        {
            return new Textual(persona, encuesta, pregunta, valorCSV);
        }
        // Tipo 4: Textual No Ordenada (Puede ser Única o Múltiple)
        else if (tipoR == 4) 
        { 
            if (pregunta instanceof Multiple) {
                Set<String> valoresText = new HashSet<>();
                String[] partesText = valorCSV.split(":");
                for (String parte : partesText) {
                    if (!parte.trim().isEmpty()) valoresText.add(parte.trim());
                }
                return new Textual(persona, encuesta, pregunta, valoresText);
            } else {
                // Si es Unica (o Libre), guardamos el valor tal cual
                return new Textual(persona, encuesta, pregunta, valorCSV.trim());
            }
        } 
        else 
        {
            System.err.println("Tipo de respuesta desconocido: " + tipoR);
            return null;
        }
    }
}