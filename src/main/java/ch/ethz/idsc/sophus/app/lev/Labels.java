// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;

/* package */ enum Labels implements Function<Tensor, Classification> {
  ARG_MIN(Classifier::argMin), //
  ARG_MAX(Classifier::argMax), //
  ACC_MAX(Classifier::accMax), //
  ;

  private final Function<Tensor, Classification> function;

  private Labels(Function<Tensor, Classification> function) {
    this.function = function;
  }

  @Override
  public Classification apply(Tensor vector) {
    return function.apply(vector);
  }
}
