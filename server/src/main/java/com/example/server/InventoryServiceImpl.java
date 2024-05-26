package com.example.server;

import inventory.InventoryServiceGrpc;
import inventory.AddItemRequest;
import inventory.DeleteItemRequest;
import inventory.EditItemRequest;
import inventory.ListItemsResponse;
import inventory.ReservationRequest;
import inventory.Response;
import inventory.ItemInfo;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ConcurrentHashMap;

public class InventoryServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ConcurrentHashMap<String, Item> inventory = new ConcurrentHashMap<>();

    @Override
    public void addItem(AddItemRequest request, StreamObserver<Response> responseObserver) {
        String itemId = request.getItemId();
        if (inventory.containsKey(itemId)) {
            responseObserver.onNext(Response.newBuilder().setMessage("Item already exists").setSuccess(false).build());
        } else {
            inventory.put(itemId, new Item(request.getItemId(), request.getName(), request.getQuantity(), request.getPrice()));
            responseObserver.onNext(Response.newBuilder().setMessage("Item added").setSuccess(true).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void deleteItem(DeleteItemRequest request, StreamObserver<Response> responseObserver) {
        String itemId = request.getItemId();
        if (inventory.containsKey(itemId)) {
            inventory.remove(itemId);
            responseObserver.onNext(Response.newBuilder().setMessage("Item deleted").setSuccess(true).build());
        } else {
            responseObserver.onNext(Response.newBuilder().setMessage("Item not found").setSuccess(false).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void editItem(EditItemRequest request, StreamObserver<Response> responseObserver) {
        String itemId = request.getItemId();
        Item item = inventory.get(itemId);
        if (item != null) {
            item.setName(request.getName());
            item.setQuantity(request.getQuantity());
            item.setPrice(request.getPrice());
            responseObserver.onNext(Response.newBuilder().setMessage("Item edited").setSuccess(true).build());
        } else {
            responseObserver.onNext(Response.newBuilder().setMessage("Item not found").setSuccess(false).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void reserveItem(ReservationRequest request, StreamObserver<Response> responseObserver) {
        String itemId = request.getItemId();
        int quantity = request.getQuantity();
        Item item = inventory.get(itemId);
        if (item != null && item.getAvailableQuantity() >= quantity) {
            item.setReservedQuantity(item.getReservedQuantity() + quantity);
            String customerName = request.getCustomerName();
            String phoneNumber = request.getPhoneNumber();
            System.out.println("Reserved for: " + customerName + ", Phone: " + phoneNumber);
            responseObserver.onNext(Response.newBuilder().setMessage("Item reserved").setSuccess(true).build());
        } else {
            responseObserver.onNext(Response.newBuilder().setMessage("Item not available or insufficient quantity").setSuccess(false).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void listItems(inventory.ListItemsRequest request, StreamObserver<ListItemsResponse> responseObserver) {
        ListItemsResponse.Builder responseBuilder = ListItemsResponse.newBuilder();
        for (Item item : inventory.values()) {
            ItemInfo itemInfo = ItemInfo.newBuilder()
                    .setItemId(item.getItemId())
                    .setName(item.getName())
                    .setQuantity(item.getQuantity())
                    .setReservedQuantity(item.getReservedQuantity())
                    .setAvailableQuantity(item.getAvailableQuantity())
                    .setPrice(item.getPrice())
                    .build();
            responseBuilder.addItems(itemInfo);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
