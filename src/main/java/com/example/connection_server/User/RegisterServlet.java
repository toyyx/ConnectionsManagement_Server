package com.example.connection_server.User;

import com.example.connection_server.UserDAO;
import com.example.connection_server.baiduAI.baiduAI_Face;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 获取文本数据
        String get_userName = UserDAO.GetTextFromRequest(request,"userName");
        String get_password = UserDAO.GetTextFromRequest(request,"password");
        String get_name = UserDAO.GetTextFromRequest(request,"name");
        String get_gender = UserDAO.GetTextFromRequest(request,"gender");
        String get_phoneNumber = UserDAO.GetTextFromRequest(request,"phone_number");
        String get_email = UserDAO.GetTextFromRequest(request,"email");

        // 获取上传的用户头像图片
        String filePath = UserDAO.SaveImageFromRequest(getServletContext(), request,"image");

        JSONObject jsonObject = new JSONObject();
        //检查用户是否存在
        if (UserDAO.Register(get_userName, get_password, filePath, get_name, get_gender, get_phoneNumber, get_email) && filePath!=null) {
            jsonObject.put("result", "success");
            jsonObject.put("error_msg", baiduAI_Face.getError_msg());
        } else {
            UserDAO.DeleteImage(filePath);//删除图片
            jsonObject.put("result", "fail");
        }
        response.getWriter().write(jsonObject.toString());
    }

}
