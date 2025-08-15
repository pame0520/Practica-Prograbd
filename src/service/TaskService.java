/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.DAOFactory;
import dao.TaskDAO;
import dominio.Task;
import exceptions.AppException;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author pame
 */
public class TaskService {
    private final TaskDAO dao;
    private final List<Task> cache = new ArrayList<>(); // lista en memoria (colección 1)
    private final Map<Integer, Task> mapById = new HashMap<>(); // búsqueda rápida (colección 2)
    private final Deque<Integer> undoStack; // pila de deshacer (colección 3)

    private final int undoLimit;

    public TaskService() throws AppException {
        this.dao = DAOFactory.createTaskDAO();
        this.undoLimit = Math.max(5, Integer.parseInt(Optional.ofNullable(System.getProperty("undo.limit")).orElse(String.valueOf(20))));
        this.undoStack = new ArrayDeque<>(undoLimit);
        dao.initSchema();
        reload();
    }

    public synchronized void reload() throws AppException {
        cache.clear();
        cache.addAll(dao.findAllVisible());
        mapById.clear();
        for (Task t : cache) mapById.put(t.getId(), t);
    }

    public synchronized Task addTask(String titulo, int prioridad, LocalDate fecha, boolean especial) throws AppException {
        validate(titulo, prioridad);
        Task t = new Task(0, titulo.trim(), prioridad, false, especial, fecha);
        Task persisted = dao.insert(t);
        cache.add(persisted);
        mapById.put(persisted.getId(), persisted);
        return persisted;
    }

    private void validate(String titulo, int prioridad) throws AppException {
        if (titulo == null || titulo.trim().isEmpty()) throw new AppException("El título no puede estar vacío");
        if (prioridad < 1 || prioridad > 3) throw new AppException("Prioridad fuera de rango (1-3)");
    }

    public synchronized List<Task> listTasks() {
        // demostración de Streams (paradigma declarativo)
        return cache.stream().sorted(Comparator.comparingInt(Task::getId)).collect(Collectors.toList());
    }

    public synchronized void toggleDone(int id) throws AppException {
        Task t = mapById.get(id);
        if (t == null) throw new AppException("Tarea no encontrada");
        t.setHecho(!t.isHecho());
        dao.update(t);
    }

    public synchronized void delete(int id) throws AppException {
        Task t = mapById.get(id);
        if (t == null) throw new AppException("Tarea no encontrada para eliminar");
        dao.softDelete(id);
        cache.removeIf(x -> x.getId() == id);
        mapById.remove(id);
        if (undoStack.size() == undoLimit) {
            // límite razonable: descartamos el más antiguo (cola)
            undoStack.removeFirst();
        }
        undoStack.addLast(id); // push
    }
    public synchronized boolean undoDelete() throws AppException {
        if (undoStack.isEmpty()) return false;
        int id = undoStack.removeLast();
        dao.restore(id);
        // recargar o pedir tarea por id
        Optional<Task> maybe = dao.findById(id);
        if (maybe.isPresent()) {
            Task t = maybe.get();
            cache.add(t);
            mapById.put(t.getId(), t);
            return true;
        }
        return false;
    }
}