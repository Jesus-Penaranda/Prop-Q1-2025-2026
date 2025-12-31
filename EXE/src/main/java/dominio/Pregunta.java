package dominio;

public abstract class Pregunta
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    protected Integer identificador;
    protected String enunciado;
    protected Encuesta encuesta;
    protected Integer tipoPregunta;
    protected Integer tipoRespuesta;
    protected Double Vmin = null;
    protected Double Vmax = null;

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORES                               ///
    ///////////////////////////////////////////////////////////////////////////
    public Pregunta() 
    {
        this.identificador = null;
        this.enunciado = null;
        this.encuesta = null;
        this.tipoPregunta = null;
        this.tipoRespuesta = null;
    }

    protected Pregunta(Integer id, String enun, Encuesta enc, Integer tipoP, Integer tipoR) 
    {
        this.identificador = id;
        this.enunciado = enun;
        this.encuesta = enc;
        this.tipoPregunta = tipoP;
        this.tipoRespuesta = tipoR;
    }

    public Integer getId() 
    { 
        return identificador;
    }

    public String getEnunciado() 
    { 
        return enunciado; 
    }

    public Integer getIdEncuesta() 
    {
        if (encuesta == null) return null;
        return encuesta.getId();
    }

    public Encuesta getEncuesta() 
    {
        return this.encuesta;
    }

    public Integer getTipo() 
    {
        return tipoRespuesta;
    }

    public Integer getTipoPregunta() 
    {
        return tipoPregunta;
    }


    public void setEncuesta(Encuesta enc) 
    {
        this.encuesta = enc;
    }

    public boolean perteneceAEncuesta(String id) 
    {
        if (id == null) return false;
        if (this.encuesta == null) return false;
        Integer encId = this.encuesta.getId();
        if (encId == null) return false;
        return id.equals(String.valueOf(encId));
    }

    public Double getVmin() { return Vmin; }

    public Double getVmax() { return Vmax; }

    public void setVmin(Double v) { this.Vmin = v; }
    
    public void setVmax(Double v) { this.Vmax = v; }

    public abstract void mostrarPregunta();

    public abstract boolean esRespuestaValida(String input);
}