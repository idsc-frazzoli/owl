// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.sca.ArcCos;

/** a group element SO(2) is represented as a 2x2 orthogonal matrix
 * 
 * an element of the algebra so(2) is represented as a antisymmetric element of dimension 2x2 */
public enum So2Exponential implements LieExponential {
  INSTANCE;
  // ---
  private final static Scalar ZERO = RealScalar.ZERO;

  @Override // from LieExponential
  public Tensor exp(Tensor so2) {
    Scalar angle = so2.get(1).Get(0);
    return RotationMatrix.of(angle);
  }

  @Override // from LieExponential
  public Tensor log(Tensor SO2) {
    Scalar angle = ArcCos.FUNCTION.apply(SO2.get(0).Get(0));
    return Tensors.matrix(new Scalar[][] { //
        { ZERO, angle.negate() }, //
        { angle, ZERO } });
  }
}
