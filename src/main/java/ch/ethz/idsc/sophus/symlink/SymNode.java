// code by jph
package ch.ethz.idsc.sophus.symlink;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** characterized by {@link #isNode()} == true */
/* package */ class SymNode extends SymLink {
  private final Scalar scalar;
  public Tensor position;

  /* package */ SymNode(Scalar scalar) {
    super(null, null, null);
    this.scalar = scalar;
  }

  @Override // from SymLink
  public int getIndex() {
    return scalar.number().intValue();
  }

  @Override // from SymLink
  public Tensor getPosition() {
    return Tensors.of(scalar, RealScalar.ZERO);
  }

  @Override // from SymLink
  public Tensor getPosition(GeodesicInterface geodesicInterface) {
    return position;
  }
}
