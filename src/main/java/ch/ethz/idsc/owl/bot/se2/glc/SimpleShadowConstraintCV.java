// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.indexer.Indexer;

import ch.ethz.idsc.owl.mapping.ShadowMapCV;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class SimpleShadowConstraintCV extends AbstractShadowConstraint {
  private final ShadowMapCV shadowMap;
  private final ImageRegion obsRegion;
  private final Mat initArea;
  private final float carRad;

  public SimpleShadowConstraintCV(ShadowMapCV shadowMap, ImageRegion obsRegion, float rad, float a, float tReact, boolean tse2) {
    super(a, tReact, tse2);
    this.shadowMap = shadowMap;
    this.initArea = shadowMap.getInitMap();
    this.obsRegion = obsRegion;
    this.carRad = rad;
  }

  @Override // from CostIncrementFunction
  boolean isSatisfied(StateTime childStateTime, float tBrake, Tensor ray, TensorUnaryOperator forward) {
    Mat simShadowArea = initArea.clone();
    shadowMap.updateMap(simShadowArea, childStateTime, tBrake + tReact);
    simShadowArea = shadowMap.getShape(simShadowArea, carRad);
    //
    Indexer indexer = simShadowArea.createIndexer();
    return !ray.stream().parallel() //
        .map(forward) //
        .anyMatch(local -> isMember(indexer, shadowMap.state2pixel(local))); // || obsRegion.isMember(local));
  }

  private boolean isMember(Indexer indexer, Point pixel) {
    return pixel.y() < initArea.rows() //
        && pixel.x() < initArea.cols() //
        && pixel.y() >= 0 //
        && pixel.x() >= 0 //
        && indexer.getDouble(pixel.y(), pixel.x()) == 255.0;
  }
}
