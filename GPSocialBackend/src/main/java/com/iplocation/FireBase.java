/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 *
 * @author namhcn
 */
@Service
public class FireBase {

    public static String projectId = "iplocationchat";

    public static void main(String[] args) throws IOException {
        FileInputStream serviceAccount
                = new FileInputStream("iplocationchat-firebase-adminsdk.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://iplocationchat.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }

    @PostConstruct
    public void initialize() {
        try {

            FileInputStream serviceAccount
                    = new FileInputStream("iplocationchat-firebase-adminsdk.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://iplocationchat.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
        }
    }
}
