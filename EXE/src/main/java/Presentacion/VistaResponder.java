package Presentacion;

import dominio.ctrlDominio.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class VistaResponder extends JPanel 
{
    private ctrlPresentacion ctrl;
    
    private JPanel panelFormulario;
    private JLabel lblTituloEncuesta;
    private JLabel lblDescripcion;
    private JTextField txtNombreUsuario;
    private JScrollPane scrollPane;
    
    private List<Component> inputsRespuestas; 
    private List<DatosPregunta> preguntasActuales;
    
    // Paleta de colores moderna
    private final Color COLOR_FONDO = new Color(240, 242, 245);
    private final Color COLOR_PRIMARY = new Color(99, 102, 241);
    private final Color COLOR_PRIMARY_DARK = new Color(79, 70, 229);
    private final Color COLOR_SUCCESS = new Color(16, 185, 129);
    private final Color COLOR_DANGER = new Color(239, 68, 68);
    private final Color COLOR_INFO = new Color(59, 130, 246);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_TEXT_PRIMARY = new Color(31, 41, 55);
    private final Color COLOR_TEXT_SECONDARY = new Color(107, 114, 128);
    private final Color COLOR_BORDER = new Color(229, 231, 235);

    public VistaResponder(ctrlPresentacion ctrl) 
    {
        this.ctrl = ctrl;
        this.inputsRespuestas = new ArrayList<>();
        inicializarComponentes();
    }

    private void inicializarComponentes() 
    {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        JPanel panelHeader = crearHeaderModerno();
        add(panelHeader, BorderLayout.NORTH);
        panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(COLOR_FONDO);
        panelFormulario.setBorder(new EmptyBorder(30, 40, 30, 40));

        scrollPane = new JScrollPane(panelFormulario);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setBackground(COLOR_FONDO);
        personalizarScrollBar(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel panelSur = crearPanelBotonesModerno();
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearHeaderModerno() 
    {
        JPanel header = new ModernGradientPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(35, 50, 35, 50));
        
        // Contenedor izquierdo con título y descripción
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setOpaque(false);
        
        lblTituloEncuesta = new JLabel("Cargando Encuesta...");
        lblTituloEncuesta.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTituloEncuesta.setForeground(Color.WHITE);
        lblTituloEncuesta.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblDescripcion = new JLabel("Completa todas las preguntas para enviar tus respuestas");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDescripcion.setForeground(new Color(240, 240, 245));
        lblDescripcion.setBorder(new EmptyBorder(8, 0, 0, 0));
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panelIzq.add(lblTituloEncuesta);
        panelIzq.add(lblDescripcion);
        
        // Panel derecho con input de usuario estilizado
        JPanel panelDer = new JPanel();
        panelDer.setLayout(new BoxLayout(panelDer, BoxLayout.Y_AXIS));
        panelDer.setOpaque(false);
        
        JLabel lblUsuario = new JLabel("Tu Nombre");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        txtNombreUsuario = new JTextField(20);
        txtNombreUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtNombreUsuario.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(255, 255, 255, 100), 2, true),new EmptyBorder(10, 15, 10, 15)));
        txtNombreUsuario.setBackground(Color.WHITE);
        txtNombreUsuario.setForeground(COLOR_TEXT_PRIMARY);
        txtNombreUsuario.setCaretColor(COLOR_PRIMARY);
        txtNombreUsuario.setMaximumSize(new Dimension(300, 45));
        
        txtNombreUsuario.addFocusListener(new FocusAdapter() 
        {
            @Override
            public void focusGained(FocusEvent e) 
            {
                txtNombreUsuario.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.WHITE, 2, true), new EmptyBorder(10, 15, 10, 15)));
            }
            @Override
            public void focusLost(FocusEvent e) 
            {
                txtNombreUsuario.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(255, 255, 255, 100), 2, true),new EmptyBorder(10, 15, 10, 15)));
            }
        });
        
        panelDer.add(lblUsuario);
        panelDer.add(Box.createRigidArea(new Dimension(0, 8)));
        panelDer.add(txtNombreUsuario);
        
        header.add(panelIzq, BorderLayout.WEST);
        header.add(panelDer, BorderLayout.EAST);
        
        return header;
    }
       

    private JPanel crearPanelBotonesModerno() 
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, COLOR_BORDER),new EmptyBorder(20, 40, 20, 40)));
        
        // Botones izquierda
        JPanel panelIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelIzq.setOpaque(false);
        
        JButton btnImportar = crearBotonModerno("📁 Importar Respuestas", COLOR_INFO, false);
        panelIzq.add(btnImportar);
        
        // Botones derecha
        JPanel panelDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panelDer.setOpaque(false);
        
        JButton btnCancelar = crearBotonModerno("✕ Cancelar", COLOR_DANGER, false);
        JButton btnEnviar = crearBotonModerno("✓ Enviar Respuestas", COLOR_SUCCESS, true);
        
        panelDer.add(btnCancelar);
        panelDer.add(btnEnviar);
        
        panel.add(panelIzq, BorderLayout.WEST);
        panel.add(panelDer, BorderLayout.EAST);
        btnCancelar.addActionListener(e -> ctrl.mostrarVista(ctrlPresentacion.VISTA_PRINCIPAL));
        
        btnImportar.addActionListener(e -> {
            try 
            {
                ctrl.importarRespuestas(ctrl.getIdEncuestaActual());
                mostrarNotificacion("✓ Respuestas importadas correctamente", true);
                Timer timer = new Timer(1000, ev -> ctrl.mostrarVista(ctrlPresentacion.VISTA_PRINCIPAL));
                timer.setRepeats(false);
                timer.start();
            } 
            catch (Exception ex) 
            {
                mostrarNotificacion("Error: " + ex.getMessage(), false);
            }
        });
        btnEnviar.addActionListener(e -> enviarRespuestas());
        return panel;
    }

    private JButton crearBotonModerno(String texto, Color colorBase, boolean destacado) 
    {
        Color colorHover = colorBase.darker();
        JButton btn = new JButton(texto) 
        {
            private boolean hover = false;
            
            @Override
            protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = hover ? colorHover : colorBase;
                if (!destacado) 
                {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(bgColor);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                } 
                else 
                {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(2, 4, getWidth() - 2, getHeight() - 2, 10, 10);
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                super.paintComponent(g);
            }
        };
        
        btn.setForeground(destacado ? Color.WHITE : colorBase);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(destacado ? 220 : 200, 45));
        
        btn.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                JButton source = (JButton) e.getSource();
                source.putClientProperty("hover", true);
                source.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) 
            {
                JButton source = (JButton) e.getSource();
                source.putClientProperty("hover", false);
                source.repaint();
            }
        });
        return btn;
    }

    private void personalizarScrollBar(JScrollPane scroll) 
    {
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() 
        {
            @Override
            protected void configureScrollBarColors() 
            {
                this.thumbColor = new Color(180, 180, 200);
                this.trackColor = COLOR_FONDO;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) 
            {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) 
            {
                return createZeroButton();
            }
            
            private JButton createZeroButton() 
            {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) 
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, 8, thumbBounds.height, 8, 8);
            }
        });

        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
    }

    @Override 
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && ctrl.getIdEncuestaActual() != null) cargarPreguntas();
    }

    private void cargarPreguntas() 
    {
        panelFormulario.removeAll();
        inputsRespuestas.clear();
        
        Integer id = ctrl.getIdEncuestaActual();
        lblTituloEncuesta.setText("📋 Encuesta #" + id);
        
        try 
        {
            preguntasActuales = ctrl.obtenerPreguntasDeEncuesta(id);
        } 
        catch (Exception e) 
        {
            preguntasActuales = new ArrayList<>();
        }
        
        int total = preguntasActuales.size();
        lblDescripcion.setText(String.format("Completa las %d preguntas para enviar tus respuestas", total));
        int idx = 1;
        for (DatosPregunta p : preguntasActuales) 
        {
            JPanel tarjeta = crearTarjetaPreguntaModerna(p, idx, total);
            panelFormulario.add(tarjeta);
            panelFormulario.add(Box.createRigidArea(new Dimension(0, 20)));
            idx++;
        }
        panelFormulario.revalidate();
        panelFormulario.repaint();
    }

    private JPanel crearTarjetaPreguntaModerna(DatosPregunta p, int index, int total) 
    {
        JPanel card = new JPanel() 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra suave
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 2, 16, 16);
                
                // Fondo blanco
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Borde izquierdo de color
                g2.setColor(COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, 5, getHeight(), 16, 16);
            }
            
            @Override
            public Dimension getMaximumSize() 
            {
                return new Dimension(super.getMaximumSize().width, Integer.MAX_VALUE);
            }
        };
        
        card.setLayout(new BorderLayout(15, 15));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel headerCard = new JPanel(new BorderLayout());
        headerCard.setOpaque(false);
        
        JPanel numeroPregunta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        numeroPregunta.setOpaque(false);
        
        JLabel lblNumero = new JLabel(String.format("%d/%d", index, total));
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNumero.setForeground(COLOR_PRIMARY);
        lblNumero.setOpaque(true);
        lblNumero.setBackground(new Color(99, 102, 241, 15));
        lblNumero.setBorder(new EmptyBorder(5, 12, 5, 12));
        
        numeroPregunta.add(lblNumero);
        
        // Número de pregunta en la derecha
        JLabel lblNumeroDerecha = new JLabel("Pregunta " + index);
        lblNumeroDerecha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNumeroDerecha.setForeground(COLOR_TEXT_SECONDARY);
        
        headerCard.add(numeroPregunta, BorderLayout.WEST);
        headerCard.add(lblNumeroDerecha, BorderLayout.EAST);
        
        // Enunciado de la pregunta
        JLabel lblEnunciado = new JLabel("<html><div style='width: 100%;'>" + p.enunciado + "</div></html>");
        lblEnunciado.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblEnunciado.setForeground(COLOR_TEXT_PRIMARY);
        lblEnunciado.setBorder(new EmptyBorder(10, 0, 15, 0));
        
        // Contenedor central
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(headerCard, BorderLayout.NORTH);
        centro.add(lblEnunciado, BorderLayout.CENTER);
        
        card.add(centro, BorderLayout.NORTH);
        
        // Input según tipo de pregunta
        JComponent input = crearInputModerno(p);
        card.add(input, BorderLayout.CENTER);
        
        return card;
    }

    private JComponent crearInputModerno(DatosPregunta p) 
    {
        if (p.tipoP == 1) 
        {
            if (p.tipoR == 1) 
            {
                JTextField textField = new JTextField(20);
                textField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                textField.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(8, COLOR_BORDER), new EmptyBorder(12, 15, 12, 15)));
                textField.setBackground(new Color(249, 250, 251));
                textField.setForeground(COLOR_TEXT_PRIMARY);
                textField.addKeyListener(new java.awt.event.KeyAdapter() 
                {
                    public void keyTyped(java.awt.event.KeyEvent evt) 
                    {
                        char c = evt.getKeyChar();
                        String currentText = textField.getText();
                        if (!Character.isDigit(c) && c != '.' && c != '-' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) 
                        {
                            evt.consume();
                            return;
                        }
                        if (c == '.' && currentText.contains(".")) 
                        {
                            evt.consume();
                            return;
                        }
                        if (c == '-' && (currentText.length() > 0 || textField.getCaretPosition() > 0)) 
                        {
                            evt.consume();
                            return;
                        }
                    }
                });
                inputsRespuestas.add(textField);
                return textField;
            } 
            else 
            {
                JTextArea textArea = new JTextArea(4, 20);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                textArea.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(8, COLOR_BORDER),new EmptyBorder(12, 15, 12, 15)));
                textArea.setBackground(new Color(249, 250, 251));
                textArea.setForeground(COLOR_TEXT_PRIMARY);
                JScrollPane scroll = new JScrollPane(textArea);
                scroll.setBorder(null);
                scroll.setBackground(new Color(249, 250, 251));
                inputsRespuestas.add(textArea);
                return scroll;
            }
        } 
        else 
        {
            JPanel panelOpciones = new JPanel() 
            {
                @Override
                public Dimension getMaximumSize() 
                {
                    Dimension pref = getPreferredSize();
                    return new Dimension(Integer.MAX_VALUE, pref.height);
                }
            };
            panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
            panelOpciones.setOpaque(false);
            
            ButtonGroup grupo = (p.tipoP == 3) ? new ButtonGroup() : null;
            
            if (p.opciones != null) 
            {
                for (String opcion : p.opciones) 
                {
                    JPanel itemPanel = crearOpcionModerna(opcion, p.tipoP, grupo);
                    panelOpciones.add(itemPanel);
                    panelOpciones.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
            
            inputsRespuestas.add(panelOpciones);
            return panelOpciones;
        }
    }

    private JPanel crearOpcionModerna(String texto, int tipoP, ButtonGroup grupo) 
    {
        JPanel panel = new JPanel(new BorderLayout(12, 0)) 
        {
            private boolean hover = false;
            @Override
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bg = hover ? new Color(249, 250, 251) : Color.WHITE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2.setColor(COLOR_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }
            
            @Override
            public Dimension getMaximumSize() 
            {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE, pref.height);
            }
            
            @Override
            public Dimension getPreferredSize() 
            {
                Dimension d = super.getPreferredSize();
                d.width = Integer.MAX_VALUE;
                return d;
            }
        };
        
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JToggleButton boton = (tipoP == 3) ? new JRadioButton() : new JCheckBox();
        boton.setOpaque(false);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        if (grupo != null) grupo.add(boton);
        JLabel lblTexto = new JLabel("<html><div style='width: 700px;'>" + texto + "</div></html>");
        lblTexto.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTexto.setForeground(COLOR_TEXT_PRIMARY);
        panel.add(boton, BorderLayout.WEST);
        panel.add(lblTexto, BorderLayout.CENTER);
        MouseAdapter hoverListener = new MouseAdapter() 
        {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                panel.putClientProperty("hover", true);
                panel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) 
            {
                panel.putClientProperty("hover", false);
                panel.repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                boton.setSelected(!boton.isSelected());
            }
        };
        
        panel.addMouseListener(hoverListener);
        lblTexto.addMouseListener(hoverListener);
        
        return panel;
    }

    private void enviarRespuestas() 
    {
        String nombre = txtNombreUsuario.getText().trim();
        if (nombre.isEmpty()) 
        {
            mostrarNotificacion("Debes ingresar tu nombre de usuario", false);
            return;
        }
        try 
        {
            ArrayList<String> respuestas = new ArrayList<>();
            for (Component c : inputsRespuestas) 
            {
                if (c instanceof JTextArea) 
                {
                    respuestas.add(((JTextArea) c).getText());
                }
                else if (c instanceof JTextField) 
                {
                    String texto = ((JTextField) c).getText().trim();
                    if (texto.isEmpty()) 
                    {
                        mostrarNotificacion("Completa todas las preguntas numéricas", false);
                        return;
                    }
                    respuestas.add(texto);
                }
                else if (c instanceof JPanel) 
                {
                    List<String> seleccionadas = new ArrayList<>();
                    for (Component comp : ((JPanel) c).getComponents()) 
                    {
                        if (comp instanceof JPanel) 
                        {
                            for (Component inner : ((JPanel) comp).getComponents()) 
                            {
                                if (inner instanceof JToggleButton && ((JToggleButton) inner).isSelected()) 
                                {
                                    JPanel parentPanel = (JPanel) comp;
                                    for (Component sibling : parentPanel.getComponents()) 
                                    {
                                        if (sibling instanceof JLabel) 
                                        {
                                            String textoHtml = ((JLabel) sibling).getText();
                                            String textoLimpio = textoHtml.replaceAll("<[^>]*>", "").trim();
                                            seleccionadas.add(textoLimpio);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    respuestas.add(String.join(";", seleccionadas));
                }
            }
            
            ctrl.procesarRespuestasUsuario(nombre, ctrl.getIdEncuestaActual(), respuestas);
            mostrarNotificacion("Respuestas enviadas correctamente", true);
            
            Timer timer = new Timer(1500, e -> ctrl.mostrarVista(ctrlPresentacion.VISTA_PRINCIPAL));
            timer.setRepeats(false);
            timer.start();
            
        } 
        catch (Exception e) 
        {
            mostrarNotificacion("Error: " + e.getMessage(), false);
        }
    }

    private void mostrarNotificacion(String mensaje, boolean exito) 
    {
        NotisBonitas.show(this, mensaje, exito);
    }

    public void actualizarNombreUsuario() {   
        String user = ctrl.getUsuarioLogeado();
        if(user != null) {
            this.txtNombreUsuario.setText(user);
            this.txtNombreUsuario.repaint();
        }
    }
    
    class ModernGradientPanel extends JPanel 
    {
        @Override
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            GradientPaint gradient = new GradientPaint(
                0, 0, COLOR_PRIMARY,
                getWidth(), getHeight(), COLOR_PRIMARY_DARK
            );
            
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    class RoundedBorder extends AbstractBorder 
    {
        private int radius;
        private Color color;
        
        RoundedBorder(int radius, Color color) 
        {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) 
        {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        
        @Override
        public Insets getBorderInsets(Component c) 
        {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }
    }
}