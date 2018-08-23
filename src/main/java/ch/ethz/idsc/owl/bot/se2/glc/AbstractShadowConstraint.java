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
import ch.ethz.idsc.subare.util.GlobalAssert;
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
  final float tReact;
  final float timeStep = 0.1f; // TODO get from state integrator
  final int steps;
  final Tensor dir = AngleVector.of(RealScalar.ZERO);

  public AbstractShadowConstraint(float a, float tReact) {
    this.a = a;
    GlobalAssert.that(tReact <= 0.4f); //
    this.tReact = tReact;
    this.steps = Math.max((int) Math.ceil(tReact / timeStep), 1);
  }

  @Override
  public final boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    // TODO there are few different values for vel => precompute
    float vel = flow.getU().Get(0).number().floatValue();
    float tBrake = vel / a;
    float dBrake = tBrake * vel / 2;
    Tensor range = Subdivide.of(0, dBrake, RESOLUTION);
    Tensor ray = TensorProduct.of(range, dir);
    StateTime childStateTime = Lists.getLast(trajectory); // statetime at child node
    Se2Bijection se2Bijection = new Se2Bijection(childStateTime.state());
    // -
    StateTime pastStateTime = trajectory.get(trajectory.size() - steps);
    return isSatisfied(pastStateTime, tBrake, ray, se2Bijection.forward());
  }

  abstract boolean isSatisfied(StateTime stateTime, float tBrake, Tensor ray, TensorUnaryOperator forward);
}
