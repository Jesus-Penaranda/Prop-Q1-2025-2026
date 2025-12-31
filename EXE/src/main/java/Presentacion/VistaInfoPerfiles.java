package Presentacion;

import dominio.Perfil;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;

public class VistaInfoPerfiles extends JPanel 
{
    private ctrlPresentacion ctrl;
    private JTextPane areaInfo; 

    public VistaInfoPerfiles(ctrlPresentacion ctrl) 
    {
        this.ctrl = ctrl;
        inicializarComponentes();
    }

    private void inicializarComponentes() 
    {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 245, 255));

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JLabel lblMenu = new JLabel("RESULTADOS");
        lblMenu.setForeground(Color.LIGHT_GRAY);
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sidebar.add(lblMenu); sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnCalidad = crearBotonSidebar("Ver Calidad");
        JButton btnRep     = crearBotonSidebar("Representantes");
        JButton btnInd     = crearBotonSidebar("Individuos");
        
        JButton btnGrafica = crearBotonSidebar("Ver Gráfica Kmeans");
        btnGrafica.setBackground(new Color(230, 126, 34)); 
        
        sidebar.add(btnCalidad); sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnRep);     sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnInd);     sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnGrafica); sidebar.add(Box.createVerticalGlue());

        JButton btnExportar = crearBotonSidebar("Exportar CSV");
        btnExportar.setBackground(new Color(39, 174, 96));
        sidebar.add(btnExportar); sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnVolver = crearBotonSidebar("Volver");
        btnVolver.setBackground(new Color(192, 57, 43));
        sidebar.add(btnVolver);

        add(sidebar, BorderLayout.WEST);
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(20, 30, 30, 30));

        JLabel lblTitulo = new JLabel("Análisis de Clusters");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(80, 80, 80));
        lblTitulo.setBorder(new EmptyBorder(0,0,20,0));
        panelCentro.add(lblTitulo, BorderLayout.NORTH);
        areaInfo = new JTextPane();
        areaInfo.setEditable(false);
        areaInfo.setContentType("text/html");
        areaInfo.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(areaInfo);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220)));        
        panelCentro.add(scroll, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);
        btnCalidad.addActionListener(e -> mostrarCalidad());
        btnRep.addActionListener(e -> mostrarRepresentantes());
        btnInd.addActionListener(e -> mostrarIndividuos());
        
        btnGrafica.addActionListener(e -> {
            try 
            {
                if(ctrl.getIdEncuestaActual() == null) throw new Exception("No hay encuesta seleccionada.");
                mostrarVentanaGrafica();
            } 
            catch (Exception ex) {mostrarError("Error gráfico: " + ex.getMessage());}
        });
        
        btnExportar.addActionListener(e -> {
            try { ctrl.exportarPerfiles(ctrl.getIdEncuestaActual()); JOptionPane.showMessageDialog(this, "¡Exportado!"); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });
        
        btnVolver.addActionListener(e -> { 
            areaInfo.setText(""); 
            ctrl.mostrarVista(ctrlPresentacion.VISTA_ANALISIS); 
        });
    }

    private void mostrarVentanaGrafica() 
    {
        JFrame frame = new JFrame("Gráfico clustering");
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        try 
        {
            ArrayList<Perfil> perfiles = ctrl.obtenerPerfiles(ctrl.getIdEncuestaActual());
            ArrayList<Integer> tamanyos = new ArrayList<>();
            for (Perfil p : perfiles) tamanyos.add(ctrl.obtenerIndividuos(p).size());
            MatplotlibPanel plot = new MatplotlibPanel(tamanyos);
            frame.add(plot, BorderLayout.CENTER);
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(this, "Error datos: " + e.getMessage());
            return;
        }
        
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(255, 248, 220)); // Es color crema 
        panelInferior.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel lblNota = new JLabel(
            "<html><div style='text-align:center;'>" +
            "<span style='color:#e67e22; font-size:16px;'>⚠️</span> " +
            "<b>Nota:</b> Esta visualización muestra datos representativos simulados para ilustrar la distribución de los diferentes clusters.<br>" +
            "Los puntos no reflejan las coordenadas exactas de los datos multidimensionales reales." +
            "</div></html>"
        );
        lblNota.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNota.setForeground(new Color(120, 120, 120));
        lblNota.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrar.setBackground(new Color(52, 73, 94));
        btnCerrar.setForeground(Color.BLACK);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setPreferredSize(new Dimension(100, 35));
        btnCerrar.addActionListener(e -> frame.dispose());
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.add(btnCerrar);
        
        panelInferior.add(lblNota, BorderLayout.CENTER);
        panelInferior.add(panelBoton, BorderLayout.SOUTH);
        frame.add(panelInferior, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    class MatplotlibPanel extends JPanel 
    {
        private int clusters;
        private ArrayList<Point2D> puntos;
        private ArrayList<Integer> tamanyos;
        
        private Color[] paleta = 
        {
            new Color(31, 119, 180, 200), new Color(255, 127, 14, 200),
            new Color(44, 160, 44, 200), new Color(214, 39, 40, 200),
            new Color(148, 103, 189, 200), new Color(140, 86, 75, 200)
        };

        class Point2D { double x, y; int cluster; Point2D(double x, double y, int c){this.x=x;this.y=y;this.cluster=c;} }

        public MatplotlibPanel(ArrayList<Integer> tamanyosReales) 
        {
            this.tamanyos = tamanyosReales;
            this.clusters = tamanyosReales.size();
            this.puntos = generarDatosSimulados(tamanyosReales);
            setBackground(Color.WHITE);
        }

        private ArrayList<Point2D> generarDatosSimulados(ArrayList<Integer> tamanyos) 
        {
            ArrayList<Point2D> lista = new ArrayList<>();

            // Se usa una semilla para controlar que siempre sea el mismo gráfico
            long semilla = 0;
            for (int t : tamanyos) semilla += t;
            Random rand = new Random(semilla);
            for (int c = 0; c < tamanyos.size(); c++) 
            {
                double centerX = (rand.nextDouble()*8) - 4;
                double centerY = (rand.nextDouble()*8) - 4;
                int n = tamanyos.get(c); 
                for (int i = 0; i < n; i++) 
                {
                    double px = centerX + rand.nextGaussian()*0.5;
                    double py = centerY + rand.nextGaussian()*0.5;
                    lista.add(new Point2D(px, py, c));
                }
            }
            return lista;
        }

        @Override
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int pad = 60; 
            double minVal = -5.0;
            double maxVal = 5.0;
            double range = maxVal - minVal;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            DecimalFormat df = new DecimalFormat("0.0");
            
            int numDivs = 10;
            for (int i = 0; i <= numDivs; i++) 
            {
                int x = pad + (int)((w - 2*pad)*(double)i / numDivs);
                double val = minVal + (range*i / numDivs);
                
                g2.setColor(new Color(240, 240, 240));
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(x, pad, x, h - pad);                
                g2.setColor(Color.GRAY);
                g2.drawLine(x, h - pad, x, h - pad + 5);
                String s = df.format(val);
                int strW = g2.getFontMetrics().stringWidth(s);
                g2.drawString(s, x - strW/2, h - pad + 20);
            }
            // Rejilla Horizontal
            for (int i = 0; i <= numDivs; i++) 
            {
                int y = h - pad - (int)((h - 2*pad)*(double)i / numDivs);
                double val = minVal + (range*i / numDivs);                
                g2.setColor(new Color(240, 240, 240));
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(pad, y, w - pad, y);
                g2.setColor(Color.GRAY);
                g2.drawLine(pad - 5, y, pad, y);
                
                String s = df.format(val);
                int strW = g2.getFontMetrics().stringWidth(s);
                g2.drawString(s, pad - strW - 10, y + 4);
            }

            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(pad, pad, w - 2*pad, h - 2*pad); // Caja exterior
            for (Point2D p : puntos) 
            {
                double normX = (p.x - minVal) / range;
                double normY = (p.y - minVal) / range;
                
                int x = pad + (int)(normX*(w - 2*pad));
                int y = h - pad - (int)(normY*(h - 2*pad));
                
                // Solo pintar si cae dentro (por si acaso el random sse sale)
                if (x >= pad && x <= w - pad && y >= pad && y <= h - pad) 
                {
                    g2.setColor(paleta[p.cluster%paleta.length]);
                    g2.fill(new Ellipse2D.Double(x - 5, y - 5, 10, 10)); 
                    g2.setColor(new Color(0,0,0,80));
                    g2.draw(new Ellipse2D.Double(x - 5, y - 5, 10, 10));
                }
            }

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString("Componente Principal 1", w / 2 - 60, h - 15);
            Graphics2D g2Rot = (Graphics2D) g2.create();
            g2Rot.rotate(-Math.PI / 2);
            g2Rot.drawString("Componente Principal 2", -h / 2 - 60, 20);
            g2Rot.dispose();
            if (clusters > 0) 
            {
                int legX = w - 140; int legY = pad + 10;
                int alto = clusters*22 + 10;
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRect(legX, legY, 130, alto);
                g2.setColor(Color.GRAY);
                g2.drawRect(legX, legY, 130, alto);
                for (int i = 0; i < clusters; i++) 
                {
                    g2.setColor(paleta[i%paleta.length]);
                    g2.fillOval(legX + 10,legY + 10 + i*22, 10, 10);
                    g2.setColor(Color.DARK_GRAY);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2.drawString("Cluster " + (i + 1) + " (n = " + tamanyos.get(i) + ")", legX + 25, legY + 20 + i*22);
                }
            }
        }
    }

    private void mostrarCalidad() 
    {
        try 
        { 
            double cal = ctrl.obtenerCalidad(ctrl.getIdEncuestaActual());
            areaInfo.setText("<html><body style='font-family:Segoe UI; padding:20px;'><div style='background-color:#E8F8F5; border-left:5px solid #1ABC9C; padding:20px;'><h2 style='color:#16A085;margin:0'>Calidad</h2><h1>"+String.format("%.4f", cal)+"</h1></div></body></html>");
        } 
        catch(Exception e) {mostrarError(e.getMessage());}
    }
    private void mostrarRepresentantes() 
    {
        try 
        { 
            ArrayList<Perfil> p = ctrl.obtenerPerfiles(ctrl.getIdEncuestaActual());
            StringBuilder sb = new StringBuilder("<html><body style='font-family:Segoe UI; padding:15px;'><h2>Representantes</h2>");
            int i=1; for(Perfil pf : p) sb.append("<div style='background:#F4ECF7; border:1px solid #D2B4DE; padding:10px; margin-bottom:10px;'><h3>Cluster "+(i++)+"</h3><pre>"+ctrl.obtenerRepresentante(pf)+"</pre></div>");
            sb.append("</body></html>"); areaInfo.setText(sb.toString()); areaInfo.setCaretPosition(0);
        } 
        catch(Exception e) {mostrarError(e.getMessage());}
    }
    private void mostrarIndividuos() 
    {
        try 
        { 
            ArrayList<Perfil> p = ctrl.obtenerPerfiles(ctrl.getIdEncuestaActual());
            StringBuilder sb = new StringBuilder("<html><body style='font-family:Segoe UI; padding:15px;'><h2>Individuos</h2>");
            int i=1; for(Perfil pf : p) sb.append("<div style='background:#EBF5FB; border-left:4px solid #3498DB; padding:10px; margin-bottom:10px;'><h3>Cluster "+(i++)+"</h3><p>"+String.join(", ", ctrl.obtenerIndividuos(pf))+"</p></div>");
            sb.append("</body></html>"); areaInfo.setText(sb.toString()); areaInfo.setCaretPosition(0);
        } 
        catch(Exception e) {mostrarError(e.getMessage());}
    }
    private void mostrarError(String m) { areaInfo.setText("<html><body><h3 style='color:red'>Error</h3>"+m+"</body></html>"); }
    private JButton crearBotonSidebar(String t) 
    {
        JButton b = new JButton(t); b.setBackground(new Color(52,73,94)); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMargin(new Insets(0,15,0,0)); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
}