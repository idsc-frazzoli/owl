// code by gjoel, jph
package ch.ethz.idsc.tensor.fig;

import java.io.Serializable;
import java.util.Objects;

/* package */ class ComparableLabel implements Comparable<ComparableLabel>, Serializable {
  private static final long serialVersionUID = 6313667721475781988L;
  // ---
  private final int index;
  /** may not be null */
  private String string;

  public ComparableLabel(int index) {
    this.index = index;
    string = "";
  }

  @Override // from Comparable
  public int compareTo(ComparableLabel comparableLabel) {
    return Integer.compare(index, comparableLabel.index);
  }

  public void setString(String string) {
    this.string = Objects.requireNonNull(string);
  }

  @Override
  public String toString() {
    return string;
  }
}
