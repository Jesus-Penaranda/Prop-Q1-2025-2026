package dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cjt_Respuestas
{
    ///////////////////////////////////////////////////////////////////////////
    ///                             ATRIBUTOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    
    private HashMap<String, ArrayList<Respuesta>> cjt_resp; // Conjunto de respuestas de una encuesta en formato matriz
    private HashSet<String> no_preg; // Conjunto con los enunciados de las preguntas que no queremos incluir en el cluster

    ///////////////////////////////////////////////////////////////////////////
    ///                         CONSTRUCTORAS                               ///
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructora vacía
     */
    public Cjt_Respuestas() 
    {
        cjt_resp = new HashMap<>();
        no_preg = new HashSet<>();
    }

    /**
     * Constructora con parámetro
     * @param cjt_resp Conjunto de respuestas de una encuesta
     */
    public Cjt_Respuestas(HashMap<String, ArrayList<Respuesta>> cjt_resp) 
    {
        this.cjt_resp = new HashMap<>(cjt_resp);
        no_preg = new HashSet<>();
    }

    /**
     * Constructora con parámetros
     * @param cjt_resp Conjunto de respuestas de una encuesta
     * @param no_p Conjunto de respuestas que no se quieren incluir en el clustering
     */
    public Cjt_Respuestas(HashMap<String, ArrayList<Respuesta>> cjt_resp, HashSet<String> no_p) 
    {
        this.cjt_resp = new HashMap<>(cjt_resp);
        no_preg = new HashSet<>(no_p);
    }
    ///////////////////////////////////////////////////////////////////////////
    ///                         MET. PUBLICOS                               ///
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Añadir respuesta de una Persona 
     * @param r respuesta asociada a una pregunta de una encuesta específica
     * @param nombre identidicador de la persona
    */
    public void add_resp(Respuesta r, String nombre) 
    {
        ArrayList<Respuesta> lista = cjt_resp.get(nombre);
        if (lista == null) 
        {
            lista = new ArrayList<>();
            cjt_resp.put(nombre, lista);
        }
        lista.add(r);
    }

    /**
     * Añadir respuestas a ignorar para el clustering 
     * @param no Conjunto con los enunciados de las preguntas que queremos rechazar
    */
    public void addNoPreg(HashSet<String> no) { this.no_preg = no; }

    /**
     * Añadir conjunto de respuestas de una Persona 
     * @param nombre identidicador de la persona
     * @param r conjunto de respuestas
     * */
    public void add_all_resp(String nombre, ArrayList<Respuesta> r) 
    {
        cjt_resp.put(nombre, r);
    }


    /**
     * Imprimir Respuestas de una encuesta 
     * @param nombre identidicador de la persona
    */
    public void print_respuestas(String nombre) 
    {
        ArrayList<Respuesta> respuestas = cjt_resp.get(nombre);
        if (respuestas == null) return;
        else 
        {
            for (Respuesta a : respuestas) 
            {
                System.out.println(a.getValor());
            }
        }
    }
    /**
     * Getter de la matriz necesaria para el cálculo del clustering
     * @return Matriz con las respuestas de cada persona de la encuesta
     */
    public HashMap<String, ArrayList<Respuesta>> getMatriz() 
    { 
        HashMap<String, ArrayList<Respuesta>> matriz = new HashMap<>();
        for (Map.Entry<String, ArrayList<Respuesta>> entry : cjt_resp.entrySet()) 
        {
            String nombrePersona = entry.getKey();        
            ArrayList<Respuesta> r = entry.getValue();    
            ArrayList<Respuesta> nueva_resp = new ArrayList<>();
            for (Respuesta resp : r) 
            {
                if (no_preg == null || !no_preg.contains(resp.getPregunta().getEnunciado())) nueva_resp.add(resp);
            }
            matriz.put(nombrePersona, nueva_resp);
        }
        return matriz; 
    }

    /**
     * Getter de los identificadores de todas las personas que han respondido a una encuesta específica
     * @return Lista de todos los identificadores
     */
    public ArrayList<String> getPersonas() 
    { 
        return new ArrayList<String>(this.cjt_resp.keySet()); 
    }

    /**
     * Getter de las respuestas de la persona identificada por id
     * @return Lista de las respuestas de esa persona
     */
    public ArrayList<Respuesta> getRespuestasPersona(String id) 
    { 
        return cjt_resp.get(id); 
    }

    /**
     * Calcula la media de un conjunto de respuestas de la misma columna
     * @param respuestas ArrayList con respuestas de la misma pregunta
     * @return Una Respuesta que representa el centroide
     */
    public static Respuesta calcularMedia(ArrayList<Respuesta> respuestas) 
    {
        if (respuestas == null || respuestas.isEmpty()) return null;
        int tipoPregunta = respuestas.get(0).getPregunta().getTipo();
        if (tipoPregunta == 1 || tipoPregunta == 2) return calcularMediaNumerica(respuestas);
        else if (tipoPregunta == 3 || tipoPregunta == 4) return calcularModaTextual(respuestas);
        else return null;
    }

    /**
     * Calcula la media aritmética para respuestas numéricas.
     * Tipos: 1-cuantitativa ordenada, 2-cuantitativa no ordenada
     * @param respuestas ArrayList con respuestas de la misma pregunta
     */
    private static Respuesta calcularMediaNumerica(ArrayList<Respuesta> respuestas) 
    {
        double suma = 0.0;
        int count = 0;
        Numerica primeraNum = (Numerica) respuestas.get(0);
        for (Respuesta r : respuestas) 
        {
            Numerica num = (Numerica) r;
            suma += (Double) num.getValor();
            count++;
        }
        double media = suma / count;
        Numerica resultado = new Numerica( primeraNum.getPersona(), primeraNum.getEncuesta(), primeraNum.getPregunta());
        resultado.setValorUnic(media);
        return resultado;
    }

    /**
     * Calcula la moda para respuestas textuales (categóricas).
     * Tipos: 3 (cualitativa ordenada), 4 (cualitativa no ordenada)
     * @param respuestas ArrayList con respuestas de la misma pregunta
     * @return Respuesta con la media de la columna
     */
    private static Respuesta calcularModaTextual(ArrayList<Respuesta> respuestas) 
    {
        Textual primeraText = (Textual) respuestas.get(0);
        Object valor = primeraText.getValor(); // Hay que mirar si es múltiple, única o libre
        if (valor instanceof String) return calcularModaSimple(respuestas);
        else if (valor instanceof Set)return calcularModaMultiple(respuestas);
        else return null; // Libre
    }

    /**
     * Moda simple: encuentra el valor más frecuente.
     * @param respuestas ArrayList con respuestas de la misma pregunta
     */
    private static Respuesta calcularModaSimple(ArrayList<Respuesta> respuestas) 
    {
        HashMap<String, Integer> frecuencias = new HashMap<>();
        for (Respuesta r : respuestas) 
        {
            Textual txt = (Textual) r;
            String valor = (String) txt.getValor();
            if (frecuencias.containsKey(valor)) frecuencias.put(valor, frecuencias.get(valor) + 1);
            else frecuencias.put(valor, 1);
        }
        String moda = null;
        int maxFreq = 0;
        for (Map.Entry<String, Integer> entry : frecuencias.entrySet()) 
        {
            if (entry.getValue() > maxFreq) 
            {
                maxFreq = entry.getValue();
                moda = entry.getKey();
            }
        }
        Textual primeraText = (Textual) respuestas.get(0);
        Textual resultado = new Textual(primeraText.getPersona(),primeraText.getEncuesta(),primeraText.getPregunta() );
        resultado.setValorUnic(moda);
        return resultado;
    }

    /**
     * Moda múltiple: encuentra el conjunto de valores más frecuente.
     * @param respuestas ArrayList con respuestas de la misma pregunta
     */
    private static Respuesta calcularModaMultiple(ArrayList<Respuesta> respuestas) 
    {
        HashMap<String, Integer> frecuenciasItems = new HashMap<>();
        for (Respuesta r : respuestas) 
        {
            Set<String> valores = (Set<String>) r.getValor();
            if (valores != null) 
            {
                for (String item : valores) frecuenciasItems.put(item, frecuenciasItems.getOrDefault(item, 0) + 1);
            }
        }
        int maxFreq = 0;
        for (Integer freq : frecuenciasItems.values()) if (freq > maxFreq) maxFreq = freq;
        Set<String> moda = new HashSet<>();
        if (maxFreq > 0) 
        {
            for (Map.Entry<String, Integer> entry : frecuenciasItems.entrySet()) if (entry.getValue() == maxFreq) moda.add(entry.getKey());
        }

        Textual primeraText = (Textual) respuestas.get(0);
        Textual resultado = new Textual(primeraText.getPersona(), primeraText.getEncuesta(), primeraText.getPregunta());
        resultado.setValorsMultiples(moda);
        return resultado;
    }

    /**
     * Imprime el identificador de la persona que representa un cluster especifico
     * @param rep Persona representativa
     */
    public void print_repsentante(String rep) 
    {
        System.out.println(rep);
    }

    /**
     * Imprime las respuestas de la persona que representa un cluster especifico
     * @param rep Persona representativa
     */
    public void print_respuestas_repsentante(String rep) 
    {
        if (rep == null) return;
        ArrayList<Respuesta> resp = cjt_resp.get(rep);
        boolean primero = true;
        for (Respuesta r : resp) 
        {
            if ((r.getPregunta() instanceof Libre) || (r.getPregunta() instanceof Unica)) 
            {
                if (primero) 
                {
                    System.out.print(String.valueOf(r.getValor()));
                    primero = false;
                }
                else System.out.print("," + String.valueOf(r.getValor()));
            }
            else if (r.getPregunta() instanceof Multiple)
            {
                Object valor = r.getValor();
                String valorStr = "";
                if (valor instanceof Set) 
                {
                    Set<?> conjunto = (Set<?>) valor;
                    StringBuilder sb = new StringBuilder();
                    boolean primerElemento = true;
                    for (Object elemento : conjunto) 
                    {
                        if (!primerElemento) sb.append(":");
                        sb.append(elemento.toString());
                        primerElemento = false;
                    }
                    valorStr = sb.toString();
                }
                else valorStr = String.valueOf(valor);
                if (primero) 
                {
                    System.out.print(valorStr);
                    primero = false;
                }
                else System.out.print("," + valorStr);
            }
        }
        System.out.println(); // Salto de línea al final
    }

    /**
     * Creación de los perfiles de un cluster
     * @param k
     * @return Array de Perfiles correspondientes a un cluster
     */
    public ArrayList<Perfil> crear_perfiles(IEstrategiaAlgoritmo k) 
    {

        int num_centr = k.getK(); 
        ArrayList<Perfil> perfiles = new ArrayList<>();
        ArrayList<ArrayList<String>> asignaciones = k.getInfoAsignaciones();
        for (int i = 0; i < num_centr; i++) 
        {
            ArrayList<String> nombres = asignaciones.get(i);
            ArrayList<Respuesta> centroide = k.getCentroides().get(i);
            perfiles.add(new Perfil(nombres, centroide, this));
        }   
        return perfiles;
    }
}