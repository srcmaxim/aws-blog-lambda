package io.srcmaxim.blog.model.dynamodb;

public interface DynamoDb {

    interface Blog {
        String TABLE_NAME = "Blog";
        String PK = "PK";
        String SK = "SK";
        String content = "context";
        String readMinutes = "readMinutes";
        String publishDate = "publishDate";
        String GSI1PK = "GSI1PK";
        String title = "title";
        String GSI1SK = "GSI1SK";
        String message = "message";
        String userEmail = "userEmail";
    }

    interface BlogGSI1 {
        String GSI1_NAME = "GSI1";
        String PK = "PK";
        String SK = "SK";
        String readMinutes = "readMinutes";
        String GSI1PK = "GSI1PK";
        String title = "title";
        String GSI1SK = "GSI1SK";
    }

}
