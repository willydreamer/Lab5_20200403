package com.example.lab5_20200403;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import com.example.lab5_20200403.Entity.User;
import com.example.lab5_20200403.Entity.UserList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.example.lab5_20200403.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicialización necesaria
        saveInitialJson();
        listarArchivosGuardados();

        //Login
        binding.ingresarbotonlogin.setOnClickListener(v -> {
            String codigoIngresado = binding.usuarioCodigo.getText().toString().trim();
            User user = autenticarUsuario(codigoIngresado);

            if (user != null) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("authenticated_user", user);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Código incorrecto", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private User autenticarUsuario(String codigo) {
        UserList userList = cargarUsuarios();

        for (User user : userList.getUsers()) {
            if (user.getCodigo().equals(codigo)) {
                Log.d("msg-test-UserAutenticado", "usuario autenticado: " + user.getNombre());
                return user;
            }
        }

        return null;
    }

    public UserList cargarUsuarios() {
        String fileName = "PucpUsers.json";
        UserList userList = null;

        try (FileInputStream fileInputStream = openFileInput(fileName);
             FileReader fileReader = new FileReader(fileInputStream.getFD());
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonData = stringBuilder.toString();
            Gson gson = new Gson();
            userList = gson.fromJson(jsonData, UserList.class);

            for (User u : userList.getUsers()) {
                Log.d("msg-test-loadUsers", "nombre usuario: " + u.getNombre());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public void listarArchivosGuardados(){
        String[] archivosGuardados = fileList();

        for(String archivo: archivosGuardados){
            System.out.println(archivo);
        }
    }

    private void saveInitialJson() {
        // Estos son los usuarios que inicialmente existen el el sistema
        User[] users = new User[]{
                new User("20200401", "Juan", "Perez", new ArrayList<>()),
                new User("20200402", "Maria", "Lopez", new ArrayList<>()),
                new User("20200403", "Willy", "Huallpa", new ArrayList<>()),
                new User("20200404", "Carla", "Ramirez", new ArrayList<>()),
                new User("20200405", "Luis", "Herrera", new ArrayList<>())
        };

        UserList userList = new UserList();
        userList.setUsers(users);

        guardarUsuariosComoJson(userList);
    }

    public void guardarUsuariosComoJson(UserList userList) {
        Gson gson = new Gson();
        String userListJson = gson.toJson(userList);

        String fileNameJson = "PucpUsers.json";

        try (FileOutputStream fileOutputStream = this.openFileOutput(fileNameJson, Context.MODE_PRIVATE);
             FileWriter fileWriter = new FileWriter(fileOutputStream.getFD())) {
            fileWriter.write(userListJson);
            Log.d("msg-test-PucpUsers", "Archivo guardado correctamente");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}