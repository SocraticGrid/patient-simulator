/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.tablemodel;

import com.cognitive.template.SimulationRuleTemplate;
import com.cognitive.template.ThresholdRuleTemplate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
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
public class ThresholdRuleTemplateTableModel extends AbstractRuleTemplateTableModel<ThresholdRuleTemplate> {

    public static enum ColumnHeaders{
        WHEN("When"),
        THRESHOLD("For"),
        MODIFY("Modify"),
        BY("By"),
        RESET_WHEN("Reset When");
        
        private final String text;
        
        ColumnHeaders(String text){
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
            if (ColumnHeaders.WHEN.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new TextAreaCellEditor(true));
            } else if (ColumnHeaders.THRESHOLD.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JFormattedTextField(NumberFormat.getInstance())));
            } else if (ColumnHeaders.MODIFY.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JComboBox(JCpSimParameter.values())));
            } else if (ColumnHeaders.BY.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JFormattedTextField(new DecimalFormat())));
            } else if (ColumnHeaders.RESET_WHEN.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new TextAreaCellEditor(true));
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

    public ThresholdRuleTemplateTableModel() {

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
        values.add(new ArrayList<String>() {
            {
                add("data[V_PEEP] > 10");
                add("10000");
                add("P_COMPLIANCE");
                add("-0.0012");
                add("");
            }
        });
        this.fireTableDataChanged();
    }
    
    @Override
    public void addRow(ThresholdRuleTemplate rule) {
        List<String> row = new ArrayList<String>();
        
        for (ThresholdRuleTemplateTableModel.ColumnHeaders columnHeader : ThresholdRuleTemplateTableModel.ColumnHeaders.values()) {
            switch(columnHeader){
                case BY:
                    row.add(String.valueOf(rule.getValue()));
                    break;
                case MODIFY:
                    row.add(rule.getTarget().toString());
                    break;
                case WHEN:
                    row.add(rule.getCondition());
                    break;
                case RESET_WHEN:
                    row.add(rule.getResetCondition());
                    break;
                case THRESHOLD:
                    row.add(String.valueOf(rule.getThreshold()));
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
            
            String condition = rowData.get(ThresholdRuleTemplateTableModel.ColumnHeaders.WHEN.ordinal()).trim();
            String resetCondition = rowData.get(ThresholdRuleTemplateTableModel.ColumnHeaders.RESET_WHEN.ordinal()).trim();
            JCpSimParameter target = JCpSimParameter.valueOf(rowData.get(ThresholdRuleTemplateTableModel.ColumnHeaders.MODIFY.ordinal()));
            String valueExpression = rowData.get(ThresholdRuleTemplateTableModel.ColumnHeaders.BY.ordinal()).trim();
            String thresholdExpression = rowData.get(ThresholdRuleTemplateTableModel.ColumnHeaders.THRESHOLD.ordinal()).trim();

            if (condition.isEmpty()){
                throw new IllegalArgumentException(ThresholdRuleTemplateTableModel.ColumnHeaders.WHEN+" must not be empty");
            }
            if (thresholdExpression.isEmpty()){
                throw new IllegalArgumentException(ThresholdRuleTemplateTableModel.ColumnHeaders.THRESHOLD+" must not be empty");
            }
            if (valueExpression.isEmpty()){
                throw new IllegalArgumentException(ThresholdRuleTemplateTableModel.ColumnHeaders.BY+" must not be empty");
            }
            
            ThresholdRuleTemplate rule = new ThresholdRuleTemplate(condition, target, Double.parseDouble(valueExpression), Long.parseLong(thresholdExpression),resetCondition);
            
            results.add(rule);
        }
        
        return results;
    }
}
