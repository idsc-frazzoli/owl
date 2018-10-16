// code by jph
package ch.ethz.idsc.owl.symlink;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class SymNode extends SymLink {
  private final Scalar symScalar;
  public Tensor position;

  public SymNode(Scalar symScalar) {
    super(null, null, null);
    this.symScalar = symScalar;
  }

  @Override
  public int getIndex() {
    return symScalar.number().intValue();
  }

  @Override
  public Tensor getPosition() {
    return Tensors.of(symScalar, RealScalar.ZERO);
  }

  @Override
  public Tensor getPosition(GeodesicInterface geodesicInterface) {
    return position;
  }
}
