// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum HuarongObstacleRegion implements Region<Tensor> {
  INSTANCE;
  // ---
  @Override
  public boolean isMember(Tensor state) {
    int[][] array = new int[7][6];
    for (Tensor stone : state) {
      int index = stone.Get(0).number().intValue();
      int px = stone.Get(1).number().intValue();
      int py = stone.Get(2).number().intValue();
      switch (index) {
      case 0:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        ++array[px + 1][py + 1];
        break;
      case 1:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        break;
      case 2:
        ++array[px + 0][py + 0];
        ++array[px + 0][py + 1];
        break;
      case 3:
        ++array[px + 0][py + 0];
        break;
      default:
        throw new RuntimeException("unknown: " + index);
      }
    }
    boolean isOk = true;
    for (int px = 0; px <= 6; ++px) {
      isOk &= array[px][0] == 0;
      isOk &= array[px][5] == 0;
    }
    for (int py = 0; py <= 5; ++py) {
      isOk &= array[0][py] == 0;
      isOk &= array[6][py] == 0;
    }
    for (int px = 1; px <= 5; ++px)
      for (int py = 1; py <= 4; ++py)
        isOk &= array[px][py] <= 1;
    return !isOk;
  }
}
