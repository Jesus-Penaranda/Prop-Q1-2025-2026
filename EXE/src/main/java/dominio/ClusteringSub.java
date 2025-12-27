package dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClusteringSub implements IEstrategiaAlgoritmo
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    private int k; //Numero de clusters
    private ArrayList<ArrayList<Respuesta>> centroidesFinales; 
    private ArrayList<ArrayList<String>> infoAsignacionesFinales; 
    private double indiceCalidad;

    private IniKmeansPP ini;
    private Cjt_Respuestas respuestas; // Matriz con el conjunto de las respuestas
    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////
    public ClusteringSub(Cjt_Respuestas respuestas, IniKmeansPP ini, int k)
    {
        this.k = k;
        this.infoAsignacionesFinales = new ArrayList<>();
        this.centroidesFinales = new ArrayList<>();
        this.ini = ini;
        this.respuestas = respuestas;
        this.indiceCalidad = Double.NaN; // Aun no calculado
    }
    ///////////////////////////////////////////////////////////////////////////
    ///                         GETTERS                                     ///
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public ArrayList<ArrayList<Respuesta>> getCentroides()
    {
        return this.centroidesFinales;
    }
    @Override
    public ArrayList<ArrayList<String>> getInfoAsignaciones()
    {
        return this.infoAsignacionesFinales;
    }
    @Override
    public double getIndiceCalidad()
    {
        return this.indiceCalidad;
    }
    @Override
    public int getK()
    {
        return this.k;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void iniciaAlgoritmo()
    {
        HashMap<String, ArrayList<Respuesta>> matriz = respuestas.getMatriz();
        ArrayList<ArrayList<Respuesta>> centroides = new ArrayList<>();
        ArrayList<ArrayList<String>> infoAsignaciones = new ArrayList<>();
        this.ini.iniCentroides(this.k, centroides, matriz);

        for (int i = 0; i < this.k; i++)
        {
            ArrayList<String> a = new ArrayList<>();
            infoAsignaciones.add(a);
        }

        for (Map.Entry<String, ArrayList<Respuesta>> entrada : matriz.entrySet())
        {
            String id = entrada.getKey();
            ArrayList<Respuesta> punto = entrada.getValue();
            int cl = asignaCentroide(punto, centroides);
            infoAsignaciones.get(cl).add(id);
        }
        this.centroidesFinales = new ArrayList<>(centroides);
        this.infoAsignacionesFinales = new ArrayList<>(infoAsignaciones);
        evaluaCalidad(matriz);
    }
    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PRIVADOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    private int asignaCentroide(ArrayList<Respuesta> punto, ArrayList<ArrayList<Respuesta>> centroides)
    {
        int centroideAsignado = 0;
        double distMinima = Double.MAX_VALUE;
        for (int i = 0; i <centroides.size(); i++)
        {
            ArrayList<Respuesta> c = centroides.get(i);
            double distGlobal = 0;
            int n = punto.size();
            for (int j = 0; j < n; j++)
            {
                Respuesta r1 = punto.get(j);
                Respuesta r2 = c.get(j);
                double distLocal = r1.distance(r2);
                distGlobal += distLocal;
            }
            distGlobal = distGlobal/n;

            if (distGlobal < distMinima)
            {
                distMinima = distGlobal;
                centroideAsignado = i;
            }
        }
        return centroideAsignado;
    }

    private void evaluaCalidad(HashMap<String, ArrayList<Respuesta>> matriz)
    {
        double sumaSiluetasGlobal = 0.0;
        int numPuntosTotales = matriz.size();
        if (numPuntosTotales == 0 || this.k <= 1)
        {
            this.indiceCalidad = 0.0; 
            return;
        }

        for (Map.Entry<String, ArrayList<Respuesta>> entrada_i : matriz.entrySet()) 
        {
            String id_i = entrada_i.getKey();
            ArrayList<Respuesta> punto_i = entrada_i.getValue();
            int clusterDe_i = -1;
            for (int c_idx = 0; c_idx < this.k; c_idx++) 
            {
                if (this.infoAsignacionesFinales.get(c_idx).contains(id_i)) 
                {
                    clusterDe_i = c_idx;
                    break;
                }
            }
            
            if (clusterDe_i == -1) continue;
            double sumaDistancias_a = 0.0;
            int numPuntos_a = 0;
            ArrayList<String> puntosDelCluster_i = this.infoAsignacionesFinales.get(clusterDe_i);

            for (String id_j : puntosDelCluster_i) {
                if (id_i.equals(id_j)) continue; // No comparar consigo mismo

                ArrayList<Respuesta> punto_j = matriz.get(id_j);

                double distGlobal = 0.0;
                int n = punto_i.size();
                for(int j_dim = 0; j_dim < n; j_dim++) {
                    Respuesta r1 = punto_i.get(j_dim);
                    Respuesta r2 = punto_j.get(j_dim);
                    double distLocal = r1.distance(r2);
                    distGlobal += distLocal;
                }
                double dist_ij = distGlobal / (double)n;

                sumaDistancias_a += dist_ij;
                numPuntos_a++;
            }
            // Si numPuntos_a es 0, es un clúster de 1 solo punto. a_i es 0.
            double a_i = (numPuntos_a == 0) ? 0.0 : sumaDistancias_a / (double)numPuntos_a;


            //Calcular b(i): Separación (distancia media MÍNIMA a OTRO clúster)
            double b_i = Double.MAX_VALUE;

            for (int c_idx_otro = 0; c_idx_otro < this.k; c_idx_otro++) {
                if (c_idx_otro == clusterDe_i) continue; // Saltar el propio clúster

                ArrayList<String> puntosDelCluster_j = this.infoAsignacionesFinales.get(c_idx_otro);
                if (puntosDelCluster_j.isEmpty()) continue; // Saltar clústeres vacíos

                double sumaDistancias_b = 0.0;
                int numPuntos_b = 0;

                for (String id_j : puntosDelCluster_j) {
                    ArrayList<Respuesta> punto_j = matriz.get(id_j);

                    double distGlobal = 0.0;
                    int n = punto_i.size();
                    for(int j_dim = 0; j_dim < n; j_dim++) {
                        Respuesta r1 = punto_i.get(j_dim);
                        Respuesta r2 = punto_j.get(j_dim);
                        double distLocal = r1.distance(r2);
                        distGlobal += distLocal;
                    }
                    double dist_ij = distGlobal / (double)n;

                    sumaDistancias_b += dist_ij;
                    numPuntos_b++;
                }
                
                double distMediaCluster = sumaDistancias_b / (double)numPuntos_b;
                b_i = Math.min(b_i, distMediaCluster);
            }
            
            // Si b_i sigue siendo MAX_VALUE (p.ej. todos los demás clústers estaban vacíos),
            // lo tratamos como un caso de 1 solo clúster (s(i) será 0).
            if (b_i == Double.MAX_VALUE) {
                b_i = 0.0; 
            }

            //Calcular s(i) para este punto
            double s_i = 0.0;
            if (Double.compare(a_i, b_i) == 0) {
                 s_i = 0.0;
            } else if (Math.max(a_i, b_i) == 0.0) { // Clúster de 1 solo punto
                s_i = 0.0;
            } else {
                s_i = (b_i - a_i) / Math.max(a_i, b_i);
            }
            
            sumaSiluetasGlobal += s_i;
        }

        // El índice global es la media de todos los s(i)
        this.indiceCalidad = sumaSiluetasGlobal / (double) numPuntosTotales;
    }
}

