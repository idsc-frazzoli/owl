// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum GokartPoseDataV2 implements GokartPoseData {
  INSTANCE;
  // ---
  private final List<String> list = Collections.unmodifiableList(ResourceData.lines("/dubilab/app/tpqv50.vector"));

  @Override // from GokartPoseData
  public List<String> list() {
    return list;
  }

  @Override // from GokartPoseData
  public Tensor getPose(String name, int limit) {
    return Tensor.of(ResourceData.of("/dubilab/app/tpqv50/" + name + ".csv").stream() //
        .limit(limit) //
        .map(row -> row.extract(1, 4)));
  }

  @Override // from GokartPoseData
  public Scalar getSampleRate() {
    return Quantity.of(50, "s^-1");
  }
}
