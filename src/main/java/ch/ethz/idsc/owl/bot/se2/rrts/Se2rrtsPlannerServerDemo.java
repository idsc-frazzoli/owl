// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.sample.SphereRandomSample;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.RrtsNodeCollections;
import ch.ethz.idsc.owl.rrts.RrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;

/* package */ enum Se2rrtsPlannerServerDemo {
  ;
  private static final Random RANDOM = new Random();

  public static void main(String[] args) throws Exception {
    ImageRegion imageRegion = //
        ImageRegions.loadFromRepository("/io/track0_100.png", Tensors.vector(7, 7), false);
    Tensor lbounds = imageRegion.origin().copy().append(RealScalar.ZERO).unmodifiable();
    Tensor ubounds = imageRegion.range().copy().append(Pi.TWO).unmodifiable();
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery( //
        imageRegion, RealScalar.of(0.05));
    TransitionSpace transitionSpace = ClothoidTransitionSpace.INSTANCE;
    // ---
    RrtsPlannerServer server = new RrtsPlannerServer( //
        transitionSpace, //
        transitionRegionQuery, //
        RationalScalar.of(1, 10), //
        Se2StateSpaceModel.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return RrtsNodeCollections.clothoid(lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return BoxRandomSample.of(lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return SphereRandomSample.of(goal, RealScalar.ONE);
      }
    };
    // ---
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(60, 477);
    owlyFrame.jFrame.setBounds(100, 100, 550, 550);
    owlyFrame.addBackground(RegionRenders.create(imageRegion));
    StateTime stateTime = new StateTime(lbounds, RealScalar.ZERO);
    Tensor goal = BoxRandomSample.of(lbounds, ubounds).randomSample(RANDOM);
    Tensor trajectory = Tensors.empty();
    int frame = 0;
    while (frame++ < 5 && owlyFrame.jFrame.isVisible()) {
      server.setGoal(goal);
      server.insertRoot(stateTime);
      server.setState(stateTime);
      new Expand<>(server).steps(200);
      owlyFrame.setRrts(transitionSpace, server.getRoot().get(), transitionRegionQuery);
      Optional<List<TrajectorySample>> optional = server.getTrajectory();
      if (optional.isPresent()) {
        optional.get().stream().map(TrajectorySample::stateTime).map(StateTime::state).map(Extract2D.FUNCTION).forEach(trajectory::append);
        owlyFrame.geometricComponent.addRenderInterface(new RenderInterface() {
          @Override
          public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
            Path2D path = geometricLayer.toPath2D(trajectory);
            graphics.setStroke(new BasicStroke(2));
            graphics.setColor(Color.BLACK);
            graphics.draw(path);
          }
        });
        owlyFrame.geometricComponent.jComponent.repaint();
        // ---
        stateTime = Lists.getLast(optional.get()).stateTime();
        goal = BoxRandomSample.of(lbounds, ubounds).randomSample(RANDOM);
      }
      System.out.println(frame + "/" + 5);
      Thread.sleep(10);
    }
  }
}
