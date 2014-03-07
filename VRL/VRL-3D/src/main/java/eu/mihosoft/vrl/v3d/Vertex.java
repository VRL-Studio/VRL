package eu.mihosoft.vrl.v3d;

/**
 * Represents a vertex of a polygon. Use your own vertex class instead of this
 * one to provide additional features like texture coordinates and vertex
 * colors. Custom vertex classes need to provide a `pos` property and `clone()`,
 * `flip()`, and `interpolate()` methods that behave analogous to the ones
 * defined by `Vertex`. This class provides `normal` so convenience functions
 * like `CSG.sphere()` can return a smooth vertex normal, but `normal` is not
 * used anywhere else.
 */
public class Vertex {

    public Vector pos;
    public Vector normal;

    /**
     * Constructor. Creates a vertex.
     *
     * @param pos position
     * @param normal normal
     */
    public Vertex(Vector pos, Vector normal) {
        this.pos = pos;
        this.normal = normal;
    }

    @Override
    public Vertex clone() {
        return new Vertex(pos.clone(), normal.clone());
    }

    /**
     * Invert all orientation-specific data. (e.g. vertex normal). Called when
     * the orientation of a polygon is flipped.
     */
    public void flip() {
        normal = normal.negated();
    }

    /**
     * Create a new vertex between this vertex and `other` by linearly
     * interpolating all properties using a parameter of `t`. Subclasses should
     * override this to interpolate additional properties.
     */
    public Vertex interpolate(Vertex other, double t) {
        return new Vertex(pos.lerp(other.pos, t),
                normal.lerp(other.normal, t));
    }

    /**
     * Returns this vertex in STL string format.
     * 
     * @return this vertex in STL string format
     */
    public String toStlString() {
        return "vertex " + this.pos.toStlString();
    }

    /**
     * Applies the specified transform to this vertex.
     * @param transform the transform to apply
     * @return this vertex
     */
    public Vertex transform(Transform transform) {
        pos = pos.transform(transform);
        return this;
    }

    /**
     * Applies the specified transform to a copy of this vertex.
     * @param transform the transform to apply
     * @return a copy of this transform
     */
    public Vertex transformed(Transform transform) {
        return clone().transform(transform);
    }

}
