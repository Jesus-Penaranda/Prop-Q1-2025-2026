package dominio;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementación de una estrategia de inicialización trivial.
 * Selecciona los primeros 'k' puntos de la matriz de datos como
 * centroides iniciales.
 */
public class IniTrivial implements IEstrategiaIni {
    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////
    public IniTrivial()
    {

    }
    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public void iniCentroides(int k, ArrayList<ArrayList<Respuesta>> centroides, HashMap<String, ArrayList<Respuesta>> matriz) 
    {
        centroides.clear();
        int contador = 0;
        // Iteramos sobre los valores de la matriz
        for (ArrayList<Respuesta> punto : matriz.values()) {
            
            // Si ya tenemos 'k' centroides, paramos.
            if (contador >= k) break;
            centroides.add(new ArrayList<>(punto));
            contador++;
        }
        // Por ahora, simplemente inicializará matriz.size() centroides.
        // Habria que llamar a excepcion
    }
}
