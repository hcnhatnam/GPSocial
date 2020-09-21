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
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.iplocation.entites.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author namhcn
 */
@Service
public class UserFireBaseService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserFireBaseService.class);

    public static final String COL_NAME = "UsersTest";
    public static final Firestore dbFirestore = FirestoreClient.getFirestore();

    public void saveUser(User userFireBase) {
        try {
            dbFirestore.collection(COL_NAME).document(userFireBase.get_id()).set(userFireBase);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public Optional<User> getUser(String _id) {
        try {

            DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(_id);
            ApiFuture<DocumentSnapshot> future = documentReference.get();

            DocumentSnapshot document = future.get();

            if (document.exists()) {
                User userFireBase = document.toObject(User.class);
                return Optional.of(userFireBase);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    public Optional<User> getUserByEmail(String email) throws InterruptedException, ExecutionException {
        //asynchronously retrieve multiple documents
        ApiFuture<QuerySnapshot> future
                = dbFirestore.collection(COL_NAME).whereEqualTo("email", true).limit(1).get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (DocumentSnapshot document : documents) {
            return Optional.of(document.toObject(User.class));
        }
        return Optional.empty();
    }

    public String updateUser(User userFireBase) throws InterruptedException, ExecutionException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document(userFireBase.get_id()).set(userFireBase);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteUser(String name) {
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(name).delete();
        return "Document with Patient ID " + name + " has been deleted";
    }

    public List<User> getAllUser() {
        List<User> userFireBases = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection(COL_NAME).get();
            // future.get() blocks on response
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            documents.forEach(document -> {
                userFireBases.add(document.toObject(User.class));
            });
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return userFireBases;
    }

    public static void main(String[] args) {

    }
}
