// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/** symmetric positive definite 2 x 2 matrices */
public class S1GeodesicDisplay extends SnGeodesicDisplay {
  private static final long serialVersionUID = 6398104295573497654L;
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

  @Override // from GeodesicDisplay
  public VectorLogManifold vectorLogManifold() {
    return SnManifold.INSTANCE;
  }
}
