/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.workflow;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VNode;
import java.util.function.Predicate;

/**
 * Workflow utility class.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class WorkflowUtil {
    
    public static final String CONTROL_FLOW = "control";
    public static final String DATA_FLOW = "control";
    public static final String EVENT_FLOW = "event";
    

    private WorkflowUtil() {
        throw new AssertionError();
    }

    /**
     * Returns a predicate that indicates whether a connector is connected with
     * the specified connection type.
     *
     * <b>Note:</b> the predicate can be used to filter streams and collections.
     *
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether a connector is connected with
     * the specified connection type
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> connectorConnected(String connectionType) {
        return (Connector c) -> {
            return c.getType().equals(connectionType)
                    && !c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).isEmpty();
        };
    }

    /**
     * Returns a predicate that indicates whether a connector is not connected
     * with the specified connection type.
     *
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether a connector is not connected
     * with the specified connection type
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> connectorNotConnected(
            String connectionType) {
        return connectorConnected(connectionType).negate();
    }

    /**
     * Returns a predicate that indicates whether a node is connected with the
     * specified connection type.
     *
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether a node is connected with the
     * specified connection type
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<VNode> nodeConnected(String connectionType) {
        return (VNode n) -> {
            return !n.getInputs().filtered(connectorConnected(connectionType)).
                    isEmpty();
        };
    }

    /**
     * Returns a predicate that indicates whether a node is not connected with
     * the specified connection type.
     *
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether a node is not connected with
     * the specified connection type
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<VNode> nodeNotConnected(String connectionType) {
        return nodeConnected(connectionType).negate();
    }

    /**
     * Returns a predicate that indicates whether the number of connections of
     * the specified connector is bigger than the expected number of
     * connections. Only connections of the specifed type are counted.
     *
     * @param expectedNumConn expected number of connections
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether the number of connections of
     * the specified connector is bigger than the expected number of connections
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> moreThanConnections(int expectedNumConn,
            String connectionType) {
        return (Connector c) -> {
            return c.getType().equals(connectionType)
                    && c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).size() > expectedNumConn;
        };
    }

    /**
     * Returns a predicate that indicates whether the number of connections of
     * the specified connector is smaller than the expected number of
     * connections. Only connections of the specifed type are counted.
     *
     * @param expectedNumConn expected number of connections
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether the number of connections of
     * the specified connector is smaller than the expected number of
     * connections
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> lessThanConnections(int expectedNumConn,
            String connectionType) {
        return (Connector c) -> {
            return c.getType().equals(connectionType)
                    && c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).size() < expectedNumConn;
        };
    }

    /**
     * Returns a predicate that indicates whether the number of connections of
     * the specified connector is equal to the expected number of connections.
     * Only connections of the specifed type are counted.
     *
     * @param expectedNumConn expected number of connections
     * @param connectionType connection type (e.g. "data" or "control")
     * @return a predicate that indicates whether the number of connections of
     * the specified connector is equal to the expected number of connections
     *
     * @see java.util.stream.Stream
     */
    public static Predicate<Connector> numberOfConnections(int expectedNumConn,
            String connectionType) {
        return (Connector c) -> {
            return c.getType().equals(connectionType)
                    && c.getNode().getFlow().
                    getConnections(connectionType).
                    getAllWith(c).size() == expectedNumConn;
        };
    }

}
