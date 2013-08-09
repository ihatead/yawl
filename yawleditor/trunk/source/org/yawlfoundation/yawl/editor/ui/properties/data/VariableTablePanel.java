package org.yawlfoundation.yawl.editor.ui.properties.data;

import org.yawlfoundation.yawl.editor.ui.properties.dialog.ExtendedAttributesDialog;
import org.yawlfoundation.yawl.editor.ui.util.ResourceLoader;
import org.yawlfoundation.yawl.elements.data.YParameter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Michael Adams
 * @date 9/08/12
 */
public class VariableTablePanel extends JPanel implements ActionListener, ListSelectionListener {

    private VariableTable table;
    private DataVariableDialog parent;
    private JToolBar toolbar;
    private TableType tableType;

    // toolbar buttons
    private JButton btnUp;
    private JButton btnDown;
    private JButton btnAdd;
    private JButton btnDel;
    private JToggleButton btnEdit;
    private JButton btnMapping;
    private JButton btnMIVar;
    private JButton btnExAttributes;
    private JLabel status;

    private static final String iconPath = "/org/yawlfoundation/yawl/editor/ui/resources/miscicons/";


    public VariableTablePanel(java.util.List<VariableRow> rows, TableType tableType,
                              String netElementName, DataVariableDialog parent) {
        this.parent = parent;
        this.tableType = tableType;
        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(createTable(rows, tableType, netElementName));
        scrollPane.setPreferredSize(new Dimension(tableType.getPreferredWidth(), 180));
        add(createToolBar(), BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);
        if (tableType == TableType.Net) {
            btnMapping.setVisible(false);
            btnMIVar.setVisible(false);
            btnExAttributes.setVisible(false);
        }
        table.getSelectionModel().addListSelectionListener(this);
        enableButtons(true);
    }


    public void valueChanged(ListSelectionEvent event) {
        clearStatus();
    }


    public VariableTable getTable() { return table; }


    public void showErrorStatus(String msg) {
        status.setForeground(Color.RED);
        status.setText("    " + msg);
        if (table.isEditing()) btnEdit.setEnabled(false);
    }


    public void showOKStatus(String msg) {
        status.setForeground(Color.GRAY);
        status.setText("    " + msg);
        btnEdit.setEnabled(true);
    }


    public void clearStatus() {
        status.setText(null);
        btnEdit.setEnabled(true);
    }


    public void showMIButton(boolean show) { btnMIVar.setVisible(show); }


    public void actionPerformed(ActionEvent event) {
        clearStatus();
        String action = event.getActionCommand();
        if (action.equals("Edit")) {
            setEditMode(btnEdit.isSelected());
        }
        else if (action.equals("Add")) {
            table.addRow();
            btnEdit.setSelected(true);
            setEditMode(true);
        }
        else if (action.equals("Del")) {
            table.removeRow();
            enableButtons(true);
        }
        else if (action.equals("Up")) {
            table.moveSelectedRowUp();
        }
        else if (action.equals("Down")) {
            table.moveSelectedRowDown();
        }
        else if (action.equals("Map")) {
            new MappingDialog(parent.getNetTablePanel(),
                    table.getSelectedVariable()).setVisible(true);
        }
        else if (action.equals("MarkMI")) {
            if (! parent.setMultiInstanceRow(table.getSelectedVariable())) {
                showErrorStatus("Invalid MI data type");
            }
        }
        else if (action.equals("ExAt")) {
            VariableRow row = table.getSelectedVariable();
            YParameter parameter = parent.getParameter(row.getName(), row.getUsage());
            if (parameter != null) {
                new ExtendedAttributesDialog(parent, parameter).setVisible(true);
            }
        }
    }


    public VariableRow getVariableAtRow(int row) {
        return table.getVariables().get(row);
    }


    public void showToolBar(boolean show) { toolbar.setVisible(show); }


    public VariableTablePanel copy() {
        return new VariableTablePanel(table.getVariables(), tableType,
                table.getNetElementName(), parent);
    }

    public DataVariableDialog getVariableDialog() { return parent; }

    private JTable createTable(java.util.List<VariableRow> rows, TableType tableType,
                               String netElementName) {
        table = new VariableTable(tableType.getModel());
        table.setVariables(rows);
        table.setNetElementName(netElementName);
        VariableRowUsageEditor usageEditor = new VariableRowUsageEditor();
        table.setDefaultEditor(Integer.class, usageEditor);
        VariableRowStringEditor stringEditor = new VariableRowStringEditor(this);
        table.setDefaultEditor(String.class, stringEditor);
        VariableRowUsageRenderer usageRenderer = new VariableRowUsageRenderer();
        table.setDefaultRenderer(Integer.class, usageRenderer);
        VariableRowStringRenderer stringRenderer = new VariableRowStringRenderer();
        table.setDefaultRenderer(String.class, stringRenderer);
        if (table.getRowCount() > 0) table.selectRow(0);
        return table;
    }


    private JToolBar createToolBar() {
        toolbar = new JToolBar();
        toolbar.setBorder(null);
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        btnAdd = createToolBarButton("plus", "Add", " Add ");
        toolbar.add(btnAdd);
        btnDel = createToolBarButton("minus", "Del", " Remove ");
        toolbar.add(btnDel);
        btnUp = createToolBarButton("arrow_up", "Up", " Move up ");
        toolbar.add(btnUp);
        btnDown = createToolBarButton("arrow_down", "Down", " Move down ");
        toolbar.add(btnDown);
        toolbar.add(createEditButton("pencil", "Edit", " Edit "));
        btnMapping = createToolBarButton("mapping", "Map", " Map ");
        toolbar.add(btnMapping);
        btnMIVar = createToolBarButton("miVar", "MarkMI", " Mark as MI ");
        toolbar.add(btnMIVar);
        btnExAttributes = createToolBarButton("exat", "ExAt", " Ext. Attributes ");
        toolbar.add(btnExAttributes);
        status = new JLabel();
        toolbar.add(status);
        return toolbar;
    }


    private JButton createToolBarButton(String iconName, String action, String tip) {
        JButton button = new JButton(getIcon(iconName));
        button.setActionCommand(action);
        button.setToolTipText(tip);
        button.addActionListener(this);
        return button;
    }

    private JToggleButton createEditButton(String iconName, String action, String tip) {
        btnEdit = new JToggleButton(getIcon(iconName));
        btnEdit.setActionCommand(action);
        btnEdit.setToolTipText(tip);
        btnEdit.addActionListener(this);
        btnEdit.setSelectedIcon(getIcon("editPressed"));
        return btnEdit;
    }


    private ImageIcon getIcon(String iconName) {
        return ResourceLoader.getImageAsIcon(iconPath + iconName + ".png");
    }


    protected void enableButtons(boolean enable) {
        boolean hasRows = table.getRowCount() > 0;
        btnAdd.setEnabled(enable);
        btnDel.setEnabled(enable && hasRows);
        btnUp.setEnabled(enable && hasRows);
        btnDown.setEnabled(enable && hasRows);
        btnEdit.setEnabled(hasRows);
        btnMapping.setEnabled(enable && hasRows);
        btnMIVar.setEnabled(enable && hasRows);
    }

    private void setEditMode(boolean editing) {
        table.setEditable(editing);
        enableButtons(!editing);
    }

}
