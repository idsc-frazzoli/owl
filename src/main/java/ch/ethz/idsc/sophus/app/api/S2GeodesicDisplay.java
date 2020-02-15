// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Optional;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
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
import ch.ethz.idsc.tensor.sca.Sqrt;

/** symmetric positive definite 2 x 2 matrices */
public class S2GeodesicDisplay extends SnGeodesicDisplay {
  private static final TensorUnaryOperator PAD_RIGHT = PadRight.zeros(3, 3);
  public static final GeodesicDisplay INSTANCE = new S2GeodesicDisplay(RADIUS);

  /***************************************************/
  public S2GeodesicDisplay(Scalar radius) {
    super(radius);
  }

  @Override
  public int dimensions() {
    return 2;
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
    Optional<Tensor> optional = optionalProject(xya);
    if (optional.isPresent())
      return optional.get();
    Tensor xyz = xya.divide(getRadius());
    xyz.set(RealScalar.ZERO, 2);
    return Normalize.with(Norm._2).apply(xyz);
  }

  public Optional<Tensor> optionalProject(Tensor xya) {
    Tensor xy = xya.extract(0, 2).divide(getRadius());
    Scalar normsq = Norm2Squared.ofVector(xy);
    if (Scalars.lessThan(normsq, RealScalar.ONE)) {
      Scalar z = Sqrt.FUNCTION.apply(RealScalar.ONE.subtract(normsq));
      return Optional.of(xy.append(z));
    }
    return Optional.empty();
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor xyz) {
    return xyz.extract(0, 2).multiply(getRadius());
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
}
