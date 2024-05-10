package com.example.connection_server.Relation;

import com.example.connection_server.UserDAO;
import com.example.connection_server.baiduAI.baiduAI_Face;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

public class DeleteRelationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 获取文本数据
        String get_deletePersonId = request.getParameter("personId").trim();

        //进行删除操作
        JSONObject jsonObject = new JSONObject();
        if(UserDAO.DeleteRelation(get_deletePersonId)){
            jsonObject.put("result", "success");
            jsonObject.put("error_msg", baiduAI_Face.getError_msg());
        }else{
            jsonObject.put("result", "fail");
        }
        response.getWriter().write(jsonObject.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request,response);
    }
}
