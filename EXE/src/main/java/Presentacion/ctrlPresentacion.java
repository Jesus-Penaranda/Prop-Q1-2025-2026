package Presentacion;

import dominio.ctrlDominio;
import dominio.ctrlDominio.DatosPregunta;
import dominio.Encuesta; 
import dominio.Pregunta; 
import dominio.Perfil; 
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.*;

public class ctrlPresentacion 
{
    private ctrlDominio controlDominio;
    private JFrame framePrincipal;
    private JPanel panelContenidos;
    private CardLayout cardLayout;

    private VistaPrincipal vistaPrincipal;
    private VistaCreacion vistaCreacion;
    private VistaResponder vistaResponder;
    private VistaEdicion vistaEdicion;
    private VistaInfoPerfiles vistaInfoPerfiles;
    private VistaAnalisis vistaAnalisis;

    private VistaLogin vistaLogin; 
    private Integer idEncuestaActual = null; 
    
    public static final String VISTA_PRINCIPAL = "Principal";
    public static final String VISTA_CREACION = "Creacion";
    public static final String VISTA_RESPONDER = "Responder";
    public static final String VISTA_EDICION = "Edicion";
    public static final String VISTA_INFO_PERFILES = "InfoPerfiles";
    public static final String VISTA_ANALISIS = "Analisis";
    public static final String VISTA_LOGIN = "Login";

    public ctrlPresentacion() {controlDominio = new ctrlDominio();}

    public void importarPreguntas(Integer id, String nombreArchivo) throws Exception {controlDominio.importarPreguntas(id, nombreArchivo);}
    public List<String> getOpcionesPregunta(dominio.Pregunta p) {return controlDominio.getOpcionesPregunta(p);}

    public void inicializarPresentacion() 
    {
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (Exception ignored) {}

        vistaAnalisis = new VistaAnalisis(this);
        framePrincipal = new JFrame("Gestión de Encuestas PROP");
        cardLayout = new CardLayout();
        panelContenidos = new JPanel(cardLayout);
        vistaPrincipal = new VistaPrincipal(this);
        vistaCreacion = new VistaCreacion(this);
        vistaResponder = new VistaResponder(this);
        vistaEdicion = new VistaEdicion(this);
        vistaInfoPerfiles = new VistaInfoPerfiles(this);
        vistaLogin = new VistaLogin(this);
        panelContenidos.add(vistaPrincipal, VISTA_PRINCIPAL);
        panelContenidos.add(vistaCreacion, VISTA_CREACION);
        panelContenidos.add(vistaResponder, VISTA_RESPONDER);
        panelContenidos.add(vistaEdicion, VISTA_EDICION);
        panelContenidos.add(vistaInfoPerfiles, VISTA_INFO_PERFILES);
        panelContenidos.add(vistaLogin, VISTA_LOGIN);
        panelContenidos.add(vistaAnalisis, VISTA_ANALISIS);

        framePrincipal.add(panelContenidos);
        framePrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePrincipal.setResizable(true);
        framePrincipal.setSize(1300, 900); 
        framePrincipal.setLocationRelativeTo(null);
        framePrincipal.setVisible(true);
        mostrarVista(VISTA_LOGIN);
    }

    public void mostrarVista(String nombreVista) 
    {
        if (nombreVista.equals(VISTA_PRINCIPAL)) vistaPrincipal.actualizarListaEncuestas(); 
        else if (nombreVista.equals(VISTA_EDICION)) vistaEdicion.cargarDatosEncuesta();
        else if (nombreVista.equals(VISTA_RESPONDER)) vistaResponder.actualizarNombreUsuario();
        cardLayout.show(panelContenidos, nombreVista);
    }

    public Map<Integer, Double> calcularKOptimo(Integer id, int kMax, String alg, String ini, HashSet<String> no_preg) throws Exception 
    {
        return controlDominio.calcularElbow(id, kMax, alg, ini, no_preg);
    }
   
    public void setIdEncuestaActual(Integer id) {this.idEncuestaActual = id;}
    public Integer getIdEncuestaActual() { return this.idEncuestaActual; }
    public List<Integer> obtenerListaEncuestas() {return controlDominio.getListaIdsEncuestas();}

    public void importarEncuesta(Integer id) throws Exception 
    {
        controlDominio.importarEncuesta(id);
        this.idEncuestaActual = id; 
        mostrarVista(VISTA_PRINCIPAL);
    }

    public void eliminarEncuesta(Integer id) throws Exception 
    {
        controlDominio.eliminarEncuesta(id);
        if (id.equals(idEncuestaActual)) idEncuestaActual = null;
        vistaPrincipal.actualizarListaEncuestas();
    }

    public Integer crearEncuesta(ArrayList<DatosPregunta> datos) throws Exception 
    {
        Integer id = controlDominio.crearEncuesta(datos);
        this.idEncuestaActual = id;
        return id;
    }

    public void loginUsuario(String usuario, String password) 
    {
        int resultado = controlDominio.login(usuario, password);
        if (resultado == 2) 
        {
            mostrarVista(VISTA_PRINCIPAL);
            NotisBonitas.show(vistaLogin, "Bienvenido de nuevo, " + usuario + "!", true);
            vistaResponder.actualizarNombreUsuario();
        }
        else if (resultado == 0) NotisBonitas.show(vistaLogin, "El usuario no existe", false);
        else NotisBonitas.show(vistaLogin, "Contraseña incorrecta", false);
    }

    public void registrarUsuario(String usuario, String password) 
    {
        boolean exito = controlDominio.registrar(usuario, password);
        if (exito) 
        {
            NotisBonitas.show(vistaLogin, "Cuenta creada correctamente", true);
            Timer timer = new Timer(1000, e -> mostrarVista(VISTA_PRINCIPAL));
            timer.setRepeats(false);
            timer.start();
        } 
        else
        {
            NotisBonitas.show(vistaLogin, "El usuario ya existe", false);
        }
    }

    public ArrayList<DatosPregunta> obtenerPreguntasDeEncuesta(Integer id) 
    {
        ArrayList<DatosPregunta> listaDatos = new ArrayList<>();   
        Encuesta e = controlDominio.getEncuesta(id);
        if (e != null) 
        {
            for (Pregunta p : e.getPreguntes()) 
            {
                List<String> opciones = controlDominio.getOpcionesPregunta(p);
                listaDatos.add(new DatosPregunta(p.getEnunciado(), p.getTipoPregunta(), p.getTipo(), opciones)); 
            }
        }
        return listaDatos;
    }

    public void actualizarEncuestaExistente(Integer id, ArrayList<DatosPregunta> nuevasPreguntas) throws Exception {controlDominio.guardarEncuestaEditada(id, nuevasPreguntas); }
    
    public void procesarRespuestasUsuario(String idUsuario, Integer idEncuesta, ArrayList<String> respuestasTexto) throws Exception {
        controlDominio.responderEncuesta(idEncuesta, idUsuario, respuestasTexto);
    }

    public void importarRespuestas(Integer id) throws Exception { controlDominio.importarRespuestas(id);}

    public void analizarEncuesta(Integer id, int k, String alg, String ini, HashSet<String> no_preg) throws Exception 
    {
        controlDominio.analizarEncuesta(id, k, alg, ini, no_preg);
        this.idEncuestaActual = id; 
    }

    public ArrayList<Perfil> obtenerPerfiles(Integer id) throws Exception 
    {
        return controlDominio.obtenerPerfiles(id);
    }

    public double obtenerCalidad(Integer id) throws Exception 
    {
        return controlDominio.obtenerCalidadClustering(id);
    }
    
    public String obtenerRepresentante(Perfil p) {return controlDominio.obtenerRepresentante(p);}
    
    public ArrayList<String> obtenerIndividuos(Perfil p) { return controlDominio.obtenerListaIndividuos(p); }

    public void importarPerfiles(Integer id) throws Exception {controlDominio.importarPerfiles(id);}    
    public void exportarPerfiles(Integer id) throws Exception {controlDominio.exportarPerfiles(id);}

    public JFrame getFrame() { return framePrincipal; }

    public String getUsuarioLogeado() { return controlDominio.getUsuarioLogeado(); }
}