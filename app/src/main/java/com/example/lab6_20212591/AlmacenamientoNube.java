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
    public void guardarArchivo(Uri uriArchivo, String nombreArchivo, Context context) {
        StorageReference archivoRef = storageRef.child("imagenes/" + nombreArchivo);
        UploadTask uploadTask = archivoRef.putFile(uriArchivo);

        uploadTask.addOnSuccessListener(taskSnapshot ->
                archivoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Toast.makeText(context, "Archivo subido: " + uri.toString(), Toast.LENGTH_LONG).show();
                })
        ).addOnFailureListener(e ->
                Toast.makeText(context, "Error al subir: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    // Obtener URL de archivo específico proveniente de Storage
    public void obtenerArchivo(String nombreArchivo, OnArchivoObtenidoListener listener) {
        StorageReference archivoRef = storageRef.child("imagenes/" + nombreArchivo);
        archivoRef.getDownloadUrl()
                .addOnSuccessListener(uri -> listener.onArchivoObtenido(uri.toString()))
                .addOnFailureListener(e -> listener.onArchivoError(e.getMessage()));
    }

    public interface OnArchivoObtenidoListener {
        void onArchivoObtenido(String url);
        void onArchivoError(String error);
    }
}