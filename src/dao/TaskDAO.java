/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dominio.Task;
import exceptions.AppException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author pame
 */
public interface TaskDAO {
    Task insert(Task t) throws AppException;
    List<Task> findAllVisible() throws AppException;
    Optional<Task> findById(int id) throws AppException;
    void update(Task t) throws AppException;
    void softDelete(int id) throws AppException; // marca visible=false
    void restore(int id) throws AppException;    // restaura visible=true
    void initSchema() throws AppException;       // crear tablas si no existen
}
