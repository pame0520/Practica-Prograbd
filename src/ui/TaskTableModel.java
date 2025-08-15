/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import dominio.Task;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author pame
 */
public class TaskTableModel extends AbstractTableModel {
    private final String[] cols = {"ID", "Título", "Prioridad", "Estado", "Especial", "Fecha"};
    private List<Task> data;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TaskTableModel(List<Task> list) { this.data = list; }

    public void setData(List<Task> list) {
        this.data = list;
        fireTableDataChanged();
    }

    public int getRowCount() { return data == null ? 0 : data.size(); }
    public int getColumnCount() { return cols.length; }
    public String getColumnName(int column) { return cols[column]; }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Task t = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return t.getId();
            case 1: return t.getTitulo();
            case 2: return t.getPrioridad();
            case 3: return t.isHecho() ? "Hecho" : "Pendiente";
            case 4: return t.isEspecial() ? "★" : "";
            case 5: return t.getFecha() == null ? "" : t.getFecha().format(df);
            default: return "";
        }
    }

    public Task getTaskAt(int row) {
        return data.get(row);
    }
}