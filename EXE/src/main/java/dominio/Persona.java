package dominio;

public class Persona
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    private String identificador;

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
    * Constructora vacía
    * */
    public Persona()
    {
        identificador = "";
    }

    /**
     * Constructora con parámetros
     * */
    public Persona(String identificador)
    {
        this.identificador = identificador;
    }

    /**
    * Constructora copia
    * */
    public Persona(Persona a)
    {
        identificador = a.identificador;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    /**
    * Constructora de identificador
    */
    public String getId()
    {
        return this.identificador;
    }
}
