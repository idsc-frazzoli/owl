// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Collection;

@FunctionalInterface
public interface StateTimeCollector {
  /** @return collection of accumulated {@link StateTime}s */
  Collection<StateTime> getMembers();
}
