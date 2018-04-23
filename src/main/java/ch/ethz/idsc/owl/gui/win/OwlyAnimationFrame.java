// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.data.TimeKeeper;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.AnimationInterface;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.ren.EtaRender;
import ch.ethz.idsc.owl.gui.ren.GoalRender;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

public class OwlyAnimationFrame extends TimerFrame {
  private static final Dimension RECORDING = new Dimension(400, 400);
  private static final int MARGIN = 100; // 170;
  // ---
  private final EtaRender etaRender = new EtaRender(Tensors.empty());
  private final GoalRender goalRender = new GoalRender(null);
  private final TreeRender treeRender = new TreeRender(null);
  private final List<AnimationInterface> animationInterfaces = new CopyOnWriteArrayList<>();
  /** reference to the entity that is controlled by the user */
  private AnimationInterface controllable = null;
  /** the obstacle query is set in {@link #setPlannerConstraint(TrajectoryRegionQuery)}
   * it is intentionally set to null here lest the application forget */
  MousePlanner mousePlanner = new MousePlanner();
  public final DefTrPlCall trajectoryPlannerCallback = new DefTrPlCall();
  private final JToggleButton jToggleButtonRecord = new JToggleButton("record");

  public OwlyAnimationFrame() {
    geometricComponent.addRenderInterface(GridRender.INSTANCE);
    geometricComponent.addRenderInterface(etaRender);
    geometricComponent.addRenderInterface(goalRender);
    geometricComponent.addRenderInterface(treeRender);
    { // periodic task for integration
      final TimerTask timerTask = new TimerTask() {
        TimeKeeper timeKeeper = new TimeKeeper();

        @Override
        public void run() {
          Scalar now = timeKeeper.now();
          animationInterfaces.forEach(ani -> ani.integrate(now));
        }
      };
      timer.schedule(timerTask, 100, 20);
    }
    {
      jToggleButtonRecord.addActionListener(new ActionListener() {
        TimerTask timerTask;

        @Override
        public void actionPerformed(ActionEvent event) {
          boolean selected = jToggleButtonRecord.isSelected();
          if (selected) {
            TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
            File directory = UserHome.Pictures(abstractEntity.getClass().getSimpleName() + "_" + System.currentTimeMillis());
            directory.mkdir();
            GlobalAssert.that(directory.isDirectory());
            timerTask = new TimerTask() {
              int count = 0;
              Point point = null;

              @Override
              public void run() {
                BufferedImage offscreen = offscreen();
                StateTime stateTime = abstractEntity.getStateTimeNow();
                Point now = geometricComponent.toPixel(stateTime.state());
                if (Objects.isNull(point) || MARGIN < PointUtil.inftyNorm(point, now))
                  point = now;
                Dimension dimension = RECORDING;
                BufferedImage bufferedImage = //
                    new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
                bufferedImage.getGraphics().drawImage(offscreen, //
                    dimension.width / 2 - point.x, //
                    dimension.height / 2 - point.y, null);
                try {
                  ImageIO.write(bufferedImage, IMAGE_FORMAT, new File(directory, //
                      String.format("owly_%05d.%s", count++, IMAGE_FORMAT)));
                } catch (Exception exception) {
                  exception.printStackTrace();
                }
              }
            };
            timer.schedule(timerTask, 100, 100);
          } else
            timerTask.cancel();
        }
      });
      jToolBar.add(jToggleButtonRecord);
    }
    // ---
    mousePlanner.geometricComponent = geometricComponent; // FIXME cyclic dependency !?!?!
    mousePlanner.trajectoryPlannerCallback = trajectoryPlannerCallback;
    geometricComponent.jComponent.addMouseListener(mousePlanner);
  }

  public void set(AnimationInterface animationInterface) {
    GlobalAssert.that(animationInterfaces.isEmpty()); // TODO this logic is messy
    mousePlanner.controllable = animationInterface;
    trajectoryPlannerCallback.controllable = animationInterface;
    if (Objects.isNull(controllable))
      controllable = animationInterface;
    add(animationInterface);
  }

  /** modifies the obstacle region in between mouse-clicks
   * (so far only relevant for the standard planner)
   * 
   * @param plannerConstraint */
  public void setPlannerConstraint(PlannerConstraint plannerConstraint) {
    mousePlanner.plannerConstraint = plannerConstraint;
  }

  /** @param renderInterface */
  public void addBackground(RenderInterface renderInterface) {
    geometricComponent.addRenderInterfaceBackground(renderInterface);
  }

  public void add(AnimationInterface animationInterface) {
    animationInterfaces.add(animationInterface);
    if (animationInterface instanceof RenderInterface) {
      RenderInterface renderInterface = (RenderInterface) animationInterface;
      geometricComponent.addRenderInterface(renderInterface);
    }
  }
}
