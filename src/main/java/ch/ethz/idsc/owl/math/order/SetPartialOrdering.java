package ch.ethz.idsc.owl.math.order;

import java.util.Optional;
import java.util.Set;

/** Creates partial ordering of sets.
 * For two sets A & B, A < B is satisfied if A is a subset of B.
 * 
 * @author astoll */
public enum SetPartialOrdering implements PartialOrdering<Set<?>> {
  INSTANCE;
  @Override
  public Optional<Integer> compare(Set<?> a, Set<?> b) {
    boolean aInB = b.containsAll(a);
    boolean bInA = a.containsAll(b);
    boolean aEqualsB = aInB && bInA;
    if (aEqualsB) {
      return Optional.of(0);
    }
    if (aInB) {
      return Optional.of(-1);
    }
    if (bInA) {
      return Optional.of(1);
    }
    return Optional.empty();
  }
}
