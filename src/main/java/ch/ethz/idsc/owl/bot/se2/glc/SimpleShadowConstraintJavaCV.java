// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.indexer.Indexer;

import ch.ethz.idsc.owl.mapping.ShadowMapSpherical;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

class SimpleShadowConstraintJavaCV extends AbstractShadowConstraint {
  private final ShadowMapSpherical shadowMap;
  private final Mat initArea;

  public SimpleShadowConstraintJavaCV(ShadowMapSpherical shadowMapPed, float a, float reactionTime) {
    super(a, reactionTime);
    // ---
    this.shadowMap = shadowMapPed;
    this.initArea = shadowMapPed.getInitMap();
  }

  @Override // from CostIncrementFunction
  boolean isSatisfied(StateTime childStateTime, float tStop, Tensor ray, TensorUnaryOperator forward) {
    Mat simShadowArea = initArea.clone();
    shadowMap.updateMap(simShadowArea, childStateTime, tStop + tReact);
    Indexer indexer = simShadowArea.createIndexer();
    return !ray.stream().parallel() //
        .map(forward) //
        .map(shadowMap::state2pixel) //
        .anyMatch(local -> isMember(indexer, local));
  }

  private boolean isMember(Indexer indexer, Point pixel) {
    return pixel.y() < initArea.rows() //
        && pixel.x() < initArea.cols() //
        && indexer.getDouble(pixel.y(), pixel.x()) == 255.0;
  }
}
