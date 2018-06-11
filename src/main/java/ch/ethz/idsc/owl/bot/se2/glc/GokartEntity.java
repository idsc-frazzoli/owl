// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Optional;
import java.util.Random;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.state.EntityControl;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;

/** test if api is sufficient to model gokart */
public class GokartEntity extends CarEntity {
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  static final Scalar SPEED = RealScalar.of(2.5);
  static final Scalar LOOKAHEAD = RealScalar.of(3.0);
  static final Scalar MAX_TURNING_PLAN = Degree.of(15);
  static final Scalar MAX_TURNING_RATE = Degree.of(23);
  static final FlowsInterface CARFLOWS = CarFlows.forward(SPEED, MAX_TURNING_PLAN);
  public static final Tensor SHAPE = ResourceData.of("/gokart/footprint/20171201.csv");
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
    super(stateTime, //
        new PurePursuitControl(LOOKAHEAD, MAX_TURNING_RATE), //
        PARTITIONSCALE, CARFLOWS, SHAPE);
    // ---
    add(localizationFeedback);
  }
}
