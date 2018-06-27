// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.io.Serializable;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.mapping.ShadowMapSpherical;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public final class SimpleShadowConstraintJavaCV implements PlannerConstraint, Serializable {
  private final ShadowMapSpherical shadowMap;
  private final float a;
  private final float reactionTime;
  private final Mat initArea;
  private final Tensor dir = AngleVector.of(RealScalar.ZERO);

  public SimpleShadowConstraintJavaCV(ShadowMapSpherical shadowMapPed, float a, float reactionTime) {
    this.shadowMap = shadowMapPed;
    this.a = a;
    this.reactionTime = reactionTime;
    this.initArea = shadowMapPed.getInitMap();
  }

  @Override // from CostIncrementFunction
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    //
    float vel = flow.getU().Get(0).number().floatValue();
    float tStop = vel / a + reactionTime + reactionTime;
    float dStop = tStop * vel / 2;
    // simulate shadow map during braking
    Mat simShadowArea = initArea.clone();
    StateTime childStateTime = trajectory.get(trajectory.size() - 1);
    shadowMap.updateMap(simShadowArea, childStateTime, tStop);
    //  ---
    Se2Bijection se2Bijection = new Se2Bijection(childStateTime.state());
    TensorUnaryOperator forward = se2Bijection.forward();
    Tensor range = Subdivide.of(0, dStop, 10);
    Tensor ray = TensorProduct.of(range, dir);
    UByteRawIndexer sI = simShadowArea.createIndexer();
    return !ray.stream() //
        .anyMatch(local -> isMember(sI, forward.apply(local)));
  }

  private boolean isMember(Indexer sI, Tensor state) {
    Point pixel = shadowMap.state2pixel(state);
    return sI.getDouble(pixel.y(), pixel.x()) == 255.0;
  }
}
