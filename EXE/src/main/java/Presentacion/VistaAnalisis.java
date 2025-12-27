package Presentacion;

import dominio.Perfil;
import dominio.ctrlDominio.DatosPregunta;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class VistaAnalisis extends JPanel 
{
    private ctrlPresentacion ctrl;
    private JTextField txtK;
    private JComboBox<String> cmbAlgoritmo;
    private JComboBox<String> cmbInicializacion;
    private JPanel panelCheckboxes;
    private List<JCheckBox> listaCheckboxes;
    
    private JButton btnAnalizar;
    private JButton btnElbow;
    private JButton btnVolver;
    private JButton btnExportar;
    private JButton btnImportar;
    private SwingWorker<Map<Integer, Double>, Integer> cargar_la_k = null;
    private JPanel panelResultados;
    
    private final Color COLOR_GRADIENT_START = new Color(102, 126, 234);
    private final Color COLOR_GRADIENT_END = new Color(118, 75, 162);
    private final Color COLOR_ACCENT = new Color(46, 204, 113);

    public VistaAnalisis(ctrlPresentacion ctrl) 
    {
        this.ctrl = ctrl;
        this.listaCheckboxes = new ArrayList<>();
        inicializarComponentes();
    }

    private void inicializarComponentes() 
    {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        
        JPanel panelHeader = new GradientPanel();
        panelHeader.setLayout(new BorderLayout());
        panelHeader.setBorder(new EmptyBorder(25, 40, 25, 40));
        
        JLabel lblTitulo = new JLabel("Análisis de Clustering");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        
        btnVolver = crearBotonGradiente("Volver", new Color(142, 68, 173), new Color(155, 89, 182));
        btnVolver.setPreferredSize(new Dimension(120, 35));
        btnVolver.addActionListener(e -> ctrl.mostrarVista(ctrlPresentacion.VISTA_PRINCIPAL));
        panelHeader.add(btnVolver, BorderLayout.EAST);
        
        add(panelHeader, BorderLayout.NORTH);
        
        JPanel panelContenido = new JPanel(new BorderLayout(20, 20));
        panelContenido.setBackground(new Color(245, 247, 250));
        panelContenido.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        JPanel panelConfig = crearPanelConfiguracion();
        panelResultados = new JPanel(new BorderLayout(10, 10));
        panelResultados.setOpaque(false);
        
        JPanel contenedorSplit = new JPanel(new GridLayout(1, 2, 20, 0));
        contenedorSplit.setOpaque(false);
        contenedorSplit.add(panelConfig);
        contenedorSplit.add(panelResultados);
        
        panelContenido.add(contenedorSplit, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);
    }

    @Override
    public void setVisible(boolean b) 
    {
        super.setVisible(b);
        if (b) cargarPreguntasFiltro();
    }

    private void cargarPreguntasFiltro() 
    {
        if (panelCheckboxes == null) return;
        panelCheckboxes.removeAll();
        listaCheckboxes.clear();
        
        Integer id = ctrl.getIdEncuestaActual();
        if (id == null) return;

        try 
        {
            List<DatosPregunta> preguntas = ctrl.obtenerPreguntasDeEncuesta(id);
            for (DatosPregunta p : preguntas) 
            {
                JCheckBox chk = new JCheckBox(p.enunciado);
                chk.setSelected(true); 
                chk.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                chk.setForeground(new Color(80, 80, 80));
                chk.setOpaque(false);
                chk.setFocusPainted(false);
                chk.setName(p.enunciado);
                
                listaCheckboxes.add(chk);
                panelCheckboxes.add(chk);
                panelCheckboxes.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } 
        catch (Exception e) 
        {
            JLabel lblErr = new JLabel("Error cargando preguntas");
            lblErr.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblErr.setForeground(Color.RED);
            panelCheckboxes.add(lblErr);
        }
        
        panelCheckboxes.revalidate();
        panelCheckboxes.repaint();
    }

    private HashSet<String> obtenerFiltroSeleccionado() 
    {
        HashSet<String> ignoradas = new HashSet<>();
        for (JCheckBox chk : listaCheckboxes) 
        {
            if (!chk.isSelected()) ignoradas.add(chk.getText());
        }
        return ignoradas;
    }

    private JPanel crearPanelConfiguracion() 
    {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        
        JPanel cardForm = new JPanel() 
        {
            @Override 
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
            }
        };
        cardForm.setLayout(new BoxLayout(cardForm, BoxLayout.Y_AXIS));
        cardForm.setOpaque(false);
        cardForm.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitConfig = new JLabel("Configuración del Clustering");
        lblTitConfig.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitConfig.setForeground(COLOR_GRADIENT_END);
        lblTitConfig.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(lblTitConfig);
        cardForm.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel lblAlg = crearLabelInput("Algoritmo:");
        lblAlg.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(lblAlg);
        cardForm.add(Box.createRigidArea(new Dimension(0, 5)));
        
        cmbAlgoritmo = new JComboBox<>(new String[]{"K-MEANS", "K-MEDOIDES", "CLUSTERSUB"});
        estilizarCombo(cmbAlgoritmo);
        cmbAlgoritmo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbAlgoritmo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(cmbAlgoritmo);
        cardForm.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel lblIni = crearLabelInput("Inicialización:");
        lblIni.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(lblIni);
        cardForm.add(Box.createRigidArea(new Dimension(0, 5)));
        
        cmbInicializacion = new JComboBox<>(new String[]{"K-MEANSPP", "TRIVIAL"});
        estilizarCombo(cmbInicializacion);
        cmbInicializacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbInicializacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(cmbInicializacion);
        cardForm.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel lblK = crearLabelInput("Número de Clusters (K):");
        lblK.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(lblK);
        cardForm.add(Box.createRigidArea(new Dimension(0, 5)));
        
        txtK = new JTextField("3");
        estilizarInput(txtK);
        txtK.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtK.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(txtK);
        cardForm.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblVar = crearLabelInput("Variables a incluir:");
        lblVar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(lblVar);
        cardForm.add(Box.createRigidArea(new Dimension(0, 5)));

        panelCheckboxes = new JPanel();
        panelCheckboxes.setLayout(new BoxLayout(panelCheckboxes, BoxLayout.Y_AXIS));
        panelCheckboxes.setOpaque(false);
        
        JScrollPane scrollCheck = new JScrollPane(panelCheckboxes);
        scrollCheck.setOpaque(false);
        scrollCheck.getViewport().setOpaque(false);
        scrollCheck.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        scrollCheck.setPreferredSize(new Dimension(200, 100));
        scrollCheck.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        scrollCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollCheck.getVerticalScrollBar().setUnitIncrement(16);
        
        cardForm.add(scrollCheck);
        cardForm.add(Box.createRigidArea(new Dimension(0, 20)));
        
        btnElbow = crearBotonGradiente("🔍 Calcular K Óptimo", new Color(255, 152, 0), new Color(255, 87, 34));
        btnElbow.addActionListener(e -> calcularElbow());
        btnElbow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnElbow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(btnElbow);
        cardForm.add(Box.createRigidArea(new Dimension(0, 10)));
        
        btnAnalizar = crearBotonGradiente("Ejecutar Análisis", COLOR_GRADIENT_START, COLOR_GRADIENT_END);
        btnAnalizar.addActionListener(e -> ejecutarAnalisis());
        btnAnalizar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnAnalizar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(btnAnalizar);
        cardForm.add(Box.createRigidArea(new Dimension(0, 20)));
        
        btnImportar = crearBotonSecundario("📥 Importar Perfiles", new Color(52, 152, 219));
        btnImportar.addActionListener(e -> importarPerfiles());
        btnImportar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnImportar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(btnImportar);
        cardForm.add(Box.createRigidArea(new Dimension(0, 10)));
        
        btnExportar = crearBotonSecundario("📤 Exportar Perfiles", new Color(46, 204, 113));
        btnExportar.addActionListener(e -> exportarPerfiles());
        btnExportar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnExportar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(btnExportar);
        cardForm.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(cardForm);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 38));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return btn;
    }

    private void calcularElbow() 
    {
        try 
        {
            Integer id = ctrl.getIdEncuestaActual();
            if (id == null) 
            {
                NotisBonitas.show(this, "No hay encuesta seleccionada", false);
                return;
            }

            String alg = (String) cmbAlgoritmo.getSelectedItem();
            String ini = (String) cmbInicializacion.getSelectedItem();
            
            JPanel panelProgreso = new JPanel() 
            {
                @Override
                protected void paintComponent(Graphics g) 
                {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0, 0, 0, 15));
                    g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 15, 15);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 15, 15);
                }
            };
            panelProgreso.setLayout(new BoxLayout(panelProgreso, BoxLayout.Y_AXIS));
            panelProgreso.setBorder(new EmptyBorder(20, 20, 20, 20));
            panelProgreso.setOpaque(false);
            
            JLabel lblTitulo = new JLabel("Calculando K óptimo");
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTitulo.setForeground(COLOR_GRADIENT_END);
            lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setPreferredSize(new Dimension(300, 25));
            progressBar.setMaximumSize(new Dimension(300, 25));
            progressBar.setForeground(new Color(255, 152, 0));
            progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblEstado = new JLabel("Iniciando...");
            lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblEstado.setForeground(new Color(100, 100, 100));
            lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton btnCancelar = crearBotonGradiente("Cancelar", new Color(231, 76, 60), new Color(192, 57, 43));
            btnCancelar.setPreferredSize(new Dimension(120, 35));
            btnCancelar.setMaximumSize(new Dimension(120, 35));
            btnCancelar.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            panelProgreso.add(lblTitulo);
            panelProgreso.add(Box.createRigidArea(new Dimension(0, 15)));
            panelProgreso.add(progressBar);
            panelProgreso.add(Box.createRigidArea(new Dimension(0, 10)));
            panelProgreso.add(lblEstado);
            panelProgreso.add(Box.createRigidArea(new Dimension(0, 15)));
            panelProgreso.add(btnCancelar);
            
            cargar_la_k = new SwingWorker<Map<Integer, Double>, Integer>() 
            {
                @Override
                protected Map<Integer, Double> doInBackground() throws Exception 
                {
                    int kMax = 10;
                    for (int i = 10; i <= 90; i += 10) 
                    {
                        if (isCancelled()) return new LinkedHashMap<>();
                        publish(i);
                        Thread.sleep(200); 
                    }
                    Map<Integer, Double> resultado = ctrl.calcularKOptimo(id, kMax, alg, ini, obtenerFiltroSeleccionado());
                    publish(100);
                    return resultado;
                }

                @Override
                protected void process(java.util.List<Integer> chunks) 
                {
                    int ultimo = chunks.get(chunks.size() - 1);
                    progressBar.setValue(ultimo);
                    lblEstado.setText(String.format("Procesando... %d%%", ultimo));
                }

                @Override
                protected void done() 
                {
                    if (isCancelled()) 
                    {
                        panelResultados.removeAll();
                        panelResultados.revalidate();
                        panelResultados.repaint();
                        NotisBonitas.show(VistaAnalisis.this, "Cálculo cancelado", false);
                        return;
                    }
                    
                    try 
                    {
                        Map<Integer, Double> resultados = get();
                        if (resultados.isEmpty()) 
                        {
                            panelResultados.removeAll();
                            panelResultados.revalidate();
                            panelResultados.repaint();
                            NotisBonitas.show(VistaAnalisis.this, "No se pudieron calcular resultados", false);
                            return;
                        }
                        
                        int kOptimo = resultados.containsKey(-1) ? resultados.get(-1).intValue() : 2;
                        
                        mostrarGraficaElbow(resultados, kOptimo);
                        txtK.setText(String.valueOf(kOptimo));
                        NotisBonitas.show(VistaAnalisis.this, "K óptimo encontrado: " + kOptimo, true);
                    } 
                    catch (Exception ex) 
                    {
                        panelResultados.removeAll();
                        panelResultados.revalidate();
                        panelResultados.repaint();
                        NotisBonitas.show(VistaAnalisis.this, ex.getMessage(), false);
                    }
                }
            };
            
            btnCancelar.addActionListener(e -> {
                if (cargar_la_k != null && !cargar_la_k.isDone()) 
                {
                    cargar_la_k.cancel(true);
                }
            });
            
            panelResultados.removeAll();
            panelResultados.setLayout(new GridBagLayout());
            panelResultados.add(panelProgreso);
            panelResultados.revalidate();
            panelResultados.repaint();
            
            cargar_la_k.execute();
        } 
        catch (Exception ex) 
        {
            NotisBonitas.show(this, "Error: " + ex.getMessage(), false);
        }
    }

    private void mostrarGraficaElbow(Map<Integer, Double> datos, int kOptimo) 
    {
        panelResultados.removeAll();
        
        JLabel lblTit = new JLabel("K Óptimo Encontrado");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(COLOR_GRADIENT_END);
        lblTit.setBorder(new EmptyBorder(0, 0, 10, 0));
        GraficaElbowPanel grafica = new GraficaElbowPanel(datos, kOptimo);
        grafica.setPreferredSize(new Dimension(400, 300));
        
        panelResultados.setLayout(new BorderLayout(10, 10));
        panelResultados.add(lblTit, BorderLayout.NORTH);
        panelResultados.add(grafica, BorderLayout.CENTER);
        Double calidadKOptimo = datos.get(kOptimo);
        String textoCalidad = (calidadKOptimo != null) ? String.format("%.4f", calidadKOptimo) : "N/A";
        JLabel lblInfo = new JLabel(String.format("<html><b>K Óptimo: %d</b><br/>Calidad: %s</html>", kOptimo, textoCalidad));        
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setForeground(new Color(80, 80, 80));
        lblInfo.setBorder(new EmptyBorder(10, 0, 0, 0));
        panelResultados.add(lblInfo, BorderLayout.SOUTH);
        
        panelResultados.revalidate();
        panelResultados.repaint();
    }

    private void ejecutarAnalisis() 
    {
        try 
        {
            Integer id = ctrl.getIdEncuestaActual();
            if (id == null) 
            {
                NotisBonitas.show(this, "No hay encuesta seleccionada", false);
                return;
            }

            int k = Integer.parseInt(txtK.getText().trim());
            if (k < 2) 
            {
                NotisBonitas.show(this, "K debe ser al menos 2", false);
                return;
            }

            String alg = (String) cmbAlgoritmo.getSelectedItem();
            String ini = (String) cmbInicializacion.getSelectedItem();
            ctrl.analizarEncuesta(id, k, alg, ini, obtenerFiltroSeleccionado());   
            ArrayList<Perfil> perfiles = ctrl.obtenerPerfiles(id);
            double calidad = ctrl.obtenerCalidad(id);
            
            mostrarResultadosAnalisis(perfiles, calidad);
            
            NotisBonitas.show(this, "Análisis completado con éxito", true);
        } 
        catch (NumberFormatException ex) 
        {
            NotisBonitas.show(this, "K debe ser un número válido", false);
        } 
        catch (Exception ex) 
        {
            NotisBonitas.show(this, "Error: " + ex.getMessage(), false);
        }
    }

    private void mostrarResultadosAnalisis(ArrayList<Perfil> perfiles, double calidad) 
    {
        panelResultados.removeAll();
        
        JPanel cardResultados = new JPanel() 
        {
            @Override 
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255), 0, getHeight(), new Color(248, 249, 252));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); 
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);
            }
        };
        cardResultados.setLayout(new BorderLayout(0, 25));
        cardResultados.setOpaque(false);
        cardResultados.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);

        JLabel iconoExito = new JLabel("✓");
        iconoExito.setFont(new Font("Segoe UI", Font.BOLD, 64));
        iconoExito.setForeground(COLOR_ACCENT);
        iconoExito.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTit = new JLabel("Clustering Completado");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTit.setForeground(COLOR_GRADIENT_END);
        lblTit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("El análisis se ha ejecutado correctamente");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(120, 120, 120));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelHeader.add(iconoExito);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 10)));
        panelHeader.add(lblTit);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 5)));
        panelHeader.add(lblSubtitulo);
        
        JPanel panelMetricas = new JPanel(new GridLayout(1, 2, 15, 0));
        panelMetricas.setOpaque(false);
        
        JPanel tarjetaCalidad = crearTarjetaMetrica("Índice de Calidad",calidad > 0 ? String.format("%.4f", calidad) : "N/A",calidad > 0 ? "Basado en coeficiente de silueta" : "Perfiles importados",  new Color(102, 126, 234), "📈");
        JPanel tarjetaPerfiles = crearTarjetaMetrica("Perfiles Generados",String.valueOf(perfiles.size()),perfiles.size() + " grupos identificados",new Color(46, 204, 113),"🏃‍♀️‍➡️");
        panelMetricas.add(tarjetaCalidad);
        panelMetricas.add(tarjetaPerfiles);

        JSeparator separador = new JSeparator();
        separador.setForeground(new Color(230, 230, 230));

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelAcciones.setOpaque(false);
        
        JButton btnVerPerfiles = crearBotonModerno( "Ver Detalles de Perfiles", COLOR_ACCENT, new Color(39, 174, 96), "");
        btnVerPerfiles.addActionListener(e -> {
            ctrl.setIdEncuestaActual(ctrl.getIdEncuestaActual());
            ctrl.mostrarVista(ctrlPresentacion.VISTA_INFO_PERFILES);
        });
        
        panelAcciones.add(btnVerPerfiles);
        
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setOpaque(false);
        
        panelContenido.add(panelHeader);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 25)));
        panelContenido.add(panelMetricas);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 25)));
        panelContenido.add(separador);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));
        panelContenido.add(panelAcciones);
        
        cardResultados.add(panelContenido, BorderLayout.CENTER);
        panelResultados.add(cardResultados);
        panelResultados.revalidate();
        panelResultados.repaint();
    }

    private JPanel crearTarjetaMetrica(String titulo, String valor, String descripcion, Color colorAccent, String icono) 
    {
        JPanel tarjeta = new JPanel() 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorAccent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 10, 10);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 4, getWidth(), getHeight() - 4, 15, 15);
                g2.setColor(new Color(0, 0, 0, 5));
                g2.drawRoundRect(1, 5, getWidth() - 2, getHeight() - 6, 15, 15);
            }
        };
        tarjeta.setLayout(new BorderLayout(10, 10));
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        lblIcono.setForeground(colorAccent);
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono.setPreferredSize(new Dimension(50, 50));
        
        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setOpaque(false);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(new Color(120, 120, 120));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(colorAccent);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(150, 150, 150));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panelTexto.add(lblTitulo);
        panelTexto.add(Box.createRigidArea(new Dimension(0, 5)));
        panelTexto.add(lblValor);
        panelTexto.add(Box.createRigidArea(new Dimension(0, 3)));
        panelTexto.add(lblDesc);
        
        tarjeta.add(lblIcono, BorderLayout.WEST);
        tarjeta.add(panelTexto, BorderLayout.CENTER);
        return tarjeta;
    }

    private JButton crearBotonModerno(String texto, Color c1, Color c2, String icono) 
    {
        JButton btn = new JButton(texto + " " + icono) 
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
                
                Color color1 = hover ? c1.brighter() : c1;
                Color color2 = hover ? c2.brighter() : c2;
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (hover) 
                {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(0, 2, getWidth(), getHeight(), 12, 12);
                }
                
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 45));
        return btn;
    }

    private void importarPerfiles() 
    {
        try 
        {
            Integer id = ctrl.getIdEncuestaActual();
            if (id == null) 
            {
                NotisBonitas.show(this, "No hay encuesta seleccionada", false);
                return;
            }
            ctrl.importarPerfiles(id);
            ArrayList<Perfil> perfiles = ctrl.obtenerPerfiles(id);
            if (perfiles == null || perfiles.isEmpty()) 
            {
                NotisBonitas.show(this, "No se encontraron perfiles para importar", false);
                return;
            }
            mostrarResultadosAnalisis(perfiles, 0.0);
            NotisBonitas.show(this, "Perfiles importados correctamente: " + perfiles.size() + " perfiles", true);
        } 
        catch (Exception ex) 
        {
            NotisBonitas.show(this, "Error al importar: " + ex.getMessage(), false);
        }
    }

    private void exportarPerfiles() 
    {
        try 
        {
            Integer id = ctrl.getIdEncuestaActual();
            if (id == null) 
            {
                NotisBonitas.show(this, "No hay encuesta seleccionada", false);
                return;
            }
            ctrl.exportarPerfiles(id);
            NotisBonitas.show(this, "Perfiles exportados correctamente", true);
        } 
        catch (Exception ex) 
        {
            NotisBonitas.show(this, "Error al exportar: " + ex.getMessage(), false);
        }
    }
    
    private JLabel crearLabelInput(String texto) 
    {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    private void estilizarInput(JTextField txt) 
    {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private void estilizarCombo(JComboBox<String> cmb) 
    {
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmb.setBackground(Color.WHITE);
        cmb.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
    }

    private JButton crearBotonGradiente(String texto, Color c1, Color c2) 
    {
        JButton btn = new JButton(texto) 
        {
            @Override 
            protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 40));
        return btn;
    }
    private class GradientPanel extends JPanel 
    {
        @Override 
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, COLOR_GRADIENT_START, getWidth(), getHeight(), COLOR_GRADIENT_END);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    private class GraficaElbowPanel extends JPanel 
    {
        private Map<Integer, Double> datos;
        private int kOptimo;
        private int margenIzq = 70; 
        private int margenDer = 50;
        private int margenTop = 50;
        private int margenBot = 50;

        public GraficaElbowPanel(Map<Integer, Double> datos, int kOptimo) 
        {
            this.datos = datos;
            this.kOptimo = kOptimo;
            setBackground(Color.WHITE);
            setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (datos == null || datos.isEmpty()) return;

            int width = getWidth();
            int height = getHeight();
            int graphWidth = width - margenIzq - margenDer;
            int graphHeight = height - margenTop - margenBot;

            List<Integer> ks = new ArrayList<>(datos.keySet());
            ks.remove(Integer.valueOf(-1)); 
            Collections.sort(ks);
            
            if (ks.isEmpty()) return;
            
            double minCalidad = -1.0;
            double maxCalidad = 1.0;

            g2.setColor(new Color(150, 150, 150));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(margenIzq, height - margenBot, width - margenDer, height - margenBot);
            g2.drawLine(margenIzq, margenTop, margenIzq, height - margenBot);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(new Color(80, 80, 80));
            g2.drawString("K (Número de Clusters)", width / 2 - 60, height - 10);
            g2.rotate(-Math.PI / 2);
            g2.drawString("Índice de Calidad", -height / 2 - 50, 20);
            g2.rotate(Math.PI / 2);

            if (ks.size() == 1) 
            {
                Integer k = ks.get(0);
                double calidad = datos.get(k);
                int x = margenIzq + graphWidth / 2;
                int y = height - margenBot - (int) ((calidad - minCalidad) * graphHeight / (maxCalidad - minCalidad));
                
                g2.setColor(new Color(255, 87, 34));
                g2.fillOval(x - 8, y - 8, 16, 16);
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 5, y - 5, 10, 10);
                g2.setColor(new Color(255, 87, 34));
                g2.fillOval(x - 3, y - 3, 6, 6);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(String.format("K=%d ★", k), x + 10, y - 10);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(new Color(100, 100, 100));
                g2.drawString(String.valueOf(k), x - 5, height - margenBot + 20);
            } 
            else 
            {
                g2.setStroke(new BasicStroke(2));
                int prevX = -1, prevY = -1;
                
                for (int i = 0; i < ks.size(); i++) 
                {
                    Integer k = ks.get(i);
                    double calidad = datos.get(k);
                    int x = margenIzq + (int) ((k - ks.get(0)) * graphWidth / (ks.get(ks.size() - 1) - ks.get(0)));
                    int y = height - margenBot - (int) ((calidad - minCalidad) * graphHeight / (maxCalidad - minCalidad));
                    
                    if (prevX != -1) 
                    {
                        g2.setColor(COLOR_GRADIENT_START);
                        g2.drawLine(prevX, prevY, x, y);
                    }
                    if (k == kOptimo) 
                    {
                        g2.setColor(new Color(255, 87, 34));
                        g2.fillOval(x - 8, y - 8, 16, 16);
                        g2.setColor(Color.WHITE);
                        g2.fillOval(x - 5, y - 5, 10, 10);
                        g2.setColor(new Color(255, 87, 34));
                        g2.fillOval(x - 3, y - 3, 6, 6);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        g2.drawString(String.format("K=%d ★", k), x + 10, y - 10);
                    } 
                    else 
                    {
                        g2.setColor(COLOR_GRADIENT_START);
                        g2.fillOval(x - 5, y - 5, 10, 10);
                        g2.setColor(Color.WHITE);
                        g2.fillOval(x - 3, y - 3, 6, 6);
                    }
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    g2.setColor(new Color(100, 100, 100));
                    g2.drawString(String.valueOf(k), x - 5, height - margenBot + 20);
                    prevX = x;
                    prevY = y;
                }
            }
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) 
            {
                double val = minCalidad + (maxCalidad - minCalidad) * i / 5.0;
                int y = height - margenBot - (int) (graphHeight * i / 5.0);
                String texto = String.format("%.3f", val);
                int strW = g2.getFontMetrics().stringWidth(texto);
                g2.setColor(new Color(100, 100, 100));
                g2.drawString(texto, margenIzq - strW - 10, y + 5);
                g2.setColor(new Color(230, 230, 230));
                g2.drawLine(margenIzq, y, width - margenDer, y);
            }
        }
    }
}