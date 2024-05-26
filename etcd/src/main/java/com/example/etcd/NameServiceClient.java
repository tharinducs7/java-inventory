package com.example.etcd;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class NameServiceClient {
    private final EtcdClient etcdClient;

    public NameServiceClient(String etcdAddress) throws IOException {
        this.etcdClient = new EtcdClient(etcdAddress);
    }

    public void registerService(String serviceName, String address) throws IOException {
        // Append to the list of addresses for the service
        String existingAddresses = etcdClient.get(serviceName);
        JSONArray addresses = existingAddresses.isEmpty() ? new JSONArray() : new JSONArray(existingAddresses);
        addresses.put(address);
        etcdClient.put(serviceName, addresses.toString());
    }

    public List<String> discoverServices(String serviceName) throws IOException {
        String response = etcdClient.get(serviceName);
        JSONObject responseObject = new JSONObject(response);
        JSONArray kvs = responseObject.getJSONArray("kvs");

        List<String> addressList = new ArrayList<>();
        for (int i = 0; i < kvs.length(); i++) {
            JSONObject kv = kvs.getJSONObject(i);
            String key = new String(Base64.getDecoder().decode(kv.getString("key")));
            String value = new String(Base64.getDecoder().decode(kv.getString("value")));
            if (key.equals(serviceName)) {
                addressList.add(value);
            }
        }
        return addressList;
    }
}
