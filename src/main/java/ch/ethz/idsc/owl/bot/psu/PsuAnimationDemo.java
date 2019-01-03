// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.ren.VectorFieldRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.VectorFields;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

public class PsuAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        new StateTime(Tensors.vector(0, 0), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new PsuTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new PsuEntity(episodeIntegrator, trajectoryControl);
    owlyAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlyAnimationFrame, trajectoryEntity, EmptyObstacleConstraint.INSTANCE);
    // ---
    Tensor range = Tensors.vector(Math.PI, 3);
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(range.negate(), range);
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    vectorFieldRender.uv_pairs = //
        VectorFields.of(PsuStateSpaceModel.INSTANCE, points, Array.zeros(1), RealScalar.of(0.1));
    owlyAnimationFrame.addBackground(vectorFieldRender);
    owlyAnimationFrame.addBackground(GridRender.INSTANCE);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new PsuAnimationDemo().start().jFrame.setVisible(true);
  }
}
