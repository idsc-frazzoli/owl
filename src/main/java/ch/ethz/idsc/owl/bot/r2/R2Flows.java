// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.Sign;

public class R2Flows implements FlowsInterface, Serializable {
  private final Scalar speed;

  public R2Flows(Scalar speed) {
    if (Sign.isNegativeOrZero(speed))
      throw TensorRuntimeException.of(speed);
    this.speed = speed;
  }

  @Override
  public Collection<Flow> getFlows(int resolution) {
    GlobalAssert.that(2 < resolution); // otherwise does not cover plane
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    List<Flow> list = new ArrayList<>();
    for (Tensor u : CirclePoints.of(resolution))
      list.add(StateSpaceModels.createFlow(stateSpaceModel, u.multiply(speed)));
    return list;
  }

  public Flow stayPut() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    return StateSpaceModels.createFlow(stateSpaceModel, Array.zeros(2));
  }
}
