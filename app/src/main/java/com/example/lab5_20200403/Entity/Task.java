package com.example.lab5_20200403.Entity;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private String nombre;
    private String descripcion;
    private Date fechaVencimiento;
    private Date fechaRecordatorio;

    public Task(String nombre, String descripcion, Date fechaVencimiento, Date fechaRecordatorio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaRecordatorio = fechaRecordatorio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Date getFechaRecordatorio() {
        return fechaRecordatorio;
    }

    public void setFechaRecordatorio(Date fechaRecordatorio) {
        this.fechaRecordatorio = fechaRecordatorio;
    }
}
