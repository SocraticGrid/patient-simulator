/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.umd.cs.piccolox.pswing;

import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * The <b>PSwingCanvas</b> is a PCanvas that can display Swing components with the PSwing adapter.
 *
 * @author Benjamin B. Bederson
 * @author Sam R. Reid
 * @author Lance E. Good
 */

public class PSwingCanvas extends PCanvas {
    public static final String SWING_WRAPPER_KEY = "Swing Wrapper";
    private static PSwingRepaintManager pSwingRepaintManager = new PSwingRepaintManager();

    private SwingWrapper swingWrapper;
    private PSwingEventHandler swingEventHandler;

    /**
     * Construct a new PSwingCanvas.
     */
    public PSwingCanvas() {
        swingWrapper = new SwingWrapper( this );
        add( swingWrapper );
        RepaintManager.setCurrentManager( pSwingRepaintManager );
        pSwingRepaintManager.addPSwingCanvas( this );

        swingEventHandler = new PSwingEventHandler( this, getCamera() );//todo or maybe getCameraLayer() or getRoot()?
        swingEventHandler.setActive( true );
    }

    JComponent getSwingWrapper() {
        return swingWrapper;
    }

    public void addPSwing( PSwing pSwing ) {
        swingWrapper.add( pSwing.getComponent() );
    }

    public void removePSwing( PSwing pSwing ) {
        swingWrapper.remove( pSwing.getComponent() );
    }

    private static class SwingWrapper extends JComponent {
        private PSwingCanvas pSwingCanvas;

        public SwingWrapper( PSwingCanvas pSwingCanvas ) {
            this.pSwingCanvas = pSwingCanvas;
            setSize( new Dimension( 0, 0 ) );
            setPreferredSize( new Dimension( 0, 0 ) );
            putClientProperty( SWING_WRAPPER_KEY, SWING_WRAPPER_KEY );
        }

        public PSwingCanvas getpSwingCanvas() {
            return pSwingCanvas;
        }
    }

}