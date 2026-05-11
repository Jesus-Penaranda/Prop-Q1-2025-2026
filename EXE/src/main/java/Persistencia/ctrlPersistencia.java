package Persistencia;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dominio.*;

public class ctrlPersistencia 
{
    
    private GestorEncuestas gestorEncuestas;
    private GestorRespuestas gestorRespuestas;
    private GestorPerfiles gestorPerfiles;
    private GestorPreguntas gestorPreguntas;

    public ctrlPersistencia() 
    {
        gestorEncuestas = new GestorEncuestas();
        gestorRespuestas = new GestorRespuestas();
        gestorPerfiles = new GestorPerfiles();
        try 
        {
            gestorPreguntas = new GestorPreguntas();
        } 
        catch (IOException e) 
        {
            System.err.println("Error al inicializar GestorPreguntas: " + e.getMessage());
        }
    }

    public Encuesta cargar_encuesta (Integer idEncuesta) throws IOException 
    {
        return gestorEncuestas.cargar(idEncuesta);
    }

    public void guardar_encuesta (Encuesta e) throws IOException 
    {
        gestorEncuestas.guardar(e);
    }

    public ArrayList<Perfil> cargar_perfiles (String nombreArchivo) throws IOException 
    {
        return gestorPerfiles.cargarConjunto(nombreArchivo);
    }

    public void guardar_perfiles (List<Perfil> perfiles, String nombreArchivo) throws IOException 
    {
        gestorPerfiles.guardarConjunto(perfiles, nombreArchivo);
    }

    public Cjt_Respuestas cargar_respuestas (Encuesta encuesta) throws IOException 
    {
        return gestorRespuestas.cargar(encuesta);
    }

    public void guardar_respuestas (Cjt_Respuestas respuestas, Encuesta encuesta) throws IOException 
    {
        gestorRespuestas.guardar(respuestas, encuesta);
    }

    public ArrayList<Pregunta> cargar_preguntas (String nombreArchivo, Encuesta encuesta) throws IOException 
    {
        return gestorPreguntas.cargarConjunto(nombreArchivo, encuesta);
    }

    public void guardar_preguntas (List<Pregunta> preguntas, String nombreArchivo) throws IOException 
    {
        gestorPreguntas.guardarConjunto(preguntas, nombreArchivo);
    }
}