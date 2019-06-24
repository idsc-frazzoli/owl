// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.Tensor;

public abstract class LieGroupAbstractNoise {
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;

  public LieGroupAbstractNoise(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
  }

  public final Tensor leftNoise(Tensor tensor) {
    return lieGroup.element(lieExponential.exp(noise())).combine(tensor);
  }

  public final Tensor rightNoise(Tensor tensor) {
    return lieGroup.element(tensor).combine(lieExponential.exp(noise()));
  }

  protected abstract Tensor noise();
}
