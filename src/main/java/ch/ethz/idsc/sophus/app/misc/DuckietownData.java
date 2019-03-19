// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.sophus.surf.RotationMatrix3D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum DuckietownData {
  ;
  public static final Tensor POSE_20190301_0 = ResourceData.of("/autolab/localization/pose/20190301_0.csv");
  // File FILE = UserName.is("datahaki") //
  // ? HomeDirectory.file("duckiebot_0_poses.csv")
  // : HomeDirectory.file("Desktop/MA/duckietown/duckiebot_0_poses.csv");
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

  public static void main(String[] args) {
    // TODO EPHEMERAL JPH
    // System.out.println(Import.of());
    Tensor states = states(POSE_20190301_0);
    System.out.println(states);
    System.out.println(states);
  }
}
