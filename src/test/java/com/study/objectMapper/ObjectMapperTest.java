package com.study.objectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class ObjectMapperTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POJO 객체를 JSON 형태의 String으로 변환한다.")
    void wrtieTest() throws JsonProcessingException {

        User semi = new User("semi", 30);
        String json = objectMapper.writeValueAsString(semi);

        System.out.println("json = " + json);
    }

    @Test
    @DisplayName("JSON 형태 String을 JsonNode로 변환한다. ")
    void test2() throws JsonProcessingException {
        String json = "{\"name\":\"semi\", \"age\":30}";
        JsonNode jsonNode = objectMapper.readTree(json);

        String name = jsonNode.get("name").asText();
        int age = jsonNode.get("age").asInt();

        System.out.println("name = " + name);
        System.out.println("age = " + age);

        User user = objectMapper.readValue(json, User.class);
        System.out.println("user = " + user);
    }

    @Test
    void test3() throws JsonProcessingException {
        String jsonArr = "[{\"name\":\"semi\", \"age\":30},{\"name\":\"enjung\",\"age\":10}]";
        //String jsonArr = "[{\"name\":\"Ryan\",\"age\":30},{\"name\":\"Jake\",\"age\":20}]";
        List<User> users = objectMapper.readValue(jsonArr, new TypeReference<>() {});


        List<Map<String, String>> maps = objectMapper.readValue(jsonArr, new TypeReference<List<Map<String, String>>>() {
        });
        for (User user : users) {
            System.out.println("user = " + user);
        }


        User user = new User();

        System.out.println("================");

        for (Map<String, String> map : maps) {
            System.out.println("map.get(\"name\") = " + map.get("name"));
            System.out.println("map.get(\"age\") = " + map.get("age"));
        }
    }


    static class User {
        private String name;
        private int age;

        public User() {
        }

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
