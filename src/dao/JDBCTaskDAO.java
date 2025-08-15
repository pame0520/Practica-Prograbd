/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.sun.jdi.connect.spi.Connection;
import db.DBConnection;
import dominio.Task;
import exceptions.AppException;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.lang.model.util.Types;

/**
 *
 * @author pame
 */
public class JDBCTaskDAO implements TaskDAO {

    private static final String CREATE_SQL =
            "CREATE TABLE IF NOT EXISTS tareas (" +
            "id SERIAL PRIMARY KEY, " +
            "titulo VARCHAR(255) NOT NULL, " +
            "prioridad SMALLINT NOT NULL, " +
            "hecho BOOLEAN NOT NULL DEFAULT false, " +
            "especial BOOLEAN NOT NULL DEFAULT false, " +
            "fecha DATE, " +
            "visible BOOLEAN NOT NULL DEFAULT true, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static final String INSERT_SQL =
            "INSERT INTO tareas (titulo, prioridad, hecho, especial, fecha, visible) " +
            "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

    private static final String SELECT_ALL_SQL =
            "SELECT id, titulo, prioridad, hecho, especial, fecha, visible FROM tareas " +
            "WHERE visible = true ORDER BY id";

    private static final String UPDATE_SQL =
            "UPDATE tareas SET titulo=?, prioridad=?, hecho=?, especial=?, fecha=?, " +
            "updated_at=CURRENT_TIMESTAMP WHERE id=?";

    private static final String SOFT_DELETE_SQL =
            "UPDATE tareas SET visible=false, updated_at=CURRENT_TIMESTAMP WHERE id=?";

    private static final String RESTORE_SQL =
            "UPDATE tareas SET visible=true, updated_at=CURRENT_TIMESTAMP WHERE id=?";

    private static final String SELECT_BY_ID =
            "SELECT id, titulo, prioridad, hecho, especial, fecha, visible FROM tareas WHERE id = ?";

    @Override
    public void initSchema() throws AppException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement()) {
            st.execute(CREATE_SQL);
        } catch (Exception ex) {
            throw new AppException("Error creando esquema", ex);
        }
    }

    @Override
    public Task insert(Task t) throws AppException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(INSERT_SQL)) {
                ps.setString(1, t.getTitulo());
                ps.setInt(2, t.getPrioridad());
                ps.setBoolean(3, t.isHecho());
                ps.setBoolean(4, t.isEspecial());
                if (t.getFecha() != null) {
                    ps.setDate(5, Date.valueOf(t.getFecha()));
                } else {
                    ps.setNull(5, Types.DATE);
                }
                ps.setBoolean(6, t.isVisible());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        t.setId(rs.getInt(1));
                    }
                }

                c.commit();
                return t;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception ex) {
            throw new AppException("Error insertando tarea", ex);
        }
    }

    @Override
    public List<Task> findAllVisible() throws AppException {
        List<Task> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Task t = new Task();
                t.setId(rs.getInt("id"));
                t.setTitulo(rs.getString("titulo"));
                t.setPrioridad(rs.getInt("prioridad"));
                t.setHecho(rs.getBoolean("hecho"));
                t.setEspecial(rs.getBoolean("especial"));
                Date d = rs.getDate("fecha");
                if (d != null) t.setFecha(d.toLocalDate());
                t.setVisible(rs.getBoolean("visible"));
                list.add(t);
            }
        } catch (Exception ex) {
            throw new AppException("Error leyendo tareas", ex);
        }
        return list;
    }

    @Override
    public Optional<Task> findById(int id) throws AppException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Task t = new Task();
                    t.setId(rs.getInt("id"));
                    t.setTitulo(rs.getString("titulo"));
                    t.setPrioridad(rs.getInt("prioridad"));
                    t.setHecho(rs.getBoolean("hecho"));
                    t.setEspecial(rs.getBoolean("especial"));
                    Date d = rs.getDate("fecha");
                    if (d != null) t.setFecha(d.toLocalDate());
                    t.setVisible(rs.getBoolean("visible"));
                    return Optional.of(t);
                }
            }
        } catch (Exception ex) {
            throw new AppException("Error findById", ex);
        }
        return Optional.empty();
    }

    @Override
    public void update(Task t) throws AppException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, t.getTitulo());
            ps.setInt(2, t.getPrioridad());
            ps.setBoolean(3, t.isHecho());
            ps.setBoolean(4, t.isEspecial());
            if (t.getFecha() != null) {
                ps.setDate(5, Date.valueOf(t.getFecha()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setInt(6, t.getId());
            ps.executeUpdate();
        } catch (Exception ex) {
            throw new AppException("Error actualizando tarea", ex);
        }
    }

    @Override
    public void softDelete(int id) throws AppException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(SOFT_DELETE_SQL)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception ex) {
            throw new AppException("Error eliminando tarea", ex);
        }
    }

    @Override
    public void restore(int id) throws AppException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(RESTORE_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw new AppException("Error restaurando tarea", ex);
        }
    }
}