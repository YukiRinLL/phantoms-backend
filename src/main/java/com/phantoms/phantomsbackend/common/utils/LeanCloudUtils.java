package com.phantoms.phantomsbackend.common.utils;

import cn.leancloud.LCException;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.types.LCNull;
import io.reactivex.Observable;

import java.util.Collections;
import java.util.List;

public class LeanCloudUtils {

    // KV Storage: Store key-value pairs
    public static boolean storeKV(String key, String value) {
        LCObject kvObject = new LCObject("KVStore");
        kvObject.put("key", key);
        kvObject.put("value", value);
        Observable<? extends LCObject> observable = kvObject.saveInBackground();
        try {
            observable.blockingFirst();
            return true;
        } catch (Exception e) {
            System.out.println("Storage failed: " + e.getMessage());
            return false;
        }
    }

    // KV Storage: Retrieve key-value pairs
    public static String getKV(String key) {
        LCQuery<LCObject> query = new LCQuery<>("KVStore");
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            if (!results.isEmpty()) {
                return results.get(0).getString("value");
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Retrieval failed: " + e.getMessage());
            return null;
        }
    }

    // KV Storage: Update key-value pairs
    public static boolean updateKV(String key, String newValue) {
        LCQuery<LCObject> query = new LCQuery<>("KVStore");
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            if (!results.isEmpty()) {
                LCObject kvObject = results.get(0);
                kvObject.put("value", newValue);
                Observable<? extends LCObject> updateObservable = kvObject.saveInBackground();
                updateObservable.blockingFirst();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    // KV Storage: Delete key-value pairs
    public static boolean deleteKV(String key) {
        LCQuery<LCObject> query = new LCQuery<>("KVStore");
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            if (!results.isEmpty()) {
                LCObject kvObject = results.get(0);
                Observable<LCNull> deleteObservable = kvObject.deleteInBackground();
                deleteObservable.blockingFirst();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Deletion failed: " + e.getMessage());
            return false;
        }
    }

    // Structured Data Storage: Create an object
    public static boolean createObject(String className, String key, String value) {
        LCObject object = new LCObject(className);
        object.put("key", key);
        object.put("value", value);
        Observable<? extends LCObject> observable = object.saveInBackground();
        try {
            observable.blockingFirst();
            return true;
        } catch (Exception e) {
            System.out.println("Object creation failed: " + e.getMessage());
            return false;
        }
    }

    // Structured Data Storage: Query objects
    public static List<LCObject> queryObject(String className, String key, String value) {
        LCQuery<LCObject> query = new LCQuery<>(className);
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            return observable.blockingFirst();
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Structured Data Storage: Update an object
    public static boolean updateObject(String className, String objectId, String key, String newValue) {
        LCObject object = LCObject.createWithoutData(className, objectId);
        object.put("value", newValue);
        Observable<? extends LCObject> observable = object.saveInBackground();
        try {
            observable.blockingFirst();
            return true;
        } catch (Exception e) {
            System.out.println("Object update failed: " + e.getMessage());
            return false;
        }
    }

    // Structured Data Storage: Delete an object
    public static boolean deleteObject(String className, String objectId) {
        LCObject object = LCObject.createWithoutData(className, objectId);
        Observable<LCNull> observable = object.deleteInBackground();
        try {
            observable.blockingFirst();
            return true; // 操作成功
        } catch (Exception e) {
            System.out.println("Object deletion failed: " + e.getMessage());
            return false; // 操作失败
        }
    }
}