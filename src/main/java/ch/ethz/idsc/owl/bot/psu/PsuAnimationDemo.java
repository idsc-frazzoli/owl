// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ren.VectorFieldRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.VectorFields;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.state.EmptyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class PsuAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        new StateTime(Tensors.vector(0, 0), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new PsuTrajectoryControl();
    owlyAnimationFrame.set(new PsuEntity(episodeIntegrator, trajectoryControl));
    owlyAnimationFrame.setObstacleQuery(EmptyTrajectoryRegionQuery.INSTANCE);
    // ---
    Tensor range = Tensors.vector(Math.PI, 3);
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface sampler = new BoxRandomSample(range.negate(), range);
    Tensor points = Tensor.of(RandomSample.of(sampler, 1000).stream());
    vectorFieldRender.uv_pairs = //
        VectorFields.of(PsuStateSpaceModel.INSTANCE, points, PsuTrajectoryControl.FALLBACK_CONTROL, RealScalar.of(0.1));
    owlyAnimationFrame.addBackground(vectorFieldRender);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new PsuAnimationDemo().start().jFrame.setVisible(true);
  }
}
