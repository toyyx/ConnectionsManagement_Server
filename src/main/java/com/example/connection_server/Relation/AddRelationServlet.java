package com.example.connection_server.Relation;

import com.example.connection_server.UserDAO;
import com.example.connection_server.baiduAI.baiduAI_Face;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

public class AddRelationServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 获取文本数据
        String get_userId = UserDAO.GetTextFromRequest(request,"userId");
        String get_relationship = UserDAO.GetTextFromRequest(request,"relationship");
        String get_name = UserDAO.GetTextFromRequest(request,"name");
        String get_gender = UserDAO.GetTextFromRequest(request,"gender");
        String get_phone_number = UserDAO.GetTextFromRequest(request,"phone_number");
        String get_email = UserDAO.GetTextFromRequest(request,"email");
        String get_notes = UserDAO.GetTextFromRequest(request,"notes");
        String get_image_path = UserDAO.SaveImageFromRequest(getServletContext(), request,"image");

        JSONObject jsonObject = new JSONObject();
        if(UserDAO.AddRelation(new Relation(Integer.parseInt(get_userId),get_relationship,get_name,get_gender,get_phone_number,get_email,get_notes,get_image_path))){
            jsonObject.put("result", "success");
            jsonObject.put("error_msg", baiduAI_Face.getError_msg());
        }else{
            jsonObject.put("result", "fail");
        }
        response.getWriter().write(jsonObject.toString());
    }
}
