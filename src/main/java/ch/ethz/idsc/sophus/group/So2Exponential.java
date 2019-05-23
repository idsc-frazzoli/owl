// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.sca.ArcCos;

/** a group element SO(2) is represented as a 2x2 orthogonal matrix
 * 
 * an element of the algebra so(2) is represented as 'vector' of length 1 */
// TODO JPH/OB SO(2) as R mod 2pi with offset -pi
public enum So2Exponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor vector) {
    return RotationMatrix.of(vector.Get(0));
  }

  @Override // from LieExponential
  public Tensor log(Tensor matrix) {
    return Tensors.of(ArcCos.FUNCTION.apply(matrix.get(0).Get(0)));
  }
}
