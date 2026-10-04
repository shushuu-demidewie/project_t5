package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.CategoryDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import model.Category;
import model.Order;
import model.OrderDetail;
import model.Product;
import model.User;

/**
 * Giao diện Bán hàng POS chính (Point of Sale) - Thiết kế Hiện đại & Chuyên nghiệp.
 */
public class POSMainForm extends JFrame {

    // Màu sắc chủ đạo (Luxury Cafe Emerald & Slate Theme)
    private static final Color COLOR_PRIMARY = new Color(15, 118, 110);      // Emerald đậm #0f766e
    private static final Color COLOR_PRIMARY_LIGHT = new Color(204, 251, 241); // Mint sáng #ccfbf1
    private static final Color COLOR_ACCENT = new Color(16, 185, 129);       // Xanh lá tươi #10b981
    private static final Color COLOR_DARK = new Color(15, 23, 42);           // Slate đen #0f172a
    private static final Color COLOR_BG = new Color(248, 250, 252);          // Xám nhạt hiện đại #f8fafc
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(226, 232, 240);      // Viền thanh thoát #e2e8f0
    private static final Color COLOR_DANGER = new Color(239, 68, 68);         // Đỏ tươi #ef4444
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);   // Chữ phụ #64748b

    // Người dùng hiện tại
    private User currentUser;

    // Các lớp DAO
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private OrderDAO orderDAO;

    // Bên trái: Thực đơn món
    private JComboBox<Category> cboCategories;
    private JTextField txtSearchProduct;
    private JTable tblProducts;
    private DefaultTableModel productTableModel;
    private JSpinner spnQuantity;
    private JButton btnAddToCart;
    private JButton btnRefreshProducts;

    // Bên phải: Giỏ hàng & Thanh toán
    private JTable tblCart;
    private DefaultTableModel cartTableModel;
    private List<OrderDetail> cartItems;
    private JLabel lblCartCountBadge;
    private JButton btnIncreaseQty;
    private JButton btnDecreaseQty;
    private JButton btnRemoveCartItem;
    private JButton btnClearCart;

    // Thành phần thanh toán
    private JLabel lblOrderCodeVal;
    private JLabel lblCashierVal;
    private JLabel lblOrderDateVal;
    private JLabel lblTotalAmountVal;
    private JTextField txtCustomerCash;
    private JLabel lblChangeMoneyVal;
    private JButton btnCheckout;
    private JLabel lblClock;

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
        startClock();
    }

    private void initUI() {
        setTitle("HỆ THỐNG BÁN HÀNG POS - TRÀ SỮA & CAFE STATION");
        setSize(1260, 780);
        setMinimumSize(new Dimension(1080, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BG);

        // 1. Header trên cùng
        rootPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Chia 2 cột tỷ lệ 55% - 45%
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.56);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(6);
        splitPane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        splitPane.setBackground(COLOR_BG);

        splitPane.setLeftComponent(createLeftProductPanel());
        splitPane.setRightComponent(createRightCartPanel());

        rootPanel.add(splitPane, BorderLayout.CENTER);
        add(rootPanel);

        // Hỗ trợ phím tắt F9 để thanh toán nhanh
        setupKeyboardShortcuts();
    }

    /**
     * Header thương hiệu, đồng hồ thời gian thực và thông tin nhân viên
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setPreferredSize(new Dimension(1260, 66));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Logo & Tên quán bên trái
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("🧋");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        brandPanel.add(lblLogo);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        JLabel lblTitle = new JLabel("BOBA & COFFEE STATION POS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("Hệ thống bán hàng & Thu ngân trực tuyến v2.0");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubTitle.setForeground(COLOR_PRIMARY_LIGHT);

        titleBox.add(lblTitle);
        titleBox.add(lblSubTitle);
        brandPanel.add(titleBox);

        header.add(brandPanel, BorderLayout.WEST);

        // Thông tin người trực, đồng hồ & nút chức năng bên phải
        JPanel rightMetaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        rightMetaPanel.setOpaque(false);

        // Đồng hồ điện tử
        lblClock = new JLabel("--:--:--");
        lblClock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblClock.setForeground(COLOR_PRIMARY);
        lblClock.setOpaque(true);
        lblClock.setBackground(COLOR_PRIMARY_LIGHT);
        lblClock.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(94, 234, 212), 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        rightMetaPanel.add(lblClock);

        // Huy hiệu thu ngân
        JLabel lblUserAvatar = new JLabel(currentUser.getFullName().substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        lblUserAvatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserAvatar.setForeground(Color.WHITE);
        lblUserAvatar.setOpaque(true);
        lblUserAvatar.setBackground(new Color(245, 158, 11)); // Màu cam hổ phách
        lblUserAvatar.setPreferredSize(new Dimension(30, 30));
        lblUserAvatar.setBorder(new LineBorder(Color.WHITE, 1));

        JLabel lblUserInfo = new JLabel(currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        lblUserInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserInfo.setForeground(Color.WHITE);

        rightMetaPanel.add(lblUserAvatar);
        rightMetaPanel.add(lblUserInfo);

        // Nút xem lịch sử hóa đơn
        JButton btnHistory = createStyledButton("📋 Lịch sử đơn", new Color(13, 148, 136), Color.WHITE, 12);
        btnHistory.addActionListener(e -> showOrderHistoryDialog());
        rightMetaPanel.add(btnHistory);

        // Nút đăng xuất
        JButton btnLogout = createStyledButton("🚪 Đăng xuất", COLOR_DANGER, Color.WHITE, 12);
        btnLogout.addActionListener(e -> logout());
        rightMetaPanel.add(btnLogout);

        header.add(rightMetaPanel, BorderLayout.EAST);
        return header;
    }

    /**
     * Cột trái: Bộ lọc thực đơn & Danh sách món ăn/đồ uống
     */
    private JPanel createLeftProductPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        leftPanel.setBackground(COLOR_CARD);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        // Tiêu đề cột & thanh tìm kiếm
        JPanel topBox = new JPanel(new BorderLayout(6, 6));
        topBox.setOpaque(false);

        JLabel lblMenuTitle = new JLabel("📋 THỰC ĐƠN ĐỒ UỐNG");
        lblMenuTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMenuTitle.setForeground(COLOR_DARK);
        lblMenuTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        topBox.add(lblMenuTitle, BorderLayout.NORTH);

        // Dòng lọc danh mục & tìm kiếm
        JPanel filterRow = new JPanel(new BorderLayout(8, 0));
        filterRow.setOpaque(false);

        cboCategories = new JComboBox<>();
        cboCategories.setPreferredSize(new Dimension(190, 34));
        cboCategories.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cboCategories.setBackground(Color.WHITE);
        cboCategories.addActionListener(e -> onCategoryChanged());
        filterRow.add(cboCategories, BorderLayout.WEST);

        // Ô tìm kiếm món
        JPanel searchBox = new JPanel(new BorderLayout(4, 0));
        searchBox.setOpaque(false);

        txtSearchProduct = new JTextField();
        txtSearchProduct.setPreferredSize(new Dimension(180, 34));
        txtSearchProduct.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearchProduct.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        txtSearchProduct.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { onSearchProduct(); }
            @Override
            public void removeUpdate(DocumentEvent e) { onSearchProduct(); }
            @Override
            public void changedUpdate(DocumentEvent e) { onSearchProduct(); }
        });

        btnRefreshProducts = createStyledButton("Làm mới", new Color(241, 245, 249), COLOR_DARK, 12);
        btnRefreshProducts.setBorder(new LineBorder(COLOR_BORDER, 1));
        btnRefreshProducts.addActionListener(e -> {
            txtSearchProduct.setText("");
            if (cboCategories.getItemCount() > 0) cboCategories.setSelectedIndex(0);
            loadProducts(productDAO.getAll());
        });

        searchBox.add(txtSearchProduct, BorderLayout.CENTER);
        searchBox.add(btnRefreshProducts, BorderLayout.EAST);
        filterRow.add(searchBox, BorderLayout.CENTER);

        topBox.add(filterRow, BorderLayout.CENTER);
        leftPanel.add(topBox, BorderLayout.NORTH);

        // Bảng danh sách sản phẩm
        String[] columns = {"Mã SP", "Tên món đồ uống", "Danh mục", "Đơn giá", "Trạng thái"};
        productTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblProducts = new JTable(productTableModel);
        styleTable(tblProducts);

        // Căn chỉnh cột
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightRender = new DefaultTableCellRenderer();
        rightRender.setHorizontalAlignment(SwingConstants.RIGHT);

        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(55);
        tblProducts.getColumnModel().getColumn(0).setCellRenderer(centerRender);

        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(210);

        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(130);
        tblProducts.getColumnModel().getColumn(2).setCellRenderer(centerRender);

        tblProducts.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblProducts.getColumnModel().getColumn(3).setCellRenderer(new PriceCellRenderer());

        tblProducts.getColumnModel().getColumn(4).setPreferredWidth(95);
        tblProducts.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        // Nhấp đúp chuột để thêm nhanh
        tblProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addProductToCart();
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblProducts);
        scrollTable.setBorder(new LineBorder(COLOR_BORDER, 1));
        scrollTable.getViewport().setBackground(Color.WHITE);
        leftPanel.add(scrollTable, BorderLayout.CENTER);

        // Thanh công cụ bên dưới bảng món
        JPanel bottomActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        bottomActionPanel.setBackground(COLOR_BG);
        bottomActionPanel.setBorder(new LineBorder(COLOR_BORDER, 1));

        JLabel lblQty = new JLabel("Số lượng chọn:");
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bottomActionPanel.add(lblQty);

        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spnQuantity.setPreferredSize(new Dimension(65, 34));
        spnQuantity.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bottomActionPanel.add(spnQuantity);

        btnAddToCart = createStyledButton("➕ Thêm vào giỏ", COLOR_ACCENT, Color.WHITE, 13);
        btnAddToCart.setPreferredSize(new Dimension(160, 36));
        btnAddToCart.addActionListener(e -> addProductToCart());
        bottomActionPanel.add(btnAddToCart);

        leftPanel.add(bottomActionPanel, BorderLayout.SOUTH);
        return leftPanel;
    }

    /**
     * Cột phải: Bảng giỏ hàng tạm, nút chỉnh SL & Khung thanh toán
     */
    private JPanel createRightCartPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBackground(COLOR_CARD);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        // Tiêu đề giỏ hàng
        JPanel cartHeader = new JPanel(new BorderLayout());
        cartHeader.setOpaque(false);

        JLabel lblCartTitle = new JLabel("🛒 ĐƠN HÀNG TẠM TÍNH");
        lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCartTitle.setForeground(COLOR_DARK);

        lblCartCountBadge = new JLabel("0 món");
        lblCartCountBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCartCountBadge.setForeground(COLOR_PRIMARY);
        lblCartCountBadge.setOpaque(true);
        lblCartCountBadge.setBackground(COLOR_PRIMARY_LIGHT);
        lblCartCountBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JPanel cartTitleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cartTitleRow.setOpaque(false);
        cartTitleRow.add(lblCartTitle);
        cartTitleRow.add(lblCartCountBadge);

        cartHeader.add(cartTitleRow, BorderLayout.WEST);

        // Nút xóa toàn bộ giỏ
        btnClearCart = createStyledButton("Xóa giỏ", new Color(254, 226, 226), COLOR_DANGER, 11);
        btnClearCart.setBorder(new LineBorder(new Color(254, 202, 202), 1));
        btnClearCart.addActionListener(e -> clearCart());
        cartHeader.add(btnClearCart, BorderLayout.EAST);

        // Bọc phần bảng giỏ hàng
        JPanel cartWrapper = new JPanel(new BorderLayout(6, 6));
        cartWrapper.setOpaque(false);
        cartWrapper.add(cartHeader, BorderLayout.NORTH);

        String[] cartCols = {"STT", "Mã", "Tên món", "Đơn giá", "SL", "Thành tiền"};
        cartTableModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCart = new JTable(cartTableModel);
        styleTable(tblCart);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        tblCart.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblCart.getColumnModel().getColumn(0).setCellRenderer(centerRender);

        tblCart.getColumnModel().getColumn(1).setPreferredWidth(45);
        tblCart.getColumnModel().getColumn(1).setCellRenderer(centerRender);

        tblCart.getColumnModel().getColumn(2).setPreferredWidth(170);

        tblCart.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblCart.getColumnModel().getColumn(3).setCellRenderer(new PriceCellRenderer());

        tblCart.getColumnModel().getColumn(4).setPreferredWidth(45);
        tblCart.getColumnModel().getColumn(4).setCellRenderer(centerRender);

        tblCart.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblCart.getColumnModel().getColumn(5).setCellRenderer(new PriceCellRenderer());

        JScrollPane cartScroll = new JScrollPane(tblCart);
        cartScroll.setBorder(new LineBorder(COLOR_BORDER, 1));
        cartScroll.getViewport().setBackground(Color.WHITE);
        cartWrapper.add(cartScroll, BorderLayout.CENTER);

        // Thanh thao tác trên từng dòng giỏ hàng (+ / - / Xóa món)
        JPanel cartControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        cartControlPanel.setOpaque(false);

        btnDecreaseQty = createStyledButton("➖ Giảm 1", Color.WHITE, COLOR_DARK, 11);
        btnDecreaseQty.setBorder(new LineBorder(COLOR_BORDER, 1));
        btnDecreaseQty.addActionListener(e -> changeQuantity(-1));

        btnIncreaseQty = createStyledButton("➕ Tăng 1", Color.WHITE, COLOR_DARK, 11);
        btnIncreaseQty.setBorder(new LineBorder(COLOR_BORDER, 1));
        btnIncreaseQty.addActionListener(e -> changeQuantity(1));

        btnRemoveCartItem = createStyledButton("✕ Xóa món", COLOR_DANGER, Color.WHITE, 11);
        btnRemoveCartItem.addActionListener(e -> removeSelectedCartItem());

        cartControlPanel.add(btnDecreaseQty);
        cartControlPanel.add(btnIncreaseQty);
        cartControlPanel.add(btnRemoveCartItem);

        cartWrapper.add(cartControlPanel, BorderLayout.SOUTH);
        rightPanel.add(cartWrapper, BorderLayout.CENTER);

        // Khung Thanh Toán & Hóa Đơn
        rightPanel.add(createPaymentPanel(), BorderLayout.SOUTH);
        return rightPanel;
    }

    /**
     * Khung tính tiền & Hóa đơn nổi bật
     */
    private JPanel createPaymentPanel() {
        JPanel paymentPanel = new JPanel(new BorderLayout(8, 8));
        paymentPanel.setBackground(COLOR_BG);
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        // Form thông tin hóa đơn
        JPanel metaGrid = new JPanel(new GridLayout(2, 2, 10, 4));
        metaGrid.setOpaque(false);

        lblOrderCodeVal = new JLabel("HD000000");
        lblOrderCodeVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblOrderCodeVal.setForeground(COLOR_PRIMARY);

        lblCashierVal = new JLabel(currentUser.getFullName());
        lblCashierVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCashierVal.setForeground(COLOR_DARK);

        lblOrderDateVal = new JLabel(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        lblOrderDateVal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblOrderDateVal.setForeground(COLOR_TEXT_MUTED);

        JLabel lblMeta1 = new JLabel("Mã HĐ: ");
        lblMeta1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMeta1.setForeground(COLOR_TEXT_MUTED);
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p1.setOpaque(false);
        p1.add(lblMeta1);
        p1.add(lblOrderCodeVal);

        JLabel lblMeta2 = new JLabel("Thu ngân: ");
        lblMeta2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMeta2.setForeground(COLOR_TEXT_MUTED);
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p2.setOpaque(false);
        p2.add(lblMeta2);
        p2.add(lblCashierVal);

        metaGrid.add(p1);
        metaGrid.add(p2);
        metaGrid.add(lblOrderDateVal);

        // THẺ TỔNG TIỀN NỔI BẬT
        JPanel totalBanner = new JPanel(new BorderLayout(8, 0));
        totalBanner.setBackground(COLOR_DARK);
        totalBanner.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel lblTotalTitle = new JLabel("TỔNG TIỀN THANH TOÁN");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalTitle.setForeground(new Color(148, 163, 184)); // Slate-400

        lblTotalAmountVal = new JLabel("0 đ", SwingConstants.RIGHT);
        lblTotalAmountVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotalAmountVal.setForeground(new Color(52, 211, 153)); // Xanh ngọc nổi bật

        totalBanner.add(lblTotalTitle, BorderLayout.WEST);
        totalBanner.add(lblTotalAmountVal, BorderLayout.EAST);

        // Khu vực Nhập Tiền Khách Đưa & Gợi ý tiền nhanh
        JPanel cashBox = new JPanel(new BorderLayout(4, 6));
        cashBox.setOpaque(false);

        // Các nút chọn nhanh mệnh giá
        JPanel quickCashRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        quickCashRow.setOpaque(false);
        quickCashRow.add(new JLabel("Mệnh giá nhanh: "));

        String[] quickCashTags = {"Vừa đủ", "50.000", "100.000", "200.000", "500.000"};
        for (String tag : quickCashTags) {
            JButton btnTag = createStyledButton(tag, Color.WHITE, COLOR_DARK, 11);
            btnTag.setBorder(new LineBorder(COLOR_BORDER, 1));
            btnTag.addActionListener(e -> {
                if ("Vừa đủ".equals(tag)) {
                    txtCustomerCash.setText(currencyFormat.format(calculateTotalCartAmount()));
                } else {
                    txtCustomerCash.setText(tag);
                }
                calculateChangeMoney();
            });
            quickCashRow.add(btnTag);
        }

        JPanel cashInputRow = new JPanel(new BorderLayout(8, 0));
        cashInputRow.setOpaque(false);

        JLabel lblCashTitle = new JLabel("Tiền khách đưa (VNĐ):");
        lblCashTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCashTitle.setForeground(COLOR_DARK);

        txtCustomerCash = new JTextField("0");
        txtCustomerCash.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtCustomerCash.setPreferredSize(new Dimension(200, 36));
        txtCustomerCash.setHorizontalAlignment(JTextField.RIGHT);
        txtCustomerCash.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        txtCustomerCash.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { calculateChangeMoney(); }
            @Override
            public void removeUpdate(DocumentEvent e) { calculateChangeMoney(); }
            @Override
            public void changedUpdate(DocumentEvent e) { calculateChangeMoney(); }
        });

        cashInputRow.add(lblCashTitle, BorderLayout.WEST);
        cashInputRow.add(txtCustomerCash, BorderLayout.CENTER);

        cashBox.add(quickCashRow, BorderLayout.NORTH);
        cashBox.add(cashInputRow, BorderLayout.CENTER);

        // Hàng tiền thừa trả khách
        JPanel changeRow = new JPanel(new BorderLayout(8, 0));
        changeRow.setOpaque(false);

        JLabel lblChangeTitle = new JLabel("Tiền thừa trả khách:");
        lblChangeTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblChangeTitle.setForeground(COLOR_DARK);

        lblChangeMoneyVal = new JLabel("0 đ", SwingConstants.RIGHT);
        lblChangeMoneyVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblChangeMoneyVal.setForeground(COLOR_ACCENT);

        changeRow.add(lblChangeTitle, BorderLayout.WEST);
        changeRow.add(lblChangeMoneyVal, BorderLayout.EAST);

        // Nút Thanh toán lớn
        btnCheckout = createStyledButton("⚡ XÁC NHẬN THANH TOÁN (F9)", COLOR_ACCENT, Color.WHITE, 15);
        btnCheckout.setPreferredSize(new Dimension(220, 48));
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.addActionListener(e -> handleCheckout());

        // Ghép các phần vào paymentPanel
        JPanel centerStack = new JPanel(new GridLayout(4, 1, 0, 8));
        centerStack.setOpaque(false);
        centerStack.add(metaGrid);
        centerStack.add(totalBanner);
        centerStack.add(cashBox);
        centerStack.add(changeRow);

        paymentPanel.add(centerStack, BorderLayout.CENTER);
        paymentPanel.add(btnCheckout, BorderLayout.SOUTH);

        return paymentPanel;
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ DỮ LIỆU & SỰ KIỆN GIAO DIỆN
    // ==========================================

    private void loadCategories() {
        cboCategories.removeAllItems();
        cboCategories.addItem(new Category(0, "✨ -- Tất cả danh mục --"));

        List<Category> list = categoryDAO.getAll();
        for (Category cat : list) {
            String prefix = "🥤 ";
            if (cat.getCategoryName().contains("Trà Sữa")) prefix = "🧋 ";
            else if (cat.getCategoryName().contains("Quả") || cat.getCategoryName().contains("Trái")) prefix = "🍑 ";
            else if (cat.getCategoryName().contains("Cà Phê") || cat.getCategoryName().contains("Cafe")) prefix = "☕ ";
            else if (cat.getCategoryName().contains("Đá Xay")) prefix = "🍧 ";
            else if (cat.getCategoryName().contains("Topping")) prefix = "🍮 ";

            cboCategories.addItem(new Category(cat.getId(), prefix + cat.getCategoryName()));
        }
    }

    private void loadProducts(List<Product> products) {
        productTableModel.setRowCount(0);
        for (Product p : products) {
            String status = p.getStatus();
            if (status == null || "0".equals(status) || status.trim().isEmpty() || "1".equals(status)) {
                status = "Còn hàng";
            }

            productTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getProductName(),
                    (p.getCategoryName() != null ? p.getCategoryName() : "Khác"),
                    currencyFormat.format(p.getPrice()) + " đ",
                    status
            });
        }
    }

    private void onCategoryChanged() {
        Category selected = (Category) cboCategories.getSelectedItem();
        if (selected == null || selected.getId() == 0) {
            loadProducts(productDAO.getAll());
        } else {
            loadProducts(productDAO.getByCategory(selected.getId()));
        }
    }

    private void onSearchProduct() {
        String keyword = txtSearchProduct.getText().trim();
        if (keyword.isEmpty()) {
            onCategoryChanged();
        } else {
            loadProducts(productDAO.searchByName(keyword));
        }
    }

    private void addProductToCart() {
        int selectedRow = tblProducts.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhấp chọn một món trong danh sách thực đơn để thêm!",
                    "Nhắc nhở",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) productTableModel.getValueAt(selectedRow, 0);
        String productName = (String) productTableModel.getValueAt(selectedRow, 1);
        String priceStr = (String) productTableModel.getValueAt(selectedRow, 3);
        String status = (String) productTableModel.getValueAt(selectedRow, 4);

        if ("Hết hàng".equalsIgnoreCase(status) || "Inactive".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                    "Món [" + productName + "] hiện đang tạm hết hàng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double unitPrice = 0;
        try {
            unitPrice = Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
        } catch (Exception ex) {
            unitPrice = 0;
        }

        int qtyToAdd = (int) spnQuantity.getValue();

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

        updateCartTable();
        spnQuantity.setValue(1);
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0;
        int stt = 1;
        int totalItems = 0;

        for (OrderDetail detail : cartItems) {
            double lineTotal = detail.getSubTotal();
            total += lineTotal;
            totalItems += detail.getQuantity();

            cartTableModel.addRow(new Object[]{
                    stt++,
                    detail.getProductId(),
                    detail.getProductName(),
                    currencyFormat.format(detail.getUnitPrice()) + " đ",
                    detail.getQuantity(),
                    currencyFormat.format(lineTotal) + " đ"
            });
        }

        lblCartCountBadge.setText(totalItems + " món");
        lblTotalAmountVal.setText(currencyFormat.format(total) + " đ");
        calculateChangeMoney();
    }

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
                    "Số lượng bằng 0, bạn có muốn xóa món [" + detail.getProductName() + "] khỏi giỏ?",
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

    private void removeSelectedCartItem() {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa trong giỏ hàng!");
            return;
        }

        cartItems.remove(selectedRow);
        updateCartTable();
    }

    private void clearCart() {
        if (cartItems.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sạch toàn bộ món trong giỏ hàng?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            updateCartTable();
        }
    }

    private void calculateChangeMoney() {
        try {
            double total = calculateTotalCartAmount();
            String cashText = txtCustomerCash.getText().replaceAll("[^0-9.]", "").trim();
            double customerCash = cashText.isEmpty() ? 0 : Double.parseDouble(cashText);

            double change = customerCash - total;
            if (total == 0) {
                lblChangeMoneyVal.setText("0 đ");
                lblChangeMoneyVal.setForeground(COLOR_ACCENT);
                return;
            }

            if (customerCash < total) {
                lblChangeMoneyVal.setText("Thiếu " + currencyFormat.format(Math.abs(change)) + " đ");
                lblChangeMoneyVal.setForeground(COLOR_DANGER);
            } else {
                lblChangeMoneyVal.setText(currencyFormat.format(change) + " đ");
                lblChangeMoneyVal.setForeground(COLOR_ACCENT);
            }
        } catch (Exception ex) {
            lblChangeMoneyVal.setText("0 đ");
        }
    }

    private double calculateTotalCartAmount() {
        double total = 0;
        for (OrderDetail item : cartItems) {
            total += item.getSubTotal();
        }
        return total;
    }

    private void generateNewOrderCode() {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        lblOrderCodeVal.setText("HD" + timestamp);
        lblOrderDateVal.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
    }

    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Giỏ hàng đang trống! Vui lòng chọn món trước khi thanh toán.",
                    "Nhắc nhở",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = calculateTotalCartAmount();
        String cashText = txtCustomerCash.getText().replaceAll("[^0-9.]", "").trim();
        double customerCash = cashText.isEmpty() ? 0 : Double.parseDouble(cashText);

        if (customerCash < total) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Tiền khách đưa nhỏ hơn tổng tiền đơn hàng! Bạn vẫn muốn tiếp tục ghi nhận đơn?",
                    "Cảnh báo chưa đủ tiền",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (option != JOptionPane.YES_OPTION) {
                txtCustomerCash.requestFocus();
                return;
            }
        }

        Order order = new Order();
        order.setOrderCode(lblOrderCodeVal.getText());
        order.setUserId(currentUser.getId());
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setTotalAmount(total);

        int newOrderId = orderDAO.createOrder(order, cartItems);

        if (newOrderId > 0) {
            showInvoiceDialog(order, cartItems, customerCash, (customerCash - total));

            cartItems.clear();
            updateCartTable();
            txtCustomerCash.setText("0");
            lblChangeMoneyVal.setText("0 đ");
            generateNewOrderCode();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Thanh toán thất bại do lỗi cơ sở dữ liệu! Vui lòng kiểm tra lại kết nối XAMPP / MySQL.",
                    "Lỗi thanh toán",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Popup hóa đơn thiết kế dạng biên lai nhiệt
     */
    private void showInvoiceDialog(Order order, List<OrderDetail> details, double customerCash, double change) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("         BOBA & COFFEE STATION           \n");
        sb.append("        PHIẾU THANH TOÁN BÁN HÀNG        \n");
        sb.append("=========================================\n");
        sb.append("Mã hóa đơn: ").append(order.getOrderCode()).append("\n");
        sb.append("Ngày giờ:   ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(order.getOrderDate())).append("\n");
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
        sb.append(String.format("TỔNG CỘNG:              %15s đ\n", currencyFormat.format(order.getTotalAmount())));
        sb.append(String.format("Tiền khách đưa:         %15s đ\n", currencyFormat.format(customerCash)));
        sb.append(String.format("Tiền trả lại:           %15s đ\n", currencyFormat.format(Math.max(0, change))));
        sb.append("=========================================\n");
        sb.append("  [ĐÃ THANH TOÁN] - CẢM ƠN QUÝ KHÁCH!    \n");
        sb.append("  Wifi: BobaCoffee_Guest • Pass: 88888888\n");
        sb.append("=========================================\n");

        JTextArea txtInvoice = new JTextArea(sb.toString());
        txtInvoice.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtInvoice.setEditable(false);
        txtInvoice.setBackground(new Color(255, 255, 250));
        txtInvoice.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(txtInvoice);
        scroll.setPreferredSize(new Dimension(390, 480));
        scroll.setBorder(new LineBorder(COLOR_BORDER, 1));

        JOptionPane.showMessageDialog(this,
                scroll,
                "IN HÓA ĐƠN THÀNH CÔNG",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Popup tra cứu & xem lại lịch sử các hóa đơn đã bán
     */
    private void showOrderHistoryDialog() {
        JDialog dialog = new JDialog(this, "LỊCH SỬ ĐƠN HÀNG & TRA CỨU HÓA ĐƠN", true);
        dialog.setSize(850, 520);
        dialog.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBackground(COLOR_BG);
        pnl.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Thống kê nhanh
        double totalRev = orderDAO.getTotalRevenue();
        double todayRev = orderDAO.getTodayRevenue();
        int todayCnt = orderDAO.getTodayOrderCount();

        JPanel statRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statRow.setOpaque(false);
        statRow.add(createMiniStatBox("Doanh thu hôm nay", currencyFormat.format(todayRev) + " đ", new Color(16, 185, 129)));
        statRow.add(createMiniStatBox("Đơn bán hôm nay", todayCnt + " đơn", new Color(59, 130, 246)));
        statRow.add(createMiniStatBox("Tổng doanh thu", currencyFormat.format(totalRev) + " đ", new Color(245, 158, 11)));
        pnl.add(statRow, BorderLayout.NORTH);

        // Bảng dữ liệu hóa đơn
        String[] cols = {"ID", "Mã Hóa Đơn", "Ngày giờ bán", "Thu ngân", "Tổng tiền (VNĐ)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblHistory = new JTable(model);
        styleTable(tblHistory);

        List<Order> list = orderDAO.getAllOrders();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (Order o : list) {
            model.addRow(new Object[]{
                    o.getId(),
                    o.getOrderCode(),
                    o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "",
                    o.getUserName(),
                    currencyFormat.format(o.getTotalAmount()) + " đ"
            });
        }

        JScrollPane scroll = new JScrollPane(tblHistory);
        scroll.setBorder(new LineBorder(COLOR_BORDER, 1));
        pnl.add(scroll, BorderLayout.CENTER);

        // Nút xem lại hóa đơn đã chọn
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        bottom.setOpaque(false);

        JButton btnViewSelected = createStyledButton("📄 Xem lại biên lai này", COLOR_PRIMARY, Color.WHITE, 12);
        btnViewSelected.addActionListener(e -> {
            int row = tblHistory.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn một hóa đơn trong danh sách để xem!");
                return;
            }
            int orderId = (int) model.getValueAt(row, 0);
            Order selOrder = orderDAO.getOrderById(orderId);
            if (selOrder != null) {
                List<OrderDetail> details = orderDAO.getOrderDetailsByOrderId(orderId);
                showInvoiceDialog(selOrder, details, selOrder.getTotalAmount(), 0);
            }
        });

        JButton btnClose = createStyledButton("Đóng", Color.WHITE, COLOR_DARK, 12);
        btnClose.setBorder(new LineBorder(COLOR_BORDER, 1));
        btnClose.addActionListener(e -> dialog.dispose());

        bottom.add(btnViewSelected);
        bottom.add(btnClose);
        pnl.add(bottom, BorderLayout.SOUTH);

        dialog.add(pnl);
        dialog.setVisible(true);
    }

    private JPanel createMiniStatBox(String title, String val, Color valColor) {
        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setForeground(COLOR_TEXT_MUTED);

        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.BOLD, 15));
        v.setForeground(valColor);

        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất khỏi ca bán hàng?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginForm().setVisible(true);
        }
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            if (lblClock != null) {
                lblClock.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        });
        timer.start();
    }

    private void setupKeyboardShortcuts() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F9) {
                    handleCheckout();
                }
            }
        });
        setFocusable(true);
    }

    // ==========================================
    // UI HELPER & CUSTOM RENDERERS
    // ==========================================

    private JButton createStyledButton(String text, Color bg, Color fg, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(COLOR_PRIMARY_LIGHT);
        table.setSelectionForeground(COLOR_PRIMARY);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowGrid(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(COLOR_DARK);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(new LineBorder(COLOR_BORDER, 1));
    }

    /**
     * Renderer hiển thị giá tiền nổi bật
     */
    private class PriceCellRenderer extends DefaultTableCellRenderer {
        public PriceCellRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                setForeground(new Color(15, 118, 110)); // Emerald
            }
            return this;
        }
    }

    /**
     * Renderer hiển thị huy hiệu trạng thái ● Còn hàng / ● Hết hàng
     */
    private class StatusBadgeRenderer extends DefaultTableCellRenderer {
        public StatusBadgeRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String val = (value != null) ? value.toString() : "Còn hàng";
            if ("Hết hàng".equalsIgnoreCase(val) || "Inactive".equalsIgnoreCase(val)) {
                setText("● Hết hàng");
                if (!isSelected) setForeground(COLOR_DANGER);
            } else {
                setText("● Còn hàng");
                if (!isSelected) setForeground(COLOR_ACCENT);
            }
            return this;
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
            new POSMainForm(null).setVisible(true);
        });
    }
}
