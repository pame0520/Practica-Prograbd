/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import exceptions.AppException;

/**
 *
 * @author pame
 */
public class SimulatedTaskDAO implements TaskDAO {
    private List<Task> storage = new ArrayList<>();
    private AtomicInteger seq = new AtomicInteger(0);
    private final File file;

    public SimulatedTaskDAO() {
        file = new File("simulated_tasks.ser");
        load();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            storage = (List<Task>) ois.readObject();
            int max = storage.stream().mapToInt(Task::getId).max().orElse(0);
            seq.set(max);
        } catch (Exception e) {
            System.err.println("No se pudo cargar datos simulados: " + e.getMessage());
        }
    }

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(storage);
        } catch (Exception e) {
            System.err.println("No se pudo guardar datos simulados: " + e.getMessage());
        }
    }

    @Override
    public Task insert(Task t) throws AppException {
        int id = seq.incrementAndGet();
        t.setId(id);
        storage.add(t);
        save();
        return t;
    }

    @Override
    public List<Task> findAllVisible() throws AppException {
        return storage.stream().filter(Task::isVisible).sorted(Comparator.comparingInt(Task::getId)).collect(Collectors.toList());
    }

    @Override
    public Optional<Task> findById(int id) throws AppException {
        return storage.stream().filter(x -> x.getId() == id).findFirst();
    }

    @Override
    public void update(Task t) throws AppException {
        findById(t.getId()).ifPresent(orig -> {
            orig.setTitulo(t.getTitulo());
            orig.setPrioridad(t.getPrioridad());
            orig.setHecho(t.isHecho());
            orig.setEspecial(t.isEspecial());
            orig.setFecha(t.getFecha());
            orig.setVisible(t.isVisible());
            save();
        });
    }

    @Override
    public void softDelete(int id) throws AppException {
        findById(id).ifPresent(x -> { x.setVisible(false); save(); });
    }

    @Override
    public void restore(int id) throws AppException {
        findById(id).ifPresent(x -> { x.setVisible(true); save(); });
    }

    @Override
    public void initSchema() throws AppException {
        // No-op for simulated
    }
}