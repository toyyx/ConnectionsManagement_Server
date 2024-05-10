package com.example.connection_server.Communication;

import com.example.connection_server.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

public class DeleteCommunicationServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 获取文本数据
        String get_deleteEventId = request.getParameter("eventId").trim();
        //进行删除操作
        JSONObject jsonObject = new JSONObject();
        if(UserDAO.DeleteCommunication(get_deleteEventId)){
            jsonObject.put("result", "success");
        }else{
            jsonObject.put("result", "fail");
        }
        response.getWriter().write(jsonObject.toString());
    }
}
