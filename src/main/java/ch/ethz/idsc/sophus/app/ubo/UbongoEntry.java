// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class UbongoEntry implements Serializable {
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
