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
import com.iplocation.entites.IData;
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
public class FirebaseCRUD<T extends IData> implements IDataOperate<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseCRUD.class);
    private final Class<T> typeObjClass;
    private final String colName;

    public FirebaseCRUD(Class<T> typeParameterClass, String colName) {
        this.typeObjClass = typeParameterClass;
        this.colName = colName;
    }

    public static final Firestore dbFirestore = FirestoreClient.getFirestore();

    @Override
    public Optional<T> save(T userFireBase) {
        try {
            dbFirestore.collection(colName).document(userFireBase.getId()).set(userFireBase);
            return Optional.of(userFireBase);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
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

    @Override
    public Optional<T> getByField(String fieldName, String fieldValue) {
        try {

            ApiFuture<QuerySnapshot> future
                    = dbFirestore.collection(colName).whereEqualTo(fieldName, fieldValue).limit(1).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                return Optional.of(document.toObject(typeObjClass));
            }

        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> update(T obj) {
        try {
            dbFirestore.collection(colName).document(obj.getId()).set(obj);
            return Optional.of(obj);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> deleteUser(String id) {
        try {
            Optional<T> opObj = get(id);
            if (opObj.isPresent()) {
                dbFirestore.collection(colName).document(id).delete();
                return opObj;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

//    public List<T> getAll() {
//        List<T> listObj = new ArrayList<>();
//        try {
//            ApiFuture<QuerySnapshot> future = dbFirestore.collection(colName).get();
//            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//            documents.forEach(document -> {
//                listObj.add(document.toObject(typeObjClass));
//            });
//        } catch (InterruptedException | ExecutionException ex) {
//            LOGGER.error(ex.getMessage(), ex);
//        }
//        return listObj;
//    }
}
