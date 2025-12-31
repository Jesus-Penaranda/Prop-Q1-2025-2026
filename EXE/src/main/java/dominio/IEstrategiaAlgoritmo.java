package dominio;
import java.util.ArrayList;

/**
 * Interfaz que define el contrato para cualquier algoritmo de clustering.
 * Obliga a las clases que la implementen a tener un método principal
 * para ejecutar el algoritmo.
 */
public interface IEstrategiaAlgoritmo 
{
    /**
     * Ejecuta el proceso principal del algoritmo de clustering.
     */
    public void iniciaAlgoritmo();

    /**
     * Getter de centroides
     * @return Lista con los centroides
     */
    public ArrayList<ArrayList<Respuesta>> getCentroides();

    /**
     * Getter de asignaciones de respuestas a centroides
     * @return Lista con las asignaciones
     */
    public ArrayList<ArrayList<String>> getInfoAsignaciones();

    /**
     * Getter de la calidad general del clustering
     * @return Valor numérico de la calidad representativa
     */
    public double getIndiceCalidad();

    /**
     * Getter del número de clusters
     * @return Número que reprsenta el total de clusters seleccionados
     */
    public int getK();
}
