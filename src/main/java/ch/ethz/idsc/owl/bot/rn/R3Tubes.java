// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.region.ImplicitFunctionRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;

/** example functions for use with RnImplicitObstacle
 *
 * positive return value (including zero) means inside obstacle region */
/* package */ class R3Tubes extends ImplicitFunctionRegion implements Serializable {
  /** 3-dimensional tube network
   * the challenge is to find the path through narrow passages along the diagonal
   *
   * suggested values for use are
   * extent = 2
   * root = ( -2.0, -2.0, -1.8 )
   * goal = ( 2.0, 1.8, 2.0 )
   *
   * motivated by
   * https://reference.wolfram.com/language/ref/ContourPlot3D.html */
  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor tensor) {
    final double[] data = Primitives.toDoubleArray(tensor);
    double x = data[0];
    double y = data[1];
    double z = data[2];
    double x2 = x * x;
    double y2 = y * y;
    double z2 = z * z;
    double a = x2 + y2 + z2;
    double val = x2 * x2 + y2 * y2 + z2 * z2 - a * a + 2.72 * a;
    return RealScalar.of(val - 2.8);
  }
}
