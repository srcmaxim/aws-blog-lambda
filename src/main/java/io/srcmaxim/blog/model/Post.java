package io.srcmaxim.blog.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
public class Post {

    public String id;
    public String title;
    public String content;
    public int readMinutes;
    public LocalDate publishDate;
    public List<String> tags = new ArrayList<>();
    public List<Message> messages = new ArrayList<>();

    @RegisterForReflection
    public static class Message {

        public LocalDate createdAt;
        public String message;
        public String userEmail;

    }

}
