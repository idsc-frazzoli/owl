// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Optional;
import java.util.Random;

import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.state.EntityControl;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;

/** test if api is sufficient to model gokart */
/* package */ class GokartEntity extends CarEntity {
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  static final Scalar SPEED = RealScalar.of(2.5);
  static final CarFlows CARFLOWS = new CarForwardFlows(SPEED, Degree.of(15));
  static final Tensor SHAPE = ResourceData.of("/demo/gokart/footprint.csv");
  // ---
  /** simulation of occasional feedback from localization algorithm */
  private final EntityControl localizationFeedback = new EntityControl() {
    private final Random random = new Random();
    private boolean trigger = false;

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.GODMODE;
    }

    @Override
    public Optional<Tensor> control(StateTime tail, Scalar now) {
      Optional<Tensor> optional = Optional.empty();
      if (trigger) {
        optional = Optional.of(Tensors.vector( //
            random.nextGaussian(), // shift x
            random.nextGaussian(), // shift y
            random.nextGaussian())); // shift angle
      }
      trigger = 0 == random.nextInt(20); // TODO use now to alter position every 1[s] for instance
      return optional;
    }
  };

  public GokartEntity(StateTime stateTime) {
    super(stateTime, new CarTrajectoryControl(), PARTITIONSCALE, CARFLOWS, SHAPE);
    // ---
    add(localizationFeedback);
  }

  public Tensor coords_X() {
    ScalarSummaryStatistics scalarSummaryStatistics = //
        SHAPE.stream().map(tensor -> tensor.Get(0)).collect(ScalarSummaryStatistics.collector());
    return Subdivide.of(scalarSummaryStatistics.getMin(), scalarSummaryStatistics.getMax(), 3);
  }
}
