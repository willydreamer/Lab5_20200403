package com.example.lab5_20200403;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20200403.Entity.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private FloatingActionButton fab;
    private ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        fab = findViewById(R.id.fab);

        taskList = new ArrayList<>();

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab.setOnClickListener(view -> {
            // Lógica para agregar nueva tarea
            // Mostrar diálogo para ingresar nueva tarea
        });

    }

}