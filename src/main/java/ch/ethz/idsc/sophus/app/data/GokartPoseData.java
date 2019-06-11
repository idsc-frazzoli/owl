// code by jph
package ch.ethz.idsc.sophus.app.data;

import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum GokartPoseData {
  INSTANCE;
  // ---
  private final List<String> list = Collections.unmodifiableList(ResourceData.lines("/dubilab/app/pose/index.vector"));

  public List<String> list() {
    return list;
  }

  public static Tensor getPose(String name, int limit) {
    return Tensor.of(ResourceData.of("/dubilab/app/pose/" + name + ".csv").stream() //
        .limit(limit) //
        .map(row -> row.extract(1, 4)));
  }
}
