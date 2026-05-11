package dominio;

import java.util.List;

public class Unica extends Pregunta
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////

    protected List<String> opciones;

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructora para una Pregunta Unica.
     * Recibe todos los parámetros necesarios para la clase base (Pregunta)
     * y se los pasa.
     * @param identificador ID de la pregunta
     * @param enunciado Texto de la pregunta
     * @param encuesta Objeto Encuesta al que pertenece
     * @param tipo Tipo de análisis (1-4)
     * @param opciones Lista de strings con las opciones de respuesta
     */
    public Unica(Integer identificador, String enunciado, Encuesta encuesta, Integer tipoP, Integer tipoR, List<String> opciones) 
    {
        super(identificador, enunciado, encuesta, tipoP, tipoR);
        this.opciones = opciones;
        // Calcular Vmax y Vmin si las opciones son num
        try 
        {
            double vmax = Double.parseDouble(opciones.get(0));
            double vmin = Double.parseDouble(opciones.get(0));
            for (String op : opciones) 
            {
                double val = Double.parseDouble(op);
                if (val > vmax) vmax = val;
                if (val < vmin) vmin = val;
            }
            setVmin(vmin);
            setVmax(vmax);

        } 
        catch (NumberFormatException e) {}
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

     /**
     * Devuelve el número de opciones de esta pregunta.
     * @return Integer con el número de opciones.
     */
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
            // Muestra las opciones numeradas (empezando per 1)
            System.out.println("  " + (i + 1) + ". " + opciones.get(i));
        }
        System.out.print("Introduce tu opción (un número): ");
    }

    /**
     * Valida si el input del usuario es un número válido
     * y está dentro del rango de las opciones.
     * @param input El texto introducido por el usuario.
     * @return true si es válido, false en caso contrario.
     */
    @Override
    public boolean esRespuestaValida(String input) {
        try {
            int numRespuesta = Integer.parseInt(input); // Intenta convertir el text a número
            
            // Valida que el número esté dentro del rango de opciones
            if (numRespuesta >= 1 && numRespuesta <= opciones.size()) {
                return true; // Respuesta válida
            } else {
                System.out.println("Error: El número tiene que estar entre 1 i " + opciones.size());
                return false; // Respuesta inválida (fuera de rango)
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Tienes que introducir un número.");
            return false; // Respuesta inválida (no es un número)
        }
    }

    /**
     * Devuelve el índice numérico de una opción
     * @param t El texto de la opción a buscar
     * @return El índice (numeral) de la opción
     */
    public Integer getNumeral(String t) 
    {
        if (opciones == null) return null;
        int numeral = opciones.indexOf(t);
        if (numeral == -1) return null;
        return numeral;
    }

}
