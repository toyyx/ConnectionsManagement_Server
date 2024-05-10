package com.example.connection_server.User;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {

    private int userId;
    private String userName;
    private String password;
    private String name;
    private String gender;
    private String image_path;
    private String phone_number;
    private String email;

}