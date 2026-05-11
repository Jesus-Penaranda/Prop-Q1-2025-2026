package Presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class VistaLogin extends JPanel 
{

    private ctrlPresentacion ctrl;

    private final Color COLOR_LEFT_START = new Color(102, 126, 234);
    private final Color COLOR_LEFT_END = new Color(118, 75, 162);
    private final Color COLOR_BTN = new Color(102, 126, 234);
    private JTextField txtUser;
    private JPasswordField txtPass;

    public VistaLogin(ctrlPresentacion ctrl) 
    {
        this.ctrl = ctrl;
        setLayout(new GridLayout(1, 2));
        JPanel panelIzquierdo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, COLOR_LEFT_START, getWidth(), getHeight(), COLOR_LEFT_END);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelIzquierdo.setLayout(new GridBagLayout());
        
        // Contenido Izquierda
        JLabel lblLogo = new JLabel("📊");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        lblLogo.setForeground(new Color(255, 255, 255, 50)); // Transparente sutil
        
        JLabel lblTituloApp = new JLabel("Gestor de Encuestas");
        lblTituloApp.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTituloApp.setForeground(Color.WHITE);
        
        JLabel lblSlogan = new JLabel("Gestiona, Analiza y Decide");
        lblSlogan.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        lblSlogan.setForeground(new Color(230, 230, 230));

        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.gridx = 0; 
        gbcL.gridy = 0;
        panelIzquierdo.add(lblLogo, gbcL);
        gbcL.gridy++;
        panelIzquierdo.add(Box.createVerticalStrut(20), gbcL);
        gbcL.gridy++;
        panelIzquierdo.add(lblTituloApp, gbcL);
        gbcL.gridy++;
        panelIzquierdo.add(lblSlogan, gbcL);

        JPanel panelDerecho = new JPanel();
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setLayout(new GridBagLayout()); // Para centrar el formulario

        JPanel formBox = new JPanel();
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setOpaque(false);
        formBox.setBorder(new EmptyBorder(0, 50, 0, 50)); // Márgenes laterales

        JLabel lblLogin = new JLabel("Bienvenido");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogin.setForeground(new Color(50, 50, 50));
        lblLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Introduce tus credenciales para entrar");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUser = crearInput("Usuario");
        txtPass = crearPassInput();
        JButton btnEntrar = new JButton("Iniciar Sesión")
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setContentAreaFilled(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        JButton btnRegistrar = new JButton("Crear Cuenta")
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(COLOR_BTN);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                super.paintComponent(g);
            }
        };
        btnRegistrar.setForeground(COLOR_BTN);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setContentAreaFilled(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnEntrar.addActionListener(e -> login());
        btnRegistrar.addActionListener(e -> registrar()); 
        formBox.add(lblLogin);
        formBox.add(lblSub);
        formBox.add(Box.createVerticalStrut(30));
        formBox.add(new JLabel("Usuario"));
        formBox.add(Box.createVerticalStrut(5));
        formBox.add(txtUser);
        formBox.add(Box.createVerticalStrut(15));
        formBox.add(new JLabel("Contraseña"));
        formBox.add(Box.createVerticalStrut(5));
        formBox.add(txtPass);
        formBox.add(Box.createVerticalStrut(30));
        formBox.add(btnEntrar);
        formBox.add(Box.createVerticalStrut(10)); 
        formBox.add(btnRegistrar);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.gridx = 0; 
        gbcR.gridy = 0;
        gbcR.weightx = 1.0; 
        gbcR.fill = GridBagConstraints.HORIZONTAL;
        gbcR.insets = new Insets(0, 40, 0, 40);
        panelDerecho.add(formBox, gbcR);
        add(panelIzquierdo);
        add(panelDerecho);
    }

    private void registrar() 
    {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if (u.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Introduce un usuario");
            return;
        }
        if (p.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Introduce una contraseña");
            return;
        }
        ctrl.registrarUsuario(u, p);
    }

    private void login() 
    {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if (u.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Introduce un usuario");
            return;
        }
        if (p.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Introduce una contraseña");
            return;
        }
        ctrl.loginUsuario(u, p);
    }

    private JTextField crearInput(String ph) 
    {
        JTextField t = new JTextField();
        estilizarCampo(t);
        return t;
    }

    private JPasswordField crearPassInput() 
    {
        JPasswordField t = new JPasswordField();
        estilizarCampo(t);
        return t;
    }


    private void estilizarCampo(JTextField t) 
    {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(new Color(50, 50, 50));
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)),BorderFactory.createEmptyBorder(5, 5, 10, 5)));
        t.setBackground(Color.WHITE);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        t.addFocusListener(new java.awt.event.FocusAdapter() 
        {
            public void focusGained(java.awt.event.FocusEvent evt) 
            {
                t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BTN),BorderFactory.createEmptyBorder(5, 5, 10, 5)));
            }
            public void focusLost(java.awt.event.FocusEvent evt) 
            {
                t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)),BorderFactory.createEmptyBorder(5, 5, 10, 5)));
            }
        });
    }
}