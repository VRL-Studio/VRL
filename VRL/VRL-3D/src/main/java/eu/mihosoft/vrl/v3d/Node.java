package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.List;

// # class Node
// Holds a node in a BSP tree. A BSP tree is built from a collection of polygons
// by picking a polygon to split along. That polygon (and all other coplanar
// polygons) are added directly to that node and the other polygons are added to
// the front and/or back subtrees. This is not a leafy BSP tree since there is
// no distinction between internal and leaf nodes.
public class Node {

    private List<Polygon> polygons;
    private Plane plane;
    private Node front;
    private Node back;

    public Node(List<Polygon> polygons) {
        this.polygons = new ArrayList<>();
        if (polygons != null) {
            this.build(polygons);
        }
    }

    public Node() {
        this(null);
    }

    @Override
    public Node clone() {
        Node node = new Node();
        node.plane = this.plane == null ? null : this.plane.clone();
        node.front = this.front == null ? null : this.front.clone();
        node.back = this.back == null ? null : this.back.clone();
        node.polygons = new ArrayList<>();
        polygons.forEach((polygon) -> {
            node.polygons.add(polygon.clone());
        });
        return node;
    }

    // Convert solid space to empty space and empty space to solid space.
    public void invert() {
        for (Polygon polygon : this.polygons) {
            polygon.flip();
        }
        this.plane.flip();
        if (this.front != null) {
            this.front.invert();
        }
        if (this.back != null) {
            this.back.invert();
        }
        Node temp = this.front;
        this.front = this.back;
        this.back = temp;
    }

    // Recursively remove all polygons in `polygons` that are inside this BSP
    // tree.
    public List<Polygon> clipPolygons(List<Polygon> polygons) {
        if (this.plane == null) {
            return new ArrayList<>(polygons);
        }
        List<Polygon> front = new ArrayList<>();
        List<Polygon> back = new ArrayList<>();
        for (Polygon polygon : polygons) {
            this.plane.splitPolygon(polygon, front, back, front, back);
        }
        if (this.front != null) {
            front = this.front.clipPolygons(front);
        }
        if (this.back != null) {
            back = this.back.clipPolygons(back);
        } else {
            back = new ArrayList<>(0);
        }
        return Utils.concat(front, back);
    }

    // Remove all polygons in this BSP tree that are inside the other BSP tree
    // `bsp`.
    public void clipTo(Node bsp) {
        this.polygons = bsp.clipPolygons(this.polygons);
        if (this.front != null) {
            this.front.clipTo(bsp);
        }
        if (this.back != null) {
            this.back.clipTo(bsp);
        }
    }

    // Return a list of all polygons in this BSP tree.
    public List<Polygon> allPolygons() {
        List<Polygon> polygons = new ArrayList<>(this.polygons);
        if (this.front != null) {
            polygons = Utils.concat(polygons, this.front.allPolygons());
        }
        if (this.back != null) {
            polygons = Utils.concat(polygons, this.back.allPolygons());
        }
        return polygons;
    }

    // Build a BSP tree out of `polygons`. When called on an existing tree, the
    // new polygons are filtered down to the bottom of the tree and become new
    // nodes there. Each set of polygons is partitioned using the first polygon
    // (no heuristic is used to pick a good split).
    public void build(List<Polygon> polygons) {

        if (polygons.isEmpty()) {
            return;
        }

        if (this.plane == null) {
            this.plane = polygons.get(0).plane.clone();
        }
        List<Polygon> front = new ArrayList<>();
        List<Polygon> back = new ArrayList<>();
        polygons.stream().forEach((polygon) -> {
            this.plane.splitPolygon(polygon, this.polygons, this.polygons, front, back);
        });
        if (front.size() > 0) {
            if (this.front == null) {
                this.front = new Node();
            }
            this.front.build(front);
        }
        if (back.size() > 0) {
            if (this.back == null) {
                this.back = new Node();
            }
            this.back.build(back);
        }
    }
}
