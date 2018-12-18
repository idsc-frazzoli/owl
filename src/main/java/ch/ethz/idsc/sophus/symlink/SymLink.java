// code by jph
package ch.ethz.idsc.sophus.symlink;

import java.util.Objects;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Min;

/** SymNode extends from here */
/* package */ class SymLink {
  private static final Scalar SHIFT_Y = RealScalar.of(.5);

  public static SymLink build(SymScalar symScalar) {
    if (symScalar.isScalar())
      return new SymNode(symScalar.evaluate());
    return new SymLink(build(symScalar.getP()), build(symScalar.getQ()), symScalar.ratio());
  }

  // ---
  public final SymLink lP;
  public final SymLink lQ;
  public final Scalar lambda;

  public SymLink(SymLink lP, SymLink lQ, Scalar lambda) {
    this.lP = lP;
    this.lQ = lQ;
    this.lambda = lambda;
  }

  public final boolean isNode() {
    return Objects.isNull(lP);
  }

  public final int depth() {
    if (isNode())
      return 0;
    return Math.max(lP.depth(), lQ.depth()) + 1;
  }

  public int getIndex() {
    throw new RuntimeException();
  }

  public Tensor getPosition() {
    Tensor posP = lP.getPosition();
    Tensor posQ = lQ.getPosition();
    Tensor x = RnGeodesic.INSTANCE.split(posP.Get(0), posQ.Get(0), lambda);
    Scalar y = Min.of(posP.Get(1), posQ.Get(1)).subtract(SHIFT_Y);
    return Tensors.of(x, y);
  }

  public Tensor getPosition(GeodesicInterface geodesicInterface) {
    return geodesicInterface.split( //
        lP.getPosition(geodesicInterface), //
        lQ.getPosition(geodesicInterface), //
        lambda);
  }
}
