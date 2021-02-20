// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.MetricBiinvariant;
import ch.ethz.idsc.sophus.hs.spd.Spd0Exponential;
import ch.ethz.idsc.sophus.hs.spd.SpdBiinvariantMean;
import ch.ethz.idsc.sophus.hs.spd.SpdGeodesic;
import ch.ethz.idsc.sophus.hs.spd.SpdManifold;
import ch.ethz.idsc.sophus.hs.spd.SpdMetric;
import ch.ethz.idsc.sophus.hs.spd.SpdTransport;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.red.Diagonal;

/** symmetric positive definite 2 x 2 matrices */
public enum Spd2Display implements ManifoldDisplay {
  INSTANCE;

  private static final Scalar SCALE = RealScalar.of(0.2);
  private static final Tensor CIRCLE_POINTS = CirclePoints.of(43).multiply(SCALE).unmodifiable();
  private static final TensorUnaryOperator PAD_RIGHT = PadRight.zeros(3, 3);

  @Override // from GeodesicDisplay
  public Geodesic geodesicInterface() {
    return SpdGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public TensorUnaryOperator tangentProjection(Tensor p) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return CIRCLE_POINTS;
  }

  private static Tensor xya2sim(Tensor xya) {
    xya = xya.multiply(SCALE);
    Tensor sim = DiagonalMatrix.with(xya.extract(0, 2));
    sim.set(xya.Get(2), 0, 1);
    sim.set(xya.Get(2), 1, 0);
    return sim;
  }

  private static Tensor sim2xya(Tensor sim) {
    return Diagonal.of(sim).append(sim.get(0, 1)).divide(SCALE);
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor sim = xya2sim(xya);
    return Spd0Exponential.INSTANCE.exp(sim);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor sym) {
    Tensor sim = Spd0Exponential.INSTANCE.log(sym);
    return sim2xya(sim).extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor sym) {
    Tensor matrix = PAD_RIGHT.apply(sym); // log is possible
    matrix.set(RealScalar.ONE, 2, 2);
    return Se2Matrix.translation(toPoint(sym)).dot(matrix);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return null;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public HsManifold hsManifold() {
    return SpdManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsTransport hsTransport() {
    return SpdTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return SpdMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Biinvariant metricBiinvariant() {
    return MetricBiinvariant.VECTORIZE0;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return SpdBiinvariantMean.INSTANCE;
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public String toString() {
    return "Spd2";
  }
}
