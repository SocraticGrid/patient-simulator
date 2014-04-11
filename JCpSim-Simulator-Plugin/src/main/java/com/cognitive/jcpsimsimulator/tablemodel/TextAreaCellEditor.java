/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.tablemodel;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellEditor;

public class TextAreaCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JTable table;
    private boolean disableTabKey;
 
    public TextAreaCellEditor() {
        init();
    }
 
    public TextAreaCellEditor(boolean disableTabKey) {
        this.disableTabKey = disableTabKey;
        init();
    }
 
    private void init() {
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(3);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                textArea.requestFocusInWindow();
                textArea.selectAll();
            }
        });
        if (disableTabKey){
            textArea.addKeyListener(this);
        }
    }
 
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textArea.setText(value.toString());
        if (this.table == null){
            this.table = table;
        }
        table.setRowHeight(textArea.getRows() * 20);
        return scrollPane;
    }
 
    @Override
    public Object getCellEditorValue() {
        return textArea.getText(); 
    }
 
    @Override
    public void keyTyped(KeyEvent e) { }
 
    @Override
    public void keyPressed(KeyEvent ke) {
        System.out.println(table.getEditingRow() +","+table.getEditingColumn());
        // tab key will transfer focus to next cell
        if (disableTabKey && ke.getKeyCode() == KeyEvent.VK_TAB && !ke.isShiftDown()) {
            ke.consume();
 
            int column = table.getEditingColumn();
            int row = table.getEditingRow();
            if ((column + 1)  >= table.getColumnCount()) {
                // if column is last column, check if there is next row
                if ((row + 1) >= table.getRowCount()){
                    row = -1;
                }
                else {
                    row++;
                    column = 0;
                }
 
            } else{
                column++;
            }
 
            if (row > -1 && column > -1){
                table.changeSelection(row, column, false, false);
            }
 
        }
    }
 
    @Override
    public void keyReleased(KeyEvent e) { }
}
