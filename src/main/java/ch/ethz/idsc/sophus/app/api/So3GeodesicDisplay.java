// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.rn.RnTransport;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.lie.so3.So3BiinvariantMean;
import ch.ethz.idsc.sophus.lie.so3.So3Exponential;
import ch.ethz.idsc.sophus.lie.so3.So3Geodesic;
import ch.ethz.idsc.sophus.lie.so3.So3Group;
import ch.ethz.idsc.sophus.lie.so3.So3Manifold;
import ch.ethz.idsc.sophus.lie.so3.So3Metric;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** symmetric positive definite 2 x 2 matrices */
public class So3GeodesicDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.4)).unmodifiable();
  private static final Scalar RADIUS = RealScalar.of(7);
  // ---
  public static final GeodesicDisplay INSTANCE = new So3GeodesicDisplay(RADIUS);
  /***************************************************/
  private final Scalar radius;

  public So3GeodesicDisplay(Scalar radius) {
    this.radius = radius;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return So3Geodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return TRIANGLE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor axis = xya.divide(radius);
    Scalar norm = Norm._2.ofVector(axis);
    if (Scalars.lessThan(RealScalar.ONE, norm))
      axis = axis.divide(norm);
    return So3Exponential.INSTANCE.exp(axis);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor xyz) {
    return So3Exponential.INSTANCE.log(xyz).extract(0, 2).multiply(radius);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor xyz) {
    return Se2Matrix.translation(toPoint(xyz));
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return So3Group.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsExponential hsExponential() {
    return LieExponential.of(lieGroup(), So3Exponential.INSTANCE);
  }

  @Override // from GeodesicDisplay
  public HsTransport hsTransport() {
    return RnTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return So3Metric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return So3BiinvariantMean.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public FlattenLogManifold flattenLogManifold() {
    return So3Manifold.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "SO3";
  }
}
