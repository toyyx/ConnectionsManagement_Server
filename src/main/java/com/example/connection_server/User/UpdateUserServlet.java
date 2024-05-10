package com.example.connection_server.User;

import com.example.connection_server.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import java.io.IOException;

public class UpdateUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 获取文本数据
        String get_userId = UserDAO.GetTextFromRequest(request,"userId");
        String get_userName = UserDAO.GetTextFromRequest(request,"userName");
        String get_name = UserDAO.GetTextFromRequest(request,"name");
        String get_gender = UserDAO.GetTextFromRequest(request,"gender");
        String get_phoneNumber = UserDAO.GetTextFromRequest(request,"phone_number");
        String get_email = UserDAO.GetTextFromRequest(request,"email");

        // 获取上传的用户头像图片
        String filePath = UserDAO.SaveImageFromRequest(getServletContext(), request,"image");
        String get_image_path = UserDAO.GetTextFromRequest(request,"image_path");
        UserDAO.DeleteImage(get_image_path);

        //进行更新操作
        JSONObject jsonObject = new JSONObject();
        if(UserDAO.UpdateUser(get_userId, get_userName, get_name, get_gender, get_phoneNumber, get_email,filePath)){
            jsonObject.put("result", "success");
        }else{
            jsonObject.put("result", "fail");
        }
        response.getWriter().write(jsonObject.toString());
    }

}
