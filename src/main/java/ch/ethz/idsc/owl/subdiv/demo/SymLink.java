// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Min;

public class SymLink {
  public final SymLink lP;
  public final SymLink lQ;
  private final Scalar lambda;

  public SymLink(SymLink lP, SymLink lQ, Scalar lambda) {
    this.lP = lP;
    this.lQ = lQ;
    this.lambda = lambda;
  }

  public Tensor getPosition() {
    Tensor posP = lP.getPosition();
    Tensor posQ = lQ.getPosition();
    Tensor x = RnGeodesic.INSTANCE.split(posP.Get(0), posQ.Get(0), lambda);
    Scalar y = Min.of(posP.Get(1), posQ.Get(1)).subtract(RealScalar.ONE);
    return Tensors.of(x, y);
  }

  public static SymLink build(SymScalar symScalar) {
    if (symScalar.isScalar())
      return new SymNode(symScalar.evaluate());
    return new SymLink(build(symScalar.getP()), build(symScalar.getQ()), symScalar.ratio());
  }
}
