// code by jph
package ch.ethz.idsc.owl.symlink;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class SymNode extends SymLink {
  private final Scalar symScalar;

  public SymNode(Scalar symScalar) {
    super(null, null, null);
    this.symScalar = symScalar;
  }

  @Override
  public Tensor getPosition() {
    return Tensors.of(symScalar, RealScalar.ZERO);
  }
}
