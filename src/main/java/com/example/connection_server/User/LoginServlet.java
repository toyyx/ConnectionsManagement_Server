package com.example.connection_server.User;

import com.example.connection_server.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import java.io.IOException;
import java.util.Objects;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");
        JSONObject jsonObject = new JSONObject();

        //获取查询用户
        User resultUser= UserDAO.queryUser(request.getParameter("userName").trim());

        //查询用户名为空，表示用户未注册
        if(resultUser.getUserName()==null){
            System.out.println("LoginServlet:查询结果：用户不存在");
            jsonObject.put("result", "fail");
        }else if(Objects.equals(resultUser.getPassword(), request.getParameter("password").trim())){//用户存在且密码正确
            jsonObject.put("result", "success");
            jsonObject.put("userId", resultUser.getUserId());
            jsonObject.put("userName", resultUser.getUserName());
            jsonObject.put("password", resultUser.getPassword());
            jsonObject.put("name", resultUser.getName());
            jsonObject.put("gender", resultUser.getGender());
            jsonObject.put("image_path", resultUser.getImage_path());
            jsonObject.put("phone_number", resultUser.getPhone_number());
            jsonObject.put("email", resultUser.getEmail());
        }else{
            jsonObject.put("result", "fail");//用户存在但密码错误
        }
        response.getWriter().write(jsonObject.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}