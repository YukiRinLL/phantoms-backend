package com.phantoms.phantomsbackend.common.utils;

import cn.leancloud.LCACL;
import cn.leancloud.LCException;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.types.LCNull;
import io.reactivex.Observable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LeanCloudUtils {

    // Store key-value pairs with ACL
    public static boolean storeKV(String key, String value, String userId) {
        LCObject kvObject = new LCObject("KVStore");
        kvObject.put("key", key);
        kvObject.put("value", value);

        LCACL acl = new LCACL();
        acl.setReadAccess(userId, true);
        acl.setWriteAccess(userId, true);
        kvObject.setACL(acl);

        Observable<? extends LCObject> observable = kvObject.saveInBackground();
        try {
            observable.blockingFirst();
            return true;
        } catch (Exception e) {
            System.out.println("Storage failed: " + e.getMessage());
            return false;
        }
    }

    // Retrieve key-value pairs with permission check
    public static String getKV(String key, String userId) {
        LCQuery<LCObject> query = new LCQuery<>("KVStore");
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            if (!results.isEmpty()) {
                LCObject kvObject = results.get(0);
                LCACL acl = kvObject.getACL();
                if (acl != null && acl.getReadAccess(userId)) {
                    return kvObject.getString("value");
                } else {
                    System.out.println("No permission to read this object.");
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Retrieval failed: " + e.getMessage());
            return null;
        }
    }

    // Update key-value pairs with permission check
    public static boolean updateKV(String key, String newValue, String userId) {
        LCQuery<LCObject> query = new LCQuery<>("KVStore");
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            if (!results.isEmpty()) {
                LCObject kvObject = results.get(0);
                LCACL acl = kvObject.getACL();
                if (acl != null && acl.getWriteAccess(userId)) {
                    kvObject.put("value", newValue);
                    Observable<? extends LCObject> updateObservable = kvObject.saveInBackground();
                    updateObservable.blockingFirst();
                    return true;
                } else {
                    System.out.println("No permission to update this object.");
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    // Delete key-value pairs with permission check
    public static boolean deleteKV(String key, String userId) {
        LCQuery<LCObject> query = new LCQuery<>("KVStore");
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            if (!results.isEmpty()) {
                LCObject kvObject = results.get(0);
                LCACL acl = kvObject.getACL();
                if (acl != null && acl.getWriteAccess(userId)) {
                    Observable<LCNull> deleteObservable = kvObject.deleteInBackground();
                    deleteObservable.blockingFirst();
                    return true;
                } else {
                    System.out.println("No permission to delete this object.");
                    return false;
                }
            } else {
                System.out.println("No record found for key: " + key);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Deletion failed: " + e.getMessage());
            return false;
        }
    }

    // Create an object with ACL
    public static boolean createObject(String className, String key, String value, String userId) {
        LCObject object = new LCObject(className);
        object.put("key", key);
        object.put("value", value);

        LCACL acl = new LCACL();
        acl.setReadAccess(userId, true);
        acl.setWriteAccess(userId, true);
        object.setACL(acl);

        Observable<? extends LCObject> observable = object.saveInBackground();
        try {
            observable.blockingFirst();
            return true;
        } catch (Exception e) {
            System.out.println("Object creation failed: " + e.getMessage());
            return false;
        }
    }

    // Query objects with permission check
    public static List<LCObject> queryObject(String className, String key, String value, String userId) {
        LCQuery<LCObject> query = new LCQuery<>(className);
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            return results.stream()
                    .filter(obj -> {
                        LCACL acl = obj.getACL();
                        return acl != null && acl.getReadAccess(userId);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Update an object with permission check
    public static boolean updateObject(String className, String objectId, String key, String newValue, String userId) {
        LCObject object = LCObject.createWithoutData(className, objectId);
        LCACL acl = object.getACL();
        if (acl != null && acl.getWriteAccess(userId)) {
            object.put("value", newValue);
            Observable<? extends LCObject> observable = object.saveInBackground();
            try {
                observable.blockingFirst();
                return true;
            } catch (Exception e) {
                System.out.println("Object update failed: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("No permission to update this object.");
            return false;
        }
    }

    // Delete an object with permission check
    public static boolean deleteObject(String className, String objectId, String userId) {
        LCObject object = LCObject.createWithoutData(className, objectId);
        LCACL acl = object.getACL();
        if (acl != null && acl.getWriteAccess(userId)) {
            Observable<LCNull> observable = object.deleteInBackground();
            try {
                observable.blockingFirst();
                return true;
            } catch (Exception e) {
                System.out.println("Object deletion failed: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("No permission to delete this object.");
            return false;
        }
    }
}