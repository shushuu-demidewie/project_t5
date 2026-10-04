package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.CategoryDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import model.Category;
import model.Order;
import model.OrderDetail;
import model.Product;
import model.User;

/**
 * Giao diện Bán hàng POS chính (Point of Sale).
 * Được chia thành 2 phần:
 * - Bên trái: Danh mục và danh sách sản phẩm/đồ uống để chọn.
 * - Bên phải: Bảng giỏ hàng tạm, tính toán tổng tiền, tiền khách đưa, tiền thừa và nút Thanh toán.
 */
public class POSMainForm extends JFrame {

    // Người dùng hiện tại đang đăng nhập
    private User currentUser;

    // Các lớp DAO
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private OrderDAO orderDAO;

    // Thành phần bên trái: Sản phẩm & Danh mục
    private JComboBox<Category> cboCategories;
    private JTextField txtSearchProduct;
    private JTable tblProducts;
    private DefaultTableModel productTableModel;
    private JSpinner spnQuantity;
    private JButton btnAddToCart;
    private JButton btnRefreshProducts;

    // Thành phần bên phải: Giỏ hàng & Thanh toán
    private JTable tblCart;
    private DefaultTableModel cartTableModel;
    private List<OrderDetail> cartItems; // Danh sách món đang có trong giỏ hàng
    private JButton btnIncreaseQty;
    private JButton btnDecreaseQty;
    private JButton btnRemoveCartItem;
    private JButton btnClearCart;

    // Các thành phần thanh toán
    private JLabel lblOrderCodeVal;
    private JLabel lblCashierVal;
    private JLabel lblOrderDateVal;
    private JLabel lblTotalAmountVal;
    private JTextField txtCustomerCash;
    private JLabel lblChangeMoneyVal;
    private JButton btnCheckout;

    // Định dạng tiền tệ
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");

    public POSMainForm(User user) {
        this.currentUser = (user != null) ? user : new User(1, "admin", "123", "Quản Trị Viên", "ADMIN");
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
        this.orderDAO = new OrderDAO();
        this.cartItems = new ArrayList<>();

        initUI();
        loadCategories();
        loadProducts(productDAO.getAll());
        generateNewOrderCode();
    }

    private void initUI() {
        setTitle("HỆ THỐNG BÁN HÀNG POS - TRÀ SỮA & ĐỒ UỐNG");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel tổng thể
        JPanel rootPanel = new JPanel(new BorderLayout());

        // 1. Header trên cùng
        rootPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Chia đôi màn hình bằng JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.55); // Bên trái chiếm 55%, bên phải chiếm 45%
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(6);

        splitPane.setLeftComponent(createLeftProductPanel());
        splitPane.setRightComponent(createRightCartPanel());

        rootPanel.add(splitPane, BorderLayout.CENTER);

        add(rootPanel);
    }

    /**
     * Tạo Header hiển thị thương hiệu và thông tin nhân viên đăng nhập.
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(1200, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Tiêu đề quán
        JLabel lblTitle = new JLabel("TRÀ SỮA & CAFE - BÁN HÀNG POS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);

        // Thông tin người trực và nút Đăng xuất
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel lblUserInfo = new JLabel("Nhân viên: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        lblUserInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUserInfo.setForeground(new Color(236, 240, 241));

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        userPanel.add(lblUserInfo);
        userPanel.add(btnLogout);
        header.add(userPanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Tạo Panel bên trái: Lọc theo danh mục, Tìm kiếm và Danh sách món ăn/đồ uống.
     */
    private JPanel createLeftProductPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

        // Panel bộ lọc & tìm kiếm trên cùng
        JPanel filterPanel = new JPanel(new BorderLayout(8, 8));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Tra cứu thực đơn",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(52, 73, 94)
        ));

        // Dòng trên: Chọn danh mục
        JPanel catPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        catPanel.add(new JLabel("Danh mục:"));
        cboCategories = new JComboBox<>();
        cboCategories.setPreferredSize(new Dimension(200, 30));
        cboCategories.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboCategories.addActionListener(e -> onCategoryChanged());
        catPanel.add(cboCategories);

        // Dòng dưới: Ô tìm kiếm món theo tên
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Tìm kiếm món:"));
        txtSearchProduct = new JTextField(16);
        txtSearchProduct.setPreferredSize(new Dimension(180, 30));
        txtSearchProduct.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearchProduct.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { onSearchProduct(); }
            @Override
            public void removeUpdate(DocumentEvent e) { onSearchProduct(); }
            @Override
            public void changedUpdate(DocumentEvent e) { onSearchProduct(); }
        });
        searchPanel.add(txtSearchProduct);

        btnRefreshProducts = new JButton("Làm mới");
        btnRefreshProducts.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefreshProducts.setFocusPainted(false);
        btnRefreshProducts.addActionListener(e -> {
            txtSearchProduct.setText("");
            cboCategories.setSelectedIndex(0);
            loadProducts(productDAO.getAll());
        });
        searchPanel.add(btnRefreshProducts);

        filterPanel.add(catPanel, BorderLayout.NORTH);
        filterPanel.add(searchPanel, BorderLayout.SOUTH);
        leftPanel.add(filterPanel, BorderLayout.NORTH);

        // Bảng danh sách sản phẩm
        String[] columns = {"Mã SP", "Tên món", "Danh mục", "Đơn giá (VNĐ)", "Trạng thái"};
        productTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên bảng
            }
        };

        tblProducts = new JTable(productTableModel);
        tblProducts.setRowHeight(28);
        tblProducts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblProducts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblProducts.getTableHeader().setBackground(new Color(230, 240, 250));

        // Căn giữa mã và trạng thái, căn phải đơn giá
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblProducts.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblProducts.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblProducts.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblProducts.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblProducts.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Nhấp đúp chuột để thêm nhanh vào giỏ hàng
        tblProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addProductToCart();
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblProducts);
        leftPanel.add(scrollTable, BorderLayout.CENTER);

        // Thanh công cụ bên dưới bảng sản phẩm (chọn số lượng & nút thêm món)
        JPanel bottomActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomActionPanel.setBackground(new Color(245, 245, 245));
        bottomActionPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        bottomActionPanel.add(new JLabel("Số lượng:"));
        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spnQuantity.setPreferredSize(new Dimension(65, 30));
        spnQuantity.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bottomActionPanel.add(spnQuantity);

        btnAddToCart = new JButton("+ Thêm vào giỏ");
        btnAddToCart.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddToCart.setBackground(new Color(46, 204, 113));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFocusPainted(false);
        btnAddToCart.setPreferredSize(new Dimension(140, 32));
        btnAddToCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddToCart.addActionListener(e -> addProductToCart());
        bottomActionPanel.add(btnAddToCart);

        leftPanel.add(bottomActionPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    /**
     * Tạo Panel bên phải: Bảng giỏ hàng tạm, nút chỉnh số lượng và ô tính tiền thanh toán.
     */
    private JPanel createRightCartPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        // Panel Giỏ hàng tạm
        JPanel cartWrapper = new JPanel(new BorderLayout(5, 5));
        cartWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Hóa đơn / Giỏ hàng tạm",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(52, 73, 94)
        ));

        String[] cartCols = {"STT", "Mã", "Tên món", "Đơn giá", "SL", "Thành tiền"};
        cartTableModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCart = new JTable(cartTableModel);
        tblCart.setRowHeight(28);
        tblCart.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblCart.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblCart.getTableHeader().setBackground(new Color(254, 249, 231));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        tblCart.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblCart.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblCart.getColumnModel().getColumn(1).setPreferredWidth(45);
        tblCart.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblCart.getColumnModel().getColumn(3).setPreferredWidth(85);
        tblCart.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblCart.getColumnModel().getColumn(4).setPreferredWidth(45);
        tblCart.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblCart.getColumnModel().getColumn(5).setPreferredWidth(95);
        tblCart.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        JScrollPane cartScroll = new JScrollPane(tblCart);
        cartWrapper.add(cartScroll, BorderLayout.CENTER);

        // Thanh công cụ thao tác trên giỏ hàng (tăng/giảm/xóa)
        JPanel cartControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));

        btnIncreaseQty = new JButton("(+)");
        btnIncreaseQty.setToolTipText("Tăng thêm 1");
        btnIncreaseQty.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnIncreaseQty.addActionListener(e -> changeQuantity(1));

        btnDecreaseQty = new JButton("(-)");
        btnDecreaseQty.setToolTipText("Giảm đi 1");
        btnDecreaseQty.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDecreaseQty.addActionListener(e -> changeQuantity(-1));

        btnRemoveCartItem = new JButton("Xóa món");
        btnRemoveCartItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRemoveCartItem.setBackground(new Color(231, 76, 60));
        btnRemoveCartItem.setForeground(Color.WHITE);
        btnRemoveCartItem.setFocusPainted(false);
        btnRemoveCartItem.addActionListener(e -> removeSelectedCartItem());

        btnClearCart = new JButton("Xóa giỏ");
        btnClearCart.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClearCart.setBackground(new Color(149, 165, 166));
        btnClearCart.setForeground(Color.WHITE);
        btnClearCart.setFocusPainted(false);
        btnClearCart.addActionListener(e -> clearCart());

        cartControlPanel.add(btnDecreaseQty);
        cartControlPanel.add(btnIncreaseQty);
        cartControlPanel.add(btnRemoveCartItem);
        cartControlPanel.add(btnClearCart);

        cartWrapper.add(cartControlPanel, BorderLayout.SOUTH);
        rightPanel.add(cartWrapper, BorderLayout.CENTER);

        // Panel Thanh toán & Hóa đơn
        JPanel paymentPanel = createPaymentPanel();
        rightPanel.add(paymentPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    /**
     * Tạo Panel chi tiết thanh toán tiền, nhập tiền khách đưa và nút Thanh toán.
     */
    private JPanel createPaymentPanel() {
        JPanel paymentPanel = new JPanel(new BorderLayout(5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Thanh toán & Hóa đơn",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(52, 73, 94)
        ));

        JPanel formGrid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Mã hóa đơn & Thu ngân
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        formGrid.add(new JLabel("Mã HĐ:"), gbc);

        lblOrderCodeVal = new JLabel("HD000000");
        lblOrderCodeVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblOrderCodeVal.setForeground(new Color(41, 128, 185));
        gbc.gridx = 1; gbc.weightx = 0.3;
        formGrid.add(lblOrderCodeVal, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        formGrid.add(new JLabel("Thu ngân:"), gbc);

        lblCashierVal = new JLabel(currentUser.getFullName());
        lblCashierVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 3; gbc.weightx = 0.3;
        formGrid.add(lblCashierVal, gbc);

        // Dòng 2: Thời gian tạo đơn
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        formGrid.add(new JLabel("Thời gian:"), gbc);

        lblOrderDateVal = new JLabel(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        lblOrderDateVal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.8;
        formGrid.add(lblOrderDateVal, gbc);
        gbc.gridwidth = 1;

        // Dòng 3: TỔNG TIỀN THANH TOÁN
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblTotalTitle = new JLabel("TỔNG TIỀN:");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formGrid.add(lblTotalTitle, gbc);

        lblTotalAmountVal = new JLabel("0 VNĐ");
        lblTotalAmountVal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalAmountVal.setForeground(new Color(231, 76, 60));
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.7;
        formGrid.add(lblTotalAmountVal, gbc);
        gbc.gridwidth = 1;

        // Dòng 4: TIỀN KHÁCH ĐƯA
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblCashTitle = new JLabel("Tiền khách đưa:");
        lblCashTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formGrid.add(lblCashTitle, gbc);

        txtCustomerCash = new JTextField("0");
        txtCustomerCash.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtCustomerCash.setPreferredSize(new Dimension(150, 32));
        txtCustomerCash.setHorizontalAlignment(JTextField.RIGHT);
        txtCustomerCash.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { calculateChangeMoney(); }
            @Override
            public void removeUpdate(DocumentEvent e) { calculateChangeMoney(); }
            @Override
            public void changedUpdate(DocumentEvent e) { calculateChangeMoney(); }
        });
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.7;
        formGrid.add(txtCustomerCash, gbc);
        gbc.gridwidth = 1;

        // Dòng 5: TIỀN THỪA TRẢ KHÁCH
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        JLabel lblChangeTitle = new JLabel("Tiền thừa trả khách:");
        lblChangeTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formGrid.add(lblChangeTitle, gbc);

        lblChangeMoneyVal = new JLabel("0 VNĐ");
        lblChangeMoneyVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblChangeMoneyVal.setForeground(new Color(39, 174, 96));
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.7;
        formGrid.add(lblChangeMoneyVal, gbc);
        gbc.gridwidth = 1;

        paymentPanel.add(formGrid, BorderLayout.CENTER);

        // Nút Thanh Toán
        btnCheckout = new JButton("XÁC NHẬN THANH TOÁN (F9)");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.setBackground(new Color(39, 174, 96));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setPreferredSize(new Dimension(200, 44));
        btnCheckout.setFocusPainted(false);
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.addActionListener(e -> handleCheckout());

        paymentPanel.add(btnCheckout, BorderLayout.SOUTH);

        return paymentPanel;
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ DỮ LIỆU & SỰ KIỆN GIAO DIỆN
    // ==========================================

    /**
     * Tải danh mục vào ComboBox cboCategories.
     */
    private void loadCategories() {
        cboCategories.removeAllItems();
        // Tùy chọn xem tất cả
        cboCategories.addItem(new Category(0, "-- Tất cả danh mục --"));

        List<Category> list = categoryDAO.getAll();
        for (Category cat : list) {
            cboCategories.addItem(cat);
        }
    }

    /**
     * Tải danh sách sản phẩm lên JTable bên trái.
     */
    private void loadProducts(List<Product> products) {
        productTableModel.setRowCount(0);
        for (Product p : products) {
            productTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getProductName(),
                    (p.getCategoryName() != null ? p.getCategoryName() : "Khác"),
                    currencyFormat.format(p.getPrice()),
                    p.getStatus()
            });
        }
    }

    /**
     * Xử lý khi thay đổi danh mục trên ComboBox.
     */
    private void onCategoryChanged() {
        Category selected = (Category) cboCategories.getSelectedItem();
        if (selected == null || selected.getId() == 0) {
            loadProducts(productDAO.getAll());
        } else {
            loadProducts(productDAO.getByCategory(selected.getId()));
        }
    }

    /**
     * Tìm kiếm sản phẩm khi người dùng gõ vào ô tìm kiếm.
     */
    private void onSearchProduct() {
        String keyword = txtSearchProduct.getText().trim();
        if (keyword.isEmpty()) {
            onCategoryChanged();
        } else {
            loadProducts(productDAO.searchByName(keyword));
        }
    }

    /**
     * Thêm sản phẩm được chọn từ bảng sản phẩm vào giỏ hàng tạm.
     */
    private void addProductToCart() {
        int selectedRow = tblProducts.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một món trong danh sách thực đơn để thêm!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) productTableModel.getValueAt(selectedRow, 0);
        String productName = (String) productTableModel.getValueAt(selectedRow, 1);
        String priceStr = (String) productTableModel.getValueAt(selectedRow, 3);
        String status = (String) productTableModel.getValueAt(selectedRow, 4);

        if ("Hết hàng".equalsIgnoreCase(status) || "Inactive".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                    "Món [" + productName + "] hiện đang hết hàng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double unitPrice = 0;
        try {
            unitPrice = currencyFormat.parse(priceStr).doubleValue();
        } catch (Exception ex) {
            unitPrice = 0;
        }

        int qtyToAdd = (int) spnQuantity.getValue();

        // Kiểm tra xem món đã có trong giỏ hàng tạm chưa
        boolean exists = false;
        for (OrderDetail detail : cartItems) {
            if (detail.getProductId() == productId) {
                detail.setQuantity(detail.getQuantity() + qtyToAdd);
                exists = true;
                break;
            }
        }

        if (!exists) {
            OrderDetail newDetail = new OrderDetail(productId, productName, qtyToAdd, unitPrice);
            cartItems.add(newDetail);
        }

        // Cập nhật lại giao diện giỏ hàng
        updateCartTable();

        // Reset lại số lượng về 1
        spnQuantity.setValue(1);
    }

    /**
     * Cập nhật JTable giỏ hàng và tổng tiền.
     */
    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0;
        int stt = 1;

        for (OrderDetail detail : cartItems) {
            double lineTotal = detail.getSubTotal();
            total += lineTotal;
            cartTableModel.addRow(new Object[]{
                    stt++,
                    detail.getProductId(),
                    detail.getProductName(),
                    currencyFormat.format(detail.getUnitPrice()),
                    detail.getQuantity(),
                    currencyFormat.format(lineTotal)
            });
        }

        lblTotalAmountVal.setText(currencyFormat.format(total) + " VNĐ");
        calculateChangeMoney();
    }

    /**
     * Tăng hoặc giảm số lượng của món đang chọn trong giỏ hàng.
     */
    private void changeQuantity(int delta) {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món trong giỏ hàng để thay đổi số lượng!");
            return;
        }

        OrderDetail detail = cartItems.get(selectedRow);
        int newQty = detail.getQuantity() + delta;
        if (newQty <= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Số lượng bằng 0, bạn có muốn xóa món [" + detail.getProductName() + "] khỏi giỏ hàng?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                cartItems.remove(selectedRow);
            }
        } else {
            detail.setQuantity(newQty);
        }

        updateCartTable();
    }

    /**
     * Xóa món đang chọn khỏi giỏ hàng.
     */
    private void removeSelectedCartItem() {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa trong giỏ hàng!");
            return;
        }

        cartItems.remove(selectedRow);
        updateCartTable();
    }

    /**
     * Xóa toàn bộ giỏ hàng tạm.
     */
    private void clearCart() {
        if (cartItems.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tất cả các món trong giỏ hàng?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            updateCartTable();
        }
    }

    /**
     * Tính tiền thừa trả lại khách hàng.
     */
    private void calculateChangeMoney() {
        try {
            double total = calculateTotalCartAmount();
            String cashText = txtCustomerCash.getText().replaceAll("[^0-9.]", "").trim();
            double customerCash = cashText.isEmpty() ? 0 : Double.parseDouble(cashText);

            double change = customerCash - total;
            if (customerCash < total) {
                lblChangeMoneyVal.setText("Còn thiếu: " + currencyFormat.format(Math.abs(change)) + " VNĐ");
                lblChangeMoneyVal.setForeground(new Color(231, 76, 60)); // Màu đỏ
            } else {
                lblChangeMoneyVal.setText(currencyFormat.format(change) + " VNĐ");
                lblChangeMoneyVal.setForeground(new Color(39, 174, 96)); // Màu xanh lá
            }
        } catch (Exception ex) {
            lblChangeMoneyVal.setText("0 VNĐ");
        }
    }

    /**
     * Tính tổng số tiền hiện tại của giỏ hàng.
     */
    private double calculateTotalCartAmount() {
        double total = 0;
        for (OrderDetail item : cartItems) {
            total += item.getSubTotal();
        }
        return total;
    }

    /**
     * Sinh mã hóa đơn mới (Ví dụ: HD20261004-1234).
     */
    private void generateNewOrderCode() {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        lblOrderCodeVal.setText("HD" + timestamp);
        lblOrderDateVal.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
    }

    /**
     * Xử lý xác nhận thanh toán và ghi vào CSDL qua JDBC Transaction.
     */
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Giỏ hàng đang trống! Vui lòng chọn món trước khi thanh toán.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = calculateTotalCartAmount();
        String cashText = txtCustomerCash.getText().replaceAll("[^0-9.]", "").trim();
        double customerCash = cashText.isEmpty() ? 0 : Double.parseDouble(cashText);

        if (customerCash < total) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Tiền khách đưa nhỏ hơn tổng tiền đơn hàng! Bạn vẫn muốn tiếp tục thanh toán (Ghi nợ)?",
                    "Cảnh báo chưa đủ tiền",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (option != JOptionPane.YES_OPTION) {
                txtCustomerCash.requestFocus();
                return;
            }
        }

        // Tạo đối tượng Order
        Order order = new Order();
        order.setOrderCode(lblOrderCodeVal.getText());
        order.setUserId(currentUser.getId());
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setTotalAmount(total);

        // Gọi OrderDAO áp dụng Transaction lưu cả Order và List<OrderDetail>
        int newOrderId = orderDAO.createOrder(order, cartItems);

        if (newOrderId > 0) {
            // Hiển thị hóa đơn chi tiết
            showInvoiceDialog(order, cartItems, customerCash, (customerCash - total));

            // Làm mới giỏ hàng sau khi thanh toán thành công
            cartItems.clear();
            updateCartTable();
            txtCustomerCash.setText("0");
            lblChangeMoneyVal.setText("0 VNĐ");
            generateNewOrderCode();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Thanh toán thất bại do lỗi cơ sở dữ liệu! Vui lòng kiểm tra lại kết nối XAMPP / MySQL.",
                    "Lỗi thanh toán",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hiển thị popup hóa đơn xem trước / in hóa đơn.
     */
    private void showInvoiceDialog(Order order, List<OrderDetail> details, double customerCash, double change) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("          HÓA ĐƠN THANH TOÁN             \n");
        sb.append("        TRÀ SỮA & COFFEE POS             \n");
        sb.append("=========================================\n");
        sb.append("Mã hóa đơn: ").append(order.getOrderCode()).append("\n");
        sb.append("Ngày tạo:   ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(order.getOrderDate())).append("\n");
        sb.append("Thu ngân:   ").append(currentUser.getFullName()).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-18s %4s %8s %9s\n", "Tên món", "SL", "Đ.Giá", "T.Tiền"));
        sb.append("-----------------------------------------\n");

        for (OrderDetail d : details) {
            String name = d.getProductName();
            if (name.length() > 16) {
                name = name.substring(0, 14) + "..";
            }
            sb.append(String.format("%-18s %4d %8s %9s\n",
                    name,
                    d.getQuantity(),
                    currencyFormat.format(d.getUnitPrice()),
                    currencyFormat.format(d.getSubTotal())));
        }

        sb.append("-----------------------------------------\n");
        sb.append(String.format("TỔNG TIỀN:              %15s đ\n", currencyFormat.format(order.getTotalAmount())));
        sb.append(String.format("Tiền khách đưa:         %15s đ\n", currencyFormat.format(customerCash)));
        sb.append(String.format("Tiền trả lại:           %15s đ\n", currencyFormat.format(Math.max(0, change))));
        sb.append("=========================================\n");
        sb.append("       CẢM ƠN QUÝ KHÁCH & HẸN GẶP LẠI!   \n");
        sb.append("=========================================\n");

        javax.swing.JTextArea txtInvoice = new javax.swing.JTextArea(sb.toString());
        txtInvoice.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInvoice.setEditable(false);
        txtInvoice.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(txtInvoice);
        scroll.setPreferredSize(new Dimension(380, 450));

        JOptionPane.showMessageDialog(this,
                scroll,
                "THANH TOÁN THÀNH CÔNG",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Xử lý đăng xuất và quay lại màn hình Login.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất khỏi hệ thống?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginForm().setVisible(true);
        }
    }

    /**
     * Phương thức main để chạy thử nghiệm độc lập màn hình POS.
     */
    public static void main(String[] args) {
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
            new POSMainForm(null).setVisible(true);
        });
    }
}
