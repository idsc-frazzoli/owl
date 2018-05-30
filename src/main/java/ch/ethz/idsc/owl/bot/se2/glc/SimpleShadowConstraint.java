// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.mapping.ShadowMap;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;

public final class SimpleShadowConstraint implements PlannerConstraint, Serializable {
  private final ShadowMap shadowMap;
  private final float mu;
  private final float reactionTime;
  private final Area initArea;

  public SimpleShadowConstraint(ShadowMap shadowMap, float mu, float reactionTime) {
    this.shadowMap = shadowMap;
    this.mu = mu;
    this.reactionTime = reactionTime;
    this.initArea = new Area(shadowMap.getInitMap());
  }

  @Override // from CostIncrementFunction
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    //
    StateTime childStateTime = trajectory.get(trajectory.size() - 1);
    double posX = childStateTime.state().Get(0).number().doubleValue();
    double posY = childStateTime.state().Get(1).number().doubleValue();
    float vel = flow.getU().Get(0).number().floatValue();
    float dStop = vel * vel / (2 * 9.81f * mu);
    float tStop = 2 * dStop / vel + reactionTime;
    dStop += vel * reactionTime;
    // simulate shadow map during braking
    Area simShadowArea = new Area(initArea);
    shadowMap.updateMap(simShadowArea, childStateTime, tStop);
    // calculate braking path
    float angle = childStateTime.state().Get(2).number().floatValue();
    double deltaX = Math.cos(angle) * dStop;
    double deltaY = Math.sin(angle) * dStop;
    Line2D brakingLine = new Line2D.Double(posX, posY, posX + deltaX, posY + deltaY);
    Stroke stroke = new BasicStroke(0.001f, BasicStroke.CAP_ROUND, BasicStroke.CAP_BUTT);
    Area brakingLineArea = new Area(stroke.createStrokedShape(brakingLine));
    simShadowArea.intersect(brakingLineArea);
    boolean value = simShadowArea.isEmpty();
    return value;
  }
}
