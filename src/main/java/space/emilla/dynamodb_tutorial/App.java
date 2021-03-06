package space.emilla.dynamodb_tutorial;
/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import java.io.IOException;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.BillingMode;

import space.emilla.dynamodb_tutorial.models.Character;

public class App {

    private static final AmazonDynamoDB client = setUp();
    private static final DynamoDBMapper mapper = new DynamoDBMapper(client);

    private static AmazonDynamoDB setUp() {
        var credentials = InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(true);

        var endpoint = new AmazonDynamoDBClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2");
        var client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(endpoint).build();
        try {
            credentials.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return client;
    }
    public static void main(String[] args) {
        // describeTable(client);
        prepareTable(Character.class);
        prepareRecords();
        System.out.println("Table scanning...");
        mapper.scan(Character.class, new DynamoDBScanExpression()).stream().limit(scanNonNegativeInteger()).forEach(System.out::println);
    }

    static int scanNonNegativeInteger() {
        var scanner = new Scanner(System.in);
        System.out.print("Scanning size > ");
        var scannedString = scanner.nextLine();
        scanner.close();
        var nonNegativeInteger = Integer.parseInt(scannedString);
        if (nonNegativeInteger <= 0) { throw new IllegalArgumentException("A non negative integer should not be equal or less than zero"); }

        return nonNegativeInteger;
    }

    static void describeTable(AmazonDynamoDB client) {
        client.listTables().getTableNames().forEach(System.out::println);
    }

    static void prepareTable(Class<?> clazz) {
        if (client.listTables().getTableNames().stream().filter((name) -> name.equals("characters")).count() != 0) { return; }

        System.out.println("Sample table inserting...");
        var createTableRequest = mapper.generateCreateTableRequest(clazz).withBillingMode(BillingMode.PAY_PER_REQUEST);
        client.createTable(createTableRequest);
    }

    static void prepareRecords() {
        if(mapper.count(Character.class, new DynamoDBScanExpression()) != 0) { return; }

        System.out.println("Sample records inserting...");
        Function<Integer, String> sequencialNameGenerator = (serial) -> "Character #" + serial;
        var characters = IntStream.rangeClosed(1, 10000)
            .mapToObj((i) -> new Character(sequencialNameGenerator.apply(i), i, "this is my profile"))
            .collect(Collectors.toSet());
        mapper.batchSave(characters);
        System.out.println("Inserted records: " + characters.size());
    }
}
