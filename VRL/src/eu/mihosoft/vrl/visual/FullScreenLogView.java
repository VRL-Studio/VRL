/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.visual;

import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.JTextArea;

/**
 * Fullscreen logview used in canvas as background log, layout is exactly same size as parent.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FullScreenLogView extends JTextArea implements VLayoutControllerProvider{
    
    private VLayoutController layoutController = new LayoutControllerImpl();

    @Override
    public VLayoutController getLayoutController() {
        return layoutController;
    }

    @Override
    public void setBoundsLocked(boolean value) {
        // ignored
    }

    @Override
    public boolean isIngoredByLayout() {
        return false;
    }
    
    static class LayoutControllerImpl implements VLayoutController {

        @Override
        public void layoutComponent(JComponent c) {
            Container parent = c.getParent();
            c.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        }
        
    }
    
}
