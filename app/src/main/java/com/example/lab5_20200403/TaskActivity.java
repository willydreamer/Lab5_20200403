package com.example.lab5_20200403;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20200403.Entity.Task;
import com.example.lab5_20200403.Entity.User;
import com.example.lab5_20200403.Entity.UserList;
import com.example.lab5_20200403.databinding.ActivityTaskBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private ActivityTaskBinding binding;
    private static final int REQUEST_CODE_CREATE_TASK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        User authenticatedUser = (User) getIntent().getSerializableExtra("authenticated_user");
        String saludo = "Hola " + authenticatedUser.getNombre() + "!";
        binding.tasksTextSaludo.setText(saludo);
        taskList = authenticatedUser.getTareas();

        taskAdapter = new TaskAdapter(taskList);

        binding.tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.tasksRecyclerView.setAdapter(taskAdapter);

        binding.crearTask.setOnClickListener(v -> {
            Intent intent = new Intent(TaskActivity.this, CreateTaskActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_TASK);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_TASK && resultCode == RESULT_OK && data != null) {
            Task newTask = (Task) data.getSerializableExtra("newTask");
            if (newTask != null) {
                taskList.add(newTask);
                taskAdapter.notifyItemInserted(taskList.size() - 1);

                // Para sobreescribir el archivo json
                UserList userList = cargarUsuarios();
                User authenticatedUser = (User) getIntent().getSerializableExtra("authenticated_user");

                if (userList != null && authenticatedUser != null) {
                    for (User user : userList.getUsers()) {
                        if (user.getCodigo().equals(authenticatedUser.getCodigo())) {
                            // Agregar la nueva tarea al usuario autenticado
                            user.getTareas().add(newTask);
                            break;
                        }
                    }
                    guardarUsuariosComoJson(userList);
                }
            }
        }
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
}


