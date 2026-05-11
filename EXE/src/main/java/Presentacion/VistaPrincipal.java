package Presentacion;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class VistaPrincipal extends JPanel 
{
    private ctrlPresentacion ctrl;
    private JPanel panelListaEncuestas;
    
    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private final Color COLOR_GRADIENT_START = new Color(102, 126, 234);
    private final Color COLOR_GRADIENT_END = new Color(118, 75, 162);
    private final Color COLOR_BTN_RESPONDER = new Color(52, 152, 219); 
    private final Color COLOR_BTN_ANALIZAR = new Color(155, 89, 182);  
    private final Color COLOR_BTN_EDITAR = new Color(241, 196, 15);    
    private final Color COLOR_BTN_BORRAR = new Color(231, 76, 60);     
    private final Color HEADER_BTN_MAIN_BG = Color.WHITE;
    private final Color HEADER_BTN_MAIN_FG = new Color(102, 126, 234); 
    
    private final Color HEADER_BTN_HELP_BG = new Color(46, 204, 113); 
    private final Color HEADER_BTN_HELP_FG = Color.WHITE;
    private final Color HEADER_BTN_EXIT_BG = new Color(231, 76, 60);
    private final Color HEADER_BTN_EXIT_FG = Color.WHITE;

    public VistaPrincipal(ctrlPresentacion ctrl) 
    {
        this.ctrl = ctrl;
        inicializarComponentes();
    }

    private void inicializarComponentes() 
    {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        JPanel panelHeader = new GradientPanel();
        panelHeader.setLayout(new BorderLayout()); 
        panelHeader.setBorder(new EmptyBorder(25, 40, 25, 40));
        JLabel titulo = new JLabel("Gestión de Encuestas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        JButton btnCrear = crearBotonHeader("+ Crear", HEADER_BTN_MAIN_BG, HEADER_BTN_MAIN_FG);
        JButton btnImportar = crearBotonHeader("Importar", HEADER_BTN_MAIN_BG, HEADER_BTN_MAIN_FG);
        JButton btnAyuda = crearBotonHeader("Ayuda", HEADER_BTN_HELP_BG, HEADER_BTN_HELP_FG);
        JButton btnSalir = crearBotonHeader("Salir", HEADER_BTN_EXIT_BG, HEADER_BTN_EXIT_FG);
        
        panelBotones.add(btnCrear);
        panelBotones.add(btnImportar);
        panelBotones.add(btnAyuda);
        panelBotones.add(btnSalir);

        panelHeader.add(titulo, BorderLayout.WEST);
        panelHeader.add(panelBotones, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);
        panelListaEncuestas = new JPanel();
        panelListaEncuestas.setLayout(new BoxLayout(panelListaEncuestas, BoxLayout.Y_AXIS));
        panelListaEncuestas.setBackground(COLOR_FONDO);
        panelListaEncuestas.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JScrollPane scroll = new JScrollPane(panelListaEncuestas);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        btnCrear.addActionListener(e -> ctrl.mostrarVista(ctrlPresentacion.VISTA_CREACION));

        btnImportar.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Introduce el ID de la encuesta a importar:");
            if (input != null && !input.trim().isEmpty()) 
            {
                try { 
                    ctrl.importarEncuesta(Integer.parseInt(input.trim()));
                    NotisBonitas.show(this, "✓ Encuesta importada correctamente", true);
                    Timer timer = new Timer(1000, ev -> actualizarListaEncuestas());
                    timer.setRepeats(false);
                    timer.start();
                } 
                catch (Exception ex) {
                    NotisBonitas.show(this, "Error: " + ex.getMessage(), false);
                }
            }
        });

        btnAyuda.addActionListener(e -> mostrarAyuda());

        btnSalir.addActionListener(e -> {
            Object[] options = {"Si, Salir", "Cancelar"};
            int confirm = JOptionPane.showOptionDialog(this, "Cerrar aplicación?", "Salir",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        actualizarListaEncuestas();
    }

    public void actualizarListaEncuestas() 
    {
        panelListaEncuestas.removeAll();
        List<Integer> encuestas = ctrl.obtenerListaEncuestas();
        if (encuestas.isEmpty()) 
        {
            panelListaEncuestas.add(Box.createVerticalGlue());
            JLabel lblVacio = new JLabel("No hay encuestas disponibles. Crea o importa una.");
            lblVacio.setForeground(Color.GRAY);
            lblVacio.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblVacio.setAlignmentX(CENTER_ALIGNMENT);
            panelListaEncuestas.add(lblVacio);
            panelListaEncuestas.add(Box.createVerticalGlue());
        } 
        else 
        {
            for (Integer id : encuestas) 
            {
                panelListaEncuestas.add(crearTarjetaEncuesta(id));
                panelListaEncuestas.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        }
        panelListaEncuestas.revalidate();
        panelListaEncuestas.repaint();
    }

    private JPanel crearTarjetaEncuesta(Integer id) 
    {
        JPanel card = new JPanel(new BorderLayout()) 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 15, 15);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 15, 15);
                g2.setColor(COLOR_GRADIENT_START);
                g2.fillRoundRect(0, 0, 10, getHeight() - 5, 15, 15);
                g2.fillRect(5, 0, 5, getHeight() - 5);
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setBorder(new EmptyBorder(10, 25, 10, 15));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        JLabel lblTitulo = new JLabel("Encuesta #" + id);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(50, 50, 50));
        JLabel lblSub = new JLabel("Estado: Activa");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        infoPanel.add(lblTitulo);
        infoPanel.add(lblSub);
        card.add(infoPanel, BorderLayout.WEST);

        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        accionesPanel.setOpaque(false);

        JButton btnResponder = crearBtnAccion("Responder", COLOR_BTN_RESPONDER);
        JButton btnAnalizar = crearBtnAccion("Analizar", COLOR_BTN_ANALIZAR);
        JButton btnEditar = crearBtnAccion("Editar", COLOR_BTN_EDITAR);
        JButton btnBorrar = crearBtnAccion("🗑️", COLOR_BTN_BORRAR);
        btnBorrar.setPreferredSize(new Dimension(40, 32));

         btnResponder.addActionListener(e -> { ctrl.setIdEncuestaActual(id); ctrl.mostrarVista(ctrlPresentacion.VISTA_RESPONDER); });
        btnAnalizar.addActionListener(e ->  { ctrl.setIdEncuestaActual(id); ctrl.mostrarVista(ctrlPresentacion.VISTA_ANALISIS); });
        btnEditar.addActionListener(e -> { ctrl.setIdEncuestaActual(id); ctrl.mostrarVista(ctrlPresentacion.VISTA_EDICION); });        
        btnBorrar.addActionListener(e -> {
            try 
            { 
                ctrl.eliminarEncuesta(id);
                NotisBonitas.show(this, "✔️ Encuesta eliminada correctamente", true); // Emoji de google para que quede mas bonito
                Timer timer = new Timer(1000, ev -> actualizarListaEncuestas());
                timer.setRepeats(false);
                timer.start();
            } 
            catch (Exception ex) { NotisBonitas.show(this, "Error: " + ex.getMessage(), false);}
        });

        accionesPanel.add(btnResponder);
        accionesPanel.add(btnAnalizar);
        accionesPanel.add(btnEditar);
        accionesPanel.add(btnBorrar);

        card.add(accionesPanel, BorderLayout.EAST);
        return card;
    }

    private JButton crearBotonHeader(String texto, Color bg, Color fg) 
    {
        JButton b = new JButton(texto)
        {
            @Override protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Fuente ligeramente más pequeña para q quepan
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 35)); 
        return b;
    }

    private JButton crearBtnAccion(String t, Color c) 
    {
        JButton b = new JButton(t) 
        {
            @Override protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        b.setForeground(t.equals("Editar") ? new Color(50, 50, 50) : Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(100, 32));
        return b;
    }

    private void mostrarAyuda() 
    {
        JFrame frameAyuda = new JFrame("Ayuda - Manual de Diseño de Encuestas");
        frameAyuda.setSize(850, 700);
        frameAyuda.setLocationRelativeTo(this);
        frameAyuda.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setText(
            "<html><body style='font-family: Segoe UI, Sans-serif; padding: 25px; color: #333; font-size: 14px;'>" +
            
            "<h1 style='color: #2563eb; text-align: center; margin-bottom: 5px;'>Manual para el Diseño de Encuestas</h1>" +
            "<p style='text-align: center; font-style: italic; color: #666; margin-top: 0;'>Guía rápida para elegir la configuración correcta según los datos que quieres recoger.</p>" +
            "<hr style='border: 1px solid #ddd; margin: 20px 0;'>" +
            
            "<div style='background-color: #f3f4f6; padding: 10px; margin: 15px 0; border-radius: 5px;'>" +
            "<h2 style='color: #1f2937; margin: 5px 0;'>1. Tipo de Respuesta: Cuantitativa</h2>" +
            "</div>" +
            
            "<p><b>¿Cuándo usarla?</b> Cuando la respuesta esperada es un <b>número real con valor matemático</b>.</p>" +
            "<p style='margin-left: 20px;'><i>Ejemplos:</i> Edad, Sueldo, Altura, Número de hijos.</p>" +
            "<ul style='color: #555; margin-left: 40px;'>" +
            "<li>El sistema calculará la diferencia real (distancia) entre valores.</li>" +
            "<li>(Por ejemplo: Tener 20 años se parece más a tener 25 que a tener 60).</li>" +
            "</ul>" +

            "<div style='background-color: #f3f4f6; padding: 10px; margin: 15px 0; border-radius: 5px;'>" +
            "<h2 style='color: #1f2937; margin: 5px 0;'>2. Tipo de Respuesta: Cualitativa</h2>" +
            "</div>" +
            
            "<p>Úsala cuando quieras que el usuario seleccione <b>una única opción</b> de una lista predefinida.</p>" +
            
            "<h3 style='color: #2563eb; margin-top: 20px;'>A. No Ordenada (Nominal)</h3>" +
            "<p><b>¿Cuándo usarla?</b> Cuando las opciones son distintas pero <b>no tienen jerarquía</b>.</p>" +
            "<p style='margin-left: 20px;'><i>Ejemplos:</i></p>" +
            "<ul style='margin-left: 40px;'>" +
            "<li><b>Color favorito:</b> (Rojo, Verde, Azul).</li>" +
            "<li><b>Ciudad:</b> (Madrid, Barcelona, Valencia).</li>" +
            "</ul>" +
            "<p><b>¿Cómo funciona?</b> El sistema solo distingue igualdad o desigualdad. No calcula cercanía.</p>" +

            "<h3 style='color: #2563eb; margin-top: 20px;'>B. Ordenada (Ordinal)</h3>" +
            "<p><b>¿Cuándo usarla?</b> Cuando las opciones tienen un <b>rango, nivel o intensidad lógica</b>.</p>" +
            "<p style='margin-left: 20px;'><i>Ejemplos:</i></p>" +
            "<ul style='margin-left: 40px;'>" +
            "<li><b>Nivel de satisfacción:</b> (Bajo, Medio, Alto).</li>" +
            "<li><b>Frecuencia:</b> (Nunca, A veces, Siempre).</li>" +
            "</ul>" +
            "<p><b>¿Cómo funciona?</b> El sistema entiende la distancia lógica ('Bajo' está más cerca de 'Medio' que de 'Alto').</p>" +

            "<div style='background-color: #f3f4f6; padding: 10px; margin: 15px 0; border-radius: 5px;'>" +
            "<h2 style='color: #1f2937; margin: 5px 0;'>3. Tipo de Pregunta: Texto Libre</h2>" +
            "</div>" +
            
            "<p><b>¿Cuándo usarla?</b> Cuando no hay opciones y el usuario escribe libremente.</p>" +
            "<p style='margin-left: 20px;'><i>Ejemplo:</i> Nombre de pila, Comentarios breves.</p>" +
            "<p><b>¿Cómo funciona?</b> El sistema busca similitud gramatical (Distancia de Levenshtein).</p>" +

            "<div style='background-color: #f3f4f6; padding: 10px; margin: 15px 0; border-radius: 5px;'>" +
            "<h2 style='color: #1f2937; margin: 5px 0;'>4. Tipo de Pregunta: Múltiple (Conjuntos)</h2>" +
            "</div>" +
            
            "<p><b>¿Cuándo usarla?</b> Cuando el usuario puede marcar <b>varias casillas</b> a la vez.</p>" +
            "<p style='margin-left: 20px;'><i>Ejemplo:</i> ¿Qué aficiones tienes? (Deporte, Cine, Lectura...).</p>" +
            "<p><b>¿Cómo funciona?</b> El sistema usa el Índice de Jaccard para agrupar personas con conjuntos de intereses parecidos.</p>" +

            "<div style='background-color: #f3f4f6; padding: 10px; margin: 15px 0; border-radius: 5px;'>" +
            "<h2 style='color: #1f2937; margin: 5px 0;'>5. Tipo de Pregunta: Única</h2>" +
            "</div>" +
            
            "<p><b>¿Cuándo usarla?</b> Cuando el usuario puede marcar <b>una sola casilla</b> a la vez.</p>" +
            "<p style='margin-left: 20px;'><i>Ejemplo:</i> ¿Qué color te gusta más? (Rojo, Azul, Amarillo etc.).</p>" +
            "<p><b>¿Cómo funciona?</b> El sistema mapea las opciones a una <b>escala numérica normalizada</b> (de 0 a 1) para calcular matemáticamente la distancia proporcional entre la opción elegida y las demás.</p>" +

            "<br><hr style='border: 1px solid #ddd;'>" +
            "<p style='text-align: center; color: #888; font-size: 12px;'>Para más información, consulta la documentación completa del proyecto.</p>" +
            
            "</body></html>"
        );
        
        editorPane.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        frameAyuda.add(scrollPane);
        frameAyuda.setVisible(true);
    }
    

    class GradientPanel extends JPanel 
    {
        @Override protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, COLOR_GRADIENT_START, getWidth(), 0, COLOR_GRADIENT_END));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}