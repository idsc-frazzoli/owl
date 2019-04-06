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
  protected Scalar correctedVar(Tensor waypoints, Scalar index) {
    return Mod.function(waypoints.length()).apply(index);
  }

  @Override // from TrajectoryEntryFinder
  protected Optional<Tensor> protected_apply(Tensor waypoints) {
    return Optional.of(waypoints.get(var.number().intValue()));
  }
}
