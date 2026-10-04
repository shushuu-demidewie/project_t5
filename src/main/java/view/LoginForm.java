package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import dao.UserDAO;
import model.User;

/**
 * Giao diện Đăng nhập hệ thống bán hàng Trà sữa (POS).
 */
public class LoginForm extends JFrame {

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
        setTitle("ĐĂNG NHẬP HỆ THỐNG - POS TRÀ SỮA");
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel nền chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(480, 90));
        headerPanel.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ BÁN HÀNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("TRÀ SỮA & ĐỒ UỐNG - POS SYSTEM", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubTitle.setForeground(new Color(220, 235, 252));

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblSubTitle, BorderLayout.SOUTH);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Form Input Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Label & Field: Tài khoản
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        formPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(200, 35));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(txtUsername, gbc);

        // Label & Field: Mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(200, 35));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        // Ghi chú mặc định gợi ý
        JLabel lblHint = new JLabel("Gợi ý: Nhập tài khoản và mật khẩu đã lưu trong bảng 'user'");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(new Color(127, 140, 141));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(lblHint, gbc);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(130, 38));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnExit = new JButton("Thoát");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExit.setBackground(new Color(231, 76, 60));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.setPreferredSize(new Dimension(100, 38));
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        // Lắng nghe sự kiện
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnExit.addActionListener(e -> System.exit(0));

        // Nhấn phím Enter trên ô mật khẩu để đăng nhập nhanh
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

    /**
     * Xử lý xác thực đăng nhập qua UserDAO.
     */
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
            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công! Chào mừng: " + user.getFullName() + " (" + user.getRole() + ")",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Mở màn hình POS bán hàng chính
            POSMainForm posForm = new POSMainForm(user);
            posForm.setVisible(true);

            // Ẩn màn hình đăng nhập
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
        // Thiết lập giao diện nhìn đẹp theo hệ thống
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName()) || "Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
