// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.red.Norm;

/** symmetric positive definite 2 x 2 matrices */
public class S1GeodesicDisplay extends SnGeodesicDisplay {
  public static final GeodesicDisplay INSTANCE = new S1GeodesicDisplay();

  /***************************************************/
  private S1GeodesicDisplay() {
    super(1);
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xy = xya.extract(0, 2);
    Scalar norm = Norm._2.ofVector(xy);
    return Scalars.isZero(norm) //
        ? UnitVector.of(2, 0)
        : xy.divide(norm);
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
