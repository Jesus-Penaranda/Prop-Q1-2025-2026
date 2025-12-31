package Presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class NotisBonitas extends JWindow 
{
    
    private final float MAX_OPACITY = 0.85f;
    private float opacidad = 0.0f;
    private Timer timer;

    public NotisBonitas(Window owner, String message, boolean isSuccess) 
    {
        super(owner);
        // Color, Verde si exito, Rojo/Naranja si error o aviso
        Color bgColor = isSuccess ? new Color(46, 204, 113) : new Color(231, 76, 60);

        // Panel principal con fondo de color
        JPanel mainPanel = new JPanel() 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(false);

        // Label del mensaje
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        mainPanel.add(label);
        add(mainPanel);

        setAlwaysOnTop(true);
        setOpacity(0f);
        pack();
        
        // Posicionar la noti
        if (owner != null && owner.isVisible()) setLocation(owner.getX() + (owner.getWidth() - getWidth()) / 2, owner.getY() + owner.getHeight() - 100);
        else 
        {
            setLocationRelativeTo(null);
            Point loc = getLocation();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation(loc.x, screen.height - 150);
        }
        
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
        timer = new Timer(20, e -> animate());
        timer.start();
        setVisible(true);
    }

    private int phase = 0;
    private int waitCounter = 0;

    private void animate() 
    {
        if (phase == 0) 
        { 
            opacidad += 0.05f;
            if (opacidad >= MAX_OPACITY) 
            {
                opacidad = MAX_OPACITY;
                phase = 1;
            }
        } 
        else if (phase == 1) 
        { 
            waitCounter++;
            if (waitCounter > 100) phase = 2;
        } 
        else 
        { 
            opacidad -= 0.05f;
            if (opacidad <= 0f) 
            {
                timer.stop();
                dispose();
            }
        }
        setOpacity(Math.max(0f, Math.min(MAX_OPACITY, opacidad)));
    }

    public static void show(Component c, String msg, boolean success) 
    {
        Window window = SwingUtilities.getWindowAncestor(c);
        if (window == null) 
        {
            Window[] windows = Window.getWindows();
            for (Window w : windows) 
            {
                if (w.isVisible() && w instanceof JFrame) 
                {
                    window = w;
                    break;
                }
            }
        }
        new NotisBonitas(window, msg, success);
    }
}