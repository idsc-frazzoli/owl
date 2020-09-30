// code by jph
package ch.ethz.idsc.tensor.ref;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ class ParamContainerExt extends ParamContainer {
  public static final ParamContainerExt INSTANCE = ObjectProperties.wrap(new ParamContainerExt()) //
      .set(ResourceData.properties("/io/ParamContainerExt.properties"));
  // ---
  public Tensor onlyInExt = Tensors.vector(1, 2, 3);
  @SuppressWarnings("unused")
  private Scalar _private;

  public ParamContainerExt() {
    string = "fromConstructor";
  }
}
