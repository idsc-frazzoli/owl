// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.spd.SpdExponential;
import ch.ethz.idsc.sophus.hs.spd.SpdGeodesic;
import ch.ethz.idsc.sophus.hs.spd.SpdMean;
import ch.ethz.idsc.sophus.hs.spd.SpdMetric;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Diagonal;

/** symmetric positive definite 2 x 2 matrices */
public enum Spd2GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;

  private static final Tensor CIRCLE_POINTS = CirclePoints.of(43).multiply(RealScalar.of(0.2));
  private static final TensorUnaryOperator PAD_RIGHT = PadRight.zeros(3, 3);

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return SpdGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return CIRCLE_POINTS;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor matrix = DiagonalMatrix.with(xya.extract(0, 2));
    matrix.set(xya.Get(2), 0, 1);
    matrix.set(xya.Get(2), 1, 0);
    return SpdExponential.INSTANCE.exp(matrix);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return Diagonal.of(SpdExponential.INSTANCE.log(p));
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    Tensor matrix = PAD_RIGHT.apply(p);
    matrix.set(RealScalar.ONE, 2, 2);
    Tensor log = SpdExponential.INSTANCE.log(p);
    return Se2Matrix.translation(Diagonal.of(log)).dot(matrix);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    throw new UnsupportedOperationException();
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    throw new UnsupportedOperationException();
  }

  @Override // from GeodesicDisplay
  public Scalar parametricDistance(Tensor p, Tensor q) {
    return SpdMetric.INSTANCE.distance(p, q);
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return SpdMean.INSTANCE;
  }

  @Override
  public String toString() {
    return "Spd2";
  }
}
