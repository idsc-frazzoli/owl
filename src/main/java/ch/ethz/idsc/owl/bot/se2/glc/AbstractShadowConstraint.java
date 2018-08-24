// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiFunction;

import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.tse2.Tse2StateSpaceModel;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.subare.util.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

abstract class AbstractShadowConstraint implements PlannerConstraint, Serializable {
  static final Tensor DIR = AngleVector.of(RealScalar.ZERO).unmodifiable();
  private static final int RESOLUTION = 10;
  // ---
  private final int steps;
  private final BiFunction<StateTime, Flow, Scalar> velSupplier;
  // ---
  final float a;
  final float tReact;
  final float timeStep = 0.1f; // TODO YN get from state integrator

  public AbstractShadowConstraint(float a, float tReact) {
    this(a, tReact, false);
  }

  public AbstractShadowConstraint(float a, float tReact, boolean tse2) {
    GlobalAssert.that(tReact <= 0.4f); // TODO YN magic const
    this.a = a;
    this.tReact = tReact;
    this.steps = Math.max((int) Math.ceil(tReact / timeStep), 1);
    velSupplier = tse2 //
        ? (StateTime stateTime, Flow flow) -> stateTime.state().Get(Tse2StateSpaceModel.STATE_INDEX_VEL)
        : (StateTime stateTime, Flow flow) -> flow.getU().Get(Se2StateSpaceModel.CONTROL_INDEX_VEL);
  }

  @Override
  public final boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    // TODO there are few different values for vel => precompute
    StateTime childStateTime = Lists.getLast(trajectory);
    float vel = velSupplier.apply(childStateTime, flow).number().floatValue();
    float tBrake = vel / a;
    float dBrake = tBrake * vel / 2;
    Tensor range = Subdivide.of(0, dBrake, RESOLUTION);
    Tensor ray = TensorProduct.of(range, DIR);
    Se2Bijection se2Bijection = new Se2Bijection(childStateTime.state());
    // -
    StateTime pastStateTime = trajectory.get(trajectory.size() - steps);
    return isSatisfied(pastStateTime, tBrake, ray, se2Bijection.forward());
  }

  abstract boolean isSatisfied(StateTime stateTime, float tBrake, Tensor ray, TensorUnaryOperator forward);
}
