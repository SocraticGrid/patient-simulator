/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.tablemodel;

import com.cognitive.template.PointInTimeRuleTemplate;
import com.cognitive.template.SimulationRuleTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class PointInTimeRuleTemplateTableModel extends AbstractRuleTemplateTableModel<PointInTimeRuleTemplate> {

    public static enum ColumnHeaders {

        ON("On"),
        MODIFY("Modify"),
        BY("By");
        private final String text;

        ColumnHeaders(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public static class ColumnModel extends DefaultTableColumnModel {
        private final AbstractTableModel model;

        private ColumnModel(AbstractTableModel model){
            this.model = model;
        }
        
        @Override
        public void addColumn(TableColumn aColumn) {
            if (ColumnHeaders.ON.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JFormattedTextField(new PatternFormatter("[0-9]+s"))));
            } else if (ColumnHeaders.MODIFY.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JComboBox(JCpSimParameter.values())));
            } else if (ColumnHeaders.BY.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
            }
            
            aColumn.getCellEditor().addCellEditorListener(new CellEditorListener() {

                @Override
                public void editingStopped(ChangeEvent e) {
                    model.fireTableDataChanged();
                }

                @Override
                public void editingCanceled(ChangeEvent e) {
                }
            });
            
            super.addColumn(aColumn);
        }
    }

    public PointInTimeRuleTemplateTableModel() {

        for (ColumnHeaders columnHeaders : ColumnHeaders.values()) {
            this.columnNames.add(columnHeaders.toString());
        }
    }

    @Override
    public TableColumnModel createTableColumnModel(){
        return new ColumnModel(this);
    }
    
    @Override
    public void addRow() {
        this.addRow(new PointInTimeRuleTemplate("10s", JCpSimParameter.P_COMPLIANCE, 0.1));
    }
    
    @Override
    public void addRow(PointInTimeRuleTemplate rule) {
        List<String> row = new ArrayList<String>();
        
        for (ColumnHeaders columnHeader : ColumnHeaders.values()) {
            switch(columnHeader){
                case BY:
                    row.add(String.valueOf(rule.getValue()));
                    break;
                case MODIFY:
                    row.add(rule.getTarget().toString());
                    break;
                case ON:
                    row.add(rule.getTimeExpression());
                    break;
                default:
                    throw new UnsupportedOperationException(columnHeader + "not supported");
            }
        }

        this.values.add(row);
        
        this.fireTableDataChanged();
    }

    @Override
    public List<? extends SimulationRuleTemplate> getRules() {
        List<SimulationRuleTemplate> results = new ArrayList<SimulationRuleTemplate>();

        for (List<String> rowData : this.values) {
            
            String timeExpression = rowData.get(ColumnHeaders.ON.ordinal()).trim();
            JCpSimParameter target = JCpSimParameter.valueOf(rowData.get(ColumnHeaders.MODIFY.ordinal()));
            String valueExpression = rowData.get(ColumnHeaders.BY.ordinal()).trim();

            if (timeExpression.isEmpty()){
                throw new IllegalArgumentException(ColumnHeaders.ON+" must not be empty");
            }
            if (valueExpression.isEmpty()){
                throw new IllegalArgumentException(ColumnHeaders.BY+" must not be empty");
            }
            
            //is this a fixed or a regular periodical rule?
            SimulationRuleTemplate rule = new PointInTimeRuleTemplate(timeExpression, target, Double.valueOf(valueExpression));
            
            results.add(rule);
        }
        
        return results;
    }
    
    

}
