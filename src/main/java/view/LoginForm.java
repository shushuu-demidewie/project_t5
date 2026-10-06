package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.UserDAO;
import model.User;

/**
 * Giao diện Đăng nhập hệ thống bán hàng Trà sữa (POS) - Thiết kế Hiện đại.
 */
public class LoginForm extends JFrame {

    private static final Color COLOR_PRIMARY = new Color(15, 118, 110);      // Emerald #0f766e
    private static final Color COLOR_PRIMARY_LIGHT = new Color(204, 251, 241); // Mint #ccfbf1
    private static final Color COLOR_ACCENT = new Color(16, 185, 129);       // Xanh tươi #10b981
    private static final Color COLOR_DARK = new Color(15, 23, 42);           // Slate đen #0f172a
    private static final Color COLOR_BG = new Color(248, 250, 252);          // Nền nhạt #f8fafc
    private static final Color COLOR_BORDER = new Color(226, 232, 240);      // Viền #e2e8f0

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private UserDAO userDAO;

    public LoginForm() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("ĐĂNG NHẬP HỆ THỐNG - POS TRÀ SỮA & CAFE");
        setSize(500, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);

        // Header Panel sang trọng
        JPanel headerPanel = new JPanel(new BorderLayout(8, 6));
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(500, 110));
        headerPanel.setBorder(new EmptyBorder(18, 20, 16, 20));

        JLabel lblLogo = new JLabel("🧋", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));

        JLabel lblTitle = new JLabel("BOBA & COFFEE STATION POS", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("Hệ thống quản lý bán hàng & thu ngân trực tuyến", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubTitle.setForeground(COLOR_PRIMARY_LIGHT);

        JPanel textStack = new JPanel(new GridLayout(2, 1, 0, 2));
        textStack.setOpaque(false);
        textStack.add(lblTitle);
        textStack.add(lblSubTitle);

        headerPanel.add(textStack, BorderLayout.CENTER);

        // Form Input Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_BG);
        formPanel.setBorder(new EmptyBorder(20, 45, 10, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // Label & Field: Tài khoản
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(COLOR_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(200, 38));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        formPanel.add(txtUsername, gbc);

        // Label & Field: Mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(COLOR_DARK);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(200, 38));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        // Nút điền nhanh tài khoản thử nghiệm
        JPanel demoBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        demoBox.setOpaque(false);
        demoBox.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblQuick = new JLabel("Điền nhanh: ");
        lblQuick.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblQuick.setForeground(new Color(100, 116, 139));
        demoBox.add(lblQuick);

        JButton btnQuickAdmin = new JButton("Quản trị: admin");
        btnQuickAdmin.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnQuickAdmin.setBackground(Color.WHITE);
        btnQuickAdmin.setForeground(COLOR_PRIMARY);
        btnQuickAdmin.setBorder(new LineBorder(COLOR_BORDER, 1));
        btnQuickAdmin.setFocusPainted(false);
        btnQuickAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickAdmin.addActionListener(e -> {
            txtUsername.setText("admin");
            txtPassword.setText("123456");
        });

        JButton btnQuickStaff = new JButton("Thu ngân: staff01");
        btnQuickStaff.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnQuickStaff.setBackground(Color.WHITE);
        btnQuickStaff.setForeground(COLOR_PRIMARY);
        btnQuickStaff.setBorder(new LineBorder(COLOR_BORDER, 1));
        btnQuickStaff.setFocusPainted(false);
        btnQuickStaff.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickStaff.addActionListener(e -> {
            txtUsername.setText("staff01");
            txtPassword.setText("123");
        });

        demoBox.add(btnQuickAdmin);
        demoBox.add(btnQuickStaff);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(demoBox, gbc);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 16));
        buttonPanel.setBackground(COLOR_BG);

        btnLogin = new JButton("Đăng nhập hệ thống");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(COLOR_ACCENT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(190, 42));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(new EmptyBorder(8, 16, 8, 16));

        btnExit = new JButton("Thoát");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExit.setBackground(Color.WHITE);
        btnExit.setForeground(new Color(239, 68, 68));
        btnExit.setBorder(new LineBorder(COLOR_BORDER, 1));
        btnExit.setFocusPainted(false);
        btnExit.setPreferredSize(new Dimension(90, 42));
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        // Lắng nghe sự kiện
        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> System.exit(0));

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });

        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.checkLogin(username, password);
        if (user != null) {
            POSMainForm posForm = new POSMainForm(user);
            posForm.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không chính xác!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName()) || "Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
