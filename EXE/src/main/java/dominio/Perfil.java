package dominio;

import java.util.ArrayList;

public class Perfil
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////

    private ArrayList<String> lista_individuos = new ArrayList<>();
    private String representante;
    

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
    * Constructora vacia
    * */
    public Perfil()
    {
        lista_individuos = new ArrayList<>();
        representante = null;
    }

    /**
    * Constructora con parametros
    * @param lista Lista con los indiviudos representativos del perfil
    * @param repre Persona representante del perfil
    * */
    public Perfil(ArrayList<String> lista, String repre)
    {
        lista_individuos = new ArrayList<>(lista);
        representante = repre;
    }

    /** 
    * Constructora con parametros
    * @param individuos Lista de personas pertenecientes al perfil
    * @param centroide Centroide del cluster del cual sale el perfil
    **/ 
    public Perfil(ArrayList<String> individuos, ArrayList<Respuesta> centroide, Cjt_Respuestas resps)
    {
        this.lista_individuos = new ArrayList<>(individuos);
        String rep = null;
        double minDist = Double.MAX_VALUE;

        for (String p : individuos) 
        {
            double distanciaTotal = 0.0;
            ArrayList<Respuesta> rsPersona = resps.getRespuestasPersona(p);
            
            for (Respuesta rCentroide : centroide) 
            {
                if (rCentroide == null) continue; // Saltar respuestas nulas
                
                String enunciadoBusqueda = rCentroide.getPregunta().getEnunciado();
                
                for (Respuesta rPersona : rsPersona) 
                {
                    if (rPersona.getPregunta().getEnunciado().equals(enunciadoBusqueda)) 
                    {
                        distanciaTotal += rPersona.distance(rCentroide);
                        break; 
                    }
                }
            }

            if (distanciaTotal < minDist) 
            {
                minDist = distanciaTotal;
                rep = p;
            }
        }
        this.representante = rep; 
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Getter del representante del perfil (Su identificador)
     * @return String con el identificador del representante
     */
    public String getRepresentante() { return this.representante; }

    /**
     * Getter de la lista con los identificadores de todos los individuos pertenecientes al perfil
     * @return Lista con los identificadores de los individuos
     */
    public ArrayList<String> getListaIndividuos() { return this.lista_individuos; }
}
