package com.example.lab5_20200403;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab5_20200403.Entity.Task;
import com.example.lab5_20200403.Entity.User;
import com.example.lab5_20200403.Entity.UserList;
import com.example.lab5_20200403.databinding.ActivityCreateTaskBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CreateTaskActivity extends AppCompatActivity {

    ActivityCreateTaskBinding binding;
    private Task taskToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Regresar
        binding.backButton.setOnClickListener(v -> finish());

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("task_to_edit")) {
            // Modo edición
            taskToEdit = (Task) intent.getSerializableExtra("task_to_edit");
            if (taskToEdit != null) {
                binding.titulo.setText("Editar Tarea");
                // Setear los datos de la tarea en los campos de la vista
                binding.taskNameEditText.setText(taskToEdit.getTaskName());
                binding.taskDescriptionEditText.setText(taskToEdit.getTaskDescription());
                binding.taskDatePicker.updateDate(taskToEdit.getYear(), taskToEdit.getMonth(), taskToEdit.getDay());
                binding.reminderTimePicker.setCurrentHour(taskToEdit.getHour());
                binding.reminderTimePicker.setCurrentMinute(taskToEdit.getMinute());
                binding.importanceSpinner.setSelection(getImportanceIndex(taskToEdit.getImportance()));

                // Configurar el botón de guardar cambios
                binding.createTaskButton.setText("Guardar Cambios");
                binding.createTaskButton.setOnClickListener(v -> {
                    taskToEdit.setTaskName(binding.taskNameEditText.getText().toString());
                    taskToEdit.setTaskDescription(binding.taskDescriptionEditText.getText().toString());
                    taskToEdit.setDay(binding.taskDatePicker.getDayOfMonth());
                    taskToEdit.setMonth(binding.taskDatePicker.getMonth());
                    taskToEdit.setYear(binding.taskDatePicker.getYear());
                    taskToEdit.setHour(binding.reminderTimePicker.getCurrentHour());
                    taskToEdit.setMinute(binding.reminderTimePicker.getCurrentMinute());
                    taskToEdit.setImportance(binding.importanceSpinner.getSelectedItem().toString());

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newTask", taskToEdit);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
            }
        } else {
            // Modo creación
            binding.titulo.setText("Crear Tarea");
            binding.createTaskButton.setOnClickListener(v -> {
                String taskName = binding.taskNameEditText.getText().toString();
                String taskDescription = binding.taskDescriptionEditText.getText().toString();
                int day = binding.taskDatePicker.getDayOfMonth();
                int month = binding.taskDatePicker.getMonth();
                int year = binding.taskDatePicker.getYear();
                int hour = binding.reminderTimePicker.getCurrentHour();
                int minute = binding.reminderTimePicker.getCurrentMinute();
                String importance = binding.importanceSpinner.getSelectedItem().toString();

                Task newTask = new Task(taskName, taskDescription, day, month, year, hour, minute, importance);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("newTask", newTask);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        }
    }

    private int getImportanceIndex(String importance) {
        switch (importance) {
            case "Alta":
                return 0;
            case "Media":
                return 1;
            case "Baja":
                return 2;
            default:
                return 0;
        }
    }
}
