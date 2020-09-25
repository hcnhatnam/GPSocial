/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author namhcn
 */
@Service
public class FBInitialize {

    private static final Logger LOGGER = LoggerFactory.getLogger(FBInitialize.class);
    
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
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        
    }
}
