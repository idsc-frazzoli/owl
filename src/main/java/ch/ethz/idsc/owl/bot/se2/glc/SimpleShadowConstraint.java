// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.geom.Area;
import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.mapping.ShadowMap;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public final class SimpleShadowConstraint implements PlannerConstraint, Serializable {
  private final ShadowMap shadowMap;
  private final float a;
  private final float reactionTime;
  private final Area initArea;
  private final Tensor dir = AngleVector.of(RealScalar.ZERO);

  public SimpleShadowConstraint(ShadowMap shadowMap, float a, float reactionTime) {
    this.shadowMap = shadowMap;
    this.a = a;
    this.reactionTime = reactionTime;
    this.initArea = new Area(shadowMap.getInitMap());
  }

  @Override // from CostIncrementFunction
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    //
    float vel = flow.getU().Get(0).number().floatValue();
    float tStop = vel / a + reactionTime + reactionTime;
    float dStop = tStop * vel / 2;
    // simulate shadow map during braking
    Area simShadowArea = new Area(initArea);
    StateTime childStateTime = trajectory.get(trajectory.size() - 1);
    shadowMap.updateMap(simShadowArea, childStateTime, tStop);
    //  ---
    Se2Bijection se2Bijection = new Se2Bijection(childStateTime.state());
    TensorUnaryOperator forward = se2Bijection.forward();
    Tensor range = Subdivide.of(0, dStop, 10);
    Tensor ray = TensorProduct.of(range, dir);
    return !ray.stream() //
        .anyMatch(local -> isMember(simShadowArea, forward.apply(local)));
  }

  private static boolean isMember(Area area, Tensor state) {
    return area.contains(state.Get(0).number().doubleValue(), state.Get(1).number().doubleValue());
  }
}
