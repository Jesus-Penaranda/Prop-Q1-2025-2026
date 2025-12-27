package Persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dominio.*;

public class GestorEncuestas 
{
    
    private final String PATH_DIRECTORIO = "Datos/Encuestas/";
    
    /**
     * Constructor del Gestor
     * Se asegura de que el directorio 'Datos/Encuestas' exista
     */
    public GestorEncuestas() 
    {
        File directorioDatos = new File(PATH_DIRECTORIO);
        if (!directorioDatos.exists()) directorioDatos.mkdirs();
    }

    /**
     * Genera la ruta del archivo CSV para una encuesta específica
     */
    private String getPathFichero(Integer idEncuesta) 
    {
        return PATH_DIRECTORIO + "encuesta_" + idEncuesta + ".csv";
    }

    /**
     * Guarda una encuesta en su archivo CSV correspondiente
     * Formato: ID_Pregunta,Enunciado,Tipo_Pregunta,Tipo_Respuesta,Opciones
     * @param encuesta La encuesta a guardar
     * @throws IOException Si hay un error de escritura
     */
    public void guardar(Encuesta encuesta) throws IOException 
    {
        String path = getPathFichero(encuesta.getId());
        
        try (BufferedWriter escritura = new BufferedWriter(new FileWriter(path, false))) 
        {
            // Escribir  cabecera
            escritura.write("ID_Pregunta,Enunciado,Tipo_Pregunta,Tipo_Respuesta,Opciones");
            escritura.newLine();
            ArrayList<Pregunta> preguntas = encuesta.getPreguntes();
            for (Pregunta p : preguntas) 
            {
                escritura.write(p.getId().toString());
                escritura.write(",");
                escritura.write(escaparCSV(p.getEnunciado()));
                escritura.write(",");
                escritura.write(p.getTipoPregunta().toString());
                escritura.write(",");
                escritura.write(p.getTipo().toString());
                escritura.write(",");
                if (p instanceof Unica) 
                {
                    Unica pUnica = (Unica) p;
                    escritura.write(convertirOpcionesCSV(pUnica.getOpciones()));
                } 
                else if (p instanceof Multiple) 
                {
                    Multiple pMultiple = (Multiple) p;
                    escritura.write(convertirOpcionesCSV(pMultiple.getOpciones()));
                } 
                else escritura.write(""); // Libre (no tiene opciones)
                escritura.newLine();
            }
        }
    }

    /**
     * Carga una encuesta desde su archivo CSV
     * @param idEncuesta El identificador de la encuesta a cargar
     * @return La encuesta cargada o null si no existe
     * @throws IOException Si hay un error de lectura
     */
    public Encuesta cargar(Integer idEncuesta) throws IOException 
    {
        String path = getPathFichero(idEncuesta);
        File fichero = new File(path);
        if (!fichero.exists()) return null; //No existe la encuesta
        Encuesta encuesta = new Encuesta(idEncuesta);
        ArrayList<Pregunta> preguntas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fichero))) 
        {
            String linea;
            reader.readLine(); // Saltar cabecera
            while ((linea = reader.readLine()) != null) 
            {
                if (linea.trim().isEmpty()) continue;
                String[] campos = parsearLineaCSV(linea);
                Integer idPregunta = Integer.parseInt(campos[0]);
                String enunciado = campos[1];
                Integer tipoPregunta = Integer.parseInt(campos[2]);
                Integer tipoRespuesta = Integer.parseInt(campos[3]);
                String opcionesStr = campos.length > 4 ? campos[4] : "";
                Pregunta pregunta = crearPregunta(idPregunta, enunciado, encuesta,tipoPregunta, tipoRespuesta, opcionesStr);
                preguntas.add(pregunta);
            }
        }
        encuesta.setPreguntas(preguntas);
        return encuesta;
    }

    /**
     * Verifica si existe una encuesta guardada
     * @param idEncuesta El ID de la encuesta a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existe(Integer idEncuesta) 
    {
        File fichero = new File(getPathFichero(idEncuesta));
        return fichero.exists();
    }

    /**
     * Elimina el archivo CSV de una encuesta
     * @param idEncuesta El ID de la encuesta a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminar(Integer idEncuesta) 
    {
        File fichero = new File(getPathFichero(idEncuesta));
        return fichero.delete();
    }

    /**
     * Lista todos los IDs de encuestas guardadas
     * @return Lista de IDs de encuestas
     */
    public List<Integer> listarEncuestas() 
    {
        List<Integer> ids = new ArrayList<>();
        File directorio = new File(PATH_DIRECTORIO);
        if (directorio.exists() && directorio.isDirectory()) 
        {
            File[] archivos = directorio.listFiles((dir, name) -> name.startsWith("encuesta_") && name.endsWith(".csv"));
            if (archivos != null) 
            {
                for (File archivo : archivos) 
                {
                    String nombre = archivo.getName();
                    String idStr = nombre.substring(9, nombre.length() - 4);
                    ids.add(Integer.parseInt(idStr));
                }
            }
        }
        return ids;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                      MÉTODOS AUXILIARES PRIVADOS                    ///
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Convierte una lista de opciones a formato CSV (separadas por ":")
     */
    private String convertirOpcionesCSV(List<String> opciones) 
    {
        if (opciones == null || opciones.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean primero = true;
        for (String opcion : opciones) 
        {
            if (!primero) sb.append(":");
            sb.append(escaparCSV(opcion));
            primero = false;
        }
        return sb.toString();
    }

    /**
     * Escapa caracteres especiales en CSV (comas, comillas, saltos de línea)
     */
    private String escaparCSV(String texto) 
    {
        if (texto == null) return "";
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) 
        {
            // Duplicar comillas internas, por si da error
            texto = texto.replace("\"", "\"\"");
            return "\"" + texto + "\"";
        }
        return texto;
    }

    /**
     * Parsea una línea CSV teniendo en cuenta campos entrecomillados
     */
    private String[] parsearLineaCSV(String linea) 
    {
        List<String> campos = new ArrayList<>();
        StringBuilder campoActual = new StringBuilder();
        boolean dentroComillas = false;
        for (int i = 0; i < linea.length(); i++) 
        {
            char c = linea.charAt(i);
            if (c == '\"') 
            {
                if (i + 1 < linea.length() && linea.charAt(i + 1) == '\"') 
                {
                    campoActual.append('\"');
                    i++; 
                } 
                else dentroComillas = !dentroComillas;
                
            } 
            else if (c == ',' && !dentroComillas) 
            {
                campos.add(campoActual.toString());
                campoActual = new StringBuilder();
            } 
            else campoActual.append(c);
        }
        campos.add(campoActual.toString());
        return campos.toArray(new String[0]);
    }

    /**
     * Crea el objeto Pregunta según su tipo
     */
    private Pregunta crearPregunta(Integer id, String enunciado, Encuesta encuesta, Integer tipoPregunta, Integer tipoRespuesta, String opcionesStr) 
    {
        List<String> opciones = null;
        // Si hay opciones, convertirlas de string a lista
        if (opcionesStr != null && !opcionesStr.isEmpty()) opciones = Arrays.asList(opcionesStr.split(":"));
        if (tipoPregunta == 1) return new Libre(id, enunciado, encuesta, tipoPregunta, tipoRespuesta);
        else if (tipoPregunta == 2) return new Multiple(id, enunciado, encuesta, tipoPregunta, tipoRespuesta, opciones);
        else if (tipoPregunta == 3) return new Unica(id, enunciado, encuesta, tipoPregunta, tipoRespuesta, opciones);
        else throw new RuntimeException("Tipo de pregunta desconocido: " + tipoPregunta);
    }
}