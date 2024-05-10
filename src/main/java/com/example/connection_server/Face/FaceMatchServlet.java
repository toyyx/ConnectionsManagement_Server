package com.example.connection_server.Face;

import com.example.connection_server.UserDAO;
import com.example.connection_server.baiduAI.baiduAI_Face;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.connection_server.UserDAO.DeleteImage;

public class FaceMatchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");

        // 获取上传的图片
        String filePath_1 = UserDAO.SaveImageFromRequest_Temp(getServletContext(), request,"image_1");
        String filePath_2 = UserDAO.SaveImageFromRequest_Temp(getServletContext(), request,"image_2");

        //进行更新操作
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject_result= baiduAI_Face.faceMatch(baiduAI_Face.getFileContentAsBase64(filePath_1,false),baiduAI_Face.getFileContentAsBase64(filePath_2,false));
        if(jsonObject_result!=null){
            jsonObject.put("result", "success");
            jsonObject.put("match_result",jsonObject_result);
        }else{
            jsonObject.put("result", "fail");
            jsonObject.put("error_msg", baiduAI_Face.getError_msg());
        }
        DeleteImage(filePath_1);
        DeleteImage(filePath_2);
        response.getWriter().write(jsonObject.toString());
    }
}
