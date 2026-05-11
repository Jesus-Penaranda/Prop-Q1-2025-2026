package dominio;
import java.lang.Math;
import java.util.Set;
import java.util.HashSet;

public class Numerica extends Respuesta
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    private Double valorUnic;
    private Set<Double> valorsMultiples;

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructora con parámetros de la clase padre Respuesta (per a no contestades)
     * @param persona_as Texto que corresponde a la respuesta a la pregunta asociada
     * @param encuesta_as Texto que corresponde a la respuesta a la pregunta asociada
     * @param pregunta_as Texto que corresponde a la respuesta a la pregunta asociada
     */
    public Numerica(Persona persona_as,  Encuesta encuesta_as, Pregunta pregunta_as) 
    {
        super(persona_as, encuesta_as, pregunta_as);
        this.valorUnic = null;
        this.valorsMultiples = null;
    }

    /**
     * Constructora con parámetros de la clase padre Respuesta y el valor numerico de la respuesta ( Unica)
     * @param persona_as Texto que corresponde a la respuesta a la pregunta asociada
     * @param encuesta_as Texto que corresponde a la respuesta a la pregunta asociada
     * @param pregunta_as Texto que corresponde a la respuesta a la pregunta asociada
     * @param n Valor numerico que corresponde a la respuesta a la pregunta asociada
     */
    public Numerica(Persona persona_as,  Encuesta encuesta_as, Pregunta pregunta_as, double n) 
    {
        super(persona_as, encuesta_as, pregunta_as);
        this.valorUnic = n;
        this.valorsMultiples = null;
    }

    /**
     * Constructora con parámetros de la clase padre Respuesta y el valor numerico de la respuesta (Multiple)
     * @param persona_as Texto que corresponde a la respuesta a la pregunta asociada
     * @param encuesta_as Texto que corresponde a la respuesta a la pregunta asociada
     * @param pregunta_as Texto que corresponde a la respuesta a la pregunta asociada
     * @param v Conjunt de valors numerics que corresponen a la resposta
     */
    public Numerica(Persona persona_as,  Encuesta encuesta_as, Pregunta pregunta_as, Set<Double> v) 
    {
        super(persona_as, encuesta_as, pregunta_as);
        this.valorUnic = null;
        this.valorsMultiples = v;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Getter del valor numerico de la respuesta.
     */
    @Override
    public Object getValor() 
    {
        if ((this.pregunta_as instanceof Unica) || (this.pregunta_as instanceof Libre)) return (valorUnic != null) ? valorUnic : 0.0;
        else if (this.pregunta_as instanceof Multiple) return (valorsMultiples != null) ? valorsMultiples : new HashSet<Double>();
        else return 0.0;
    }

    /**
     * Calcula la distancia entre respuesta actual y la respuesta r
     * @param r Respuesta deseada para calcular la distancia de la respuesta instanciada
     */
    public double distance(Respuesta r) 
    { 
        Numerica r2 = (Numerica) r;
        
        // Cuantitavia ordenada
        if ((this.pregunta_as instanceof Unica) ) 
        {
            Double valorA = this.valorUnic;
            Double valorB = (Double) r2.getValor();
            Unica u = (Unica) pregunta_as;
            
            if (valorA == null || valorB == null) 
            {
                if (valorA == null && valorB == null) return 0.0;
                return 1.0;
            }
            if ((u.getVmax() == null) || (u.getVmin() == null)) return 1.0;
            double Vmax = u.getVmax();
            double Vmin = u.getVmin();
            if (Vmax - Vmin == 0) return 0.0;
            return Math.abs((valorA - valorB)) /(Vmax - Vmin);

        }
        else if ((this.pregunta_as instanceof Libre) ) 
        {
            Double valorA = this.valorUnic;
            Double valorB = (Double) r2.getValor();
            Libre u = (Libre) pregunta_as;
            
            if (valorA == null || valorB == null) 
            {
                if (valorA == null && valorB == null) return 0.0;
                return 1.0;
            }
            if ((u.getVmax() == null) || (u.getVmin() == null)) return 1.0;
            double Vmax = u.getVmax();
            double Vmin = u.getVmin();
            if (Vmax - Vmin == 0) return 0.0;
            return Math.abs((valorA - valorB)) /(Vmax - Vmin);

        }
        // Cuantitavia no ordenada 
        else if (this.pregunta_as instanceof Multiple) 
        {
            Set<Double> Va = this.valorsMultiples;
            Set<Double> Vb = (Set<Double>) r2.getValor();

            if (Va == null || Vb == null) 
            {
                if (Va == null && Vb == null) return 0.0;
                return 1.0;
            }
            Set<Double> inter = new HashSet<>(Va);
            inter.retainAll(Vb);
            Set<Double> unio = new HashSet<>(Va);
            unio.addAll(Vb);

            if (unio.isEmpty()) return 0.0; // División por 0 (Error)
            double jaccard = (double) inter.size() /unio.size();
            return 1.0- jaccard;
        }
        return 0.0; 
    }

    /**
     * Establece el valor único de la respuesta textual.
     * @param valor El texto a asignar
     */
    public void setValorUnic(Double valor) { this.valorUnic = valor; }

    /**
     * Establece múltiples valores para la respuesta textual.
     * @param valores Conjunto de valores a asignar
     */
    public void setValorsMultiples(Set<Double> valores) { this.valorsMultiples = valores; }
}