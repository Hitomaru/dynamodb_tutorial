package space.emilla.dynamodb_tutorial.marshaller;

import java.time.LocalDateTime;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class LocalDateTimeMarshaller implements DynamoDBTypeConverter<String,LocalDateTime> {

    @Override
    public String convert(final LocalDateTime time) {
        return time.toString();
    }

    @Override
    public LocalDateTime unconvert(final String stringValue) {
        System.out.println(String.format("String -> LocalDateTime: {}", stringValue));
        return LocalDateTime.parse(stringValue);
    }
    
}
