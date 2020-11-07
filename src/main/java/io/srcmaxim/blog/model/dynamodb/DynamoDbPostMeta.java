package io.srcmaxim.blog.model.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class DynamoDbPostMeta {

    public String pk;
    public String sk;
    public String gsi1Pk;
    public String gsi1Sk;
    public int readMinutes;
    public String title;

    public static DynamoDbPostMeta from(Map<String, AttributeValue> item) {
        var dynamoDbPostMeta = new DynamoDbPostMeta();
        dynamoDbPostMeta.pk = item.get(DynamoDb.BlogGSI1.PK).s();
        dynamoDbPostMeta.sk = item.get(DynamoDb.BlogGSI1.SK).s();
        dynamoDbPostMeta.gsi1Pk = item.get(DynamoDb.BlogGSI1.GSI1PK).s();
        dynamoDbPostMeta.gsi1Sk = item.get(DynamoDb.BlogGSI1.GSI1SK).s();
        dynamoDbPostMeta.readMinutes = Integer.parseInt(item.get(DynamoDb.BlogGSI1.readMinutes).n());
        dynamoDbPostMeta.title = item.get(DynamoDb.BlogGSI1.title).s();
        return dynamoDbPostMeta;
    }

}
