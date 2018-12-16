// code by jph
package ch.ethz.idsc.sophus.symlink;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class SymNode extends SymLink {
  private final Scalar symScalar;
  public Tensor position;

  public SymNode(Scalar symScalar) {
    super(null, null, null);
    this.symScalar = symScalar;
  }

  @Override // from SymLink
  public int getIndex() {
    return symScalar.number().intValue();
  }

  @Override // from SymLink
  public Tensor getPosition() {
    return Tensors.of(symScalar, RealScalar.ZERO);
  }

  @Override // from SymLink
  public Tensor getPosition(GeodesicInterface geodesicInterface) {
    return position;
  }
}
