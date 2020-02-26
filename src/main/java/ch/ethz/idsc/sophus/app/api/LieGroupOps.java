// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.tensor.Tensor;

public class LieGroupOps implements Serializable {
  private final LieGroup lieGroup;

  public LieGroupOps(LieGroup lieGroup) {
    this.lieGroup = Objects.requireNonNull(lieGroup);
  }

  public Tensor allI(Tensor sequence) {
    return Tensor.of(sequence.stream().map(this::invert));
  }

  public Tensor allR(Tensor sequence, Tensor shift) {
    return Tensor.of(sequence.stream() //
        .map(lieGroup::element) //
        .map(lieGroupElement -> lieGroupElement.combine(shift)));
  }

  public Tensor allL(Tensor sequence, Tensor shift) {
    LieGroupElement lieGroupElement = lieGroup.element(shift);
    return Tensor.of(sequence.stream() //
        .map(lieGroupElement::combine));
  }

  public Tensor combine(Tensor g, Tensor h) {
    return lieGroup.element(g).combine(h);
  }

  public Tensor invert(Tensor g) {
    return lieGroup.element(g).inverse().toCoordinate();
  }

  /** @param g
   * @param h
   * @return g.h.g^-1 */
  public Tensor conjugate(Tensor g, Tensor h) {
    return lieGroup.element(lieGroup.element(g).combine(h)).combine(lieGroup.element(g).inverse().toCoordinate());
  }
}
