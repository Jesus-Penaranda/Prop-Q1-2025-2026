package dominio;

import java.util.ArrayList;
import java.util.Map;

public class Libre extends Pregunta
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructora para una Pregunta Libre.
     * Recibe todos los parámetros necesarios para la clase base (Pregunta)
     * y se los pasa.
     * @param identificador ID de la pregunta
     * @param enunciado Texto de la pregunta
     * @param encuesta Objeto Encuesta al que pertenece
     * @param tipo Tipo de análisis (1-4)
     */
    public Libre(Integer identificador, String enunciado, Encuesta encuesta, Integer tipoP, Integer tipoR) {
        // Llamada a la constructora de la clase madre (Pregunta)
        super(identificador, enunciado, encuesta, tipoP, tipoR);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Muestra la pregunta por pantalla.
     */
    @Override
    public void mostrarPregunta() {
        System.out.println(this.enunciado);
        System.out.print("Introduce tu respuesta: ");
    }

    /**
     * Valida si la entrada del usuario es válida para una respuesta libre.
     * @param input El texto introducido por el usuario.
     * @return true si el input NO está vacío o nulo, false en caso contrario.
     */
    @Override
    public boolean esRespuestaValida(String input) {
        // Una respuesta libre es válida si no és nula ni está vacía.
        if (input == null || input.isBlank()) {
            System.out.println("Error: La resposta no puede estar vacía.");
            return false;
        }
        return true;
    }

    /**
     * Calcula Vmin y Vmax para esta pregunta leyendo la columna específica
     * de la matriz de respuestas
     */
    public boolean Calcular_vmax_vmin(Map<String, ArrayList<Respuesta>> matriz, int colIndex) 
    {
        if (matriz == null || matriz.isEmpty()) return false;
        Double min = null;
        Double max = null;
        for (ArrayList<Respuesta> fila : matriz.values()) 
        {
            if (fila == null || fila.size() <= colIndex) continue;
            Respuesta r = fila.get(colIndex);
            if (r == null) continue;
            Object val = r.getValor();
            if (val instanceof Number) 
            {
                double v = ((Number) val).doubleValue();
                if (min == null) 
                {
                    // primer val num encntrado
                    min = v;
                    max = v;
                } 
                else 
                {
                    if (v < min) min = v;
                    if (v > max) max = v;
                }
            }
        }
        if ((min == null) || (max == null)) return false; // no se encontraron valores num
        this.Vmin = min;
        this.Vmax = max;
        return true;
    }
}
