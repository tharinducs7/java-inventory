package com.example.client;

import inventory.InventoryServiceGrpc;
import inventory.InventoryServiceGrpc.InventoryServiceBlockingStub;
import inventory.AddItemRequest;
import inventory.DeleteItemRequest;
import inventory.EditItemRequest;
import inventory.ReservationRequest;
import inventory.Response;
import inventory.ListItemsRequest;
import inventory.ListItemsResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class InventoryClient {
    private final InventoryServiceBlockingStub blockingStub;

    public InventoryClient(InventoryServiceBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Add Item");
            System.out.println("2. Edit Item");
            System.out.println("3. Delete Item");
            System.out.println("4. Reserve Item");
            System.out.println("5. List Items");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addItem(scanner);
                    break;
                case 2:
                    editItem(scanner);
                    break;
                case 3:
                    deleteItem(scanner);
                    break;
                case 4:
                    reserveItem(scanner);
                    break;
                case 5:
                    listItems();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addItem(Scanner scanner) {
        System.out.print("Enter item ID: ");
        String itemId = scanner.nextLine();
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        AddItemRequest request = AddItemRequest.newBuilder()
                .setItemId(itemId)
                .setName(name)
                .setQuantity(quantity)
                .setPrice(price)
                .build();
        Response response = blockingStub.addItem(request);
        System.out.println("Add Item Response: " + response.getMessage());
    }

    private void editItem(Scanner scanner) {
        System.out.print("Enter item ID: ");
        String itemId = scanner.nextLine();
        System.out.print("Enter new item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new quantity: ");
        int quantity = scanner.nextInt();
        System.out.print("Enter new price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        EditItemRequest request = EditItemRequest.newBuilder()
                .setItemId(itemId)
                .setName(name)
                .setQuantity(quantity)
                .setPrice(price)
                .build();
        Response response = blockingStub.editItem(request);
        System.out.println("Edit Item Response: " + response.getMessage());
    }

    private void deleteItem(Scanner scanner) {
        System.out.print("Enter item ID: ");
        String itemId = scanner.nextLine();

        DeleteItemRequest request = DeleteItemRequest.newBuilder()
                .setItemId(itemId)
                .build();
        Response response = blockingStub.deleteItem(request);
        System.out.println("Delete Item Response: " + response.getMessage());
    }

    private void reserveItem(Scanner scanner) {
        System.out.print("Enter reservation ID: ");
        String reservationId = scanner.nextLine();
        System.out.print("Enter item ID: ");
        String itemId = scanner.nextLine();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();

        ReservationRequest request = ReservationRequest.newBuilder()
                .setReservationId(reservationId)
                .setItemId(itemId)
                .setQuantity(quantity)
                .setCustomerName(customerName)
                .setPhoneNumber(phoneNumber)
                .build();
        Response response = blockingStub.reserveItem(request);
        System.out.println("Reserve Item Response: " + response.getMessage());
    }

    private void listItems() {
        ListItemsRequest request = ListItemsRequest.newBuilder().build();
        ListItemsResponse response = blockingStub.listItems(request);
        for (inventory.ItemInfo itemInfo : response.getItemsList()) {
            System.out.printf("ID: %s, Name: %s, Quantity: %d, Reserved: %d, Available: %d, Price: %.2f%n",
                    itemInfo.getItemId(), itemInfo.getName(), itemInfo.getQuantity(),
                    itemInfo.getReservedQuantity(), itemInfo.getAvailableQuantity(), itemInfo.getPrice());
        }
    }
}
