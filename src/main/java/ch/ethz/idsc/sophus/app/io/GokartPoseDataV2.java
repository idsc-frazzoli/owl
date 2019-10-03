// code by jph
package ch.ethz.idsc.sophus.app.io;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

/** Columns:
 * time
 * px
 * py
 * pangle
 * quality
 * vx
 * vy
 * vangle */
public class GokartPoseDataV2 implements GokartPoseData {
  public static final String PATH_FOLDER = "/dubilab/app/tpqv50";
  public static final String PATH_VECTOR = PATH_FOLDER + ".vector";
  private static final List<String> LIST = ResourceData.lines(PATH_VECTOR);
  // ---
  /** all available */
  public static final GokartPoseDataV2 INSTANCE = new GokartPoseDataV2(LIST);
  /** 20190701 */
  public static final GokartPoseDataV2 RACING_DAY = new GokartPoseDataV2(LIST.stream() //
      .filter(string -> string.startsWith("20190701")) //
      .collect(Collectors.toList()));
  // ---
  private final List<String> list;

  private GokartPoseDataV2(List<String> list) {
    this.list = list;
  }

  @Override // from GokartPoseData
  public List<String> list() {
    return Collections.unmodifiableList(list);
  }

  @Override // from GokartPoseData
  public Tensor getPose(String name, int limit) {
    return Tensor.of(ResourceData.of(PATH_FOLDER + "/" + name + ".csv").stream() //
        .limit(limit) //
        .map(row -> row.extract(1, 4)));
  }

  @Override // from GokartPoseData
  public Scalar getSampleRate() {
    return Quantity.of(50, "s^-1");
  }

  /** @param name
   * @param limit
   * @return tensor with at most given limit entries of the form {{px, py, pangle}, {vx, vy, omega}} */
  public Tensor getPoseVel(String name, int limit) {
    return Tensor.of(ResourceData.of(PATH_FOLDER + "/" + name + ".csv").stream() //
        .limit(limit) //
        .map(row -> Tensors.of(row.extract(1, 4), row.extract(5, 8))));
  }
}
