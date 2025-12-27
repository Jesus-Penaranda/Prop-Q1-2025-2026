package dominio;


public abstract class Respuesta
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    protected Persona persona_as;
    protected Encuesta encuesta_as;
    protected Pregunta pregunta_as;

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
    * Constructora con parámetros
    * @param persona_as Persona que ha respondido la pregunta de la encuesta
    * @param encuesta_as Encuesta a la que pertence la respuesta
    * @param pregunta_as Pregunta respondida de la encuesta
    * */
    public Respuesta(Persona persona_as,  Encuesta encuesta_as, Pregunta pregunta_as)
    {
        this.persona_as = persona_as;
        this.encuesta_as = encuesta_as;
        this.pregunta_as = pregunta_as;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
     
    /**
     * Obliga a las subclases a implementar este método.
     * Devuelve la persona asociada a la respuesta.
     */
    public Persona getPersona() { return persona_as; }


    /**
     * Obliga a las subclases a implementar este método.
     * Devuelve la pregunta asociada a la respuesta.
     */
    public Pregunta getPregunta() { return pregunta_as; }

    /**
     * Obliga a las subclases a implementar este método.
     * Devuelve la encuesta asociada a la respuesta.
     */
    public Encuesta getEncuesta() { return encuesta_as; }

    /**
     * Obliga a las subclases a implementar este método.
     * Devuelve el valor (Integer o String).
     */
    public abstract Object getValor();


    /**
     * Calcula la distnacia entre respuesta actual y la respuesta r
     * @param r Respuesta deseada para calcular la distancia de la respuesta instanciada
     */
    public abstract double distance(Respuesta r);
}
