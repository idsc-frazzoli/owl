// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.sophus.hs.sn.SnInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnMean;
import ch.ethz.idsc.sophus.hs.sn.SnMetric;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.Orthogonalize;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** symmetric positive definite 2 x 2 matrices */
public class S2GeodesicDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor CIRCLE = CirclePoints.of(15).multiply(RealScalar.of(0.2));
  private static final TensorUnaryOperator PAD_RIGHT = PadRight.zeros(3, 3);
  private static final Scalar RADIUS = RealScalar.of(7);
  public static final GeodesicDisplay INSTANCE = new S2GeodesicDisplay(RADIUS);
  /***************************************************/
  private final Scalar radius;

  public S2GeodesicDisplay(Scalar radius) {
    this.radius = radius;
  }

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return SnGeodesic.INSTANCE;
  }

  @Override
  public int dimensions() {
    return 2;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return CIRCLE;
  }

  /** @param xyz normalized vector
   * @return 2 x 3 matrix with rows spanning the space tangent to given xyz */
  /* package */ static Tensor tangentSpace(Tensor xyz) {
    Tensor frame = Tensors.of(xyz);
    IdentityMatrix.of(3).stream().forEach(frame::append);
    return Orthogonalize.of(frame).extract(1, 3);
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xy = xya.extract(0, 2).divide(radius);
    Scalar normsq = Norm2Squared.ofVector(xy);
    if (Scalars.lessThan(normsq, RealScalar.ONE)) {
      Scalar z = Sqrt.FUNCTION.apply(RealScalar.ONE.subtract(normsq));
      return xy.append(z);
    }
    Tensor xyz = xya.divide(radius);
    xyz.set(RealScalar.ZERO, 2);
    return Normalize.with(Norm._2).apply(xyz);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor xyz) {
    return xyz.extract(0, 2).multiply(radius);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor xyz) {
    Tensor frame = tangentSpace(xyz);
    Tensor skew = PAD_RIGHT.apply(Transpose.of(Tensors.of( //
        frame.get(0).extract(0, 2), //
        frame.get(1).extract(0, 2))));
    skew.set(RealScalar.ONE, 2, 2);
    return Se2Matrix.translation(toPoint(xyz)).dot(skew);
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
    return SnMetric.INSTANCE.distance(p, q);
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return new SnMean(Chop._05);
  }

  @Override
  public BarycentricCoordinate inverseDistanceCoordinates() {
    return SnInverseDistanceCoordinate.INSTANCE;
  }

  @Override
  public String toString() {
    return "S2";
  }
}
