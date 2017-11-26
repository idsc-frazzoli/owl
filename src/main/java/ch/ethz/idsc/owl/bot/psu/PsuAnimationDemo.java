// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.owl.gui.ren.VectorFieldRender;
import ch.ethz.idsc.owl.math.VectorFields;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.state.EmptyTrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class PsuAnimationDemo implements DemoInterface {
  @Override
  public void start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.set(new PsuEntity());
    owlyAnimationFrame.setObstacleQuery(EmptyTrajectoryRegionQuery.INSTANCE);
    // ---
    Tensor range = Tensors.vector(Math.PI, 3);
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface sampler = new BoxRandomSample(range.negate(), range);
    Tensor points = Tensor.of(RandomSample.of(sampler, 1000).stream());
    vectorFieldRender.uv_pairs = //
        VectorFields.of(PsuStateSpaceModel.INSTANCE, points, PsuEntity.FALLBACK_CONTROL, RealScalar.of(0.1));
    owlyAnimationFrame.addBackground(vectorFieldRender);
    // ---
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new PsuAnimationDemo().start();
  }
}
