/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.ui.codevisualization;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VNode;
import java.util.function.Predicate;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class WorkflowUtil {

    private WorkflowUtil() {
        throw new AssertionError();
    }

    private boolean isRoot(VNode node, String connectionType) {

        Predicate<Connector> notConnected = (Connector c) -> {
            return c.getType().equals(connectionType)
                    && !c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).isEmpty();
        };

        Predicate<VNode> rootNode = (VNode n) -> {
            return n.getInputs().filtered(notConnected).isEmpty();
        };

        return rootNode.test(node);
    }
    
//    public static Predicate<Connector>

}
