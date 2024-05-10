package com.example.connection_server.Face;

import com.example.connection_server.UserDAO;
import com.example.connection_server.baiduAI.baiduAI_Face;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.connection_server.UserDAO.DeleteImage;

public class FaceSearch_1_N_Servlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 获取文本数据
        String get_userId = UserDAO.GetTextFromRequest(request,"userId");

        // 获取上传的图片
        String filePath = UserDAO.SaveImageFromRequest_Temp(getServletContext(), request,"image");

        //进行更新操作
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray=baiduAI_Face.faceSearch(get_userId,baiduAI_Face.getFileContentAsBase64(filePath,false),50);
        if(jsonArray!=null){
            jsonObject.put("result", "success");
            jsonObject.put("search_result",jsonArray);
        }else{
            jsonObject.put("result", "fail");
            jsonObject.put("error_msg", baiduAI_Face.getError_msg());
        }
        DeleteImage(filePath);
        response.getWriter().write(jsonObject.toString());
    }
}
