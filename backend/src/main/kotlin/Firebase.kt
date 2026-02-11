package net.vpg.apex

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream

fun initializeFirebase() {
    FirebaseApp.initializeApp(
        FirebaseOptions.builder()
            .setCredentials(
                GoogleCredentials.fromStream(FileInputStream("firebase-service-account.json"))
            ).build()
    )
}
