// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;

import java.util.Optional;

public final class NaiveEntryFinder extends TrajectoryEntryFinder {
  public NaiveEntryFinder(int initialIndex) {
    super(RealScalar.of(initialIndex));
  }

  @Override // from TrajectoryEntryFinder
  protected Scalar correctedVar(Tensor waypoints, Scalar index) {
    return Mod.function(waypoints.get().length()).apply(index);
  }

  @Override // from TrajectoryEntryFinder
  protected Optional<Tensor> protected_apply(Tensor waypoints) {
    return Optional.of(waypoints.get().get(var.number().intValue()));
  }
}
