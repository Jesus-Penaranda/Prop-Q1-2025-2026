package dominio;

import java.util.ArrayList;

public class Encuesta
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////

    private ArrayList<Pregunta> preguntas;
    private Integer identificador;
    private Integer nextIdP;

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
    * Constructora vacía
    * */
    public Encuesta()
    {
        preguntas = new ArrayList<Pregunta>();
        identificador = null;
        nextIdP = 1;
    }

    public Encuesta(Integer identificador)
    {
        preguntas = new ArrayList<Pregunta>();
        this.identificador = identificador;
        nextIdP = 1;
    }

    /**
     * Constructora con parámetros
     * @param preguntas Preguntas por las cuales la encuesta está formada
     * @param identificador Identificador único que tiene la encuesta
     * */
    public Encuesta(ArrayList<Pregunta> preguntas, Integer identificador)
    {
        this.preguntas = new ArrayList<Pregunta>(preguntas);
        this.identificador = identificador;
        this.nextIdP = 1;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    public void setPregunta(Pregunta p) { preguntas.add(p); }

    public void setPreguntas(ArrayList<Pregunta> ps) { preguntas.addAll(ps); }

    public Integer nextIdP() { return nextIdP++; }

    public void eliminarPregunta(Integer id) throws Exception {
        boolean found = this.preguntas.removeIf(p -> p.getId().equals(id));

        if (!found) {
            throw new Exception("La pregunta con ID local " + id + " no existe en esta encuesta.");
        }
    }

    /**
    * Constructora de identificador
    */
    public Integer getId()
    {
        return this.identificador;
    }

    public void print_preguntas() 
    {
        for (int i = 0; i <preguntas.size(); i++) System.out.println(preguntas.get(i).getEnunciado());
    }

    public int getNumPreguntas() { return preguntas.size(); }

    public Pregunta getPregunta(int i) { return preguntas.get(i); }

    public ArrayList<Pregunta> getPreguntes() { return this.preguntas; }
}
