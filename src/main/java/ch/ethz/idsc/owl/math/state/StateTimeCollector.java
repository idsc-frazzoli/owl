// code by jph
package ch.ethz.idsc.owl.math.state;

import java.io.Serializable;
import java.util.Collection;

public interface StateTimeCollector extends Serializable {
  /** @return collection of accumulated {@link StateTime}s */
  Collection<StateTime> getMembers();
}
