// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Collection;
import java.util.Collections;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTimeRegionCallback;

// TODO functionality is not used
public enum VoidStateTimeRegionMembers implements StateTimeRegionCallback {
  INSTANCE;
  // ---
  @Override
  public void notify_isMember(StateTime stateTime) {
    // ---
  }

  @Override
  public Collection<StateTime> getMembers() {
    return Collections.emptyList();
  }
}
