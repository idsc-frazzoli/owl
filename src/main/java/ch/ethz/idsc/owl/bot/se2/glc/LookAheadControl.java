// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ abstract class LookAheadControl extends Se2TrajectoryControl {
  // ---
  final Scalar lookAhead;
  /** for drawing only */
  Tensor targetLocal = null;

  public LookAheadControl(Scalar lookAhead, Scalar maxTurningRate) {
    super(Clips.absolute(maxTurningRate));
    this.lookAhead = lookAhead;
  }

  @Override // from TrajectoryTargetRender
  public final Optional<Shape> toTarget(GeometricLayer geometricLayer) {
    Tensor _targetLocal = targetLocal; // copy reference
    if (Objects.nonNull(_targetLocal))
      return Optional.of(geometricLayer.toLine2D(_targetLocal));
    return Optional.empty();
  }
}
