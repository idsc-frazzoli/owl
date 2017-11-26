// code by jph
package ch.ethz.idsc.owl.bot.rnd;

import ch.ethz.idsc.tensor.Tensor;

public class RndState {
  public static RndState of(Tensor tensor) {
    return new RndState(tensor);
  }

  // ---
  public final Tensor x1;
  public final Tensor x2;

  public RndState(Tensor tensor) {
    int semi = tensor.length() / 2;
    x1 = tensor.extract(0, semi);
    x2 = tensor.extract(semi, tensor.length());
  }
}
