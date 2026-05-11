package dominio;

import java.util.*;
import java.io.*;
import Persistencia.ctrlPersistencia;

public class ctrlDominio 
{
    public static class DatosPregunta 
    {
        public String enunciado;
        public Integer tipoP, tipoR;
        public List<String> opciones;
        public DatosPregunta(String e, Integer p, Integer r, List<String> o) {this.enunciado = e; this.tipoP = p; this.tipoR = r; this.opciones = o;}
    }
    
    private ctrlPersistencia controlPersistencia;
    private Map<Integer, Encuesta> cjtEncuestas;
    private Map<Integer, Cjt_Respuestas> cjtRespuestas;
    private Map<Integer, ArrayList<Perfil>> resultadosClustering;
    private Map<Integer, Double> indicesCalidad = new HashMap<>();
    private Integer idE;
    private String usuarioLogueado = null;
    private Map<String, List<Integer>> mapaUsuarios; // String con el id del usuario
    private Map<String, String> mapaCredenciales;   // Contraseñas 
    private final String usuarios_archivos = "usuarios_encuestas.txt";

    public ctrlDominio() 
    {
        cjtEncuestas = new HashMap<>();
        cjtRespuestas = new HashMap<>();
        resultadosClustering = new HashMap<>();
        this.indicesCalidad = new HashMap<>();
        idE = 1;
        controlPersistencia = new ctrlPersistencia();
        mapaUsuarios = new HashMap<>();
        mapaCredenciales = new HashMap<>();
        cargarMapaUsuarios();
    }
    
    public String getUsuarioLogeado() { return this.usuarioLogueado; }

    public int login(String usuario, String password) 
    {
        if (!mapaCredenciales.containsKey(usuario)) 
        {
            return 0; // Usuario no existe
        }
        if (mapaCredenciales.get(usuario).equals(password)) 
        {
            this.usuarioLogueado = usuario;
            return 2; // Login exitoso
        }
        return 1; // Contraseña incorrecta
    }

    public boolean registrar(String usuario, String password) 
    {
        if (mapaCredenciales.containsKey(usuario)) return false;
        mapaCredenciales.put(usuario, password);
        mapaUsuarios.put(usuario, new ArrayList<>());
        guardarMapaUsuarios();
        this.usuarioLogueado = usuario;
        return true;
    }



    public Integer crearEncuesta(ArrayList<DatosPregunta> dp) throws Exception 
    {
        Integer nuevoIdE = idE++;
        while (cjtEncuestas.containsKey(nuevoIdE)){
            nuevoIdE++;
            idE++;
        }
        Encuesta e = new Encuesta(nuevoIdE);
        ArrayList<Pregunta> pregs = listaPreguntas(e, dp);
        e.setPreguntas(pregs);

        cjtEncuestas.put(nuevoIdE, e);
        cjtRespuestas.put(nuevoIdE, new Cjt_Respuestas());
        controlPersistencia.guardar_encuesta(e);

        if (usuarioLogueado != null) 
        {
            List<Integer> misEncuestas = mapaUsuarios.get(usuarioLogueado);
            if (misEncuestas == null) misEncuestas = new ArrayList<>();
            misEncuestas.add(nuevoIdE);
            mapaUsuarios.put(usuarioLogueado, misEncuestas);
            guardarMapaUsuarios(); // Guardamos en disco al instante
        }
        
        return nuevoIdE;
    }

    public List<Integer> getListaIdsEncuestas() 
    {
        // Modo admin
        if (usuarioLogueado == null) return new ArrayList<>(cjtEncuestas.keySet());
        List<Integer> misIDs = mapaUsuarios.get(usuarioLogueado);
        if (misIDs == null) return new ArrayList<>();

        List<Integer> idsValidos = new ArrayList<>();
        
        for (Integer id : misIDs) 
        {
            if (!cjtEncuestas.containsKey(id)) 
            {
                try 
                {
                    Encuesta e = controlPersistencia.cargar_encuesta(id);
                    if (e != null) 
                    {
                        cjtEncuestas.put(id, e);
                        cjtRespuestas.put(id, new Cjt_Respuestas());
                        try 
                        {
                            Cjt_Respuestas respuestasGuardadas = controlPersistencia.cargar_respuestas(e);
                            if (respuestasGuardadas != null) cjtRespuestas.put(id, respuestasGuardadas);
                        } 
                        catch (Exception ex) {}
                    }
                } 
                catch (Exception ex) 
                {
                    System.out.println("Aviso: La encuesta " + id + " figura en el perfil pero no se encuentra el archivo");
                    continue; 
                }
            }
            if (cjtEncuestas.containsKey(id)) idsValidos.add(id);
        }
        return idsValidos;
    }
    
    public Integer importarEncuesta(Integer idEncuesta) throws Exception 
    {

        if(cjtEncuestas.containsKey(idEncuesta)) throw new IllegalStateException("Ya existe una encuesta con el ID " + idEncuesta);
        Encuesta e = controlPersistencia.cargar_encuesta(idEncuesta);
        if (e == null) throw new IllegalStateException("No se ha encontrado la encuesta con ID " + idEncuesta);
        cjtEncuestas.put(idEncuesta, e);
        cjtRespuestas.put(idEncuesta, new Cjt_Respuestas());
        controlPersistencia.guardar_encuesta(e);

        if (usuarioLogueado != null) 
        {
            List<Integer> misEncuestas = mapaUsuarios.get(usuarioLogueado);
            if (!misEncuestas.contains(idEncuesta)) 
            {
                misEncuestas.add(idEncuesta);
                guardarMapaUsuarios();
            }
        }
        return idEncuesta;
    }

    private void cargarMapaUsuarios() 
    {
        File f = new File(usuarios_archivos);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) 
        {
            String linea;
            while ((linea = br.readLine()) != null) 
            {
                String[] partes = linea.split(",");
                if (partes.length >= 2) 
                {
                    String usuario = partes[0];
                    String password = partes[1];
                    mapaCredenciales.put(usuario, password); 
                    ArrayList<Integer> ids = new ArrayList<>();
                    for (int i = 2; i < partes.length; i++) 
                    {
                        try { ids.add(Integer.parseInt(partes[i]));} 
                        catch(Exception e){}
                    }
                    mapaUsuarios.put(usuario, ids);
                }
            }
        } 
        catch (Exception e) { System.out.println("Error cargando usuarios: " + e.getMessage());}
    }

    private void guardarMapaUsuarios() 
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter(usuarios_archivos))) 
        {
            for (Map.Entry<String, List<Integer>> entry : mapaUsuarios.entrySet()) 
            {
                String usuario = entry.getKey();
                String password = mapaCredenciales.get(usuario);
                pw.print(usuario + "," + password);  
                for (Integer id : entry.getValue()) pw.print("," + id);
                pw.println();
            }
        } 
        catch (Exception e) {System.out.println("Error guardando usuarios: " + e.getMessage());}
    }

     private ArrayList<Pregunta> listaPreguntas(Encuesta e, ArrayList<DatosPregunta> dp) throws Exception 
     {
        ArrayList<Pregunta> ps = new ArrayList<Pregunta>();
        for(DatosPregunta datos : dp) 
            {
            if(datos.tipoR < 1 || datos.tipoR > 4) throw new IllegalArgumentException("El tipoR " + datos.tipoR + " no es válido.");
            Pregunta p = null;
            switch (datos.tipoP) 
            {
                case 1: p = new Libre(e.nextIdP(), datos.enunciado, e, datos.tipoP, datos.tipoR); break;
                case 2: p = new Multiple(e.nextIdP(), datos.enunciado, e, datos.tipoP, datos.tipoR, datos.opciones); break;
                case 3: p = new Unica(e.nextIdP(), datos.enunciado, e, datos.tipoP, datos.tipoR, datos.opciones); break;
                default: throw new IllegalArgumentException("El tipoP " + datos.tipoP + " no es válido.");
            }
            ps.add(p);
        }
        return ps;
    }
    
    public void guardarEncuesta(Integer idEncuesta) throws Exception 
    {
        Encuesta encuesta = cjtEncuestas.get(idEncuesta);
        if(encuesta == null) throw new IllegalStateException("La encuesta con ID " + idEncuesta + " no existe.");
        controlPersistencia.guardar_encuesta(encuesta);
    }
    
    public void guardarEncuestaEditada(Integer id, ArrayList<DatosPregunta> nuevasPreguntas) throws Exception 
    {
        Encuesta e = new Encuesta(id);
        ArrayList<Pregunta> ps = listaPreguntas(e, nuevasPreguntas);
        e.setPreguntas(ps);
        cjtEncuestas.put(id, e);   
        controlPersistencia.guardar_encuesta(e);    
        cjtRespuestas.put(id, new Cjt_Respuestas());  
    }
    
    public void eliminarEncuesta(Integer id) throws Exception 
    {
        if (!cjtEncuestas.containsKey(id)) throw new Exception("La encuesta " + id + " no existe");
        cjtEncuestas.remove(id);
        cjtRespuestas.remove(id);
        if (usuarioLogueado != null && mapaUsuarios.containsKey(usuarioLogueado)) 
        {
            mapaUsuarios.get(usuarioLogueado).remove(id);
            guardarMapaUsuarios();
        }
    }
    
    public Encuesta getEncuesta(int id) { return cjtEncuestas.get(id); }
    public Cjt_Respuestas getRespuestas(int id) { return this.cjtRespuestas.get(id); }
    public boolean tieneResultadosClustering(int id) { return resultadosClustering.containsKey(id); }
    
    public List<String> getOpcionesPregunta(Pregunta p) 
    {
        List<String> ops = new ArrayList<String>();
        if (p instanceof Multiple) ops = ((Multiple)p).getOpciones();
        else if (p instanceof Unica) ops = ((Unica)p).getOpciones();
        return ops;
    }

    private boolean esNumero(String texto) {
        if (texto == null || texto.isEmpty()) return false;
        try {
            if (texto.contains(";")) {
                String[] cadena = texto.split(";");
                for(String s : cadena) Double.parseDouble(s.trim());    // Se usa trim por si hay algun espacio
            }
            else Double.parseDouble(texto.trim()); 
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /**
     * CASO DE USO: responder encuesta
     * @param idEncuesta Identificador de la encuesta que responde la encuesta
     * @param idUsuario Identificador de la persona que responde la encuesta
     * @param respuestas Lista de preguntas de la encuesta respondidas por la persona en formato String
     * @throws Exception Si una respuesta de la lista es de otra encuesta
     */
    public void responderEncuesta(Integer idEncuesta, String idUsuario, List<String> respuestasTexto) throws Exception {
        Encuesta encuesta = cjtEncuestas.get(idEncuesta);
        if (encuesta == null) {
            throw new Exception("La encuesta con ID " + idEncuesta + " no existe.");
        }

        List<Pregunta> preguntas = encuesta.getPreguntes();

        if (respuestasTexto.size() != preguntas.size()) {
            throw new Exception("El número de respuestas no coincide con el número de preguntas.");
        }

        ArrayList<Respuesta> listaRespuestasObj = new ArrayList<>();
        Respuesta r = null;

        for (int i = 0; i < preguntas.size(); i++) {
            Pregunta p = preguntas.get(i);
            String textoRespuesta = respuestasTexto.get(i);
            
            if(esNumero(textoRespuesta)) {
                if (textoRespuesta.isEmpty()) r = new Numerica(new Persona(idUsuario), encuesta, p); 
                else if (p instanceof Multiple) {
                    Set<Double> respMul = new HashSet<Double>();
                    String[] mults = textoRespuesta.split(";");
                    for (String s : mults) respMul.add(Double.parseDouble(s));

                    r = new Numerica(new Persona(idUsuario), encuesta, p, respMul);

                } else if (p instanceof Libre || p instanceof Unica) {
                    r = new Numerica(new Persona(idUsuario), encuesta, p, Double.parseDouble(textoRespuesta));
                }

            } else {
                if (textoRespuesta.isEmpty()) r = new Textual(new Persona(idUsuario), encuesta, p);
                else if (p instanceof Multiple) {
                    Set<String> respMul = new HashSet<String>();
                    String[] mults = textoRespuesta.split(";");
                    for (String s : mults) respMul.add(s);

                    r = new Textual(new Persona(idUsuario), encuesta, p, respMul);

                } else if (p instanceof Unica || p instanceof Libre) {
                    r = new Textual(new Persona(idUsuario), encuesta, p, textoRespuesta);
                }
            }

            listaRespuestasObj.add(r);
        }

        if (!cjtRespuestas.containsKey(idEncuesta)) {
            cjtRespuestas.put(idEncuesta, new Cjt_Respuestas());
        }
        
        cjtRespuestas.get(idEncuesta).add_all_resp(idUsuario, listaRespuestasObj);

        controlPersistencia.guardar_respuestas(cjtRespuestas.get(idEncuesta), encuesta);
    }

    /**
     * CASO DE USO: responder encuesta
     * @param idEncuesta Identificador de la encuesta que responde la encuesta
     * @param idPersona Identificador de la persona que responde la encuesta
     * @param respuestas Lista de preguntas de la encuesta respondidas por la persona
     * @throws Exception Si una respuesta de la lista es de otra encuesta
     */
    public void responderEncuesta(Integer idEncuesta, String idPersona, ArrayList<Respuesta> respuestas) throws Exception {
        for (Respuesta r : respuestas) if (r.getEncuesta().getId() != idEncuesta) throw new Exception("Todas las respuestas deben ser de la misma encuesta");
        
        cjtRespuestas.get(idEncuesta).add_all_resp(idPersona, respuestas);
    }

    /**
     * CASO DE USO: guardar respuestas
     * @param idEncuesta Identificador de la encuesta
     * @throws Exception
     */
    public void guardarRespuestas(Integer idEncuesta) throws Exception {
        if(cjtRespuestas.get(idEncuesta).getMatriz().size() == 0) throw new Exception("La encuesta " + idEncuesta + " no tiene respuestas");
        
        controlPersistencia.guardar_respuestas(cjtRespuestas.get(idEncuesta), cjtEncuestas.get(idEncuesta));
    }


    
    /**
    * CASO DE USO: Analizar encuesta
    * Ejecuta el algoritmo de clustering k-means y almacena el resultado
    * @param idEncuesta Identificador de la encuesta a analizar 
    * @param k Numero de perfiles a obtener en el clustering
    * @param tipoIni Tipo de inicialización para el calculo de clusters
    * @param no_preg Set los enunciados de las preguntas que no se tendran en cuenta para el analisis
    * @throws Exception Si no hay respuestas o k es inválido.
    */
    public void analizarEncuesta(Integer idEncuesta, int k, String clustering, String tipoIni, HashSet<String> no_preg) throws Exception 
    {
        Cjt_Respuestas respuestas = cjtRespuestas.get(idEncuesta);
        respuestas.addNoPreg(no_preg);
        if (respuestas == null || respuestas.getMatriz().isEmpty()) throw new Exception("No hay respuestas para analizar en la encuesta " + idEncuesta);
        Encuesta e = cjtEncuestas.get(idEncuesta);
        if (e != null) 
        {
            ArrayList<Pregunta> preguntas = e.getPreguntes();
            Map<String, ArrayList<Respuesta>> matriz = respuestas.getMatriz();
            if (preguntas != null && matriz != null) 
            {
                // En lugar de iterar por índice, iteramos por cada fila de la matriz filtrada
                for (ArrayList<Respuesta> fila : matriz.values()) 
                {
                    if (fila == null) continue;
                    for (int col = 0; col < fila.size(); col++) 
                    {
                        Respuesta r = fila.get(col);
                        if (r == null) continue;
                        Pregunta pg = r.getPregunta();
                        if (pg == null || pg.getTipo() == null || pg.getTipo() != 1) continue;
                        Object val = r.getValor();
                        if (val instanceof Number) 
                        {
                            double v = ((Number) val).doubleValue();
                            if (!Double.isFinite(v)) continue;
                            Double currentMin = pg.getVmin();
                            Double currentMax = pg.getVmax();
                            if (currentMin == null) 
                            {
                                pg.setVmin(v);
                                pg.setVmax(v);
                            } 
                            else 
                            {
                                if (v < currentMin) pg.setVmin(v);
                                if (v > currentMax) pg.setVmax(v);
                            }
                        }
                    }
                }
            }
        }
        if (respuestas.getMatriz().size() < k) throw new Exception("Se requieren al menos " + k + " respuestas, pero solo hay " + respuestas.getMatriz().size());
        IEstrategiaAlgoritmo algoritmo;
        IEstrategiaIni estrategiaIni;
        
        if (clustering.equals("K-MEANS")) 
        {
            if (tipoIni.equalsIgnoreCase("K-MEANSPP")) estrategiaIni = new IniKmeansPP();
            else if (tipoIni.equalsIgnoreCase("TRIVIAL")) estrategiaIni = new IniTrivial();
            else throw new Exception("Tipo de inicialización desconocido");
            algoritmo = new Kmeans(respuestas, estrategiaIni, k);
        }
        else if (clustering.equals("K-MEDOIDES")) algoritmo = new Kmedoides(respuestas, k);
        else if (clustering.equals("CLUSTERSUB")) 
        {
            IniKmeansPP ini = new IniKmeansPP();
            algoritmo = new ClusteringSub(respuestas, ini, k);
        } 
        else throw new Exception("El algoritmo de clustering introducido es incorrecto");
        
        algoritmo.iniciaAlgoritmo();

        ArrayList<Perfil> resultado = respuestas.crear_perfiles(algoritmo);

        resultadosClustering.put(idEncuesta, resultado);
        indicesCalidad.put(idEncuesta, algoritmo.getIndiceCalidad());
    }

     
    public ArrayList<Perfil> obtenerPerfiles(Integer idEncuesta) throws Exception 
    {
        if (resultadosClustering.get(idEncuesta) == null) return new ArrayList<>(); // Evitar null
        return resultadosClustering.get(idEncuesta);
    }

    public double obtenerCalidadClustering(Integer idEncuesta) throws Exception {
        if (!indicesCalidad.containsKey(idEncuesta)) throw new Exception("La encuesta no ha sido analizada");
        return indicesCalidad.get(idEncuesta);
    }

    public String obtenerRepresentante(Perfil p) { return p.getRepresentante(); }
    public ArrayList<String> obtenerListaIndividuos(Perfil p) { return p.getListaIndividuos(); }
    
    public void importarPerfiles(Integer idEncuesta) throws Exception {
        ArrayList<Perfil> perfiles = controlPersistencia.cargar_perfiles(idEncuesta.toString());
        resultadosClustering.put(idEncuesta, perfiles); // Corregido el put
    }

    public void exportarPerfiles(Integer idEncuesta) throws Exception{
        ArrayList<Perfil> perfiles = resultadosClustering.get(idEncuesta);
        controlPersistencia.guardar_perfiles(perfiles, idEncuesta.toString());
    }
    
    public void importarRespuestas(Integer idEncuesta) throws Exception {
        Cjt_Respuestas resps = controlPersistencia.cargar_respuestas(cjtEncuestas.get(idEncuesta));
        cjtRespuestas.put(idEncuesta, resps);
    }

    public Map<Integer, Double> calcularElbow(Integer idEncuesta, int kMax, String clustering, String tipoIni, HashSet<String> no_preg) throws Exception 
    {
        Cjt_Respuestas respuestas = cjtRespuestas.get(idEncuesta);
        if (respuestas == null || respuestas.getMatriz().isEmpty()) 
        {
            throw new Exception("No hay respuestas disponibles para calcular K óptimo. Por favor, importa o añade respuestas primero.");
        }
        
        int numRespuestas = respuestas.getMatriz().size();
        if (numRespuestas < 2)
        {
            throw new Exception("Se requieren al menos 2 respuestas para calcular el K óptimo. Actualmente hay " + numRespuestas + " respuesta(s).");
        }
        int maxK = Math.min(kMax, numRespuestas);
        if (maxK < 2) maxK = 2;
        Map<Integer, Double> resultados = new LinkedHashMap<>();
        for (int k = 2; k <= maxK; k++) 
        {
            try 
            {
                Cjt_Respuestas copiaRespuestas = cjtRespuestas.get(idEncuesta);
                copiaRespuestas.addNoPreg(no_preg);
                
                IEstrategiaAlgoritmo algoritmo;
                IEstrategiaIni estrategiaIni;
                
                if (clustering.equals("K-MEANS")) 
                {
                    if (tipoIni.equalsIgnoreCase("K-MEANSPP")) estrategiaIni = new IniKmeansPP();
                    else estrategiaIni = new IniTrivial();
                    algoritmo = new Kmeans(copiaRespuestas, estrategiaIni, k);
                } 
                else if (clustering.equals("K-MEDOIDES")) algoritmo = new Kmedoides(copiaRespuestas, k);
                else if (clustering.equals("CLUSTERSUB")) algoritmo = new ClusteringSub(copiaRespuestas, new IniKmeansPP(), k);
                else throw new Exception("Tipo de clustering desconocido: " + clustering);
                algoritmo.iniciaAlgoritmo();
                double calidad = algoritmo.getIndiceCalidad();

                if (Double.isFinite(calidad)) 
                {
                    resultados.put(k, calidad);
                }
            } 
            catch (Exception e) 
            {
                System.out.println("No se pudo calcular para k = " + k + ": " + e.getMessage());
            }
        }
        
        if (resultados.isEmpty()) 
        {
            throw new Exception("No se pudo calcular para ningún valor de k");
        }
        int kOptimo = encontrark(resultados);
        resultados.put(-1, (double) kOptimo);
        
        return resultados;
    }

    private int encontrark(Map<Integer, Double> calidades) 
    {
        if (calidades.isEmpty()) return 2;
        ArrayList<Integer> ks = new ArrayList<>(calidades.keySet());
        Collections.sort(ks);
        int kOptimo = ks.get(0);
        double maxCalidad = calidades.get(kOptimo);
        
        for (Integer k : ks) 
        {
            double calidad = calidades.get(k);
            if (calidad > maxCalidad) 
            {
                maxCalidad = calidad;
                kOptimo = k;
            }
        }
        return kOptimo;
    }

    public void importarPreguntas(Integer idEncuesta, String nombreArchivo) throws Exception 
    {
        Encuesta encuesta = cjtEncuestas.get(idEncuesta);
        if (encuesta == null) throw new IllegalStateException("La encuesta con ID " + idEncuesta + " no existe");
        ArrayList<Pregunta> preguntasImportadas = controlPersistencia.cargar_preguntas(nombreArchivo, encuesta);
        if (preguntasImportadas == null || preguntasImportadas.isEmpty()) 
        {
            throw new IllegalStateException("No se encontraron preguntas en el archivo: " + nombreArchivo);
        }
        
        ArrayList<Pregunta> preguntasActuales = encuesta.getPreguntes();
        preguntasActuales.addAll(preguntasImportadas);
        encuesta.setPreguntas(preguntasActuales);
        controlPersistencia.guardar_encuesta(encuesta);
    }

    
}