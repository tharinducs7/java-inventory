package com.example.etcd;

import java.io.IOException;
import java.util.List;

public class Main {
    private static final String ETCD_ADDRESS = "http://localhost:2379";
    private static final String SERVICE_NAME = "InventoryService";

    public static void main(String[] args) throws IOException {
        NameServiceClient nameServiceClient = new NameServiceClient(ETCD_ADDRESS);

        // Registering service example (you can modify this as per your requirement)
        nameServiceClient.registerService(SERVICE_NAME, "localhost:50051");

        // Discovering services example
        List<String> addresses = nameServiceClient.discoverServices(SERVICE_NAME);
        for (String address : addresses) {
            System.out.println("Discovered service address: " + address);
        }
    }
}
