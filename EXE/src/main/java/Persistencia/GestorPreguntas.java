package Persistencia;

import dominio.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GestorPreguntas 
{

    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    private static final String DIRECTORIO_BASE = "Datos/Preguntas/";

    
    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTOR                                 ///
    ///////////////////////////////////////////////////////////////////////////
    
    public GestorPreguntas() throws IOException 
    {
        // Crear directorio si n existe
        Path path = Paths.get(DIRECTORIO_BASE);
        if (!Files.exists(path)) Files.createDirectories(path);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    ///                         MÉTODOS PÚBLICOS                            ///
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Guarda una pregunta individual en formato CSV
     * Formato: ID,TipoP,TipoR,Enunciado,IdEncuesta,Clase,Opciones
     * @param pregunta La pregunta a guardar
     */
    public void guardar(Pregunta pregunta) throws IOException 
    {
        String nombreArchivo = DIRECTORIO_BASE + "pregunta_" + pregunta.getId() + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) 
        {
            // cabecera
            writer.write("ID,TipoP,TipoR,Enunciado,IdEncuesta,Clase,Opciones");
            writer.newLine();
            // Escribir datos
            writer.write(convertirPreguntaALinea(pregunta));
            writer.newLine();
        }
    }
    
    /**
     * Carga una pregunta individual desde CSV
     * @param idPregunta ID de la pregunta a cargar
     * @param encuesta Encuesta a la que asociar la pregunta (puede ser null)
     * @return La pregunta cargada
     */
    public Pregunta cargar(Integer idPregunta, Encuesta encuesta) throws IOException 
    {
        String nombreArchivo = DIRECTORIO_BASE + "pregunta_" + idPregunta + ".csv";
        if (!Files.exists(Paths.get(nombreArchivo))) throw new FileNotFoundException("Pregunta no encontrada: " + idPregunta);
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) 
        {
            reader.readLine(); // Saltar cabecera
            String linea = reader.readLine();
            if (linea != null && !linea.trim().isEmpty()) return convLineaPregunta(linea, encuesta);
        }
        return null;
    }
    
    /**
     * Guarda un conjunto de preguntas en un único archivo
     * @param preguntas Lista de preguntas a guardar
     * @param nombreArchivo Nombre del archivo 
     */
    public void guardarConjunto(List<Pregunta> preguntas, String nombreArchivo) throws IOException 
    {
        String ruta = DIRECTORIO_BASE + nombreArchivo + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) 
        {
            // Escribir cabecera
            writer.write("ID,TipoP,TipoR,Enunciado,IdEncuesta,Clase,Opciones");
            writer.newLine();
            // Escribir cada pregunta
            for (Pregunta p : preguntas) 
            {
                writer.write(convertirPreguntaALinea(p));
                writer.newLine();
            }
        }
    }
    
    /**
     * Carga un conjunto de preguntas desde un archivo
     * @param nombreArchivo Nombre del archivo 
     * @param encuesta Encuesta a la que asociar las preguntas (puede ser null)
     * @return lista de preguntas cargadas
     */
    public ArrayList<Pregunta> cargarConjunto(String nombreArchivo, Encuesta encuesta) throws IOException 
    {
        String ruta = DIRECTORIO_BASE + nombreArchivo + ".csv";
        
        if (!Files.exists(Paths.get(ruta))) throw new FileNotFoundException("Archivo no encontrado: " + ruta);
        ArrayList<Pregunta> preguntas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) 
        {
            reader.readLine(); // LA cabecera
            String linea;
            while ((linea = reader.readLine()) != null) 
            {
                if (!linea.trim().isEmpty()) 
                {
                    Pregunta p = convLineaPregunta(linea, encuesta);
                    if (p != null) preguntas.add(p);
                }
            }
        }
        return preguntas;
    }
    
    /**
     * Lista todas las preguntas individuales guardadas
     * @return lista de ids de preguntas
     */
    public List<Integer> listarPreguntas() throws IOException 
    {
        List<Integer> ids = new ArrayList<>();
        File directorio = new File(DIRECTORIO_BASE);
        if (!directorio.exists()) return ids;
        File[] archivos = directorio.listFiles((dir, name) -> name.startsWith("pregunta_") && name.endsWith(".csv"));
        if (archivos != null) 
        {
            for (File archivo : archivos) 
            {
                String nombre = archivo.getName();
                String idStr = nombre.substring(9, nombre.length() - 4); // "pregunta_X.csv"
                try 
                {
                    ids.add(Integer.parseInt(idStr));
                }
                catch (NumberFormatException e) 
                {
                    // Ignorar archivos con formato incorrecto
                }
            }
        }
        return ids;
    }
    
    /**
     * Lista todos los conjuntos de preguntas guardados
     * @return Lista de nombres de archivos
     */
    public ArrayList<String> listarConjuntos() throws IOException 
    {
        ArrayList<String> conjuntos = new ArrayList<>();
        File directorio = new File(DIRECTORIO_BASE);
        if (!directorio.exists()) return conjuntos;
        File[] archivos = directorio.listFiles((dir, name) -> !name.startsWith("pregunta_") && name.endsWith(".csv"));
        if (archivos != null) 
        {
            for (File archivo : archivos) 
            {
                String nombre = archivo.getName();
                conjuntos.add(nombre.substring(0, nombre.length() - 4)); 
            }
        }
        return conjuntos;
    }
    
    /**
     * Elimina una pregunta individual
     * @param idPregunta ID de la pregunta a eliminar
     * @return true si se elimino correctamente, en caso contrario false
     */
    public boolean eliminar(Integer idPregunta) throws IOException 
    {
        String nombreArchivo = DIRECTORIO_BASE + "pregunta_" + idPregunta + ".csv";
        Path path = Paths.get(nombreArchivo);
        if (Files.exists(path)) 
        {
            Files.delete(path);
            return true;
        }
        return false;
    }
    
    /**
     * Elimina un conjunto de preguntas
     * @param nombreArchivo Nombre del archivo
     * @return true si se elimino correctamente, en caso contrario false
     */
    public boolean eliminarConjunto(String nombreArchivo) throws IOException 
    {
        String ruta = DIRECTORIO_BASE + nombreArchivo + ".csv";
        Path path = Paths.get(ruta);
        if (Files.exists(path)) 
        {
            Files.delete(path);
            return true;
        }
        return false;
    }
    
    /**
     * Verifica si existe una pregunta
     * @param idPregunta ID de la pregunta
     * @return true si existe
     */
    public boolean existe(Integer idPregunta) 
    {
        String nombreArchivo = DIRECTORIO_BASE + "pregunta_" + idPregunta + ".csv";
        return Files.exists(Paths.get(nombreArchivo));
    }
    
    /**
     * Importa una pregunta desde un CSV externo y la añade a una encuesta
     * @param rutaCSV Ruta del archivo CSV externo
     * @param encuesta Encuesta a la que añadir la pregunta
     * @return La pregunta importada
     */
    public Pregunta importarDesdeCSV(String rutaCSV, Encuesta encuesta) throws IOException 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaCSV))) {
            String linea = reader.readLine();
            if (linea.toLowerCase().contains("id") || linea.toLowerCase().contains("enunciado")) linea = reader.readLine();
            if (linea != null && !linea.trim().isEmpty()) 
            {
                Pregunta pregunta = convLineaPregunta(linea, encuesta);
                if (pregunta != null) guardar(pregunta);
                return pregunta;
            }
        }
        return null;
    }
    
    /**
     * Exporta una pregunta a un CSV externo
     * @param pregunta La pregunta a exportar
     * @param rutaDestino Ruta donde guardar el CSV
     */
    public void exportarACSV(Pregunta pregunta, String rutaDestino) throws IOException 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaDestino))) 
        {
            writer.write("ID,TipoP,TipoR,Enunciado,IdEncuesta,Clase,Opciones");
            writer.newLine();
            writer.write(convertirPreguntaALinea(pregunta));
            writer.newLine();
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    ///                         MÉTODOS PRIVADOS                            ///
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Convierte una pregunta a una línea CSV
     * Formato: ID,TipoP,TipoR,Enunciado,IdEncuesta,Clase,Opciones
     * Las opciones se separan con ':'
     * @param p Pregunta correspondiente a convertir
     */
    private String convertirPreguntaALinea(Pregunta p) 
    {
        StringBuilder linea = new StringBuilder();
        linea.append(p.getId()).append(",");
        linea.append(p.getTipo()).append(",");
        linea.append(p.getTipo()).append(",");
        linea.append(escaparTexto(p.getEnunciado())).append(",");
        Encuesta encuesta = p.getEncuesta();
        if (encuesta != null && encuesta.getId() != null)linea.append(encuesta.getId());
        linea.append(",");
        String clase = "";
        if (p instanceof Libre) clase = "Libre";
        else if (p instanceof Unica) clase = "Unica";
        else if (p instanceof Multiple) clase = "Multiple";
        linea.append(clase).append(",");
        if (p instanceof Unica) 
        {
            Unica unica = (Unica) p;
            linea.append(convertirOpcionesATexto(unica.getOpciones()));
        } 
        else if (p instanceof Multiple) 
        {
            Multiple multiple = (Multiple) p;
            linea.append(convertirOpcionesATexto(multiple.getOpciones()));
        }
        return linea.toString();
    }
    
    /**
     * Convierte opciones a texto separado por ':'
     * Escapa ':' dentro de las opciones con '\:'
     * @param opciones Lista de opciones correspondientes a convertir
     */
    private String convertirOpcionesATexto(List<String> opciones)
    {
        if (opciones == null || opciones.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < opciones.size(); i++) 
        {
            if (i > 0) sb.append(":");
            String opcion = opciones.get(i).replace(":", "\\:");
            sb.append(opcion);
        }
        return sb.toString();
    }
    
    /**
     * Escapa un texto si contiene comas (lo envuelve en comillas)
     */
    private String escaparTexto(String texto) 
    {
        if (texto == null) return "";
        if (texto.contains(",")) return "\"" + texto.replace("\"", "\"\"") + "\"";
        return texto;
    }
    
    /**
     * Desescapa un texto que está entre comillas
     */
    private String desescaparTexto(String texto) 
    {
        if (texto == null) return "";
        if (texto.startsWith("\"") && texto.endsWith("\"")) 
        {
            texto = texto.substring(1, texto.length() - 1);
            texto = texto.replace("\"\"", "\"");
        }
        return texto;
    }
    
    /**
     * Convierte una línea CSV y crea la pregunta correspondiente
     * Formato: ID,TipoP,TipoR,Enunciado,IdEncuesta,Clase,Opciones
     */
    private Pregunta convLineaPregunta(String linea, Encuesta encuesta) 
    {
        try 
        {
            ArrayList<String> campos = convLineaCSV(linea);
            if (campos.size() < 6) 
            {
                System.err.println("ERROR: Línea con formato incorrecto (menos de 6 campos): " + linea);
                return null;
            }
            Integer id = Integer.parseInt(campos.get(0).trim());
            Integer tipoP = Integer.parseInt(campos.get(1).trim());
            Integer tipoR = Integer.parseInt(campos.get(2).trim());
            String enunciado = desescaparTexto(campos.get(3));
            String clase = campos.get(5).trim(); 
            String opcionesStr = campos.size() > 6 ? campos.get(6).trim() : ""; 
            
            if ("Libre".equals(clase)) 
            {
                return new Libre(id, enunciado, encuesta, tipoP, tipoR);
            }
            else if ("Unica".equals(clase)) 
            {
                ArrayList<String> opcionesU = convOpciones(opcionesStr);
                return new Unica(id, enunciado, encuesta, tipoP, tipoR, opcionesU);
            } 
            else if ("Multiple".equals(clase)) 
            {
                ArrayList<String> opcionesM = convOpciones(opcionesStr);
                return new Multiple(id, enunciado, encuesta, tipoP, tipoR, opcionesM);
            } 
            else 
            {
                System.err.println("ERROR: Clase desconocida: " + clase);
                return null;
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error parseando línea: " + linea);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Convertir una línea CSV respetando comillas
     */
    private ArrayList<String> convLineaCSV(String linea) 
    {
        ArrayList<String> campos = new ArrayList<>();
        StringBuilder campoActual = new StringBuilder();
        boolean dentroComillas = false;
        for (int i = 0; i < linea.length(); i++) 
        {
            char c = linea.charAt(i);
            if (c == '"') 
            {
                if (i + 1 < linea.length() && linea.charAt(i + 1) == '"') 
                {
                    campoActual.append('"');
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
        return campos;
    }
    
    /**
     * Convierte el campo de opciones (separadas por ':')
     */
    private ArrayList<String> convOpciones(String opcionesStr) 
    {
        ArrayList<String> opciones = new ArrayList<>();
        if (opcionesStr == null || opcionesStr.trim().isEmpty()) return opciones;
        String[] partes = opcionesStr.split("(?<!\\\\)" + ":");
        for (String parte : partes) 
        {
            if (!parte.trim().isEmpty()) 
            {
                String opcion = parte.replace("\\:", ":");
                opciones.add(opcion.trim());
            }
        }
        return opciones;
    }
}