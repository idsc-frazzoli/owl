// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;

/** clothoid is tangent at start and end points */
public class ClothoidTerminalRatio implements Serializable {
  private final Scalar head;
  private final Scalar tail;

  ClothoidTerminalRatio(Scalar head, Scalar tail) {
    this.head = head;
    this.tail = tail;
  }

  public Scalar head() {
    return head;
  }

  public Scalar tail() {
    return tail;
  }

  /** @return tail - head */
  public Scalar difference() {
    return tail.subtract(head);
  }

  @Override // from Object
  public final String toString() {
    return "{\"head\": " + head() + ", \"tail\": " + tail() + "}";
  }
}
