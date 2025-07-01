package com.example.lab6_20212591;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lab6_20212591.databinding.FragmentLaboratorio7Binding;
import com.squareup.picasso.Picasso;


public class Laboratorio7Fragment extends Fragment {

    private FragmentLaboratorio7Binding binding;
    private Uri imagenSeleccionadaUri;
    private AlmacenamientoNube almacenamientoNube;

    private final ActivityResultLauncher<Intent> selectorImagenLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imagenSeleccionadaUri = result.getData().getData();
                    binding.ivPreview.setImageURI(imagenSeleccionadaUri);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLaboratorio7Binding.inflate(inflater, container, false);
        almacenamientoNube = new AlmacenamientoNube();

        binding.btnSeleccionar.setOnClickListener(v -> abrirGaleria());

        binding.btnSubir.setOnClickListener(v -> {
            if (imagenSeleccionadaUri != null) {
                String nombreArchivo = binding.etNombreArchivo.getText().toString().trim();
                if (nombreArchivo.isEmpty()) {
                    Toast.makeText(getContext(), "Ingresa un nombre para el archivo", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!nombreArchivo.endsWith(".jpg")) {
                    nombreArchivo += ".jpg"; // extensión por default :D
                }
                binding.progressBar.setVisibility(View.VISIBLE); //Barra de progreso :D
                almacenamientoNube.guardarArchivo(imagenSeleccionadaUri, nombreArchivo, requireContext(), new AlmacenamientoNube.OnArchivoGuardadoListener() {
                    @Override
                    public void onArchivoGuardado(String url) {
                        binding.progressBar.setVisibility(View.GONE); // Ocultar progreso
                        Toast.makeText(getContext(), "Archivo subido: " + url, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onArchivoError(String error) {
                        binding.progressBar.setVisibility(View.GONE); // Ocultar progreso
                        Toast.makeText(getContext(), "Error al subir: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnDescargar.setOnClickListener(v -> {
            String input = binding.etNombreArchivo.getText().toString().trim();
            if (!input.isEmpty()) {
                final String nombre = input.endsWith(".jpg") ? input : input + ".jpg";

                binding.progressBar.setVisibility(View.VISIBLE);

                almacenamientoNube.obtenerArchivo(nombre, new AlmacenamientoNube.OnArchivoObtenidoListener() {
                    @Override
                    public void onArchivoObtenido(String url) {
                        mostrarImagen(url);
                        guardarImagenEnDescargas(url, nombre);
                        binding.progressBar.setVisibility(View.GONE); // Ocultar progreso
                    }

                    @Override
                    public void onArchivoError(String error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Ingresa el nombre del archivo", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectorImagenLauncher.launch(intent);
    }

    private void mostrarImagen(String url) {
        Picasso.get().load(url).into(binding.ivPreview);
    }

    private void guardarImagenEnDescargas(String url, String nombreArchivo) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED || android.os.Build.VERSION.SDK_INT >= 29) {

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle("Descargando imagen");
            request.setDescription(nombreArchivo);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombreArchivo);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager manager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } else {
            Toast.makeText(getContext(), "Permiso denegado para guardar archivos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}