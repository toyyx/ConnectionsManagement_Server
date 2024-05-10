package com.example.connection_server.baiduAI;

import lombok.Getter;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

/*人脸库结构如下
    |- 人脸库(appid)
       |- 用户组一（group_id）
          |- 用户01（uid）
             |- 人脸（faceid）
          |- 用户02（uid）
             |- 人脸（faceid）
             |- 人脸（faceid）
             ....
           ....
       |- 用户组二（group_id）
       |- 用户组三（group_id）
   ....
    */
//人脸搜索1:M、M:N
//用户操作：添加、更新、删除、查询用户信息、查询用户人脸列表
//用户组操作：创建、删除、查询组列表、查询组内用户列表

public class baiduAI_Face {
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    static String API_KEY;
    static String SECRET_KEY;
    static String ACCESS_TAKEN;
    @Getter
    static String error_msg;//返回的错误信息
    static RequestLimiter limiter = new RequestLimiter(1); // 限制每秒发送的请求数量为 1

    public static void main(String []args) throws IOException {
//        String responseBodyString="{\"error_code\":0,\"error_msg\":\"SUCCESS\",\"log_id\":3273658903,\"timestamp\":1714301673,\"cached\":0,\"result\":{\"face_token\":\"b5f245d780ccfbc4e3fc3f69a73de8c6\",\"user_list\":[{\"group_id\":\"groupId_1\",\"user_id\":\"userId_1\",\"user_info\":\"userName_1\",\"score\":99.999794006348},{\"group_id\":\"groupId_1\",\"user_id\":\"userId_3\",\"user_info\":\"userName_3_updated\",\"score\":90.840682983398},{\"group_id\":\"groupId_1\",\"user_id\":\"userId_2\",\"user_info\":\"userName_2\",\"score\":90.831298828125}]}}";
//        JSONObject jsonObject = new JSONObject(responseBodyString);
//        System.out.println(jsonObject.getJSONObject("result").getJSONArray("user_list"));
//        String path1="C:/Users/15562/Desktop/face/data/xzq1.jpeg";
//        String path2="C:/Users/15562/Desktop/face/data/xzq2.jpeg";
//        String path3="C:/Users/15562/Desktop/face/data/xzq3.jpg";
//
        System.out.println("getAccessToken()功能测试");
        getAccessToken();
//
//        System.out.println("getFileContentAsBase64()功能测试");
//        getFileContentAsBase64(path1,false);
//
//        System.out.println("groupAdd()功能测试");
//        groupAdd("groupId_1");
//        groupAdd("groupId_2");
//        groupAdd("groupId_3");
//        groupAdd("groupId_4");
//
//        System.out.println("groupGetlist()功能测试");
//        groupGetlist();
//
//        System.out.println("groupDelete()功能测试");
//        groupDelete("groupId_4");
//        groupGetlist();
//
//        System.out.println("faceAdd()功能测试");
//        faceAdd("groupId_1",getFileContentAsBase64(path1,false),"userId_1","userName_1");
//        faceAdd("groupId_1",getFileContentAsBase64(path2,false),"userId_2","userName_2");
//        faceAdd("groupId_1",getFileContentAsBase64(path3,false),"userId_3","userName_3");
//        faceAdd("groupId_1",getFileContentAsBase64(path3,false),"userId_4","userName_4");
//
//        System.out.println("groupGetusers()功能测试");
//        groupGetusers("groupId_1");
//
//        System.out.println("userDelete()功能测试");
//        userDelete("groupId_1","userId_4");
//        groupGetusers("groupId_1");
//
//        System.out.println("userGet()功能测试");
//        userGet("groupId_1","userId_3");
//
//        System.out.println("faceGetList()功能测试");
//        faceGetList("groupId_1","userId_3");
//
//        System.out.println("faceUpdate()功能测试");
//        faceUpdate("groupId_1",getFileContentAsBase64(path2,false),"userId_3","userName_3_updated");
//        userGet("groupId_1","userId_3");
//        faceGetList("groupId_1","userId_3");
//
//        System.out.println("faceSearch()功能测试");
//        faceSearch("groupId_1",getFileContentAsBase64(path1,false),10);
    }

    //人脸检测
    public static JSONObject faceDetect(String image_base64) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"image\":\""+image_base64+"\",\"image_type\":\"BASE64\",\"face_field\":\",age,face_type,face_shape,gender,glasses,emotion,mask,spoofing\",\"max_face_num\":120,\"face_type\":\"LIVE\",\"display_corp_image\":1}");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("faceDetect():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);//错误码18表示接口访问次数受限，此时等待一段时间后再次访问
            if(jsonObject.getInt("error_code")==0){
                return jsonObject.getJSONObject("result");
            }else{
                error_msg=jsonObject.getString("error_msg");
                return null;
            }
        }else{
            return null;
        }
    }

    //人脸检测
    public static JSONObject faceMatch(String image_base64_1,String image_base64_2) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "[{\"image\":\""+image_base64_1+"\",\"image_type\":\"BASE64\"},{\"image\":\""+image_base64_2+"\",\"image_type\":\"BASE64\"}]");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/match?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("faceMatch():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                return jsonObject.getJSONObject("result");
            }else{
                error_msg=jsonObject.getString("error_msg");
                return null;
            }
        }else{
            return null;
        }
    }

    //人脸搜索1:N
    public static JSONArray faceSearch(String group_id, String image_base64, int max_user_num) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id_list\":\""+group_id+"\",\"image\":\""+image_base64+"\",\"image_type\":\"BASE64\",\"max_user_num\":"+max_user_num+"}");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("faceSearch_1_N():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                return jsonObject.getJSONObject("result").getJSONArray("user_list");
            }else{
                error_msg=jsonObject.getString("error_msg");
                return null;
            }
        }else{
            return null;
        }
    }

    //人脸搜索M:N
    public static JSONObject faceSearch(String group_id, String image_base64, int max_face_num, int max_user_num) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id_list\":\""+group_id+"\",\"image\":\""+image_base64+"\",\"image_type\":\"BASE64\",\"max_face_num\":"+max_face_num+",\"match_threshold\":0,\"max_user_num\":"+max_user_num+"}");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/multi-search?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("faceSearch_M_N():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                return jsonObject.getJSONObject("result");
            }else{
                error_msg=jsonObject.getString("error_msg");
                return null;
            }
        }else{
            return null;
        }
    }

    //用户操作：添加、更新、删除、查询用户信息、查询用户人脸列表
    //人脸注册（添加用户）
    public static Boolean faceAdd(String group_id,String image_base64,String user_id,String user_name) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id\":\""+group_id+"\",\"image\":\""+image_base64+"\",\"image_type\":\"BASE64\",\"user_id\":\""+user_id+"\",\"user_info\":\""+user_name+"\",\"action_type\":\"REPLACE\"}");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("faceAdd():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                error_msg=jsonObject.getString("error_msg");
                return true;
            }else{
                error_msg=jsonObject.getString("error_msg");
                return false;
            }
        }else{
            return false;
        }
    }

    //人脸更新（用户更新）
    public static Boolean faceUpdate(String group_id,String image_base64,String user_id,String user_name) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id\":\""+group_id+"\",\"image\":\""+image_base64+"\",\"image_type\":\"BASE64\",\"user_id\":\""+user_id+"\",\"user_info\":\""+user_name+"\",\"action_type\":\"UPDATE\"}");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/update?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("faceUpdate():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                error_msg=jsonObject.getString("error_msg");
                return true;
            }else{
                error_msg=jsonObject.getString("error_msg");
                return false;
            }
        }else{
            return false;
        }
    }

    //删除用户
    public static Boolean userDelete(String  group_id,String user_id) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id\":\""+group_id+"\",\"user_id\":\""+user_id+"\"}");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/delete?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("userDelete():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                error_msg=jsonObject.getString("error_msg");
                return true;
            }else{
                error_msg=jsonObject.getString("error_msg");
                return false;
            }
        }else{
            return false;
        }
    }

    //获取用户信息
    public static Boolean userGet(String group_id,String user_id) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id\":\""+group_id+"\",\"user_id\":\""+user_id+"\"}");
        if(getAccessToken()) {
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/get?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("userGet():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                return true;
            }else{
                error_msg=jsonObject.getString("error_msg");
                return false;
            }
        }else{
            return false;
        }
    }

    //获取用户人脸列表
    public static Boolean faceGetList(String group_id,String user_id) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id\":\""+group_id+"\",\"user_id\":\""+user_id+"\"}");
        if(getAccessToken()) {
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/face/getlist?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("faceGetList():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                return true;
            }else{
                error_msg=jsonObject.getString("error_msg");
                return false;
            }
        }else{
            return false;
        }
    }


    //用户组操作：创建、删除、查询组列表、查询组内用户列表
    //创建用户组
    public static Boolean groupAdd(String  group_id) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id\":\""+group_id+"\"}");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/add?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("groupAdd():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                error_msg=jsonObject.getString("error_msg");
                return true;
            }else{
                error_msg=jsonObject.getString("error_msg");
                return false;
            }
        }else{
            return false;
        }

    }

    //删除用户组
    public static Boolean groupDelete(String  group_id) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id\":\""+group_id+"\"}");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/delete?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("groupDelete():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                return true;
            }else{
                error_msg=jsonObject.getString("error_msg");
                return false;
            }
        }else{
            return false;
        }
    }

    //组列表查询
    public static Boolean groupGetlist() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        if(getAccessToken()){
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getlist?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("groupGetlist():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                return true;
            }else{
                error_msg=jsonObject.getString("error_msg");
                return false;
            }
        }else{
            return false;
        }
    }

    //获取用户列表
    public static Boolean groupGetusers(String  group_id) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"group_id\":\""+group_id+"\"}");
        if(getAccessToken()) {
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getusers?access_token=" + ACCESS_TAKEN)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            JSONObject jsonObject;
            do{
                while(!limiter.canSendRequest()){
                    // 等待一段时间，然后再次检查条件
                    try {
                        Thread.sleep(1000); // 等待 1 秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Response response = HTTP_CLIENT.newCall(request).execute();
                String responseBodyString = response.body().string();
                System.out.println("groupGetusers():"+responseBodyString);
                jsonObject = new JSONObject(responseBodyString);
            }while(jsonObject.getInt("error_code")==18);
            if(jsonObject.getInt("error_code")==0){
                return true;
            }else{
                error_msg=jsonObject.getString("error_msg");
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */

    static Boolean getAccessToken() throws IOException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/usr/java/tomcat/apache-tomcat-10.1.19/webapps/connection_server-1.0-SNAPSHOT/WEB-INF/classes/config"));
            API_KEY = properties.getProperty("API_KEY");
            SECRET_KEY = properties.getProperty("SECRET_KEY");
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?client_id="+API_KEY+"&client_secret="+SECRET_KEY+"&grant_type=client_credentials")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String responseBodyString = response.body().string();
        System.out.println("getAccessToken():"+responseBodyString.stripTrailing());
        JSONObject jsonObject = new JSONObject(responseBodyString);
        if(jsonObject.has("error")){
            error_msg=jsonObject.getString("error_description");
            return false;
        }else{
            ACCESS_TAKEN=jsonObject.getString("access_token");
            return true;
        }
    }

    /**
     * 获取文件base64编码
     *
     * @param path      文件路径
     * @param urlEncode 如果Content-Type是application/x-www-form-urlencoded时,传true
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    public static String getFileContentAsBase64(String path, boolean urlEncode) throws IOException {
        byte[] b = Files.readAllBytes(Paths.get(path));

        String base64 = Base64.getEncoder().encodeToString(b);
        if (urlEncode) {
            base64 = URLEncoder.encode(base64, "utf-8");
        }
//        System.out.println("getFileContentAsBase64():"+base64);
        return base64;
    }
}
