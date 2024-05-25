package com.example.lab5_20200403;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20200403.Entity.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_task, parent, false);
        return new TaskViewHolder(view, view.getContext(),taskList, this);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private TextView titleTextRecordatorio;
        private TextView subtitleTextDescripcion;
        private TextView fechaVencimiento;
        private TextView horaRecordatorio;
        private TextView importanciaText;
        private LinearLayout alert;
        private LinearLayout alarmaLayout;
        private ImageButton menuIcon;
        private ImageView iconoImportancia;
        private Context context;
        private List<Task> taskList;
        private TaskAdapter taskAdapter;

        public TaskViewHolder(View itemView, Context context,List<Task> taskList, TaskAdapter taskAdapter) {
            super(itemView);
            this.context = context;
            this.taskList = taskList;
            this.taskAdapter = taskAdapter;
            titleTextRecordatorio = itemView.findViewById(R.id.titleTextRecordatorio);
            subtitleTextDescripcion = itemView.findViewById(R.id.subtitleTextDescripcion);
            fechaVencimiento = itemView.findViewById(R.id.fechaVencimiento);
            horaRecordatorio = itemView.findViewById(R.id.horaRecordatorio);
            importanciaText = itemView.findViewById(R.id.importanciaText);
            alert = itemView.findViewById(R.id.alert);
            alarmaLayout = itemView.findViewById(R.id.alarmaLayout);
            menuIcon = itemView.findViewById(R.id.menuIcon);
            iconoImportancia = itemView.findViewById(R.id.iconoImportancia);

            // Set click listener for the menu icon
            menuIcon.setOnClickListener(this);
        }

        public void bind(Task task) {
            titleTextRecordatorio.setText(task.getTaskName());
            subtitleTextDescripcion.setText(task.getTaskDescription());
            String date = task.getDay() + " de " + getMonthName(task.getMonth()) + ", " + task.getYear();
            fechaVencimiento.setText(date);

            String time = String.format("%02d:%02d %s",
                    (task.getHour() % 12 == 0) ? 12 : task.getHour() % 12,
                    task.getMinute(),
                    (task.getHour() >= 12) ? "pm" : "am");
            horaRecordatorio.setText(time);

            switch (task.getImportance()) {
                case "Alta":
                    importanciaText.setText("PRIORIDAD ALTA");
                    importanciaText.setTextColor(Color.parseColor("#FF6347"));
                    iconoImportancia.setColorFilter(Color.parseColor("#FF6347"));
                    alert.setVisibility(View.VISIBLE);
                    break;
                case "Media":
                    importanciaText.setText("PRIORIDAD MEDIA");
                    importanciaText.setTextColor(Color.parseColor("#4E8DC3"));
                    iconoImportancia.setColorFilter(Color.parseColor("#4E8DC3"));
                    alert.setVisibility(View.VISIBLE);
                    break;
                case "Baja":
                    importanciaText.setText("PRIORIDAD BAJA");
                    importanciaText.setTextColor(Color.parseColor("#008000"));
                    iconoImportancia.setColorFilter(Color.parseColor("#008000"));
                    alert.setVisibility(View.VISIBLE);
                    break;
                default:
                    alert.setVisibility(View.GONE);
                    break;
            }
        }

        private String getMonthName(int month) {
            String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            return months[month];
        }

        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(context, menuIcon);
            popupMenu.inflate(R.menu.menu_options);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Task task = taskList.get(position);
                if (item.getItemId() == R.id.edit_option) {
                    editTask(task);
                    return true;
                } else if (item.getItemId() == R.id.delete_option) {
                    deleteTask(position);
                    return true;
                }
            }
            return false;
        }


        private void editTask(Task task) {
            Intent intent = new Intent(context, CreateTaskActivity.class);
            intent.putExtra("task_to_edit", task);
            context.startActivity(intent);
        }

        private void deleteTask(int position) {
            taskList.remove(position);
            taskAdapter.notifyItemRemoved(position);
        }

    }
}
