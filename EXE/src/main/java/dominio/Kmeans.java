package dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Kmeans implements IEstrategiaAlgoritmo
{
    ///////////////////////////////////////////////////////////////////////////
    ///                            CONSTANTES                               ///
    ///////////////////////////////////////////////////////////////////////////
    private static final int MAX_ITERATIONS = 300; //Numero de iteracions maximo para evitar bucles infinitos.
    private static final double CONVERGENCE_TOLERANCE = 1e-4; // Constante de tolerancia usada para comparar la diferencia entre dos iteraciones del clustering.
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    private int k; //Numero de clusters
    private ArrayList<ArrayList<Respuesta>> centroidesFinal;
    private ArrayList<ArrayList<String>> infoAsignacionesFinal; //Guarda que coordenada pertenecen a cada cluster.
    private double indiceCalidad;
    private IEstrategiaIni estrategia;
    private Cjt_Respuestas respuestas; // Matriz con el conjunto de las respuestas
    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////
    public Kmeans(Cjt_Respuestas respuestas, IEstrategiaIni estrategia, int k)
    {
        this.k = k;
        this.infoAsignacionesFinal = new ArrayList<>();
        this.centroidesFinal = new ArrayList<>();
        this.estrategia = estrategia;
        this.respuestas = respuestas;
        this.indiceCalidad = Double.NaN; //Aun no calculado
    }
    ///////////////////////////////////////////////////////////////////////////
    ///                         GETTERS                                     ///
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public ArrayList<ArrayList<Respuesta>> getCentroides()
    {
        return this.centroidesFinal;
    }
    @Override
    public ArrayList<ArrayList<String>> getInfoAsignaciones()
    {
        return this.infoAsignacionesFinal;
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

        ArrayList<ArrayList<Respuesta>> cPrevios;
        ArrayList<ArrayList<Respuesta>> centroides = new ArrayList<>();

        ArrayList<ArrayList<String>> infoAsignaciones = new ArrayList<>();

        this.estrategia.iniCentroides(this.k, centroides, matriz);


        int n = 0; // Variable que cuenta las iteraciones
        boolean tolerable = true; // Variable que es cierta si la diferencia entre la posicion de los centroides actual y previa es lo suficientemente grande como para necesitar seguir calculando.
        while(n < MAX_ITERATIONS && tolerable)
        {
            infoAsignaciones = new ArrayList<>();
            for(int i = 0; i < this.k; i++)
            {
                ArrayList<String> a = new ArrayList<>();
                infoAsignaciones.add(a);
            }
            cPrevios = deepCopy(centroides);

            for(Map.Entry<String, ArrayList<Respuesta>> entrada : matriz.entrySet())
            {
                String id = entrada.getKey();
                ArrayList<Respuesta> punto = entrada.getValue();
                int cl = asignaCentroide(punto, centroides);
                infoAsignaciones.get(cl).add(id);
            }
            recalculaCentroides(centroides, infoAsignaciones, matriz);

            tolerable = checkTolerance(centroides, cPrevios);
            n++;
        }
        this.centroidesFinal = new ArrayList<>(centroides);
        this.infoAsignacionesFinal = new ArrayList<>(infoAsignaciones);
        evaluaCalidad(matriz);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PRIVADOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    private ArrayList<ArrayList<Respuesta>> deepCopy(ArrayList<ArrayList<Respuesta>> original)
    {
        ArrayList<ArrayList<Respuesta>> copia = new ArrayList<>();

        for (ArrayList<Respuesta> fila : original)
        {
            ArrayList<Respuesta> copiaFila = new ArrayList<>(fila);
            copia.add(copiaFila);
        }
        return copia;
    }

    private int asignaCentroide(ArrayList<Respuesta> punto, ArrayList<ArrayList<Respuesta>> centroides)
    {
        int centroideAsignado = 0;
        double distMinima = Double.MAX_VALUE;
        for (int i = 0; i <centroides.size(); i++)
        {
            ArrayList<Respuesta> c = centroides.get(i);
            double distGlobal = 0;
            int n = punto.size();
            for(int j = 0; j < n; j++)
            {
                Respuesta r1 = punto.get(j);
                Respuesta r2 = c.get(j);

                double distLocal = r1.distance(r2);
                distGlobal += distLocal;
            }
            distGlobal = distGlobal/n;

            if(distGlobal < distMinima)
            {
                distMinima = distGlobal;
                centroideAsignado = i;
            }
        }
        return centroideAsignado;
    }

    private void recalculaCentroides(ArrayList<ArrayList<Respuesta>> centroides, ArrayList<ArrayList<String>> infoAsignaciones, HashMap<String, ArrayList<Respuesta>> matriz)
    {
        ArrayList<ArrayList<Respuesta>> nuevosCentroides = new ArrayList<>();

        for (int i = 0; i < this.k; i++)
        {
            ArrayList<Respuesta> nuevoCentroide = new ArrayList<>();

            ArrayList<String> asignacion = infoAsignaciones.get(i); // IDs de los puntos en este cluster

            // Si ningún punto fue asignado a este cluster, mantenemos el centroide
            // en su posición anterior para evitar que desaparezca.
            if (asignacion.isEmpty()) {
                // Añadimos el centroide antiguo (de la iteración anterior)
                nuevosCentroides.add(centroides.get(i)); 
                continue; // Saltamos al siguiente cluster
            }

            // Obtenemos el número de dimensiones (columnas) del primer centroide
            int numDimensiones = centroides.get(0).size();

            // Iteramos por cada dimensión (columna j) para calcular el nuevo valor
            for (int j = 0; j < numDimensiones; j++)
            {
                // Recolectamos todas las respuestas de esta columna (j) que pertenecen a este cluster (i)
                ArrayList<Respuesta> mismaColumna = new ArrayList<>();
                for (String puntoId : asignacion)
                {
                    mismaColumna.add(matriz.get(puntoId).get(j));
                }

                Respuesta r = Cjt_Respuestas.calcularMedia(mismaColumna);
                nuevoCentroide.add(r);
            }
            nuevosCentroides.add(nuevoCentroide);
        }

        centroides.clear();
        centroides.addAll(nuevosCentroides);
    }

    private boolean checkTolerance(ArrayList<ArrayList<Respuesta>> centroides, ArrayList<ArrayList<Respuesta>> cPrevios)
    {
        boolean tolerable = false;

        int k = centroides.size();
        int c = centroides.get(0).size();

        for(int i = 0; i < k; i++)
        {
            double distGlobal = 0.0;
            for (int j = 0; j < c; j++)
            {
                Respuesta r1 = centroides.get(i).get(j);
                Respuesta r2 = cPrevios.get(i).get(j);
                if(r1 != null && r2 != null) {
                    double distLocal = r1.distance(r2);
                    distGlobal += distLocal;
                }
            }
            distGlobal = distGlobal/c;
            if (distGlobal > CONVERGENCE_TOLERANCE)
            {
                tolerable = true;
                break;
            }
        }
        return tolerable;
    }
    private void evaluaCalidad(HashMap<String, ArrayList<Respuesta>> matriz)
    {
        double sumaSiluetasGlobal = 0.0;
        int numPuntosTotales = matriz.size();

        // No se puede calcular Silueta con 1 solo clúster o 0 puntos.
        if (numPuntosTotales == 0 || this.k <= 1) {
            this.indiceCalidad = 0.0; 
            return;
        }

        // Iterar por CADA punto 'i' de la matriz
        for (Map.Entry<String, ArrayList<Respuesta>> entrada_i : matriz.entrySet()) {
            String id_i = entrada_i.getKey();
            ArrayList<Respuesta> punto_i = entrada_i.getValue();
            
            // Encontrar a qué clúster (c_idx) pertenece el punto 'i'
            int clusterDe_i = -1;
            for (int c_idx = 0; c_idx < this.k; c_idx++) {
                if (this.infoAsignacionesFinal.get(c_idx).contains(id_i)) {
                    clusterDe_i = c_idx;
                    break;
                }
            }
            
            if (clusterDe_i == -1) continue; // No fue asignado

            //Calcular a(i): Cohesión (distancia media a su propio cluster)
            double sumaDistancias_a = 0.0;
            int numPuntos_a = 0;
            ArrayList<String> puntosDelCluster_i = this.infoAsignacionesFinal.get(clusterDe_i);

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

                ArrayList<String> puntosDelCluster_j = this.infoAsignacionesFinal.get(c_idx_otro);
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
