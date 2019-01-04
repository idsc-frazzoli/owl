// code by jph
package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.tensor.Tensor;

public class SymLinkBuilder {
  private final Tensor control;

  public SymLinkBuilder(Tensor control) {
    this.control = control;
  }

  public SymLink build(SymScalar symScalar) {
    if (symScalar.isScalar()) {
      SymNode symNode = new SymNode(symScalar.evaluate());
      symNode.position = control.get(symNode.getIndex());
      return symNode;
    }
    return new SymLink(build(symScalar.getP()), build(symScalar.getQ()), symScalar.ratio());
  }
}
