package dominio;

import java.util.List;

public class Multiple extends Pregunta
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    protected List<String> opciones;

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructora para una PreguntaMultiple.
     * @param identificador ID de la pregunta
     * @param enunciado Texto de la pregunta
     * @param encuesta Objeto Encuesta al que pertenece
     * @param tipo Tipo de análisis (1-4)
     * @param opciones Lista de strings con las opciones de respuesta
     */
    public Multiple(Integer identificador, String enunciado, Encuesta encuesta, Integer tipoP, Integer tipoR, List<String> opciones) {
        super(identificador, enunciado, encuesta, tipoP, tipoR);
        this.opciones = opciones;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Devuelve la lista de opciones de esta pregunta.
     * @return Una Lista de Strings.
     */
    public List<String> getOpciones() {
        return opciones;
    }

    public int getNumModalitats() {
        return opciones.size();
    }
    
    /**
     * Muestra la pregunta y sus opciones por consola.
     */
    @Override
    public void mostrarPregunta() {
        System.out.println(this.enunciado);
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + opciones.get(i));
        }
        System.out.print("Introduce tus opciones (separadas por comas, ej: 1,3): ");
    }

    /**
     * Valida si el input del usuario es un formato válido (ej: "1,3")
     * y si todos los números están dentro del rango de opciones.
     * @param input El texto introducido por el usuario.
     * @return true si es válido, false en caso contrario.
     */
    @Override
    public boolean esRespuestaValida(String input) {
        if (input == null || input.isBlank()) {
             System.out.println("Error: La respuesta no puede estar vacía.");
            return false;
        }

        String[] partes = input.split(",");

        try {
            for (String parte : partes) {
                int numRespuesta = Integer.parseInt(parte.trim());
                
                if (numRespuesta < 1 || numRespuesta > opciones.size()) {
                    System.out.println("Error: La opción " + numRespuesta + " está fuera de rango.");
                    return false;
                }
            }
            return true;

        } catch (NumberFormatException e) {
            System.out.println("Error: Formato incorrecto. Assegúrate que son números y estan separados por comas.");
            return false;
        }
    }

    /**
    * Consultora del tipo de pregunta
    */
    public Integer getTipoPregunta() { return 2; }
}
