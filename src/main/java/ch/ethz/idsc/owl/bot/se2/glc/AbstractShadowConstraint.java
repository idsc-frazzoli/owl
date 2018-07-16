// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

abstract class AbstractShadowConstraint implements PlannerConstraint, Serializable {
  private static final int RESOLUTION = 10;
  // ---
  final float a;
  final float reactionTime;
  final Tensor dir = AngleVector.of(RealScalar.ZERO);

  public AbstractShadowConstraint(float a, float reactionTime) {
    this.a = a;
    this.reactionTime = reactionTime;
  }

  @Override
  public final boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    // TODO there are few different values for vel => precompute
    // simulate shadow map during braking
    float vel = flow.getU().Get(0).number().floatValue();
    float tStop = vel / a + reactionTime + reactionTime;
    float dStop = tStop * vel / 2;
    Tensor range = Subdivide.of(0, dStop, RESOLUTION);
    Tensor ray = TensorProduct.of(range, dir);
    StateTime childStateTime = Lists.getLast(trajectory);
    Se2Bijection se2Bijection = new Se2Bijection(childStateTime.state());
    return isSatisfied(childStateTime, tStop, ray, se2Bijection.forward());
  }

  abstract boolean isSatisfied(StateTime childStateTime, float tStop, Tensor ray, TensorUnaryOperator forward);
}
