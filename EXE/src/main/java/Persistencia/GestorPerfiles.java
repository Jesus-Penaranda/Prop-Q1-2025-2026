package Persistencia;

import dominio.*;
import java.io.*;
import java.util.*;

public class GestorPerfiles {

    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///s
    ///////////////////////////////////////////////////////////////////////////
    private static final String DIRECTORIO_BASE = "Datos/Perfiles/";


    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////
    public GestorPerfiles() 
    {
        File dir = new File(DIRECTORIO_BASE);
        if (!dir.exists()) dir.mkdirs();
    }
    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Carga un perfil desde un archivo CSV
     */
    public Perfil cargar(String nombreArchivo) throws IOException 
    {
        String ruta = DIRECTORIO_BASE + nombreArchivo + ".csv";
        File file = new File(ruta);
        if (!file.exists()) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
        {
            reader.readLine(); // Saltar cabecera
            String linea = reader.readLine();
            if (linea != null && !linea.trim().isEmpty()) 
            {
                String[] campos = linea.split(",", -1);
                String representante = campos[0];
                ArrayList<String> individuos = new ArrayList<>();
                if ((campos.length > 1) && (!(campos[1].isEmpty()))) individuos.addAll(Arrays.asList(campos[1].split(":")));
                return new Perfil(individuos, representante);
            }
        }
        return null;
    }
    /**
     * Guarda un conjunto de perfiles en un archivo CSV
     */
    public void guardarConjunto(List<Perfil> perfiles, String nombreArchivo) throws IOException 
    {
        String ruta = DIRECTORIO_BASE + nombreArchivo + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) 
        {
            writer.write("Representante,Individuos");
            writer.newLine();
            for (Perfil perfil : perfiles) 
            {
                writer.write(perfil.getRepresentante());
                writer.write(",");
                writer.write(String.join(":", perfil.getListaIndividuos()));
                writer.newLine();
            }
        }
    }
    /**
     * Carga un conjunto de perfiles desde un archivo CSV
     */
    public ArrayList<Perfil> cargarConjunto(String nombreArchivo) throws IOException 
    {
        String ruta = DIRECTORIO_BASE + nombreArchivo + ".csv";
        File file = new File(ruta);
        ArrayList<Perfil> perfiles = new ArrayList<>();
        if (!file.exists()) return perfiles;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
        {
            reader.readLine(); // Saltar cabecera
            String linea;
            while ((linea = reader.readLine()) != null) 
            {
                if (!linea.trim().isEmpty()) {
                    String[] campos = linea.split(",", -1);
                    String representante = campos[0];
                    ArrayList<String> individuos = new ArrayList<>();
                    if ((campos.length > 1) && (!(campos[1].isEmpty()))) individuos.addAll(Arrays.asList(campos[1].split(":")));
                    perfiles.add(new Perfil(individuos, representante));
                }
            }
        }
        return perfiles;
    }
    /**
     * Lista todos los archivos de perfiles guardados
     */
    public ArrayList<String> listarPerfiles() 
    {
        ArrayList<String> nombres = new ArrayList<>();
        File dir = new File(DIRECTORIO_BASE);
        if (!dir.exists()) return nombres;
        File[] archivos = dir.listFiles((d, name) -> name.endsWith(".csv"));
        if (archivos != null) 
        {
            for (File archivo : archivos) 
            {
                String nombre = archivo.getName();
                nombres.add(nombre.substring(0, nombre.length() - 4));
            }
        }
        return nombres;
    }

    /**
     * Elimina un perfil individual
     */
    public boolean eliminar(String nombreArchivo) 
    {
        String ruta = DIRECTORIO_BASE + nombreArchivo + ".csv";
        File file = new File(ruta);
        return file.exists() && file.delete();
    }

    /**
     * Exporta un perfil a un CSV externo
     */
    public void exportarACSV(Perfil perfil, String rutaDestino) throws IOException 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaDestino))) 
        {
            writer.write("Representante,Individuos");
            writer.newLine();
            writer.write(perfil.getRepresentante());
            writer.write(",");
            writer.write(String.join(":", perfil.getListaIndividuos()));
            writer.newLine();
        }
    }

    /**
     * Importa un perfil desde un CSV externo
     */
    public Perfil importarDesdeCSV(String rutaCSV) throws IOException 
    {
        File file = new File(rutaCSV);
        if (!file.exists()) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
        {
            reader.readLine(); // Saltar cabecera
            String linea = reader.readLine();
            if (linea != null && !linea.trim().isEmpty()) 
            {
                String[] campos = linea.split(",", -1);
                String representante = campos[0];
                ArrayList<String> individuos = new ArrayList<>();
                if (campos.length > 1 && !campos[1].isEmpty()) individuos.addAll(Arrays.asList(campos[1].split(":")));
                return new Perfil(individuos, representante);
            }
        }
        return null;
    }
}