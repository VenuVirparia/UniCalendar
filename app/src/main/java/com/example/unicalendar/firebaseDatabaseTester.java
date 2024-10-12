package com.example.unicalendar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class firebaseDatabaseTester {
    private static final String TAG = "FirebaseDatabaseTester";
    private final DatabaseReference databaseReference;
    private final Context context;

    public firebaseDatabaseTester(Context context) {
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("events");
    }

    public void testDatabaseWrite() {
        String formattedDate = "12-10-2024";
        DatabaseReference eventRef = databaseReference.child(formattedDate).push();

        eventRef.setValue("Test event")
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Write was successful");
                    Toast.makeText(context, "Write was successful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Write failed: " + e.getMessage());
                    Toast.makeText(context, "Write failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}