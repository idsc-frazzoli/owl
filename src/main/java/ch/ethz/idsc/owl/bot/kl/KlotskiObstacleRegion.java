// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class KlotskiObstacleRegion implements Region<Tensor> {
  public static Region<Tensor> huarong() {
    return new KlotskiObstacleRegion(7, 6);
  }

  public static Region<Tensor> fromSize(Tensor size) {
    return new KlotskiObstacleRegion( //
        size.Get(0).number().intValue(), //
        size.Get(1).number().intValue());
  }

  // ---
  private final int sx;
  private final int sy;
  private final int lx;
  private final int ly;

  private KlotskiObstacleRegion(int sx, int sy) {
    this.sx = sx;
    this.sy = sy;
    this.lx = sx - 1;
    this.ly = sy - 1;
  }

  @Override
  public boolean isMember(Tensor state) {
    int[][] array = new int[sx][sy];
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
      case 4:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 2][py + 0];
        break;
      case 5:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        break;
      case 6:
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        ++array[px + 1][py + 1];
        break;
      default:
        throw new RuntimeException("unknown: " + index);
      }
    }
    boolean isOk = true;
    for (int px = 0; px < sx; ++px)
      isOk &= array[px][ly] == 0;
    for (int py = 0; py < sy; ++py)
      isOk &= array[lx][py] == 0;
    for (int px = 1; px < lx; ++px)
      for (int py = 1; py < ly; ++py)
        isOk &= array[px][py] <= 1;
    return !isOk;
  }
}
