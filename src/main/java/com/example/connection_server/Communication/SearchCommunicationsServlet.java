package com.example.connection_server.Communication;

import com.example.connection_server.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SearchCommunicationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 将 JSONArray 内容作为响应发送到客户端
        response.getWriter().write(UserDAO.SearchCommunications(request.getParameter("userId").trim()).toString());
    }

}
