package com.example.lab5_20200403.Entity;

import com.example.lab5_20200403.Entity.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class User implements Serializable{
    private String codigo;
    private String nombre;
    private String apellido;
    private List<Task> tareas;

    public User(String codigo, String nombre, String apellido, List<Task> tareas) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tareas = tareas;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public List<Task> getTareas() {
        return tareas;
    }

    public void setTareas(List<Task> tareas) {
        this.tareas = tareas;
    }
}
