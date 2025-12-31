package Tests.Integracion;

import dominio.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.util.*;

/**
 * Test de integración: capa de persistencia testeada (Importar y exportar)
 */
public class IntegracionTest_Persistencia 
{
    @Test
    public void testFlujoGuardadoCargado() throws Exception 
    {
        System.out.println("TEST DE PERSISTENCIA: Guardar - Reinciar - Cargar - Ejecutar");
        System.out.println("\n------------------");
        System.out.println("Fase 1: Generando datos y guardando en la carpeta 'Datos/' en tu ordenador local");
        System.out.println("\n------------------");
        ctrlDominio ctrlOriginal = new ctrlDominio();
        ArrayList<ctrlDominio.DatosPregunta> datos = new ArrayList<>();
        datos.add(new ctrlDominio.DatosPregunta("Edad", 1, 1, null));
        Integer idEncuesta = ctrlOriginal.crearEncuesta(datos);
        
        Encuesta enc = ctrlOriginal.getEncuesta(idEncuesta);
        ArrayList<Pregunta> pregs = enc.getPreguntes();
        
        registrarRespuesta(ctrlOriginal, idEncuesta, enc, pregs.get(0), "Joven", 18.0);
        registrarRespuesta(ctrlOriginal, idEncuesta, enc, pregs.get(0), "Mayor", 80.0);
        
        System.out.println("Guardando encuesta ID: " + idEncuesta + " a CSV...");
        ctrlOriginal.guardarEncuesta(idEncuesta);
        System.out.println("Guardando respuestas a CSV...");
        ctrlOriginal.guardarRespuestas(idEncuesta);
        System.out.println("\n------------------");
        System.out.println("Fase 2: Simulando reinicio del sistema (Nuevo Controlador)");
        System.out.println("\n------------------");


        ctrlDominio ctrlNuevo = new ctrlDominio();
        
        boolean existeEnMemoria = true;
        try 
        {
            ctrlNuevo.getEncuesta(idEncuesta); // Esto debería ser null o fallar si no hemos cargado
            if(ctrlNuevo.getEncuesta(idEncuesta) == null) existeEnMemoria = false;
        } catch (Exception e) { existeEnMemoria = false; }
        
        System.out.println("Controlador nuevo esta vacío? " + (existeEnMemoria ? "NO" : "Si"));
        System.out.println("\n------------------");
        System.out.println("Fase 3: Importando desde CSV y Ejecutando");
        System.out.println("\n------------------");
        System.out.println("Importando encuesta ID: " + idEncuesta + "...");
        ctrlNuevo.importarEncuesta(idEncuesta);
        assertNotNull("La encuesta debería haberse cargado", ctrlNuevo.getEncuesta(idEncuesta));

        System.out.println("Importando respuestas...");
        ctrlNuevo.importarRespuestas(idEncuesta);
        
        Cjt_Respuestas respuestasCargadas = ctrlNuevo.getRespuestas(idEncuesta);
        int numPersonas = respuestasCargadas.getPersonas().size();
        System.out.println("Personas recuperadas del disco: " + numPersonas);
        assertEquals("Deberían recuperarse 2 personas", 2, numPersonas);

        System.out.println("Ejecutando K-Means (2 Clusters) sobre los datos recuperados...");
        ctrlNuevo.analizarEncuesta(idEncuesta, 2, "K-MEANS", "K-MEANSPP", new HashSet<>());
        ArrayList<Perfil> perfiles = ctrlNuevo.obtenerPerfiles(idEncuesta);
        assertEquals("El clustering debió generar 2 perfiles", 2, perfiles.size());
        System.out.println("Clustering ejecutado con éxito sobre datos persistidos.");
        System.out.println("\n------------------");
        System.out.println("Fase 4: Exportando Perfiles Resultantes");
        System.out.println("\n------------------");
        ctrlNuevo.exportarPerfiles(idEncuesta);
        File filePerfiles = new File("Datos/Perfiles/" + idEncuesta + ".csv");
        
        System.out.print("Verificando fichero de perfiles: ");
        if (filePerfiles.exists()) System.out.println("Fichero creado en " + filePerfiles.getPath());
        else System.out.println("Error: No se creó el fichero.");
        
        assertTrue(filePerfiles.exists());
        System.out.println("\nTEST COMPLETADO CORRECTAMENTE");
    }


    private void registrarRespuesta(ctrlDominio ctrl, Integer idE, Encuesta enc, Pregunta p, String idPer, Double val) throws Exception 
    {
        ArrayList<Respuesta> r = new ArrayList<>();
        r.add(new Numerica(new Persona(idPer), enc, p, val));
        ctrl.responderEncuesta(idE, idPer, r);
    }
}