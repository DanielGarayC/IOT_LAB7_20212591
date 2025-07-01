package com.example.lab6_20212591;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AlmacenamientoNube {

    private final StorageReference storageRef;

    public AlmacenamientoNube() {
        // Conexión a Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    // Guardar archivo en Firebase Storage
    public void guardarArchivo(Uri fileUri, String nombreArchivo, Context context, OnArchivoGuardadoListener listener) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("imagenes/" + nombreArchivo);
        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    listener.onArchivoGuardado(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    listener.onArchivoError(e.getMessage());
                });
    }

    // Obtener URL de archivo específico proveniente de Storage
    public void obtenerArchivo(String nombreArchivo, OnArchivoObtenidoListener listener) {
        StorageReference archivoRef = storageRef.child("imagenes/" + nombreArchivo);
        archivoRef.getDownloadUrl()
                .addOnSuccessListener(uri -> listener.onArchivoObtenido(uri.toString()))
                .addOnFailureListener(e -> listener.onArchivoError(e.getMessage()));
    }

    public interface OnArchivoGuardadoListener {
        void onArchivoGuardado(String url);
        void onArchivoError(String error);
    }

    public interface OnArchivoObtenidoListener {
        void onArchivoObtenido(String url);
        void onArchivoError(String error);
    }
}