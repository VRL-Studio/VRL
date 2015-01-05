/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.workflow;

import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class WorkflowUtilTest {

    @Test
    public void getAncestorsTest() {
        createAncestorsTestCase(1, 1, 1, 0);
        createAncestorsTestCase(1, 3, 1, 2);
        createAncestorsTestCase(10, 10, 8, 3);
        createAncestorsTestCase(10, 10, 7, 4);
        createAncestorsTestCase(10, 10, 10, 9);
        createAncestorsTestCase(100, 50, 100, 27);
    }

    public void createAncestorsTestCase(int depth, int width, int destDepth, int destWidth) {

        if (destDepth < 0 || destDepth > depth) {
            throw new IllegalArgumentException("Illegal destDepth specified: "
                    + destDepth + ". Must be between 1 and " + depth + ".");
        }

        if (destWidth < 0 || destWidth >= width) {
            throw new IllegalArgumentException("Illegal destDepth specified: "
                    + destDepth + ". Must be between 0 and " + (width - 1) + ".");
        }

        VFlow flow = FlowFactory.newFlow();
        VNode n = null;
        for (int d = 0; d < depth; d++) {
            for (int w = 0; w < width; w++) {
                if (d == destDepth - 1 && w == destWidth) {
                    n = flow.newNode();
                } else {
                    flow.newNode();
                }
            }

            flow = flow.newSubFlow();
        }

        int numAncestors = WorkflowUtil.getAncestors(n).size();

        Assert.assertTrue("Number of Ancestors must be equal to dest depth. "
                + "Expected " + destDepth + ", got " + numAncestors,
                numAncestors == destDepth);

    }

    @Test
    public void getCommonAncestorsTest() {
        createCommonAncestorTestCase(
                10, 10,
                2, 4,
                7, 6
        );
    }

    public void createCommonAncestorTestCase(int depth, int width, int n1Depth, int n1Width, int n2Depth, int n2Width, String commonAncestorId) {

        if (n1Depth < 0 || n1Depth > depth) {
            throw new IllegalArgumentException("Illegal destDepth specified: "
                    + n1Depth + ". Must be between 1 and " + depth + ".");
        }

        if (n1Width < 0 || n1Width >= width) {
            throw new IllegalArgumentException("Illegal destDepth specified: "
                    + n1Width + ". Must be between 0 and " + (width - 1) + ".");
        }

        VFlow flow = FlowFactory.newFlow();
        VNode n1 = null;
        VNode n2 = null;
        for (int d = 0; d < depth; d++) {
            for (int w = 0; w < width; w++) {

                VNode n = flow.newNode();

                if (d == n1Depth - 1 && w == n1Width) {
                    n1 = n;
                } else if (d == n2Depth - 1 && w == n2Width) {
                    n2 = n;
                }
            }

            flow = flow.newSubFlow();
        }

        Optional<VFlowModel> ancestorResult = WorkflowUtil.getCommonAncestor(n1, n2);

        if (commonAncestorId != null) {
            Assert.assertTrue("Ancestor must not exist.", !ancestorResult.isPresent());
        } else {
            Assert.assertTrue("Ancestor must exist.", ancestorResult.isPresent());
        }

        VFlowModel ancestor = WorkflowUtil.getCommonAncestor(n1, n2).get();

        if (commonAncestorId != null) {
            Assert.assertTrue("Wrong common ancestor. "
                    + "Expected " + commonAncestorId + ", got " + ancestor.getId(),
                    commonAncestorId.equals(ancestor.getId())
            );
        }

    }
}
