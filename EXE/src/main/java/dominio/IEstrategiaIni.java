package dominio;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interfaz para definir la estrategia de inicialización de centroides
 * en el algoritmo K-means.
 */
public interface IEstrategiaIni 
{
    /**
     * Inicializa la lista de centroides.
     * @param k El número de clusters (centroides) a inicializar.
     * @param centroides La lista (vacía) que será poblada con los centroides iniciales. 
     * @param matriz Los datos completos (conjunto de respuestas) de donde se seleccionarán
     * los centroides.
     */
    public void iniCentroides(int k, ArrayList<ArrayList<Respuesta>> centroides, HashMap<String, ArrayList<Respuesta>> matriz);
}