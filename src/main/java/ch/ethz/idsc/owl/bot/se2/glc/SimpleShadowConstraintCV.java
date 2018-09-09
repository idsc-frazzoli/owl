// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_imgproc;
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
  private final Mat carRad;
  private Mat negSrc = new Mat();

  public SimpleShadowConstraintCV(ShadowMapCV shadowMap, ImageRegion obsRegion, float rad, float a, float reactionTime, boolean tse2) {
    super(a, reactionTime, tse2);
    this.shadowMap = shadowMap;
    this.initArea = shadowMap.getInitMap();
    this.obsRegion = obsRegion;
    this.carRad = new Mat(Scalar.all(rad));
  }

  @Override // from CostIncrementFunction
  boolean isSatisfied(StateTime childStateTime, float tStop, Tensor ray, TensorUnaryOperator forward) {
    Mat simShadowArea = initArea.clone();
    shadowMap.updateMap(simShadowArea, childStateTime, tStop + tReact);
    //
    opencv_core.bitwise_not(simShadowArea, negSrc);
    opencv_imgproc.distanceTransform(negSrc, simShadowArea, opencv_imgproc.CV_DIST_L2, opencv_imgproc.CV_DIST_MASK_3);
    opencv_core.compare(simShadowArea, carRad, simShadowArea, opencv_core.CMP_LE);
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
