// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class KlotskiObstacleRegion implements Region<Tensor> {
  public static Region<Tensor> fromSize(Tensor size) {
    return new KlotskiObstacleRegion( //
        size.Get(0).number().intValue(), //
        size.Get(1).number().intValue());
  }

  // ---
  private final int sx;
  private final int sy;

  private KlotskiObstacleRegion(int sx, int sy) {
    this.sx = sx;
    this.sy = sy;
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
        for (int cx = 0; cx < 5; ++cx)
          for (int cy = 0; cy < 5; ++cy)
            ++array[px + cx][py + cy];
        break;
      case 6:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        break;
      case 7:
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        ++array[px + 1][py + 1];
        break;
      case 8:
        --array[px + 0][py + 0];
        --array[px + 0][py + 4];
        --array[px + 4][py + 0];
        --array[px + 4][py + 4];
        for (int cx = 0; cx < 5; ++cx)
          for (int cy = 0; cy < 5; ++cy)
            ++array[px + cx][py + cy];
        break;
      default:
        throw new RuntimeException("unknown: " + index);
      }
    }
    for (int px = 0; px < sx; ++px)
      for (int py = 0; py < sy; ++py)
        if (1 < array[px][py])
          return true;
    return false;
  }
}
