// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;

/* package */ enum Labels implements Function<Tensor, LabelInterface> {
  ARG_MAX(Classification::argMax), //
  ACC_MAX(Classification::accMax), //
  ;

  private final Function<Tensor, LabelInterface> function;

  private Labels(Function<Tensor, LabelInterface> function) {
    this.function = function;
  }

  @Override
  public LabelInterface apply(Tensor vector) {
    return function.apply(vector);
  }
}
