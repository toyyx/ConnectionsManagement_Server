package com.example.connection_server.Communication;

import com.example.connection_server.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

public class UpdateCommunicationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 获取文本数据
        String get_eventId = UserDAO.GetTextFromRequest(request,"eventId");
        String get_stratTime = UserDAO.GetTextFromRequest(request,"stratTime");
        String get_finishTime = UserDAO.GetTextFromRequest(request,"finishTime");
        String get_title = UserDAO.GetTextFromRequest(request,"title");
        String get_address = UserDAO.GetTextFromRequest(request,"address");
        String get_detail = UserDAO.GetTextFromRequest(request,"detail");
        String get_participants = UserDAO.GetTextFromRequest(request,"participants");
        //进行更新操作
        JSONObject jsonObject = new JSONObject();
        if(UserDAO.UpdateCommunication(get_eventId,get_stratTime,get_finishTime,get_title,get_address,get_detail,get_participants)){
            jsonObject.put("result", "success");
        }else{
            jsonObject.put("result", "fail");
        }
        response.getWriter().write(jsonObject.toString());
    }

}
