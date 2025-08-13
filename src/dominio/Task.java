/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;

import java.time.LocalDate;

/**
 *
 * @author pame
 */
public class Task implements Serializable {
    private int id;
    private String titulo;
    private int prioridad; // 1=Alta,2=Media,3=Baja
    private boolean hecho;
    private boolean especial;
    private LocalDate fecha;
    private boolean visible = true;

    public Task() {}

    public Task(int id, String titulo, int prioridad, boolean hecho, boolean especial, LocalDate fecha) {
        this.id = id;
        this.titulo = titulo;
        this.prioridad = prioridad;
        this.hecho = hecho;
        this.especial = especial;
        this.fecha = fecha;
    }

    // constructores, getters/setters, toString
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    public boolean isHecho() { return hecho; }
    public void setHecho(boolean hecho) { this.hecho = hecho; }
    public boolean isEspecial() { return especial; }
    public void setEspecial(boolean especial) { this.especial = especial; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    @Override
    public String toString() {
        return String.format("Task{id=%d, titulo=%s, prioridad=%d, hecho=%b, especial=%b, fecha=%s, visible=%b}",
                id, titulo, prioridad, hecho, especial, fecha, visible);
    }
}