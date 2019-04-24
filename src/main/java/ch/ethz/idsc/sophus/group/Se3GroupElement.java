// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.lie.Cross;

/** linear map that transforms tangent vector at the identity
 * to vector in tangent space of given group element
 * 
 * code based on derivation by Ethan Eade
 * "Lie Groups for 2D and 3D Transformations", p. 8 */
public class Se3GroupElement implements LieGroupElement {
  private final Tensor R;
  private final Tensor t;

  public Se3GroupElement(Tensor R, Tensor t) {
    this.R = MatrixQ.require(R);
    this.t = VectorQ.requireLength(t, 3);
  }

  /** @param g element from Lie Group SE3 as 4x4 affine matrix */
  public Se3GroupElement(Tensor g) {
    R = Se3Utils.rotation(g);
    t = Se3Utils.translation(g);
  }

  @Override // from LieGroupElement
  public Se3GroupElement inverse() {
    Tensor tR = Transpose.of(R);
    return new Se3GroupElement(tR, tR.dot(t).negate());
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor g) {
    Se3GroupElement eg = new Se3GroupElement(g);
    return Se3Utils.toMatrix4x4(R.dot(eg.R), R.dot(eg.t).add(t));
  }

  @Override // from LieGroupElement
  public Tensor adjoint(Tensor u_w) {
    Tensor u = u_w.get(0); // translation
    Tensor w = u_w.get(1); // rotation
    Tensor rw = R.dot(w);
    return Tensors.of( //
        R.dot(u).add(Cross.of(t, rw)), //
        rw);
  }
}
