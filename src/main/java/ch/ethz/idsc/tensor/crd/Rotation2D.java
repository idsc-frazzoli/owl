// code by gjoel
package ch.ethz.idsc.tensor.crd;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class Rotation2D extends CoordinateTransform {
  public static Rotation2D of(Number angle, CoordinateSystem from, CoordinateSystem to) {
    return of(RealScalar.of(angle), from, to);
  }

  public static Rotation2D of(Scalar angle, CoordinateSystem from, CoordinateSystem to) {
    return new Rotation2D(RotationMatrix.of(angle)::dot, from, to, angle);
  }

  // ---
  private Scalar angle;

  private Rotation2D(TensorUnaryOperator operator, CoordinateSystem from, CoordinateSystem to, Scalar angle) {
    super(operator, from, to);
    this.angle = angle;
  }

  protected TensorUnaryOperator inverseTensorUnaryOperator() {
    return RotationMatrix.of(angle.negate())::dot;
  }
}
