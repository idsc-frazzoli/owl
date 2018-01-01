// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.List;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.rn.RnPointcloudRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.sca.N;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2NdTreeAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    Tensor tensor = ImageRegions.grayscale(ResourceData.of("/io/track0_100.png"));
    Tensor range = Tensors.vector(10, 10);
    ImageRegion imageRegion = new ImageRegion(tensor, range, false);
    Tensor inverse = N.DOUBLE.of(imageRegion.scale().map(Scalar::reciprocal));
    List<Integer> dimensions = Dimensions.of(tensor);
    Tensor points = Tensors.empty();
    final int rows = dimensions.get(0);
    for (int row = 0; row < rows; ++row)
      for (int col = 0; col < dimensions.get(1); ++col) {
        Scalar occ = tensor.Get(row, col);
        if (Scalars.nonZero(occ))
          points.append(Tensors.vector(col, rows - row).pmul(inverse));
      }
    System.out.println(Dimensions.of(points));
    Region<Tensor> region = RnPointcloudRegion.of(points, RealScalar.of(0.3));
    // ---
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    R2Entity r2Entity = new R2Entity(Tensors.vector(0, 0));
    owlyAnimationFrame.set(r2Entity);
    owlyAnimationFrame.setObstacleQuery(SimpleTrajectoryRegionQuery.timeInvariant(region));
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    // owlyAnimationFrame.addBackground(RegionRenders.create(region));
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2NdTreeAnimationDemo().start().jFrame.setVisible(true);
  }
}
