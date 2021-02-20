// code by jph
package ch.ethz.idsc.sophus.gds;

import java.io.Serializable;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.MetricBiinvariant;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.so.SoGroup;
import ch.ethz.idsc.sophus.lie.so.SoTransport;
import ch.ethz.idsc.sophus.lie.so3.Rodrigues;
import ch.ethz.idsc.sophus.lie.so3.So3BiinvariantMean;
import ch.ethz.idsc.sophus.lie.so3.So3Geodesic;
import ch.ethz.idsc.sophus.lie.so3.So3Manifold;
import ch.ethz.idsc.sophus.lie.so3.So3Metric;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;

/** symmetric positive definite 2 x 2 matrices */
public class So3Display implements ManifoldDisplay, Serializable {
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.4)).unmodifiable();
  private static final Scalar RADIUS = RealScalar.of(7);
  // ---
  public static final ManifoldDisplay INSTANCE = new So3Display(RADIUS);
  /***************************************************/
  private final Scalar radius;

  public So3Display(Scalar radius) {
    this.radius = radius;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public Geodesic geodesicInterface() {
    return So3Geodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return TRIANGLE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor axis = xya.divide(radius);
    Scalar norm = Vector2Norm.of(axis);
    if (Scalars.lessThan(RealScalar.ONE, norm))
      axis = axis.divide(norm);
    return Rodrigues.vectorExp(axis);
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor xyz) {
    return Rodrigues.INSTANCE.vectorLog(xyz).extract(0, 2).multiply(radius);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor xyz) {
    return Se2Matrix.translation(toPoint(xyz));
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return SoGroup.INSTANCE;
  }

  @Override
  public LieExponential lieExponential() {
    return So3Manifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsManifold hsManifold() {
    return So3Manifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsTransport hsTransport() {
    return SoTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return So3Metric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Biinvariant metricBiinvariant() {
    return MetricBiinvariant.EUCLIDEAN;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return So3BiinvariantMean.INSTANCE;
  }

  @Override
  public final LineDistance lineDistance() {
    return null; // TODO line distance should be similar to s^3
  }

  @Override // from Object
  public String toString() {
    return "SO3";
  }
}
