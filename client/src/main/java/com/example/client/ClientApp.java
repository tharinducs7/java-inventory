package com.example.client;

import com.example.etcd.NameServiceClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import inventory.InventoryServiceGrpc;
import inventory.InventoryServiceGrpc.InventoryServiceBlockingStub;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClientApp {
    private static final String ETCD_ADDRESS = "http://localhost:2379";
    private static final String SERVICE_NAME = "InventoryService";

    public static void main(String[] args) throws InterruptedException, IOException {
        NameServiceClient nameServiceClient = new NameServiceClient(ETCD_ADDRESS);
        List<String> addresses = nameServiceClient.discoverServices(SERVICE_NAME);

        ManagedChannel channel = null;
        InventoryServiceBlockingStub blockingStub = null;

        for (String address : addresses) {
            try {
                channel = ManagedChannelBuilder.forTarget(address)
                        .usePlaintext()
                        .build();

                blockingStub = InventoryServiceGrpc.newBlockingStub(channel);

                // Attempt to connect by making a simple request (could be any request)
                blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS); // Set a timeout for the request

                // If we reach this point, the connection was successful
                System.out.println("Connected to server at " + address);
                break;
            } catch (Exception e) {
                System.err.println("Failed to connect to server at " + address);
                if (channel != null) {
                    channel.shutdownNow();
                }
            }
        }

        if (blockingStub == null) {
            System.err.println("Failed to connect to any server. Exiting...");
            return;
        }

        InventoryClient client = new InventoryClient(blockingStub);
        client.run();

        if (channel != null) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
