package com.example.client;

import inventory.InventoryServiceGrpc;
import inventory.InventoryServiceGrpc.InventoryServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class ClientApp {
    public static void main(String[] args) throws InterruptedException {
        String target = "localhost:50051";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        InventoryServiceBlockingStub blockingStub = InventoryServiceGrpc.newBlockingStub(channel);

        InventoryClient client = new InventoryClient(blockingStub);
        client.run();

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
