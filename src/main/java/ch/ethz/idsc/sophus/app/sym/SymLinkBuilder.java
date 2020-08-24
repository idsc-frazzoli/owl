// code by jph
package ch.ethz.idsc.sophus.app.sym;

import ch.ethz.idsc.tensor.Tensor;

public class SymLinkBuilder {
  /** @param control
   * @param symScalar
   * @return */
  public static SymLink of(Tensor control, SymScalar symScalar) {
    return new SymLinkBuilder(control).build(symScalar);
  }

  /***************************************************/
  private final Tensor control;

  private SymLinkBuilder(Tensor control) {
    this.control = control;
  }

  private SymLink build(SymScalar symScalar) {
    if (symScalar.isScalar()) {
      SymNode symNode = new SymNode(symScalar.evaluate());
      symNode.position = control.get(symNode.getIndex());
      return symNode;
    }
    return new SymLink(build(symScalar.getP()), build(symScalar.getQ()), symScalar.ratio());
  }
}
