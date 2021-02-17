// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;

/** symmetric positive definite 2 x 2 matrices */
public class S1Display extends SnDisplay {
  public static final ManifoldDisplay INSTANCE = new S1Display();

  /***************************************************/
  private S1Display() {
    super(1);
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xy = xya.extract(0, 2);
    Scalar norm = Vector2Norm.of(xy);
    return Scalars.isZero(norm) //
        ? UnitVector.of(2, 0)
        : xy.divide(norm);
  }

  @Override // from GeodesicDisplay
  public TensorUnaryOperator tangentProjection(Tensor p) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor xy) {
    return xy.copy();
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor xy) {
    return Se2Matrix.translation(toPoint(xy));
  }
}
