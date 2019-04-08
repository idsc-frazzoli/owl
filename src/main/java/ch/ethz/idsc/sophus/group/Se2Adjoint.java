// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** linear map that transforms tangent vector at the identity
 * to vector in tangent space of given group element
 * 
 * code based on derivation by Ethan Eade
 * "Lie Groups for 2D and 3D Transformations", p. 16 */
public class Se2Adjoint implements TensorUnaryOperator {
  private final Scalar px;
  private final Scalar py;
  private final Scalar ct;
  private final Scalar st;

  /** @param element from Lie Group SE2 as coordinates {x, y, omega} */
  public Se2Adjoint(Tensor xya) {
    if (xya.length() != 3)
      throw TensorRuntimeException.of(xya);
    px = xya.Get(0);
    py = xya.Get(1);
    Scalar th = xya.Get(2);
    ct = Cos.FUNCTION.apply(th);
    st = Sin.FUNCTION.apply(th);
  }

  @Override
  public Tensor apply(Tensor uvw) {
    Scalar u = uvw.Get(0);
    Scalar v = uvw.Get(1);
    Scalar w = uvw.Get(2);
    return Tensors.of( //
        ct.multiply(u).subtract(st.multiply(v)).add(py.multiply(w)), //
        st.multiply(u).add(ct.multiply(v)).subtract(px.multiply(w)), //
        w);
  }
}
