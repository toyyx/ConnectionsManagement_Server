package com.example.connection_server;

import com.example.connection_server.Relation.Relation;
import com.example.connection_server.User.User;
import com.example.connection_server.baiduAI.baiduAI_Face;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;


//数据访问对象（Data Access Object）类
public class UserDAO {

    //查询用户（若存在，返回用户，否则返回空）
    public static User queryUser(String userName) {
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        User user;
        try {
            // 构造 SQL 查询语句
            String sql = "SELECT * FROM user WHERE userName = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置查询参数
            statement.setString(1, userName);
            // 执行查询
            resultSet = statement.executeQuery();
            // 处理查询结果
            user = new User();
            if (resultSet.next()) {
                // 构造 User 对象
                user.setUserId(resultSet.getInt("userId"));
                user.setUserName(resultSet.getString("userName"));
                user.setPassword(resultSet.getString("password"));
                user.setName(resultSet.getString("name"));
                user.setGender(resultSet.getString("gender"));
                user.setImage_path(resultSet.getString("image_path"));
                user.setPhone_number(resultSet.getString("phone_number"));
                user.setEmail(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBManager.closeAll(connection, statement, resultSet);               //关闭连接
        }
        return user;
    }

    //注册用户
    public static boolean Register(String userName,String password,String image_path,String name,String gender,String phoneNumber,String email) throws IOException {
        System.out.println("Register:开始查询用户是否存在");
        //检查用户是否存在
        if(queryUser(userName).getUserName()!=null){
            System.out.println("Register:查询结果：用户存在");
            return false;
        }
        System.out.println("Register:查询结果：用户不存在");
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        int rowsInserted;
        try {
            // 构造 SQL 查询语句
            String sql = "INSERT INTO user (userName, password, image_path, name, gender, phone_number, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置参数
            statement.setString(1, userName);
            statement.setString(2, password);
            statement.setString(3, image_path);
            statement.setString(4, name);
            statement.setString(5, gender);
            statement.setString(6, phoneNumber);
            statement.setString(7, email);
            // 执行插入操作
            rowsInserted = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭连接
            DBManager.closeAll(connection, statement);
        }
        //注册成功，准备创建人脸库用户组
        if(rowsInserted > 0){
            baiduAI_Face.groupAdd(Integer.toString(queryUser(userName).getUserId()));
        }
        // 返回注册结果给用户
        return rowsInserted > 0;
    }

    //更新用户
    public static Boolean UpdateUser(String userId,String userName,String name,String gender,String phoneNumber,String email, String image_path) throws IOException {
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        int rowsInserted;
        try {
            // 构造 SQL 查询语句
            String sql = "UPDATE user SET userName = ?,name = ?, gender = ?, phone_number = ?, email = ?,image_path = ? WHERE userId = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置参数
            statement.setString(1, userName);
            statement.setString(2, name);
            statement.setString(3, gender);
            statement.setString(4, phoneNumber);
            statement.setString(5, email);
            statement.setString(6, image_path);
            statement.setInt(7, Integer.parseInt(userId));
            // 执行插入操作
            rowsInserted = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭连接
            DBManager.closeAll(connection, statement);
        }
        // 返回注册结果给用户
        return rowsInserted > 0;
    }

    //查询用户的人际关系
    public static JSONArray SearchRelations(String userId){
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        // 创建 JSON 数组来存储用户信息
        JSONArray jsonArray = new JSONArray();
        try {
            // 构造 SQL 查询语句
            String sql = "SELECT * FROM PersonalRelations WHERE userId = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置查询参数
            statement.setString(1, userId);
            // 执行查询
            resultSet = statement.executeQuery();
            // 遍历结果集，将每个用户信息转换为 JSON 对象并添加到数组中
            //②人脉关系PersonalRelations：用户编号userId、人物编号personId、关系类型（朋友、亲人、同学、其他）relationship、姓名name、性别gender、头像image_path、电话phone_number、邮箱email、备注notes
            while (resultSet.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("personId", resultSet.getInt("personId"));
                jsonObject.put("relationship", resultSet.getString("relationship"));
                jsonObject.put("name", resultSet.getString("name"));
                jsonObject.put("gender", resultSet.getString("gender"));
                jsonObject.put("image_path", resultSet.getString("image_path"));
                jsonObject.put("phone_number", resultSet.getString("phone_number"));
                jsonObject.put("email", resultSet.getString("email"));
                jsonObject.put("notes", resultSet.getString("notes"));
                jsonArray.add(jsonObject);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBManager.closeAll(connection, statement, resultSet);               //关闭连接
        }
        return jsonArray;
    }

    //根据图片路径搜索人物ID
    public static int SearchRelationId(String image_path_search){
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 构造 SQL 查询语句
            String sql = "SELECT * FROM PersonalRelations WHERE image_path = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置查询参数
            statement.setString(1, image_path_search);
            // 执行查询
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("personId");
            }else{
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBManager.closeAll(connection, statement, resultSet);               //关闭连接
        }
    }

    //根据图片路径搜索人物ID
    public static String SearchRelationImagePath(String personId){
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 构造 SQL 查询语句
            String sql = "SELECT * FROM PersonalRelations WHERE personId = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置查询参数
            statement.setString(1, personId);
            // 执行查询
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("image_path");
            }else{
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBManager.closeAll(connection, statement, resultSet);               //关闭连接
        }
    }

    //根据人物ID搜索用户ID
    public static int SearchUserId(String personId){
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 构造 SQL 查询语句
            String sql = "SELECT * FROM PersonalRelations WHERE personId = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置查询参数
            statement.setString(1, personId);
            // 执行查询
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("userId");
            }else{
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBManager.closeAll(connection, statement, resultSet);               //关闭连接
        }
    }

    //增加人际关系
    public static Boolean AddRelation(Relation newRelation) throws IOException {
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        int rowsInserted;
        try {
            // 构造 SQL 查询语句
            String sql = "INSERT INTO personalrelations (userId, relationship, name, gender, phone_number,email,notes,image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置参数
            statement.setInt(1, newRelation.getUserId());
            statement.setString(2, newRelation.getRelationship());
            statement.setString(3, newRelation.getName());
            statement.setString(4, newRelation.getGender());
            statement.setString(5, newRelation.getPhone_number());
            statement.setString(6, newRelation.getEmail());
            statement.setString(7, newRelation.getNotes());
            statement.setString(8, newRelation.getImage_path());
            // 执行插入操作
            rowsInserted = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭连接
            DBManager.closeAll(connection, statement);
        }
        //添加关系成功，准备添加人脸库用户
        if(rowsInserted > 0){
            if(baiduAI_Face.faceAdd(Integer.toString(newRelation.getUserId()),baiduAI_Face.getFileContentAsBase64(newRelation.getImage_path(),false),Integer.toString(SearchRelationId(newRelation.getImage_path())),newRelation.getName())){
                return true;
            }else{
                DeleteRelation(Integer.toString(SearchRelationId(newRelation.getImage_path())));
                return false;
            }
        }else
            return false;
    }

    //删除人际关系
    public static Boolean DeleteRelation(String personId) throws IOException {
        int userId=SearchUserId(personId);
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        int rowsInserted;
        String filepath=SearchRelationImagePath(personId);
        try {
            // 构造 SQL 查询语句
            String sql = "DELETE FROM personalrelations WHERE personId = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置参数
            statement.setInt(1, Integer.parseInt(personId));
            // 执行插入操作
            rowsInserted = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭连接
            DBManager.closeAll(connection, statement);
        }
        //删除关系成功，准备删除人脸库用户
        if(rowsInserted > 0){
            baiduAI_Face.userDelete(Integer.toString(userId),personId);
            if(filepath!=null){
                DeleteImage(filepath);
            }
        }
        // 返回注册结果给用户
        return rowsInserted > 0;
    }

    //更新人际关系
    public static Boolean UpdateRelation(String personId, String relationship, String name, String gender, String phone_number, String email, String notes, String image_path) throws IOException {
        int userId=SearchUserId(personId);
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        int rowsInserted;
        try {
            // 构造 SQL 查询语句
            String sql = "UPDATE personalrelations SET relationship = ?, name = ?, gender = ?, phone_number = ?, email = ?, notes = ?, image_path = ? WHERE personId = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置参数
            statement.setString(1, relationship);
            statement.setString(2, name);
            statement.setString(3, gender);
            statement.setString(4, phone_number);
            statement.setString(5, email);
            statement.setString(6, notes);
            statement.setString(7, image_path);
            statement.setInt(8, Integer.parseInt(personId));
            // 执行插入操作
            rowsInserted = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭连接
            DBManager.closeAll(connection, statement);
        }
        //修改关系成功，准备修改人脸库用户
        if(rowsInserted > 0){
            baiduAI_Face.faceUpdate(Integer.toString(userId),baiduAI_Face.getFileContentAsBase64(image_path,false),personId,name);
        }
        // 返回注册结果给用户
        return rowsInserted > 0;
    }

    //增加交际
    public static Boolean AddCommunication(String userId,String startTime, String finishTime,String title, String address, String detail, String participants){
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        int rowsInserted;
        try {
            // 构造 SQL 查询语句
            String sql = "INSERT INTO communication (userId, startTime, finishTime, title, address, detail, participants) VALUES (?, ?, ?, ?, ?, ?, ?)";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置参数
            statement.setString(1, userId);
            statement.setString(2, startTime);
            statement.setString(3, finishTime);
            statement.setString(4, title);
            statement.setString(5, address);
            statement.setString(6, detail);
            statement.setString(7, participants);

            // 执行插入操作
            rowsInserted = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭连接
            DBManager.closeAll(connection, statement);
        }
        // 返回注册结果给用户
        return rowsInserted > 0;
    }

    //删除交际
    public static Boolean DeleteCommunication(String eventId){
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        int rowsInserted;
        try {
            // 构造 SQL 查询语句
            String sql = "DELETE FROM communication WHERE eventId = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置参数
            statement.setString(1, eventId);
            // 执行插入操作
            rowsInserted = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭连接
            DBManager.closeAll(connection, statement);
        }
        // 返回注册结果给用户
        return rowsInserted > 0;
    }

    //更新交际
    public static Boolean UpdateCommunication(String eventId,String startTime, String finishTime, String title,String address, String detail, String participants){
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        int rowsInserted;
        try {
            // 构造 SQL 查询语句
            String sql = "UPDATE communication SET startTime = ?, finishTime = ?, title = ?,address = ?,detail = ?, participants = ? WHERE eventId = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置参数
            statement.setString(1, startTime);
            statement.setString(2, finishTime);
            statement.setString(3, title);
            statement.setString(4, address);
            statement.setString(5, detail);
            statement.setString(6, participants);
            statement.setString(7, eventId);
            // 执行插入操作
            rowsInserted = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭连接
            DBManager.closeAll(connection, statement);
        }
        // 返回注册结果给用户
        return rowsInserted > 0;
    }

    //查询交际
    public static JSONArray SearchCommunications(String userId){
        // 建立数据库连接
        Connection connection = DBManager.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        // 创建 JSON 数组来存储用户信息
        JSONArray jsonArray = new JSONArray();
        try {
            // 构造 SQL 查询语句
            String sql = "SELECT * FROM communication WHERE userId = ?";
            // 创建 PreparedStatement 对象
            statement = connection.prepareStatement(sql);
            // 设置查询参数
            statement.setString(1, userId);
            // 执行查询
            resultSet = statement.executeQuery();
            // 遍历结果集，将每个用户信息转换为 JSON 对象并添加到数组中
            //②人脉关系PersonalRelations：用户编号userId、人物编号personId、关系类型（朋友、亲人、同学、其他）relationship、姓名name、性别gender、头像image_path、电话phone_number、邮箱email、备注notes
            while (resultSet.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("eventId", resultSet.getInt("eventId"));
                jsonObject.put("startTime", resultSet.getString("startTime"));
                jsonObject.put("finishTime", resultSet.getString("finishTime"));
                jsonObject.put("title", resultSet.getString("title"));
                jsonObject.put("detail", resultSet.getString("detail"));
                jsonObject.put("address", resultSet.getString("address"));
                jsonObject.put("participants", resultSet.getString("participants"));
                jsonArray.add(jsonObject);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBManager.closeAll(connection, statement, resultSet);               //关闭连接
        }
        return jsonArray;
    }


    //从Request获取文本数据
    public static String GetTextFromRequest(HttpServletRequest request,String getText){
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = null;
        try {
            Part textPart = request.getPart(getText);
            reader = new BufferedReader(new InputStreamReader(textPart.getInputStream()));
            while ((line = reader.readLine()) != null) {// 获取全部文本数据
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException | ServletException e) {
            return null;
        }
    }

    // 保存图片到服务器
    public static String SaveImageFromRequest(ServletContext context, HttpServletRequest request, String imageName){
        Part filePart = null;
        try {
            filePart = request.getPart(imageName);
        } catch (IOException | ServletException e) {
            System.out.println(e.getMessage());
            return null;
        }

        // 保存文件到服务器
        String uploadDir = context.getRealPath("/") + "data_image/";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        String filePath = uploadDir + UUID.randomUUID()+".jpg";

        byte[] buffer;
        try (InputStream fileContent = filePart.getInputStream();
             OutputStream outputStream = new FileOutputStream(filePath)) {
            int read;
            buffer = new byte[1024];
            while ((read = fileContent.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return filePath;
        }catch (IOException e) {
            // 处理异常情况
            System.out.println(e.getMessage());
            return null;
        }
    }

    //临时保存图片
    public static String SaveImageFromRequest_Temp(ServletContext context, HttpServletRequest request, String imageName){
        Part filePart = null;
        try {
            filePart = request.getPart(imageName);
        } catch (IOException | ServletException e) {
            System.out.println(e.getMessage());
            return null;
        }

        // 保存文件到服务器
        String uploadDir = context.getRealPath("/") + "data_image_temp/";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        String filePath = uploadDir + UUID.randomUUID()+".jpg";

        byte[] buffer;
        try (InputStream fileContent = filePart.getInputStream();
             OutputStream outputStream = new FileOutputStream(filePath)) {
            int read;
            buffer = new byte[1024];
            while ((read = fileContent.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return filePath;
        }catch (IOException e) {
            // 处理异常情况
            System.out.println(e.getMessage());
            return null;
        }
    }

    // 删除图片
    public static boolean DeleteImage(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("图片删除成功");
                return true;
            } else {
                System.out.println("图片删除失败");
                return false;
            }
        } else {
            System.out.println("图片文件不存在");
            return false;
        }
    }
}
