package com.example.server;

import com.example.etcd.NameServiceClient;
import com.example.zookeeper.DistributedLock;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class InventoryServer {
    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final String ETCD_ADDRESS = "http://localhost:2379";
    private static final String SERVICE_NAME = "InventoryService";

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        int port = 50051;

        DistributedLock lock = new DistributedLock(ZOOKEEPER_ADDRESS, "/locks", "inventory_lock");

        System.out.println("Attempting to acquire lock...");
        lock.acquireLock();
        lock.waitForLock();
        System.out.println("Lock acquired!");

        NameServiceClient nameServiceClient = new NameServiceClient(ETCD_ADDRESS);
        nameServiceClient.registerService(SERVICE_NAME, "localhost:" + port);

        Server server = ServerBuilder.forPort(port)
                .addService(new InventoryServiceImpl())
                .build()
                .start();

        System.out.println("Inventory Server started on port " + port);
        server.awaitTermination();

        lock.releaseLock();
        System.out.println("Lock released");
    }
}
