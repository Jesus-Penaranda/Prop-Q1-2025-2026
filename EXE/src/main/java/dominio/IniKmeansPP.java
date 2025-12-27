package dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Implementación de la estrategia de inicialización K-Means++.
 * Esta estrategia selecciona los centroides iniciales de forma inteligente
 * para mejorar la calidad del clustering y la velocidad de convergencia.
 * 1. Elige el primer centroide aleatoriamente.
 * 2. Para cada centroide restante, elige un punto de los datos con una
 * probabilidad proporcional al cuadrado de su distancia al centroide
 * más cercano ya elegido.
 */
public class IniKmeansPP implements IEstrategiaIni 
{
    private int semilla = 1234;
    private Random rand = new Random(semilla);

    public IniKmeansPP(){}

    public IniKmeansPP(int semilla) 
    {
        this.semilla = semilla;
        this.rand = new Random(semilla);
    }

    @Override
    public void iniCentroides(int k, ArrayList<ArrayList<Respuesta>> centroides, HashMap<String, ArrayList<Respuesta>> matriz) {
        centroides.clear();
        ArrayList<ArrayList<Respuesta>> puntos = new ArrayList<>(matriz.values());

        if (k >= puntos.size()) 
        {
            for (ArrayList<Respuesta> punto : puntos) centroides.add(new ArrayList<>(punto)); 
            return;
        }

        int primerIndice = rand.nextInt(puntos.size());
        centroides.add(new ArrayList<>(puntos.get(primerIndice))); 

        double[] distanciasAlCuadrado = new double[puntos.size()];

        for (int i = 1; i < k; i++) 
        {
            double sumaTotalDistanciasSq = 0.0;
            for (int p_idx = 0; p_idx < puntos.size(); p_idx++) 
            {
                ArrayList<Respuesta> puntoActual = puntos.get(p_idx);
                double distMinima = Double.MAX_VALUE;
                for (ArrayList<Respuesta> c : centroides) 
                {
                    double dist = distanciaEntrePuntos(puntoActual, c);
                    if (dist < distMinima) distMinima = dist;
                }
                distanciasAlCuadrado[p_idx] = distMinima * distMinima;
                sumaTotalDistanciasSq += distanciasAlCuadrado[p_idx];
            }

            if (sumaTotalDistanciasSq == 0.0) 
            {
                boolean encontrado = false;
                for (ArrayList<Respuesta> p : puntos) 
                {
                    boolean yaEsCentroide = false;
                    for (ArrayList<Respuesta> c : centroides) 
                    {
                        if (distanciaEntrePuntos(p, c) == 0.0) 
                        { 
                            yaEsCentroide = true;
                            break;
                        }
                    }
                    if (!yaEsCentroide) 
                    {
                        centroides.add(new ArrayList<>(p));
                        encontrado = true;
                        break;
                    }
                }
                
                // Si no encontramos nada nuevo (todos son iguales), duplicamos el último
                if (!encontrado && !centroides.isEmpty()) centroides.add(new ArrayList<>(centroides.get(centroides.size() - 1)));
                continue;
            }

            double r = rand.nextDouble() * sumaTotalDistanciasSq;
            double sumaAcumulada = 0.0;
            for (int p_idx = 0; p_idx < puntos.size(); p_idx++) 
            {
                sumaAcumulada += distanciasAlCuadrado[p_idx];
                if (sumaAcumulada >= r) 
                {
                    centroides.add(new ArrayList<>(puntos.get(p_idx)));
                    break;
                }
            }
        } 
    }

    private double distanciaEntrePuntos(ArrayList<Respuesta> punto1, ArrayList<Respuesta> punto2) 
    {
        double distGlobal = 0.0;
        int n = punto1.size();
        if (n == 0) return 0.0;

        for(int j = 0; j < n; j++) distGlobal += punto1.get(j).distance(punto2.get(j)); 
        return distGlobal / n; 
    }

    public void setSemilla(int sem) 
    {
        this.semilla = sem; 
        this.rand = new Random(sem); 
    }
}
