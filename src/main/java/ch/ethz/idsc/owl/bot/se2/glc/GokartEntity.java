// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

/** test if api is sufficient to model gokart */
class GokartEntity extends CarEntity {
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  static final Scalar SPEED = RealScalar.of(2.5);
  static final CarFlows CARFLOWS = new CarForwardFlows(SPEED, Degree.of(15));
  static final Tensor SHAPE = ResourceData.of("/demo/gokart/footprint.csv");

  public GokartEntity(StateTime stateTime) {
    super(stateTime, new CarTrajectoryControl(), PARTITIONSCALE, CARFLOWS, SHAPE);
  }
}
