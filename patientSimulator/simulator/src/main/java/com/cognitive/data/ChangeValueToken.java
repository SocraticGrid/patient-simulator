/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.data;

import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ChangeValueToken  implements InternalToken{
    private final JCpSimParameter target;
    private final String change;
    private final boolean notifyChange;
    private final String source;
    
    //bigger salience -> change applied first
    private final int salience;

    public ChangeValueToken(JCpSimParameter target, String change, String source, boolean notifyChange) {
        this(target, change, source, 0, notifyChange);
    }
    
    public ChangeValueToken(JCpSimParameter target, String change, String source, int salience, boolean notifyChange) {
        this.target = target;
        this.change = change;
        this.notifyChange = notifyChange;
        this.source = source;
        this.salience = salience;
    }

    public ChangeValueToken(JCpSimParameter target, String change, String source) {
        this(target, change, source, true);
    }
    
    public ChangeValueToken(JCpSimParameter target, String change, String source, int salience) {
        this(target, change, source, salience, true);
    }

    public void applyChange(JCpSimData originalData){
        
        if (this.change.startsWith("f")){
            originalData.set(target, originalData.get(target) + Double.parseDouble(this.change.substring(1)));
        }else{
            originalData.set(target, originalData.get(target) + Double.parseDouble(this.change));
        }
        
    }
    
    public JCpSimParameter getTarget() {
        return target;
    }

    public String getChange() {
        return change;
    }

    public boolean isNotifyChange() {
        return notifyChange;
    }

    public String getSource() {
        return source;
    }

    public boolean isAutoRetractable() {
        return true;
    }

    public int getSalience() {
        return salience;
    }
    
}
