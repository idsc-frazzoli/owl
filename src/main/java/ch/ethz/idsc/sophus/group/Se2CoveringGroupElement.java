// code by jph
package ch.ethz.idsc.sophus.group;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** represents an element of the covering group SE(2),
 * which is defined by three real values, or equivalently by a vector from R^3
 * 
 * {@link Se2CoveringGroup} */
public class Se2CoveringGroupElement implements LieGroupElement, Serializable {
  private final Scalar px;
  private final Scalar py;
  private final Scalar pa;
  private final Scalar ca;
  private final Scalar sa;

  /** @param xya == {px, py, angle} as member of Lie group SE2 */
  public Se2CoveringGroupElement(Tensor xya) {
    px = xya.Get(0);
    py = xya.Get(1);
    pa = xya.Get(2);
    ca = Cos.FUNCTION.apply(pa);
    sa = Sin.FUNCTION.apply(pa);
  }

  Se2CoveringGroupElement(Scalar px, Scalar py, Scalar pa, Scalar ca, Scalar sa) {
    this.px = px;
    this.py = py;
    this.pa = pa;
    this.ca = ca;
    this.sa = sa;
  }

  @Override // from LieGroupElement
  public final Se2CoveringGroupElement inverse() {
    return create( //
        px.multiply(ca).add(py.multiply(sa)).negate(), //
        px.multiply(sa).subtract(py.multiply(ca)), //
        pa.negate(), //
        ca, //
        sa.negate());
  }

  /** @param tensor of the form {px, py, angle}
   * @return vector of length 3 */
  @Override // from LieGroupElement
  public Tensor combine(Tensor tensor) {
    Scalar qx = tensor.Get(0);
    Scalar qy = tensor.Get(1);
    Scalar qa = tensor.Get(2);
    return Tensors.of( //
        px.add(qx.multiply(ca)).subtract(qy.multiply(sa)), //
        py.add(qy.multiply(ca)).add(qx.multiply(sa)), //
        pa.add(qa));
  }

  Se2CoveringGroupElement create(Scalar px, Scalar py, Scalar pa, Scalar ca, Scalar sa) {
    return new Se2CoveringGroupElement(px, py, pa, ca, sa);
  }
}
