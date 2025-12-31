package dominio;

import java.util.Set;
import java.util.HashSet;
import java.lang.Math;

public class Textual extends Respuesta
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    private String valorUnic;
    private Set<String> valorsMultiples;
    
    private Integer numeral;
    private int numModalitats;

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructora para crear una respuesta textual Única.
     * @param persona_as Persona que responde
     * @param encuesta_as Encuesta a la que pertenece
     * @param pregunta_as Pregunta que se responde
     * @param t Valor textual de la respuesta
     */
    public Textual(Persona persona_as,  Encuesta encuesta_as, Pregunta pregunta_as, String t) 
    {
        super(persona_as, encuesta_as, pregunta_as);
        this.valorUnic = t;
        this.valorsMultiples = null;
        // 3 - cualitativa ordenada
        if ((pregunta_as.getTipo() == 3) && (t != null)) 
        {
            if (this.pregunta_as instanceof Unica) 
            {
                Unica u = (Unica) this.pregunta_as;
                this.numeral = u.getNumeral(t); 
                this.numModalitats = u.getNumModalitats();
            }
        }
    }

    /**
     * Constructora para crear una respuesta textual Múltiple.
     * @param persona_as Persona que responde
     * @param encuesta_as Encuesta a la que pertenece
     * @param pregunta_as Pregunta que se responde
     * @param v Conjunto de valores textuales seleccionados
     */
    public Textual(Persona persona_as,  Encuesta encuesta_as, Pregunta pregunta_as, Set<String> v) 
    {
        super(persona_as, encuesta_as, pregunta_as);
        this.valorUnic = null;
        this.valorsMultiples = v;
    }

     /**
     * Constructora para crear una respuesta textual vacía (sin responder).
     * @param persona_as Persona que responde
     * @param encuesta_as Encuesta a la que pertenece
     * @param pregunta_as Pregunta que se responde
     */
    public Textual(Persona persona_as,  Encuesta encuesta_as, Pregunta pregunta_as) 
    {
        super(persona_as, encuesta_as, pregunta_as);
        this.valorUnic = null;
        this.valorsMultiples = null;
        // 3 - cualitativa ordenada
        if (pregunta_as.getTipo() == 3) 
        {
            if (this.pregunta_as instanceof Unica) 
            {
                Unica u = (Unica) this.pregunta_as;
                this.numModalitats = u.getNumModalitats();
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
     
    /**
     * Obtiene el valor de la respuesta.
     * @return Set<String> si es respuesta múltiple, String si es única
     */
    @Override
    public Object getValor() 
    {
        if (this.pregunta_as instanceof Multiple) return valorsMultiples;
        else return valorUnic;
    }

    /**
     * Calcula la distancia entre esta respuesta y otra respuesta
     * El método varía según el tipo de pregunta
     * @param r Otra respuesta con la que calcular la distancia
     * @return Valor de distancia entre 0.0 (idénticas) y 1.0 (completamente diferentes)
     */
    @Override
    public double distance(Respuesta r) 
    { 
        Textual altra = (Textual) r;
        if(altra == null) return 1.0;
        int tipus = pregunta_as.getTipo();
        // 3- cualitativa ordenada
        if (tipus == 3) 
        {
            Integer numeralA = this.numeral;
            Integer numeralB = altra.numeral;

            if (numeralA == null || numeralB == null) 
            {
                if (numeralA == null && numeralB == null) return 0.0;
                return 1.0;
            }
            if (this.numModalitats- 1 == 0) return 0.0;
            return (double) Math.abs(numeralA - numeralB) /(this.numModalitats - 1);
        
        }
        // 4- cualitativa no ordenada
        else if (tipus == 4) 
        {
            if (this.pregunta_as instanceof Unica)
            {
                String valorA = this.valorUnic;
                String valorB = (String) altra.getValor();
                if (valorA == null || valorB == null) 
                {
                    if (valorA == null && valorB == null) return 0.0;
                    return 1.0;
                }
                return valorA.equals(valorB) ? 0.0 : 1.0;
            }
            else if (this.pregunta_as instanceof Multiple)
            {
                Set<String> valorsA = this.valorsMultiples;
                Set<String> valorsB = (Set<String>) altra.getValor();
                if (valorsA == null || valorsB == null) 
                {
                    if (valorsA == null && valorsB == null) return 0.0;
                    return 1.0;
                }
                Set<String> interseccio = new HashSet<>(valorsA);
                interseccio.retainAll(valorsB);
                Set<String> unio = new HashSet<>(valorsA);
                unio.addAll(valorsB);
                if (unio.isEmpty()) return 0.0;
                double jaccard = (double) interseccio.size() / unio.size();
                return 1.0 - jaccard;
            }
            else if (this.pregunta_as instanceof Libre)
            {
                String valorA = this.valorUnic;
                String valorB = (String) altra.getValor();
                if (valorA == null || valorB == null) 
                {
                    if (valorA == null && valorB == null) return 0.0;
                    return 1.0;
                }
                return 0.0;
            }
        }
        return 0.0;
    }
    /**
     * Establece el valor único de la respuesta textual.
     * @param valor El texto a asignar
     */
    public void setValorUnic(String valor) { this.valorUnic = valor; }

    /**
     * Establece múltiples valores para la respuesta textual.
     * @param valores Conjunto de valores a asignar
     */
    public void setValorsMultiples(Set<String> valores) { this.valorsMultiples = valores; }
    
}