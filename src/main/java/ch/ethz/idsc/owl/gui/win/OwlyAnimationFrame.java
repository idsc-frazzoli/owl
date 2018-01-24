// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.data.TimeKeeper;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.AbstractRrtsEntity;
import ch.ethz.idsc.owl.gui.ani.AnimationInterface;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.ani.TrajectoryPlannerCallback;
import ch.ethz.idsc.owl.gui.ren.EtaRender;
import ch.ethz.idsc.owl.gui.ren.GoalRender;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class OwlyAnimationFrame extends TimerFrame {
  private static final Dimension RECORDING = new Dimension(400, 400);
  private static final int MARGIN = 100; // 170;
  // ---
  private final EtaRender etaRender = new EtaRender(Tensors.empty());
  private final TrajectoryRender trajectoryRender = new TrajectoryRender();
  private final GoalRender goalRender = new GoalRender(null);
  private final TreeRender treeRender = new TreeRender(null);
  private final List<AnimationInterface> animationInterfaces = new CopyOnWriteArrayList<>();
  /** reference to the entity that is controlled by the user */
  private AnimationInterface controllable = null;
  /** the obstacle query is set in {@link #setObstacleQuery(TrajectoryRegionQuery)}
   * it is intentionally set to null here lest the application forget */
  private TrajectoryRegionQuery obstacleQuery = null;
  private final JToggleButton jToggleButtonRecord = new JToggleButton("record");

  public OwlyAnimationFrame() {
    geometricComponent.addRenderInterface(GridRender.INSTANCE);
    geometricComponent.addRenderInterface(etaRender);
    geometricComponent.addRenderInterface(trajectoryRender);
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
    geometricComponent.jComponent.addMouseListener(new MouseAdapter() {
      MotionPlanWorker mpw = null;

      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        final int mods = mouseEvent.getModifiersEx();
        final int mask = MouseWheelEvent.CTRL_DOWN_MASK; // 128 = 2^7
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
          if ((mods & mask) == 0) { // no ctrl pressed
            if (Objects.nonNull(mpw)) {
              mpw.flagShutdown();
              mpw = null;
            }
            if (controllable instanceof TrajectoryEntity) {
              TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
              final Tensor goal = geometricComponent.getMouseSe2State();
              final List<TrajectorySample> head = //
                  abstractEntity.getFutureTrajectoryUntil(abstractEntity.delayHint());
              switch (abstractEntity.getPlannerType()) {
              case STANDARD: {
                TrajectoryPlanner trajectoryPlanner = //
                    abstractEntity.createTrajectoryPlanner(obstacleQuery, goal);
                mpw = new MotionPlanWorker();
                mpw.addCallback(trajectoryPlannerCallback);
                if (Objects.nonNull(trajectoryPlannerCallbackExtra))
                  mpw.addCallback(trajectoryPlannerCallbackExtra);
                mpw.start(head, trajectoryPlanner);
                break;
              }
              case RRTS: {
                AbstractRrtsEntity abstractRrtsEntity = (AbstractRrtsEntity) abstractEntity;
                abstractRrtsEntity.startPlanner(trajectoryPlannerCallback, head, goal);
                break;
              }
              default:
                throw new RuntimeException();
              }
            }
          } else { // ctrl pressed
            System.out.println(geometricComponent.getMouseSe2State());
            if (controllable instanceof TrajectoryEntity) {
              @SuppressWarnings("unused")
              TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
              // abstractEntity.resetStateTo(owlyComponent.getMouseGoal());
            }
          }
        }
      }
    });
  }

  public TrajectoryPlannerCallback trajectoryPlannerCallbackExtra = null;
  public final TrajectoryPlannerCallback trajectoryPlannerCallback = new TrajectoryPlannerCallback() {
    @Override
    public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
      etaRender.setEta(trajectoryPlanner.getEta());
      Optional<GlcNode> optional = GlcNodes.getFinalGoalNode(trajectoryPlanner);
      // test without heuristic
      if (optional.isPresent()) {
        List<TrajectorySample> trajectory = new ArrayList<>();
        if (controllable instanceof TrajectoryEntity) {
          TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
          List<TrajectorySample> tail = //
              GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
          // Optional<GlcNode> temp = trajectoryPlanner.getBestOrElsePeek();
          // List<StateTime> tempList = GlcNodes.getPathFromRootTo(temp.get());
          // System.out.println("Root is: " + tempList.get(0).toInfoString());
          // System.out.println("TAIL: <<<<<<<");
          // Trajectories.print(tail);
          trajectory = Trajectories.glue(head, tail);
          abstractEntity.setTrajectory(trajectory);
        }
        trajectoryRender.setTrajectory(trajectory);
      } else {
        System.err.println("NO TRAJECTORY BETWEEN ROOT TO GOAL");
      }
      goalRender.fromStateTimeCollector(trajectoryPlanner.getGoalInterface());
      treeRender.setCollection(new ArrayList<>(trajectoryPlanner.getDomainMap().values()));
      // no repaint
    }

    @Override
    public void expandResult(List<TrajectorySample> head, RrtsPlanner rrtsPlanner, List<TrajectorySample> tail) {
      List<TrajectorySample> trajectory = new ArrayList<>();
      if (controllable instanceof TrajectoryEntity) {
        TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
        trajectory = Trajectories.glue(head, tail);
        abstractEntity.setTrajectory(trajectory);
      }
      trajectoryRender.setTrajectory(trajectory);
      if (rrtsPlanner.getBest().isPresent()) {
        RrtsNode root = Nodes.rootFrom(rrtsPlanner.getBest().get());
        Collection<RrtsNode> collection = Nodes.ofSubtree(root);
        treeRender.setCollection(collection);
      }
    }
  };

  public void set(AnimationInterface animationInterface) {
    GlobalAssert.that(animationInterfaces.isEmpty()); // TODO this logic is messy
    if (Objects.isNull(controllable))
      controllable = animationInterface;
    add(animationInterface);
  }

  /** modifies the obstacle region in between mouse-clicks
   * (so far only relevant for the standard planner)
   * 
   * @param obstacleQuery */
  public void setObstacleQuery(TrajectoryRegionQuery obstacleQuery) {
    this.obstacleQuery = obstacleQuery;
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
