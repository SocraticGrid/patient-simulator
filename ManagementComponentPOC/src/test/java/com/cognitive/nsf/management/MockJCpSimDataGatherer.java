/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.jcpsim.JCpSimDataGatherer;
import com.cognitive.nsf.management.jcpsim.JCpSimDataReceivedEventListener;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataProvider;

/**
 *
 * @author esteban
 */
public class MockJCpSimDataGatherer extends JCpSimDataGatherer {

    private JCpSimDataProvider dataProvider;
    
    public MockJCpSimDataGatherer(JCpSimDataProvider dataProvider) {
        super(dataProvider);
        this.dataProvider = dataProvider;
    }

    @Override
    public void start() {
    }
    
    public void pushData(){
        
        JCpSimData data = dataProvider.getData();
        
        for (JCpSimDataReceivedEventListener eventListener : eventListeners) {
            eventListener.onDataReceived(data);
        }
    }
}
