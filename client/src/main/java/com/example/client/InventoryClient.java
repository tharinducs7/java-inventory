package com.example.client;

import inventory.InventoryServiceGrpc.InventoryServiceBlockingStub;
import inventory.AddItemRequest;
import inventory.DeleteItemRequest;
import inventory.EditItemRequest;
import inventory.ReservationRequest;
import inventory.Response;

public class InventoryClient {
    private final InventoryServiceBlockingStub blockingStub;

    public InventoryClient(InventoryServiceBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    public void run() {
        addItem("1", "Item1", 10);
        editItem("1", "NewItem1", 20);
        reserveItem("1", "1", 5);
        deleteItem("1");
    }

    private void addItem(String itemId, String name, int quantity) {
        AddItemRequest request = AddItemRequest.newBuilder()
                .setItemId(itemId)
                .setName(name)
                .setQuantity(quantity)
                .build();
        Response response = blockingStub.addItem(request);
        System.out.println("Add Item Response: " + response.getMessage());
    }

    private void deleteItem(String itemId) {
        DeleteItemRequest request = DeleteItemRequest.newBuilder()
                .setItemId(itemId)
                .build();
        Response response = blockingStub.deleteItem(request);
        System.out.println("Delete Item Response: " + response.getMessage());
    }

    private void editItem(String itemId, String name, int quantity) {
        EditItemRequest request = EditItemRequest.newBuilder()
                .setItemId(itemId)
                .setName(name)
                .setQuantity(quantity)
                .build();
        Response response = blockingStub.editItem(request);
        System.out.println("Edit Item Response: " + response.getMessage());
    }

    private void reserveItem(String reservationId, String itemId, int quantity) {
        ReservationRequest request = ReservationRequest.newBuilder()
                .setReservationId(reservationId)
                .setItemId(itemId)
                .setQuantity(quantity)
                .build();
        Response response = blockingStub.reserveItem(request);
        System.out.println("Reserve Item Response: " + response.getMessage());
    }
}
