// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.sophus.surf.RotationMatrix3D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;

public enum DuckietownData {
  ;
  private static final Tensor UNIT3 = UnitVector.of(3, 2).unmodifiable();

  /** @param tensor from csv file
   * @return */
  public static List<StateTime> of(Tensor tensor) {
    return tensor.stream() //
        .map(DuckietownData::row) //
        .collect(Collectors.toList());
  }

  /** @param tensor from csv file
   * @return matrix with dimensions n x 3 in which each row consists of {x, y, heading} */
  public static Tensor states(Tensor tensor) {
    return Tensor.of(of(tensor).stream().map(StateTime::state));
  }

  /** @param vector of length 13 with entries {time, x, y, z, [9 entries of rotation matrix...]}
   * @return */
  private static StateTime row(Tensor vector) {
    Scalar time = vector.Get(0);
    Tensor xy = vector.extract(1, 3);
    // skip position z
    Tensor rotation = Partition.of(vector.extract(4, 13), 3);
    Tensor zup = rotation.get(Tensor.ALL, 2);
    Tensor flat = RotationMatrix3D.of(zup, UNIT3).dot(rotation);
    Scalar alpha = ArcTan2D.of(flat.get(Tensor.ALL, 0));
    return new StateTime(xy.append(alpha), time);
  }

  public static void main(String[] args) throws IOException {
    // TODO EPHEMERAL JPH
    Tensor states = states(Import.of(HomeDirectory.file("duckiebot_0_poses.csv")));
    System.out.println(states);
  }
}
