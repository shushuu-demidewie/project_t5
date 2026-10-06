package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Product;
import model.User;

/**
 * Servlet quản lý Sản Phẩm (Thêm, Sửa, Xóa, Tra cứu món).
 */
@WebServlet(name = "ProductManageServlet", urlPatterns = {"/products"})
public class ProductManageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("delete".equalsIgnoreCase(action)) {
            handleDelete(request, response);
            return;
        }

        // Lọc theo danh mục hoặc tìm kiếm
        String catIdParam = request.getParameter("catId");
        String keyword = request.getParameter("keyword");

        int selectedCatId = 0;
        if (catIdParam != null && !catIdParam.trim().isEmpty()) {
            try {
                selectedCatId = Integer.parseInt(catIdParam.trim());
            } catch (NumberFormatException ignored) {
                selectedCatId = 0;
            }
        }

        if (keyword != null) {
            keyword = keyword.trim();
        } else {
            keyword = "";
        }

        List<Product> productList;
        if (!keyword.isEmpty()) {
            productList = productDAO.searchByName(keyword);
        } else if (selectedCatId > 0) {
            productList = productDAO.getByCategory(selectedCatId);
        } else {
            productList = productDAO.getAll();
        }

        List<Category> categories = categoryDAO.getAll();

        request.setAttribute("products", productList);
        request.setAttribute("categories", categories);
        request.setAttribute("selectedCatId", selectedCatId);
        request.setAttribute("keyword", keyword);

        request.getRequestDispatcher("/products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("add".equalsIgnoreCase(action)) {
            handleAdd(request, response);
        } else if ("update".equalsIgnoreCase(action)) {
            handleUpdate(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }

    /**
     * Xử lý thêm sản phẩm mới.
     */
    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String productName = request.getParameter("productName");
            String catIdStr = request.getParameter("categoryId");
            String priceStr = request.getParameter("price");
            String status = request.getParameter("status");

            if (productName == null || productName.trim().isEmpty() ||
                catIdStr == null || priceStr == null) {
                response.sendRedirect(request.getContextPath() + "/products?error=missing_fields");
                return;
            }

            int categoryId = Integer.parseInt(catIdStr.trim());
            double price = Double.parseDouble(priceStr.trim());
            if (status == null || status.trim().isEmpty()) {
                status = "Còn hàng";
            }

            Product p = new Product(categoryId, productName.trim(), price, status.trim());
            boolean ok = productDAO.insert(p);

            if (ok) {
                response.sendRedirect(request.getContextPath() + "/products?msg=add_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/products?error=add_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products?error=invalid_number");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/products?error=server_error");
        }
    }

    /**
     * Xử lý cập nhật thông tin sản phẩm.
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String idStr = request.getParameter("id");
            String productName = request.getParameter("productName");
            String catIdStr = request.getParameter("categoryId");
            String priceStr = request.getParameter("price");
            String status = request.getParameter("status");

            if (idStr == null || productName == null || productName.trim().isEmpty() ||
                catIdStr == null || priceStr == null) {
                response.sendRedirect(request.getContextPath() + "/products?error=missing_fields");
                return;
            }

            int id = Integer.parseInt(idStr.trim());
            int categoryId = Integer.parseInt(catIdStr.trim());
            double price = Double.parseDouble(priceStr.trim());
            if (status == null || status.trim().isEmpty()) {
                status = "Còn hàng";
            }

            Product p = new Product(id, categoryId, productName.trim(), price, status.trim());
            boolean ok = productDAO.update(p);

            if (ok) {
                response.sendRedirect(request.getContextPath() + "/products?msg=update_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/products?error=update_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products?error=invalid_number");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/products?error=server_error");
        }
    }

    /**
     * Xử lý xóa sản phẩm.
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr.trim());
                boolean ok = productDAO.delete(id);
                if (ok) {
                    response.sendRedirect(request.getContextPath() + "/products?msg=delete_success");
                } else {
                    // Do ràng buộc khóa ngoại (món đã có trong hóa đơn bán hàng)
                    response.sendRedirect(request.getContextPath() + "/products?error=fk_constraint");
                }
                return;
            } catch (NumberFormatException ignored) {
            }
        }
        response.sendRedirect(request.getContextPath() + "/products?error=invalid_id");
    }
}
