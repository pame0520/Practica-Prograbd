/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javax.swing.JOptionPane;

/**
 *
 * @author pame
 */
public class MainFrame extends JFrame {
    private TaskService service;
    private TaskTableModel tableModel;
    private JTable table;

    // Form fields
    private JTextField txtTitulo;
    private JComboBox<Integer> cbPrioridad;
    private JCheckBox chEspecial;
    private JFormattedTextField txtFecha;
    private JLabel statusLabel;

    public MainFrame(TaskService service) {
        super("Gestor de Tareas - P1");
        this.service = service;
        initComponents();
        loadTasks();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTitulo = new JTextField(25);
        cbPrioridad = new JComboBox<>(new Integer[]{1,2,3});
        chEspecial = new JCheckBox("★ Especial");
        txtFecha = new JFormattedTextField(DateUtils.getFormatter());
        txtFecha.setColumns(8);

        form.add(new JLabel("Título:")); form.add(txtTitulo);
        form.add(new JLabel("Prioridad:")); form.add(cbPrioridad);
        form.add(new JLabel("Fecha:")); form.add(txtFecha);
        form.add(chEspecial);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnToggle = new JButton("Alternar Hecho");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnDeshacer = new JButton("Deshacer");

        form.add(btnAgregar); form.add(btnToggle); form.add(btnEliminar); form.add(btnDeshacer);

        // Table
        tableModel = new TaskTableModel(List.of());
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);

        statusLabel = new JLabel("Listo");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);

        // Listeners
        btnAgregar.addActionListener(e -> onAgregar());
        btnToggle.addActionListener(e -> onToggle());
        btnEliminar.addActionListener(e -> onEliminar());
        btnDeshacer.addActionListener(e -> onDeshacer());
    }

    private void loadTasks() {
        setStatus("Cargando tareas...");
        new SwingWorker<List<Task>, Void>() {
            protected List<Task> doInBackground() throws Exception {
                return service.listTasks();
            }
            protected void done() {
                try {
                    tableModel.setData(get());
                    setStatus("Tareas cargadas: " + tableModel.getRowCount());
                } catch (Exception ex) {
                    showError("Error al cargar tareas: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void onAgregar() {
        String titulo = txtTitulo.getText();
        int prioridad = (Integer) cbPrioridad.getSelectedItem();
        boolean especial = chEspecial.isSelected();
        LocalDate fecha = DateUtils.parse(txtFecha.getText());

        setStatus("Agregando...");
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                service.addTask(titulo, prioridad, fecha, especial);
                return null;
            }
            protected void done() {
                try {
                    get();
                    clearForm();
                    loadTasks();
                } catch (Exception ex) {
                    showError(ex.getCause() instanceof AppException ? ex.getCause().getMessage() : ex.getMessage());
                }
            }
        }.execute();
    }

    private void onToggle() {
        int r = table.getSelectedRow();
        if (r < 0) { showError("Seleccione una fila"); return; }
        int id = (int) table.getValueAt(r, 0);
        setStatus("Actualizando estado...");
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                service.toggleDone(id);
                return null;
            }
            protected void done() {
                try { get(); loadTasks(); } catch (Exception ex) { showError(ex.getMessage()); }
            }
        }.execute();
    }

    private void onEliminar() {
        int r = table.getSelectedRow();
        if (r < 0) { showError("Seleccione una fila"); return; }
        int id = (int) table.getValueAt(r, 0);
        int opt = JOptionPane.showConfirmDialog(this, "Eliminar (no borrado físico) la tarea ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        setStatus("Eliminando...");
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                service.delete(id);
                return null;
            }
            protected void done() {
                try { get(); loadTasks(); } catch (Exception ex) { showError(ex.getMessage()); }
            }
        }.execute();
    }

    private void onDeshacer() {
        setStatus("Deshaciendo...");
        new SwingWorker<Boolean, Void>() {
            protected Boolean doInBackground() throws Exception {
                return service.undoDelete();
            }
            protected void done() {
                try {
                    boolean ok = get();
                    if (!ok) showError("No hay nada para deshacer");
                    loadTasks();
                } catch (Exception ex) { showError(ex.getMessage()); }
            }
        }.execute();
    }

    private void clearForm() {
        txtTitulo.setText("");
        cbPrioridad.setSelectedIndex(1);
        chEspecial.setSelected(false);
        txtFecha.setValue(null);
    }

    private void setStatus(String s) {
        statusLabel.setText(s);
    }

    private void showError(String s) {
        setStatus("ERROR: " + s);
        JOptionPane.showMessageDialog(this, s, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
