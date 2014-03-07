package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a node in a BSP tree. A BSP tree is built from a collection of polygons
 * by picking a polygon to split along. That polygon (and all other coplanar
 * polygons) are added directly to that node and the other polygons are added to
 * the front and/or back subtrees. This is not a leafy BSP tree since there is
 * no distinction between internal and leaf nodes.
 */
final class Node {

    private List<Polygon> polygons;
    private Plane plane;
    private Node front;
    private Node back;

    /**
     *
     * @param polygons
     */
    public Node(List<Polygon> polygons) {
        this.polygons = new ArrayList<>();
        if (polygons != null) {
            this.build(polygons);
        }
    }

    /**
     *
     */
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
        polygons.forEach((Polygon p) -> {
            node.polygons.add(p.clone());
        });
        return node;
    }

    // Convert solid space to empty space and empty space to solid space.
    public void invert() {

        if (this.polygons.isEmpty()) {
            return;
        }

        for (Polygon polygon : this.polygons) {
            polygon.flip();
        }

        if (this.plane == null) {
            this.plane = polygons.get(0).plane.clone();
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

        List<Polygon> frontP = new ArrayList<>();
        List<Polygon> backP = new ArrayList<>();

        for (Polygon polygon : polygons) {
            this.plane.splitPolygon(polygon, frontP, backP, frontP, backP);
        }
        if (this.front != null) {
            frontP = this.front.clipPolygons(frontP);
        }
        if (this.back != null) {
            backP = this.back.clipPolygons(backP);
        } else {
            backP = new ArrayList<>(0);
        }

//        return Utils.concat(front, back);
        frontP.addAll(backP);
        return frontP;
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
        List<Polygon> localPolygons = new ArrayList<>(this.polygons);
        if (this.front != null) {
            localPolygons.addAll(this.front.allPolygons());
//            polygons = Utils.concat(polygons, this.front.allPolygons());
        }
        if (this.back != null) {
//            polygons = Utils.concat(polygons, this.back.allPolygons());
            localPolygons.addAll(this.back.allPolygons());
        }

        return localPolygons;
    }

    // Build a BSP tree out of `polygons`. When called on an existing tree, the
    // new polygons are filtered down to the bottom of the tree and become new
    // nodes there. Each set of polygons is partitioned using the first polygon
    // (no heuristic is used to pick a good split).
    public final void build(List<Polygon> polygons) {

        if (polygons.isEmpty()) {
            return;
        }

        if (this.plane == null) {
            this.plane = polygons.get(0).plane.clone();
        }

        List<Polygon> frontP = new ArrayList<>();
        List<Polygon> backP = new ArrayList<>();

        polygons.forEach((polygon) -> {
            this.plane.splitPolygon(polygon, this.polygons, this.polygons, frontP, backP);
        });
        if (frontP.size() > 0) {
            if (this.front == null) {
                this.front = new Node();
            }
            this.front.build(frontP);
        }
        if (backP.size() > 0) {
            if (this.back == null) {
                this.back = new Node();
            }
            this.back.build(backP);
        }
    }
}
