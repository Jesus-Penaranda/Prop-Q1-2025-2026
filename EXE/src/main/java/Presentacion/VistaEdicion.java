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

public class VistaEdicion extends JPanel 
{
    private ctrlPresentacion ctrl;
    private ArrayList<DatosPregunta> listaPreguntas;
    private DefaultListModel<DatosPregunta> listModel;
    private JLabel lblTitulo;
    
    // Inputs
    private JTextField txtEnunciado;
    private JComboBox<String> cmbTipoP;
    private JComboBox<String> cmbTipoR;

    private final Color COLOR_GRADIENT_START = new Color(255, 153, 0);  // Naranja
    private final Color COLOR_GRADIENT_END   = new Color(255, 204, 51); // Amarillo Oro
    private final Color COLOR_TEXTO_INPUT    = new Color(30, 30, 30);

    public VistaEdicion(ctrlPresentacion ctrl) 
    {
        this.ctrl = ctrl;
        this.listaPreguntas = new ArrayList<>();
        this.listModel = new DefaultListModel<>();
        inicializarComponentes();
    }

    public void cargarDatosEncuesta() 
    {
        Integer id = ctrl.getIdEncuestaActual();
        if (id == null) return;
        lblTitulo.setText("Editando Encuesta #" + id);
        listaPreguntas = ctrl.obtenerPreguntasDeEncuesta(id);
        listModel.clear();
        for (DatosPregunta dp : listaPreguntas) listModel.addElement(dp);
    }

    private void inicializarComponentes() 
    {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 245));
        JPanel panelHeader = new GradientPanel();
        panelHeader.setLayout(new BorderLayout());
        panelHeader.setBorder(new EmptyBorder(25, 40, 25, 40));

        lblTitulo = new JLabel("Editar Encuesta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setUI(new javax.swing.plaf.basic.BasicLabelUI() 
        {
            @Override
            public void paint(Graphics g, JComponent c) 
            {
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(new Color(0,0,0,40));
                g.drawString(lblTitulo.getText(), 2, lblTitulo.getHeight()-2);
                super.paint(g, c);
            }
        });

        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        add(panelHeader, BorderLayout.NORTH);
        JPanel panelCuerpo = new JPanel(new GridLayout(1, 2, 30, 0));
        panelCuerpo.setBackground(new Color(250, 250, 245));
        panelCuerpo.setBorder(new EmptyBorder(30, 40, 30, 40));
        JPanel panelIzq = new JPanel(new BorderLayout(0, 15));
        panelIzq.setOpaque(false);
        JLabel lblLista = new JLabel("Preguntas Actuales");
        lblLista.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLista.setForeground(new Color(150, 100, 0));  
        
        JList<DatosPregunta> jList = new JList<>(listModel);
        jList.setCellRenderer(new PreguntaRenderer());
        JScrollPane scroll = new JScrollPane(jList);
        scroll.setBorder(new LineBorder(new Color(230, 220, 200))); 
        
        JButton btnBorrar = crearBotonSimple("Borrar Seleccionada");
        btnBorrar.setForeground(new Color(200, 50, 50));
        
        panelIzq.add(lblLista, BorderLayout.NORTH);
        panelIzq.add(scroll, BorderLayout.CENTER);
        panelIzq.add(btnBorrar, BorderLayout.SOUTH);
        JPanel panelDer = new JPanel(new BorderLayout());
        panelDer.setOpaque(false);
        
        JPanel cardForm = new JPanel(new GridBagLayout()) 
        {
            @Override protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,10));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-6, getHeight()-6, 20, 20);
            }
        };
        cardForm.setOpaque(false);
        cardForm.setBorder(new EmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0;
        
        JLabel lblAdd = new JLabel("Añadir / Modificar");
        lblAdd.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAdd.setForeground(COLOR_GRADIENT_START);
        gbc.gridwidth = 2; cardForm.add(lblAdd, gbc);
        
        gbc.gridwidth = 1; gbc.insets = new Insets(5,0,5,0);
        cardForm.add(crearLabelInput("Enunciado:"), gbc);
        txtEnunciado = new JTextField(); estilizarInput(txtEnunciado);
        cardForm.add(txtEnunciado, gbc);

        gbc.insets = new Insets(15,0,5,0);
        cardForm.add(crearLabelInput("Tipo:"), gbc);
        cmbTipoP = new JComboBox<>(new String[]{"Libre", "Múltiple", "Única"}); estilizarCombo(cmbTipoP);
        cardForm.add(cmbTipoP, gbc);

        cardForm.add(crearLabelInput("Respuesta:"), gbc);
        cmbTipoR = new JComboBox<>(new String[]{"Cuantitativa", "Cualitativa ordenada", "Cualitativa no ordenada"}); estilizarCombo(cmbTipoR);
        cardForm.add(cmbTipoR, gbc);

        gbc.insets = new Insets(30, 0, 0, 0);
        JButton btnAdd = crearBotonGradiente("Añadir a la lista");
        cardForm.add(btnAdd, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(20, 0, 0, 0));
        JButton btnGuardar = crearBotonGradiente("GUARDAR CAMBIOS");
        JButton btnCancelar = crearBotonSimple("Cancelar");
        panelBotones.add(btnCancelar); panelBotones.add(btnGuardar);

        panelDer.add(cardForm, BorderLayout.NORTH);
        panelDer.add(panelBotones, BorderLayout.SOUTH);
        panelCuerpo.add(panelIzq);
        panelCuerpo.add(panelDer);
        add(panelCuerpo, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> agregarPregunta());
        btnBorrar.addActionListener(e -> 
        {
            int idx = jList.getSelectedIndex();
            if (idx!=-1) {listaPreguntas.remove(idx); listModel.remove(idx);}
        });
        btnGuardar.addActionListener(e -> guardarEdicion());
        btnCancelar.addActionListener(e -> ctrl.mostrarVista(ctrlPresentacion.VISTA_PRINCIPAL));
    }

    private void estilizarInput(JTextField t) 
    {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(COLOR_TEXTO_INPUT);
        t.setBackground(Color.WHITE);
        t.setBorder(new CompoundBorder(new LineBorder(new Color(220, 220, 200)), new EmptyBorder(8,10,8,10)));
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
        l.setForeground(Color.GRAY); 
        l.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
        return l;
    }
    private JButton crearBotonGradiente(String t) 
    {
        JButton b = new JButton(t) 
        {
            @Override protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,COLOR_GRADIENT_START,getWidth(),0,COLOR_GRADIENT_END));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE); 
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setContentAreaFilled(false); 
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        b.setPreferredSize(new Dimension(160, 40));
        return b;
    }
    private JButton crearBotonSimple(String t) 
    {
        JButton b = new JButton(t); b.setBackground(Color.WHITE); b.setForeground(Color.GRAY);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new LineBorder(new Color(200,200,200), 1, true)); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140, 40)); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    class GradientPanel extends JPanel 
    {
        @Override protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setPaint(new GradientPaint(0,0,COLOR_GRADIENT_START,getWidth(),getHeight(),COLOR_GRADIENT_END));
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    }
    
    private void agregarPregunta() 
    {
        String enc = txtEnunciado.getText().trim();
        if (enc.isEmpty()) return;
        List<String> op = new ArrayList<>();
        if (cmbTipoP.getSelectedIndex()>0) 
        {
           String i = JOptionPane.showInputDialog("Opciones (;):");
           if (i!=null) for(String s:i.split(";")) if(!s.trim().isEmpty()) op.add(s.trim());
        }
        DatosPregunta dp = new DatosPregunta(enc, cmbTipoP.getSelectedIndex()+1, cmbTipoR.getSelectedIndex()+1, op);
        listaPreguntas.add(dp); 
        listModel.addElement(dp); 
        txtEnunciado.setText("");
    }
    private void guardarEdicion() 
    {
        try 
        { 
            ctrl.actualizarEncuestaExistente(ctrl.getIdEncuestaActual(), listaPreguntas);
            ctrl.mostrarVista(ctrlPresentacion.VISTA_PRINCIPAL); 
        } 
        catch(Exception e){}
    }
    
    class PreguntaRenderer extends JPanel implements ListCellRenderer<DatosPregunta> 
    {
        JLabel l1=new JLabel(), l2=new JLabel();
        public PreguntaRenderer()
        {
            setLayout(new BorderLayout()); setBorder(new CompoundBorder(new EmptyBorder(5,5,5,5), new MatteBorder(0,0,1,0,new Color(230,230,230))));
            setBackground(Color.WHITE);
            l1.setFont(new Font("Segoe UI", Font.BOLD, 14)); l1.setForeground(new Color(50,50,50));
            l2.setFont(new Font("Segoe UI", Font.PLAIN, 12)); l2.setForeground(Color.GRAY);
            add(l1,BorderLayout.NORTH); add(l2,BorderLayout.SOUTH);
        }
        public Component getListCellRendererComponent(JList list, DatosPregunta v, int i, boolean s, boolean f) 
        {
            l1.setText((i + 1)+". "+v.enunciado); l2.setText("Tipo: "+v.tipoP);
            setBackground(s ? new Color(255, 248, 220) : Color.WHITE); 
            return this;
        }
    }
}