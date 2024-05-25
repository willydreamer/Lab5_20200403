package com.example.lab5_20200403;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TaskActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private ActivityTaskBinding binding;
    private static final int REQUEST_CODE_CREATE_TASK = 1;

    // Constantes para los niveles de importancia
    private static final String IMPORTANCE_HIGH = "Alto";
    private static final String IMPORTANCE_DEFAULT = "Medio";
    private static final String IMPORTANCE_LOW = "Bajo";

    String canal1 = "importanteDefault";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        crearCanalesNotificacion();

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

        notificarImportanceDefault();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_TASK && resultCode == RESULT_OK && data != null) {
            Task newTask = (Task) data.getSerializableExtra("newTask");
            if (newTask != null) {
                taskList.add(newTask);
                taskAdapter.notifyItemInserted(taskList.size() - 1);
                // notificacion de cantidad de tareas
                notificarCantidadTareas();


//                // Establecer la importancia de la notificación
//                String taskImportance = newTask.getImportance();
//                int notificationImportance;
//                if (taskImportance.equals(IMPORTANCE_HIGH) ||
//                        (taskImportance.equals(IMPORTANCE_DEFAULT) && isTaskInNext3Hours(newTask))) {
//                    newTask.setImportance("Alta");
//                    notificationImportance = NotificationManager.IMPORTANCE_HIGH;
//                } else if (taskImportance.equals(IMPORTANCE_DEFAULT)) {
//                    notificationImportance = NotificationManager.IMPORTANCE_DEFAULT;
//                } else {
//                    notificationImportance = NotificationManager.IMPORTANCE_LOW;
//                }
//                scheduleNotification(newTask, notificationImportance);

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
    private boolean isTaskInNext3Hours(Task task) {
        Calendar taskCalendar = Calendar.getInstance();
        taskCalendar.set(task.getYear(), task.getMonth() - 1, task.getDay(), task.getHour(), task.getMinute());

        Calendar now = Calendar.getInstance();

        long diffMillis = taskCalendar.getTimeInMillis() - now.getTimeInMillis();
        long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);

        return diffHours <= 3;
    }

    private void scheduleNotification(Task task, int notificationImportance) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel("my_channel_id", "My Channel", notificationImportance);
        notificationManager.createNotificationChannel(channel);

        Intent intent = new Intent(this, TaskActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.ic_notifications_24px)
                .setContentTitle("Recordatorio: " + task.getTaskName())
                .setContentText("Para el día " + task.getDay() + "/" + task.getMonth() + "/" + task.getYear())
                .setPriority(notificationImportance)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, task.getYear());
            calendar.set(Calendar.MONTH, task.getMonth() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, task.getDay());
            calendar.set(Calendar.HOUR_OF_DAY, task.getHour());
            calendar.set(Calendar.MINUTE, task.getMinute());
            calendar.set(Calendar.SECOND, 0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        notificationManager.notify(1, builder.build());
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

    public void crearCanalesNotificacion() {

        NotificationChannel channel = new NotificationChannel(canal1,
                "Canal notificaciones default",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Canal para notificaciones con prioridad default");
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        pedirPermisos();
    }
    public void pedirPermisos() {
        // TIRAMISU = 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(TaskActivity.this, new String[]{POST_NOTIFICATIONS}, 101);
        }
    }

    public void notificarImportanceDefault() {
        User authenticatedUser = (User) getIntent().getSerializableExtra("authenticated_user");

        // Crear notificación persistente
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canal1)
                .setSmallIcon(R.drawable.ic_notifications_24px)
                .setContentTitle("Usuario logueado: " + authenticatedUser.getNombre() + " " + authenticatedUser.getApellido() + "(" + authenticatedUser.getCodigo()+ ")")
                .setContentText("Haz clic aquí para volver a la pantalla principal")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOngoing(true)  // Hacer la notificación persistente
                .setAutoCancel(true);

        Notification notification = builder.build();

        // Lanzar notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, notification);
        }
    }
    public void notificarCantidadTareas() {
        User authenticatedUser = (User) getIntent().getSerializableExtra("authenticated_user");

        // Crear notificación persistente
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canal1)
                .setSmallIcon(R.drawable.ic_notifications_24px)
                .setContentTitle("Tienes " + authenticatedUser.getTareas().size() + " Tareas pendientes")
                .setContentText("Haz clic aquí para volver a la pantalla principal")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOngoing(true)  // Hacer la notificación persistente
                .setAutoCancel(true);

        Notification notification = builder.build();

        // Lanzar notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, notification);
        }
    }

}


