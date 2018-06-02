// code by jph
package ch.ethz.idsc.owl.data.tree;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.GlcNode;

/** abstract base class for reference implementation of {@link GlcNode}
 * 
 * parent is member here, therefore the class implements {@link Serializable} */
public abstract class AbstractNode<T extends Node> implements Node, Serializable {
  /** parent is null for root node */
  private T parent = null;

  /** function has to be provided by deriving class that
   * holds the data structure to store the child nodes.
   * 
   * function adds/inserts given child to collection children()
   * 
   * @param child
   * @return true if child was added to children() as a result of calling the function,
   * false if child was already present, or could not be added to children() */
  protected abstract boolean protected_insertChild(T child);

  /** function has to be provided by deriving class that
   * holds the data structure to store the child nodes.
   * 
   * function removes given child from collection children()
   * 
   * @param child
   * @return true if child was removed from collection of children */
  protected abstract boolean protected_removeChild(T child);

  @Override // from Node
  public final T parent() {
    return parent;
  }

  @SuppressWarnings("unchecked")
  @Override // from Node
  public final void removeEdgeTo(Node child) {
    boolean removed = protected_removeChild((T) child);
    if (!removed)
      throw new RuntimeException();
    if (child.isRoot())
      throw new RuntimeException();
    ((AbstractNode<T>) child).parent = null;
  }

  @SuppressWarnings("unchecked")
  @Override // from Node
  public final void insertEdgeTo(Node child) {
    boolean inserted = protected_insertChild((T) child);
    if (!inserted) // for this flow there already was a child
      throw new RuntimeException();
    if (!child.isRoot()) // child already has parent
      throw new RuntimeException();
    ((AbstractNode<T>) child).parent = (T) this;
  }

  @Override // from Node
  public final boolean isRoot() {
    return Objects.isNull(parent);
  }

  @Override // from Node
  public final boolean isLeaf() {
    return children().isEmpty();
  }
}
