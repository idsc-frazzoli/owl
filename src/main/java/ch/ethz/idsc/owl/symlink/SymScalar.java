// code by jph
package ch.ethz.idsc.owl.symlink;

import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.owl.subdiv.demo.ScalarAdapter;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;

public class SymScalar extends ScalarAdapter {
  public static Scalar of(Scalar... scalars) {
    if (scalars.length == 1)
      return new SymScalar(scalars[0]);
    if (scalars.length == 3)
      return new SymScalar(Tensors.of(scalars).unmodifiable());
    throw TensorRuntimeException.of(scalars);
  }

  public static Scalar of(Number number) {
    return of(RealScalar.of(number));
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
  public String toString() {
    return tensor.toString();
  }

  public static void main(String[] args) {
    Scalar scalar = SymScalar.of(3);
    System.out.println(scalar.getClass());
  }
}
