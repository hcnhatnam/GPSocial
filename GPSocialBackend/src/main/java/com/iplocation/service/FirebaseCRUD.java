/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author namhcn
 * @param <T>
 */
public abstract class FirebaseCRUD<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseCRUD.class);
    private final Class<T> typeObjClass;
    private final String colName;

    public FirebaseCRUD(Class<T> typeParameterClass, String colName) {
        this.typeObjClass = typeParameterClass;
        this.colName = colName;
    }

    public static final Firestore dbFirestore = FirestoreClient.getFirestore();

    public abstract String getId(T obj);

    public void save(T userFireBase) {
        try {
            dbFirestore.collection(colName).document(getId(userFireBase)).set(userFireBase);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public Optional<T> get(String _id) {
        try {
            DocumentReference documentReference = dbFirestore.collection(colName).document(_id);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                T userFireBase = document.toObject(typeObjClass);
                return Optional.of(userFireBase);
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    public Optional<T> getByField(String fieldName, String fieldValue) {
        try {

            ApiFuture<QuerySnapshot> future
                    = dbFirestore.collection(colName).whereEqualTo(fieldName, fieldValue).limit(1).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                return Optional.of(document.toObject(typeObjClass));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    public void update(T obj) throws InterruptedException, ExecutionException {
        try {
            dbFirestore.collection(colName).document(getId(obj)).set(obj);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public void deleteUser(String id) {
        try {
            dbFirestore.collection(colName).document(id).delete();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public List<T> getAll() {
        List<T> listObj = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection(colName).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            documents.forEach(document -> {
                listObj.add(document.toObject(typeObjClass));
            });
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return listObj;
    }
}
