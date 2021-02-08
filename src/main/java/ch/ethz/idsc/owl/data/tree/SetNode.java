// code by jph
package ch.ethz.idsc.owl.data.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class SetNode<T extends Node> extends AbstractNode<T> {
  private final Set<T> next = new HashSet<>();

  @Override // from Node
  public final Collection<T> children() {
    return Collections.unmodifiableCollection(next);
  }

  @Override // from AbstractNode
  protected final boolean protected_insertChild(T child) {
    return next.add(child);
  }

  @Override
  protected final boolean protected_removeChild(T child) {
    return next.remove(child);
  }
}
