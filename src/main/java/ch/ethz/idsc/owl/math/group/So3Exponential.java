// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Rodrigues;

/** a group element SO(3) is represented as a 3x3 orthogonal matrix
 * 
 * an element of the algebra so(3) is represented as a vector of length 3 */
public enum So3Exponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor vector) {
    return Rodrigues.exp(vector);
  }

  @Override // from LieExponential
  public Tensor log(Tensor matrix) {
    return Rodrigues.log(matrix);
  }
}
