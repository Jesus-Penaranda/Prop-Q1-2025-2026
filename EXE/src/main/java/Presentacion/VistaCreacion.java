package Presentacion;

import dominio.ctrlDominio.DatosPregunta;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class VistaCreacion extends JPanel 
{
    private ctrlPresentacion ctrl;
    private ArrayList<DatosPregunta> listaPreguntas;
    private DefaultListModel<DatosPregunta> listModel;
    
    private JTextField txtTituloEncuesta; 
    private JTextField txtEnunciado;
    private JComboBox<String> cmbTipoP;
    private JComboBox<String> cmbTipoR;

    private final Color COLOR_GRADIENT_START = new Color(102, 126, 234);
    private final Color COLOR_GRADIENT_END = new Color(118, 75, 162);
    private final Color COLOR_TEXTO_INPUT = new Color(30, 30, 30);

    public VistaCreacion(ctrlPresentacion ctrl) 
    {
        this.ctrl = ctrl;
        this.listaPreguntas = new ArrayList<>();
        this.listModel = new DefaultListModel<>();
        inicializarComponentes();
    }

    private void inicializarComponentes() 
    {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel panelHeader = new GradientPanel();
        panelHeader.setLayout(new BorderLayout());
        panelHeader.setBorder(new EmptyBorder(25, 40, 25, 40));
        
        JLabel lblTitulo = new JLabel("Crear Nueva Encuesta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE); 
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        add(panelHeader, BorderLayout.NORTH);

        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(new Color(245, 247, 250));
        panelContenido.setBorder(new EmptyBorder(20, 40, 20, 40));
        JPanel panelTitulo = new JPanel(new BorderLayout(10, 5));
        panelTitulo.setOpaque(false);
        panelTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JLabel lblNombre = new JLabel("Título de la Encuesta:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(new Color(80, 80, 80));
        
        txtTituloEncuesta = new JTextField();
        estilizarInput(txtTituloEncuesta);
        txtTituloEncuesta.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Letra más grande para el título
        
        panelTitulo.add(lblNombre, BorderLayout.NORTH);
        panelTitulo.add(txtTituloEncuesta, BorderLayout.CENTER);
        
        panelContenido.add(panelTitulo);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20))); // Separador
        JPanel panelCuerpo = new JPanel(new GridLayout(1, 2, 30, 0));
        panelCuerpo.setOpaque(false);
        JPanel panelIzq = new JPanel(new BorderLayout(0, 10));
        panelIzq.setOpaque(false);
        JLabel lblSub = new JLabel("Preguntas Añadidas");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(new Color(80, 80, 80));
        
        JList<DatosPregunta> jList = new JList<>(listModel);
        jList.setCellRenderer(new PreguntaRenderer());
        JScrollPane scroll = new JScrollPane(jList);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        panelIzq.add(lblSub, BorderLayout.NORTH);
        panelIzq.add(scroll, BorderLayout.CENTER);
        JPanel panelDer = new JPanel(new BorderLayout());
        panelDer.setOpaque(false);
        
        JPanel cardForm = new JPanel(new GridBagLayout()) 
        {
            @Override protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,15)); g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth()-6, getHeight()-6, 20, 20);
            }
        };
        cardForm.setOpaque(false);
        cardForm.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0); gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0;
        JLabel lblAdd = new JLabel("Añadir Pregunta");
        lblAdd.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAdd.setForeground(COLOR_GRADIENT_END);
        gbc.gridwidth = 2; cardForm.add(lblAdd, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5,0,5,0);
        
        cardForm.add(crearLabelInput("Enunciado:"), gbc);
        txtEnunciado = new JTextField(); estilizarInput(txtEnunciado);
        cardForm.add(txtEnunciado, gbc);

        gbc.insets = new Insets(15,0,5,0);
        cardForm.add(crearLabelInput("Tipo Pregunta:"), gbc);
        cmbTipoP = new JComboBox<>(new String[]{"Libre", "Múltiple", "Única"}); 
        estilizarCombo(cmbTipoP);
        cardForm.add(cmbTipoP, gbc);

        cardForm.add(crearLabelInput("Tipo Respuesta:"), gbc);
        cmbTipoR = new JComboBox<>(new String[]{"Cuantitativa", "Cualitativa ordenada", "Cualitativa no ordenada"}); 
        estilizarCombo(cmbTipoR);
        cardForm.add(cmbTipoR, gbc);

        gbc.insets = new Insets(30, 0, 0, 0);
        JButton btnAdd = crearBotonGradiente("Añadir Pregunta", new Color(46, 204, 113), new Color(39, 174, 96));
        cardForm.add(btnAdd, gbc);
        gbc.insets = new Insets(10, 0, 0, 0);
        JButton btnImportarPreguntas = crearBotonSecundario("Importar Preguntas", new Color(52, 152, 219));
        cardForm.add(btnImportarPreguntas, gbc);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton btnVolver = crearBotonSimple("Cancelar");
        JButton btnGuardar = crearBotonGradiente("GUARDAR ENCUESTA", COLOR_GRADIENT_START, COLOR_GRADIENT_END);
        
        panelBotones.add(btnVolver);
        panelBotones.add(btnGuardar);
        panelDer.add(cardForm, BorderLayout.NORTH);
        panelDer.add(panelBotones, BorderLayout.SOUTH);
        panelCuerpo.add(panelIzq);
        panelCuerpo.add(panelDer);
        panelContenido.add(panelCuerpo);
        add(panelContenido, BorderLayout.CENTER);
        btnAdd.addActionListener(e -> agregarPregunta());
        btnImportarPreguntas.addActionListener(e -> importarPreguntas());
        btnGuardar.addActionListener(e -> {
            String titulo = txtTituloEncuesta.getText().trim();
            if (titulo.isEmpty()) 
            {
                mostrarError("Por favor, ponle un título a la encuesta");
                return;
            }
            if (listaPreguntas.isEmpty()) 
            {
                mostrarError("Añade al menos una pregunta.");
                return;
            }
            try 
            {
                int id = ctrl.crearEncuesta(listaPreguntas);    
                JOptionPane.showMessageDialog(this, "Encuesta '" + titulo + "' creada con ID: " + id + "!");
                listaPreguntas.clear(); listModel.clear(); txtTituloEncuesta.setText("");
                ctrl.mostrarVista(ctrlPresentacion.VISTA_PRINCIPAL);
            } 
            catch (Exception ex) {mostrarError("Error: " + ex.getMessage());}
        });

        btnVolver.addActionListener(e -> ctrl.mostrarVista(ctrlPresentacion.VISTA_PRINCIPAL));
    }

    private void estilizarInput(JTextField t) 
    {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(COLOR_TEXTO_INPUT); t.setBackground(Color.WHITE);
        t.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200), 1, true), new EmptyBorder(8, 10, 8, 10)));
    }
    private void estilizarCombo(JComboBox c) 
    {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        c.setBackground(Color.WHITE); 
        c.setForeground(COLOR_TEXTO_INPUT);
    }
    private JLabel crearLabelInput(String t) 
    {
        JLabel l = new JLabel(t); 
        l.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
        l.setForeground(Color.GRAY); 
        return l;
    }
    private JButton crearBotonSecundario(String texto, Color colorBase) 
    {
        JButton btn = new JButton(texto) 
        {
            private boolean hover = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() 
                {
                    public void mouseEntered(java.awt.event.MouseEvent e) 
                    {
                        hover = true;
                        repaint();
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) 
                    {
                        hover = false;
                        repaint();
                    }
                });
            }
            @Override 
            protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color1 = hover ? colorBase.brighter() : colorBase;
                Color color2 = hover ? colorBase : colorBase.darker();
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    private void importarPreguntas() 
    {
        String nombreArchivo = JOptionPane.showInputDialog(this, "Nombre del archivo (sin .csv):");
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) return;
        
        try 
        {
            Persistencia.GestorPreguntas gestor = new Persistencia.GestorPreguntas();
            ArrayList<dominio.Pregunta> preguntasImportadas = gestor.cargarConjunto(nombreArchivo.trim(), null);
            for (dominio.Pregunta p : preguntasImportadas) 
            {
                List<String> opciones = ctrl.getOpcionesPregunta(p);
                DatosPregunta dp = new DatosPregunta(
                    p.getEnunciado(), 
                    p.getTipoPregunta(), 
                    p.getTipo(), 
                    opciones
                );
                listaPreguntas.add(dp);
                listModel.addElement(dp);
            }
            
            JOptionPane.showMessageDialog(this, "Preguntas importadas: " + preguntasImportadas.size());
        } 
        catch (Exception ex) 
        {
            mostrarError("Error al importar: " + ex.getMessage());
        }
    }
    private JButton crearBotonGradiente(String t, Color c1, Color c2) 
    {
        JButton b = new JButton(t) 
        {
            @Override protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,c1,getWidth(),0,c2));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10); 
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE); 
        b.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        b.setContentAreaFilled(false);
        b.setFocusPainted(false); 
        b.setBorderPainted(false); 
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        b.setPreferredSize(new Dimension(180, 40));
        return b;
    }
    private JButton crearBotonSimple(String t) 
    {
        JButton b = new JButton(t); 
        b.setBackground(Color.WHITE); 
        b.setForeground(Color.GRAY); 
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new LineBorder(new Color(200,200,200),1,true)); 
        b.setFocusPainted(false); 
        b.setPreferredSize(new Dimension(100, 40));
        return b;
    }
    private void mostrarError(String m) { JOptionPane.showMessageDialog(this, m, "Faltan datos", JOptionPane.WARNING_MESSAGE); }
    private void agregarPregunta() 
    {
        String e = txtEnunciado.getText().trim(); if(e.isEmpty()){mostrarError("Escribe enunciado");return;}
        List<String> op = new ArrayList<>();
        if (cmbTipoP.getSelectedIndex()>0)
        {
            String i = JOptionPane.showInputDialog("Opciones (;):"); 
            if (i!=null) for (String s:i.split(";")) if(!s.trim().isEmpty()) op.add(s.trim());
        }
        int tipoR;
        if (cmbTipoR.getSelectedIndex() == 0) tipoR = (cmbTipoP.getSelectedIndex() == 0) ? 1 : 2; // Cuantitativa: 1 si Libre, 2 si Múltiple
        else tipoR = cmbTipoR.getSelectedIndex() + 2; // Cualitativa ordenada = 3 y la no ordenada = 4
        DatosPregunta dp = new DatosPregunta(e, cmbTipoP.getSelectedIndex()+1, cmbTipoR.getSelectedIndex()+ 1, op);
        listaPreguntas.add(dp); 
        listModel.addElement(dp); 
        txtEnunciado.setText("");
    }
    class GradientPanel extends JPanel 
    {
        @Override protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g); Graphics2D g2=(Graphics2D)g;
            g2.setPaint(new GradientPaint(0,0,COLOR_GRADIENT_START,getWidth(),getHeight(),COLOR_GRADIENT_END));
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    }
    class PreguntaRenderer extends JPanel implements ListCellRenderer<DatosPregunta> 
    {
        JLabel l1=new JLabel(), l2=new JLabel();
        public PreguntaRenderer()
        {
            setLayout(new BorderLayout()); setBorder(new CompoundBorder(new EmptyBorder(5,5,5,5), new MatteBorder(0,0,1,0,new Color(230,230,230))));
            setBackground(Color.WHITE);
            l1.setFont(new Font("Segoe UI", Font.BOLD, 14)); l2.setFont(new Font("Segoe UI", Font.PLAIN, 12)); l2.setForeground(Color.GRAY);
            add(l1,BorderLayout.NORTH); add(l2,BorderLayout.SOUTH);
        }
        public Component getListCellRendererComponent(JList l, DatosPregunta v, int i, boolean s, boolean f) 
        {
            l1.setText((i+1)+". "+v.enunciado); l2.setText("Tipo: "+v.tipoP);
            setBackground(s?new Color(235,245,255):Color.WHITE); return this;
        }
    }
}