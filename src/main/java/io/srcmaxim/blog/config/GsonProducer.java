package io.srcmaxim.blog.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.time.LocalDate;

@Default
public class GsonProducer {

    @Produces
    @Singleton
    public Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, getLocalDateJsonSerializer())
                .registerTypeAdapter(LocalDate.class, getLocalDateJsonDeserializer())
                .create();
    }

    private static JsonSerializer<LocalDate> getLocalDateJsonSerializer() {
        return (date, type, context) -> new JsonPrimitive(date.toString());
    }

    private static JsonDeserializer<LocalDate> getLocalDateJsonDeserializer() {
        return (json, type, context) -> LocalDate.parse(json.getAsString());
    }

}
