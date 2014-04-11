/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.tablemodel;

import com.cognitive.template.SimulationRuleTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author esteban
 */
abstract class AbstractRuleTemplateTableModel<T extends SimulationRuleTemplate> extends AbstractTableModel {
    List<String> columnNames = new ArrayList<String>();
    List<List<String>> values = new ArrayList<List<String>>();

    public AbstractRuleTemplateTableModel() {
    }

    public abstract void addRow();
    
    public abstract void addRow(T rule);
    
    public abstract List<? extends SimulationRuleTemplate> getRules();

    public abstract TableColumnModel createTableColumnModel();
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        values.get(rowIndex).set(columnIndex, String.valueOf(aValue));
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return values.get(rowIndex).get(columnIndex);
    }
    
    public void deleteRowAt(int rowIndex){
        this.values.remove(rowIndex);
        this.fireTableDataChanged();
    }
    
    public void clear(){
        this.values.clear();
        this.fireTableDataChanged();
    }
}
