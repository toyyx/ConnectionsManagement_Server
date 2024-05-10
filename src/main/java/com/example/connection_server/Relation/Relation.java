package com.example.connection_server.Relation;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.TreeMap;

@Setter
@Getter
public class Relation {
    //用户id
    private int userId;
    private int personId;
    private String relationship;
    private String name;
    private String gender;
    private String phone_number;
    private String email;
    private String notes;
    private String image_path;

    public Relation(int get_userId,String get_relationship,String get_name,String get_gender,String get_phone_number,String get_email,String get_notes,String get_image_path){
        userId=get_userId;
        relationship=get_relationship;
        name=get_name;
        gender=get_gender;
        phone_number=get_phone_number;
        email=get_email;
        notes=get_notes;
        image_path=get_image_path;
    }

    // 自定义比较器
    public static class FloatValueComparator implements Comparator<Relation> {
        private TreeMap<Relation, Float> map;

        // 构造函数
        public FloatValueComparator(TreeMap<Relation, Float> map) {
            this.map = map;
        }

        @Override
        public int compare(Relation r1, Relation r2) {
            float value1 = map.get(r1);
            float value2 = map.get(r2);
            return Float.compare(value1, value2);
        }
    }

}
