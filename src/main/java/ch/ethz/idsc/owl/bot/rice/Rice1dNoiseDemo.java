// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.R2NoiseRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.ren.VectorFieldRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.VectorFields;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Rice1dNoiseDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Scalar mu = RealScalar.ZERO;
    Collection<Flow> controls = Rice2Controls.create1d(mu, 15);
    owlyAnimationFrame.set(new Rice1dEntity(mu, Tensors.vector(0, 0), controls));
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.5));
    owlyAnimationFrame.setObstacleQuery(SimpleTrajectoryRegionQuery.timeInvariant(region));
    // ---
    Tensor range = Tensors.vector(6, 1);
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface sampler = new BoxRandomSample(range.negate(), range);
    Tensor points = Tensor.of(RandomSample.of(sampler, 1000).stream());
    vectorFieldRender.uv_pairs = //
        VectorFields.of(Rice2StateSpaceModel.of(mu), points, Rice1dEntity.FALLBACK_CONTROL, RealScalar.of(0.2));
    owlyAnimationFrame.addBackground(vectorFieldRender);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice1dNoiseDemo().start().jFrame.setVisible(true);
  }
}
