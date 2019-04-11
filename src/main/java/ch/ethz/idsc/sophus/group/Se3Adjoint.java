// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** linear map that transforms tangent vector at the identity
 * to vector in tangent space of given group element
 * 
 * code based on derivation by Ethan Eade
 * "Lie Groups for 2D and 3D Transformations", p. 11 */
public class Se3Adjoint implements TensorUnaryOperator {
  private final Tensor R;
  private final Tensor t;

  /** @param element from Lie Group SE3 as 4x4 affine matrix */
  public Se3Adjoint(Tensor g) {
    R = Se3Utils.rotation(g);
    t = Se3Utils.translation(g);
  }

  @Override
  public Tensor apply(Tensor u_w) {
    Tensor u = u_w.get(0);
    Tensor w = u_w.get(1);
    Tensor rw = R.dot(w);
    return Tensors.of( //
        R.dot(u).add(Cross.of(t, rw)), //
        rw);
  }
}
