package com.example.connection_server.Relation;

import com.example.connection_server.UserDAO;
import com.example.connection_server.baiduAI.baiduAI_Face;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

public class UpdateRelationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 获取文本数据
        String get_personId = UserDAO.GetTextFromRequest(request,"personId");
        String get_relationship = UserDAO.GetTextFromRequest(request,"relationship");
        String get_name = UserDAO.GetTextFromRequest(request,"name");
        String get_gender = UserDAO.GetTextFromRequest(request,"gender");
        String get_phoneNumber = UserDAO.GetTextFromRequest(request,"phone_number");
        String get_email = UserDAO.GetTextFromRequest(request,"email");
        String get_notes = UserDAO.GetTextFromRequest(request,"notes");

        // 获取上传的图片
        String filePath = UserDAO.SaveImageFromRequest(getServletContext(), request,"image");
        String get_image_path = UserDAO.GetTextFromRequest(request,"image_path");
        UserDAO.DeleteImage(get_image_path);

        //进行更新操作
        JSONObject jsonObject = new JSONObject();
        if(UserDAO.UpdateRelation(get_personId, get_relationship, get_name, get_gender, get_phoneNumber, get_email, get_notes,filePath)){
            jsonObject.put("result", "success");
            jsonObject.put("error_msg", baiduAI_Face.getError_msg());
        }else{
            jsonObject.put("result", "fail");
        }
        response.getWriter().write(jsonObject.toString());
    }

}
