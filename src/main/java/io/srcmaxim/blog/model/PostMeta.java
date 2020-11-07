package io.srcmaxim.blog.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDate;

@RegisterForReflection
public class PostMeta {

    public String id;
    public String title;
    public int readMinutes;
    public LocalDate publishDate;
    public String tag;

}
