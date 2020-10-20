// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class UbongoEntry {
  public int i;
  public int j;
  public Ubongo ubongo;
  public Tensor stamp;

  @Override
  public String toString() {
    // return String.format("%s", ubongo);
    return String.format("%d %d %s %s", i, j, ubongo, stamp);
  }
}
