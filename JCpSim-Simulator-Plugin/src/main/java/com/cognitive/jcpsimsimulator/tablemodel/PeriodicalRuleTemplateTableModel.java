/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.tablemodel;

import com.cognitive.template.PeriodicalFixedRuleTemplate;
import com.cognitive.template.PeriodicalRuleTemplate;
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
public class PeriodicalRuleTemplateTableModel extends AbstractRuleTemplateTableModel<PeriodicalRuleTemplate> {


    public static enum ColumnHeaders {

        AFTER("After"),
        EVERY("Every"),
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
            if (ColumnHeaders.EVERY.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JFormattedTextField(new PatternFormatter("[0-9]+s"))));
            } else if (ColumnHeaders.MODIFY.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JComboBox(JCpSimParameter.values())));
            } else if (ColumnHeaders.BY.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
            } else if (ColumnHeaders.AFTER.toString().equals(aColumn.getHeaderValue())) {
                aColumn.setCellEditor(new DefaultCellEditor(new JFormattedTextField(new PatternFormatter("[0-9]+s"))));
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

    public PeriodicalRuleTemplateTableModel() {

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
        this.addRow(new PeriodicalFixedRuleTemplate("0s", "30s", JCpSimParameter.P_COMPLIANCE, -0.01));
    }

    @Override
    public void addRow(PeriodicalRuleTemplate rule) {
        List<String> row = new ArrayList<String>();
        
        for (PeriodicalRuleTemplateTableModel.ColumnHeaders columnHeader : PeriodicalRuleTemplateTableModel.ColumnHeaders.values()) {
            switch(columnHeader){
                case BY:
                    StringBuilder builder = new StringBuilder("{");
                    
                    for (int i = 0; i < rule.getValues().length; i++) {
                        if (i > 0){
                            builder.append(",");
                        }
                        builder.append(rule.getValues()[i]);
                    }
                    builder.append("}");
                    row.add(builder.toString());
                    break;
                case MODIFY:
                    row.add(rule.getTarget().toString());
                    break;
                case EVERY:
                    row.add(rule.getTimeExpression());
                    break;
                case AFTER:
                    row.add(rule.getInitialDelayExpression());
                    break;
                default:
                    throw new UnsupportedOperationException(columnHeader + "not supported");
            }
        }

        this.values.add(row);
        
        this.fireTableDataChanged();
    }

    public void addRow(PeriodicalFixedRuleTemplate rule) {
        List<String> row = new ArrayList<String>();
        
        for (PeriodicalRuleTemplateTableModel.ColumnHeaders columnHeader : PeriodicalRuleTemplateTableModel.ColumnHeaders.values()) {
            switch(columnHeader){
                case BY:
                    row.add(String.valueOf(rule.getValue()));
                    break;
                case MODIFY:
                    row.add(rule.getTarget().toString());
                    break;
                case EVERY:
                    row.add(rule.getTimeExpression());
                    break;
                case AFTER:
                    row.add(rule.getInitialDelayExpression());
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
            
            String initialDelayExpression = rowData.get(ColumnHeaders.AFTER.ordinal()).trim();
            String timeExpression = rowData.get(ColumnHeaders.EVERY.ordinal()).trim();
            JCpSimParameter target = JCpSimParameter.valueOf(rowData.get(ColumnHeaders.MODIFY.ordinal()));
            String valueExpression = rowData.get(ColumnHeaders.BY.ordinal()).trim();

            if (initialDelayExpression.isEmpty()){
                throw new IllegalArgumentException(ColumnHeaders.AFTER+" must not be empty");
            }
            if (timeExpression.isEmpty()){
                throw new IllegalArgumentException(ColumnHeaders.EVERY+" must not be empty");
            }
            if (valueExpression.isEmpty()){
                throw new IllegalArgumentException(ColumnHeaders.BY+" must not be empty");
            }
            
            //is this a fixed or a regular periodical rule?
            SimulationRuleTemplate rule;
            if (valueExpression.startsWith("{")){
                //remove { }
                valueExpression = valueExpression.substring(1, valueExpression.length()-1);
                String[] stringValues = valueExpression.split(",");
                double[] doubleValues = new double[stringValues.length];
                for (int i = 0; i < stringValues.length; i++) {
                    String v = stringValues[i];
                    doubleValues[i] = Double.parseDouble(v);
                }
                
                
                rule = new PeriodicalRuleTemplate(initialDelayExpression, timeExpression, target, doubleValues);    
            } else{
                rule = new PeriodicalFixedRuleTemplate(initialDelayExpression, timeExpression, target, Double.valueOf(valueExpression));
            }
            
            results.add(rule);
        }
        
        return results;
    }
    
    

}
