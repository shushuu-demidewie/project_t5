<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Tự động chuyển hướng tới servlet Login
    response.sendRedirect(request.getContextPath() + "/login");
%>
