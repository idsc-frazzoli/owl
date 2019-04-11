// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;

public final class NaiveEntryFinder extends TrajectoryEntryFinder {
  public NaiveEntryFinder(int initialIndex) {
    super(RealScalar.of(initialIndex));
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar var) {
    Scalar index = Mod.function(waypoints.length()).apply(var);
    return new TrajectoryEntry(Optional.of(waypoints.get(index.number().intValue())), index);
  }
}
