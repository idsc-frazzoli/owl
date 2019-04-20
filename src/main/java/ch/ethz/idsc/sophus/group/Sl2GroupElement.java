// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;

/** neutral element is {0, 0, 1}
 * 
 * Reference
 * S. Lie: Theorie der Transformationsgruppen - Erster Abschnitt, Teubner 1930 */
public class Sl2GroupElement implements LieGroupElement {
  private final Scalar a1;
  private final Scalar a2;
  private final Scalar a3;

  public Sl2GroupElement(Tensor tensor) {
    this(tensor.Get(0), //
        tensor.Get(1), //
        tensor.Get(2));
  }

  private Sl2GroupElement(Scalar a1, Scalar a2, Scalar a3) {
    if (Scalars.isZero(a3))
      throw TensorRuntimeException.of(a1, a2, a3);
    this.a1 = a1;
    this.a2 = a2;
    this.a3 = a3;
  }

  @Override // from LieGroupElement
  public Sl2GroupElement inverse() {
    return new Sl2GroupElement( //
        a1.divide(a3).negate(), //
        a2.divide(a3).negate(), //
        a3.reciprocal());
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor tensor) {
    Scalar b1 = tensor.Get(0);
    Scalar b2 = tensor.Get(1);
    Scalar b3 = tensor.Get(2);
    Scalar den = RealScalar.ONE.add(b1.multiply(a2));
    return Tensors.of( //
        a1.add(b1.multiply(a3)).divide(den), //
        b2.add(a2.multiply(b3)).divide(den), //
        b2.multiply(a1).add(b3.multiply(a3)).divide(den));
  }

  @Override // from LieGroupElement
  public Tensor adjoint(Tensor tensor) {
    throw new UnsupportedOperationException();
  }

  /* package */ Tensor vector() {
    return Tensors.of(a1, a2, a3);
  }
}
