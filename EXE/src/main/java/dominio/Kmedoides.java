package dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementació de l'algorisme K-Medoids.
 * A diferència de K-Means, el centre del clúster (medoide) és sempre un punt existent
 * en el conjunt de dades, no una mitjana matemàtica.
 */
public class Kmedoides implements IEstrategiaAlgoritmo
{
    ///////////////////////////////////////////////////////////////////////////
    ///                            CONSTANTES                               ///
    ///////////////////////////////////////////////////////////////////////////
    private static final int MAX_ITERATIONS = 300; 
    private static final double CONVERGENCE_TOLERANCE = 0.0; // En K-medoides busquem que el punt no canviï (distancia 0)
    
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    private int k; // Numero de clusters
    private ArrayList<ArrayList<Respuesta>> centroidesFinal;
    private ArrayList<ArrayList<String>> infoAsignacionesFinal; 
    private double indiceCalidad;
    private Cjt_Respuestas respuestas; // Matriz con el conjunto de las respuestas

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    public Kmedoides(Cjt_Respuestas respuestas, int k)
    {
        this.k = k;
        this.infoAsignacionesFinal = new ArrayList<>();
        this.centroidesFinal = new ArrayList<>();
        this.respuestas = respuestas;
        this.indiceCalidad = Double.NaN; 
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
        ArrayList<ArrayList<Respuesta>> medoides = new ArrayList<>(); // Usem el nom medoides encara que siguin llistes de respostes
        ArrayList<ArrayList<String>> infoAsignaciones = new ArrayList<>();

        
        inicializaMedoidesTrivial(this.k, medoides, matriz);

        int n = 0; 
        boolean tolerable = true; 
        
        while(n < MAX_ITERATIONS && tolerable)
        {
            // Reiniciar assignacions
            infoAsignaciones = new ArrayList<>();
            for(int i = 0; i < this.k; i++)
            {
                infoAsignaciones.add(new ArrayList<>());
            }
            
            cPrevios = deepCopy(medoides);

            
            for(Map.Entry<String, ArrayList<Respuesta>> entrada : matriz.entrySet())
            {
                String id = entrada.getKey();
                ArrayList<Respuesta> punto = entrada.getValue();
                int cl = asignaCentroide(punto, medoides);
                infoAsignaciones.get(cl).add(id);
            }

            
            recalculaMedoides(medoides, infoAsignaciones, matriz);

            tolerable = checkTolerance(medoides, cPrevios);
            n++;
        }
        
        this.centroidesFinal = new ArrayList<>(medoides);
        this.infoAsignacionesFinal = new ArrayList<>(infoAsignaciones);
        
        evaluaCalidad(matriz);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PRIVADOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    
    private void inicializaMedoidesTrivial(int k, ArrayList<ArrayList<Respuesta>> centroides, HashMap<String, ArrayList<Respuesta>> matriz) 
    {
        centroides.clear();
        int contador = 0;
        for (ArrayList<Respuesta> punto : matriz.values()) 
        {
            if (contador >= k) break;
            centroides.add(new ArrayList<>(punto));
            contador++;
        }
    }

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

    // Aquest mètode és idèntic al de Kmeans (assigna al centre més proper)
    private int asignaCentroide(ArrayList<Respuesta> punto, ArrayList<ArrayList<Respuesta>> medoides)
    {
        int centroideAsignado = 0;
        double distMinima = Double.MAX_VALUE;
        for (int i = 0; i < medoides.size(); i++)
        {
            ArrayList<Respuesta> c = medoides.get(i);
            
            // Càlcul de distància (si Resposta té mètode distance)
            // Assumim que la lògica de distància és la mateixa que al codi original
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

            if(distGlobal < distMinima)
            {
                distMinima = distGlobal;
                centroideAsignado = i;
            }
        }
        return centroideAsignado;
    }

    
    private void recalculaMedoides(ArrayList<ArrayList<Respuesta>> medoides, ArrayList<ArrayList<String>> infoAsignaciones, HashMap<String, ArrayList<Respuesta>> matriz)
    {
        ArrayList<ArrayList<Respuesta>> nuevosMedoides = new ArrayList<>();

        for (int i = 0; i < this.k; i++)
        {
            ArrayList<String> idsEnCluster = infoAsignaciones.get(i);
            
            // Si el clúster està buit, mantenim el medoide anterior
            if (idsEnCluster.isEmpty()) 
            {
                nuevosMedoides.add(medoides.get(i));
                continue;
            }

            // Busquem el nou medoide
            String idMejorMedoide = null;
            double menorSumaDistancias = Double.MAX_VALUE;

            // Iterem sobre cada punt CANDIDAT a ser medoide dins del clúster
            for (String idCandidato : idsEnCluster) 
            {
                ArrayList<Respuesta> candidato = matriz.get(idCandidato);
                double sumaDistanciasActual = 0.0;

                // Calculem distancia total contra la resta de punts del clúster
                for (String idOtro : idsEnCluster) 
                {
                    if (idCandidato.equals(idOtro)) continue;

                    ArrayList<Respuesta> otro = matriz.get(idOtro);
                    // Càlcul distància entre candidato i otro
                    double distGlobal = 0;
                    int dim = candidato.size();
                    for(int d = 0; d < dim; d++) distGlobal += candidato.get(d).distance(otro.get(d));
                    sumaDistanciasActual += (distGlobal / dim);
                }

                if (sumaDistanciasActual < menorSumaDistancias) 
                {
                    menorSumaDistancias = sumaDistanciasActual;
                    idMejorMedoide = idCandidato;
                }
            }
            if (idMejorMedoide != null) nuevosMedoides.add(new ArrayList<>(matriz.get(idMejorMedoide)));
            else nuevosMedoides.add(medoides.get(i));
            
        }

        medoides.clear();
        medoides.addAll(nuevosMedoides);
    }

    private boolean checkTolerance(ArrayList<ArrayList<Respuesta>> medoides, ArrayList<ArrayList<Respuesta>> cPrevios)
    {
        boolean tolerable = false;
        int k = medoides.size();
        int c = medoides.get(0).size();

        for (int i = 0; i < k; i++)
        {
            double distGlobal = 0.0;
            for (int j = 0; j < c; j++)
            {
                Respuesta r1 = medoides.get(i).get(j);
                Respuesta r2 = cPrevios.get(i).get(j);
                if(r1 != null && r2 != null) 
                {
                    double distLocal = r1.distance(r2);
                    distGlobal += distLocal;
                }
            }
            distGlobal = distGlobal/c;
            
            // En K-Medoides, si el punt canvia, la distància serà > 0.
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
                if (this.infoAsignacionesFinal.get(c_idx).contains(id_i)) 
                {
                    clusterDe_i = c_idx;
                    break;
                }
            }
            
            if (clusterDe_i == -1) continue;

            // a(i): Cohesión
            double sumaDistancias_a = 0.0;
            int numPuntos_a = 0;
            ArrayList<String> puntosDelCluster_i = this.infoAsignacionesFinal.get(clusterDe_i);

            for (String id_j : puntosDelCluster_i) 
            {
                if (id_i.equals(id_j)) continue;
                ArrayList<Respuesta> punto_j = matriz.get(id_j);
                double distGlobal = 0.0;
                int n = punto_i.size();
                for(int j_dim = 0; j_dim < n; j_dim++) 
                {
                    Respuesta r1 = punto_i.get(j_dim);
                    Respuesta r2 = punto_j.get(j_dim);
                    double distLocal = r1.distance(r2);
                    distGlobal += distLocal;
                }
                double dist_ij = distGlobal / (double)n;

                sumaDistancias_a += dist_ij;
                numPuntos_a++;
            }
            double a_i = (numPuntos_a == 0) ? 0.0 : sumaDistancias_a / (double)numPuntos_a;

            // b(i): Separación
            double b_i = Double.MAX_VALUE;

            for (int c_idx_otro = 0; c_idx_otro < this.k; c_idx_otro++) 
            {
                if (c_idx_otro == clusterDe_i) continue;

                ArrayList<String> puntosDelCluster_j = this.infoAsignacionesFinal.get(c_idx_otro);
                if (puntosDelCluster_j.isEmpty()) continue; 

                double sumaDistancias_b = 0.0;
                int numPuntos_b = 0;

                for (String id_j : puntosDelCluster_j) 
                {
                    ArrayList<Respuesta> punto_j = matriz.get(id_j);
                    double distGlobal = 0.0;
                    int n = punto_i.size();
                    for(int j_dim = 0; j_dim < n; j_dim++) 
                    {
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
            
            if (b_i == Double.MAX_VALUE) b_i = 0.0; 

            // s(i)s
            double s_i = 0.0;
            if (Double.compare(a_i, b_i) == 0) s_i = 0.0;
            else if (Math.max(a_i, b_i) == 0.0) s_i = 0.0;
            else s_i = (b_i - a_i) / Math.max(a_i, b_i);
            sumaSiluetasGlobal += s_i;
        }

        this.indiceCalidad = sumaSiluetasGlobal / (double) numPuntosTotales;
    }
}
