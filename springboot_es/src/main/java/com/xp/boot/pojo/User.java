package com.xp.boot.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

//可以自动创建索引和类型
@Document(indexName = "xp", type = "user", shards = 5, replicas = 1) //es的注解,标记实体类为文档对象
public class User {

    //@Id作用在成员变量，标记一个字段为id主键；一般id字段或是域不需要存储也不需要分词
    //同时指定这个id为"_id",默认"_id"属性值为一个唯一的随机字符串
    @Id
    private Integer id;

    //@Field标记为文档的字段,并制定映射属性
    //type：字段的类型，取值是枚举，FieldType
    //index：是否索引，布尔值类型，默认是true；
    //store,是否存储,布尔值类型,默认值是false
    //analyzer:分词器名称
    @Field(store = true, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String userName;

    @Field
    private int age;

    @Field(analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String hobby;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                ", hobby='" + hobby + '\'' +
                '}';
    }
}
