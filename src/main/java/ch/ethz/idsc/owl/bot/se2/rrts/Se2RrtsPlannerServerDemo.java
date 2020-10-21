// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.tree.Expand;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.DefaultRrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.RrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.sample.BallRandomSample;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Append;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.num.Pi;

/* package */ enum Se2RrtsPlannerServerDemo {
  ;
  public static void main(String[] args) throws Exception {
    Tensor range = Tensors.vector(7, 7).unmodifiable();
    Region<Tensor> imageRegion = //
        ImageRegions.loadFromRepository("/io/track0_100.png", range, false);
    Tensor lbounds = Array.zeros(2).unmodifiable();
    Tensor ubounds = range.unmodifiable();
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery( //
        imageRegion, RealScalar.of(0.05));
    TransitionSpace transitionSpace = ClothoidTransitionSpace.ANALYTIC;
    // ---
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        Append.of(lbounds, Pi.VALUE.negate()), //
        Append.of(ubounds, Pi.VALUE));
    RrtsPlannerServer server = new DefaultRrtsPlannerServer( //
        transitionSpace, //
        transitionRegionQuery, //
        RationalScalar.of(1, 10), //
        Se2StateSpaceModel.INSTANCE, //
        LengthCostFunction.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return Se2RrtsNodeCollections.of(transitionSpace, lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return randomSampleInterface;
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return BallRandomSample.of(goal, RealScalar.ONE);
      }

      @Override
      protected Tensor uBetween(StateTime orig, StateTime dest) {
        return CarRrtsFlow.uBetween(orig, dest);
      }
    };
    // ---
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(60, 477);
    owlyFrame.jFrame.setBounds(100, 100, 550, 550);
    owlyFrame.addBackground(RegionRenders.create(imageRegion));
    StateTime stateTime = new StateTime(Append.of(lbounds, RealScalar.ZERO), RealScalar.ZERO);
    Tensor goal = RandomSample.of(randomSampleInterface);
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
        optional.get().stream().map(TrajectorySample::stateTime).map(StateTime::state).forEach(trajectory::append);
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
        goal = RandomSample.of(randomSampleInterface);
      }
      System.out.println(frame + "/" + 5);
      Thread.sleep(10);
    }
  }
}
