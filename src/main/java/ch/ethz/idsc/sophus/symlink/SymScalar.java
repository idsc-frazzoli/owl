// code by jph
package ch.ethz.idsc.sophus.symlink;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class SymScalar extends ScalarAdapter {
  /** @param p
   * @param q
   * @param split
   * @return */
  public static Scalar of(Scalar p, Scalar q, Scalar split) {
    if (p instanceof SymScalar && q instanceof SymScalar)
      return new SymScalar(Tensors.of(p, q, split).unmodifiable());
    throw TensorRuntimeException.of(p, q, split);
  }

  public static Scalar single(int number) {
    return new SymScalar(RealScalar.of(number));
  }

  // ---
  private final Tensor tensor;

  private SymScalar(Tensor tensor) {
    this.tensor = tensor;
  }

  public Tensor tensor() {
    return tensor;
  }

  public boolean isScalar() {
    return ScalarQ.of(tensor);
  }

  public SymScalar getP() {
    return (SymScalar) tensor.Get(0);
  }

  public SymScalar getQ() {
    return (SymScalar) tensor.Get(1);
  }

  public Scalar ratio() {
    return tensor.Get(2);
  }

  public Scalar evaluate() {
    if (isScalar())
      return tensor.Get();
    return RnGeodesic.INSTANCE.split( //
        getP().evaluate(), //
        getQ().evaluate(), //
        ratio()).Get();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof SymScalar) {
      SymScalar symScalar = (SymScalar) object;
      return symScalar.tensor.equals(tensor);
    }
    return false;
  }

  @Override
  public String toString() {
    return tensor.toString();
  }
}
