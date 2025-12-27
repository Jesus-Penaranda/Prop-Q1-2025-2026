package Presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TarjetaEncuesta extends JPanel 
{

    private boolean isHover = false;
    private Color colorFondo;

    public TarjetaEncuesta(Integer id, int numRespuestas, Color bg, Color txt, Runnable onResponder, Runnable onAnalizar, Runnable onEditar, Runnable onBorrar) {
        this.colorFondo = bg;
        setLayout(new BorderLayout());
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 110)); // Un poco más alta
        setBorder(new EmptyBorder(10, 25, 10, 15));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1)); // 3 filas ahora
        infoPanel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Encuesta #" + id);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(txt);
        
        JLabel lblMeta = new JLabel("" + " " + numRespuestas + " Respuestas recibidas");
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMeta.setForeground(new Color(120, 120, 120)); // Gris suave
        
        JLabel lblEstado = new JLabel("🟢 Activa");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstado.setForeground(new Color(46, 204, 113));

        infoPanel.add(lblTitulo);
        infoPanel.add(lblMeta);
        infoPanel.add(lblEstado);
        
        add(infoPanel, BorderLayout.WEST);

        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        accionesPanel.setOpaque(false);

        accionesPanel.add(crearBotonIcono("Responder", new Color(52, 152, 219), onResponder));
        accionesPanel.add(crearBotonIcono("📊 Analizar", new Color(155, 89, 182), onAnalizar));
        accionesPanel.add(crearBotonIcono("", new Color(241, 196, 15), onEditar)); // Solo icono para editar
        accionesPanel.add(crearBotonIcono("🗑️", new Color(231, 76, 60), onBorrar)); // Solo icono para borrar

        add(accionesPanel, BorderLayout.EAST);
        addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                isHover = true;
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) 
            {
                isHover = false;
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                repaint();
            }
        });
    }

    private JButton crearBotonIcono(String texto, Color bg, Runnable action) 
    {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int shadowGap = isHover ? 2 : 5;
        int shadowAlpha = isHover ? 50 : 20;
        int moveY = isHover ? -2 : 0;

        g2.setColor(new Color(0, 0, 0, shadowAlpha));
        g2.fillRoundRect(shadowGap + 5, shadowGap + 5 + moveY, getWidth() - 10, getHeight() - 10, 15, 15);
        g2.setColor(colorFondo);
        g2.fillRoundRect(5, 5 + moveY, getWidth() - 10, getHeight() - 10, 15, 15);
        g2.setColor(new Color(102, 126, 234));
        g2.fillRoundRect(5, 5 + moveY, 10, getHeight() - 10, 15, 15);
        g2.fillRect(10, 5 + moveY, 5, getHeight() - 10);
    }
}