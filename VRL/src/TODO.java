

/**
 * Add ideas here:
 *
 *  Loading feature:
 * 
 *   TODO when loading session try to do exception catch for each logical block
 *   such as connections and try to finish everything that does not crash
 *
 *   currently this is bad because if something goes wrong it will just stop
 *   loading without resuming the load process
 * 
 *   => done
 *
 *
 *   TODO add methodinfo options just like valueOptions to add stuff like
 *        custom invokeWait behavior or even custom method representation styles
 *
 *   TODO when using methods like getReference() several problems appear:
 *        What do we do if an object is referenced by several
 *        type representations? When deserializing this usually leads to
 *        several new objects instead of references to one object.
 *
 *        Possible solution: create a new model for object persistence, i.e.,
 *                           introduce variables, each connected type
 *                           representation gets the data from the variable
 *
 *
 */
