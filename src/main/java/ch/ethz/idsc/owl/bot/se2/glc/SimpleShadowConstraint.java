// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.geom.Area;

import ch.ethz.idsc.owl.mapping.ShadowMapArea;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

// TODO_YN ideally there should be a demo or tests that still uses this implementation
/* package */ class SimpleShadowConstraint extends AbstractShadowConstraint {
  // ---
  private final ShadowMapArea shadowMap;
  private final Area initArea;

  public SimpleShadowConstraint(ShadowMapArea shadowMapPed, float a, float reactionTime) {
    super(a, reactionTime, false);
    // ---
    this.shadowMap = shadowMapPed;
    this.initArea = shadowMapPed.getInitMap();
  }

  @Override // from PlannerConstraint
  boolean isSatisfied(StateTime childStateTime, float tStop, Tensor ray, TensorUnaryOperator forward) {
    Area simShadowArea = (Area) initArea.clone();
    shadowMap.updateMap(simShadowArea, childStateTime, tStop);
    return ray.stream().map(forward) //
        .noneMatch(local -> isMember(simShadowArea, local));
  }

  private static boolean isMember(Area area, Tensor state) {
    return area.contains( //
        state.Get(0).number().doubleValue(), //
        state.Get(1).number().doubleValue());
  }
}
