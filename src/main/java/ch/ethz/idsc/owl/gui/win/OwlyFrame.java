// code by jph and jl
package ch.ethz.idsc.owl.gui.win;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.RenderElements;
import ch.ethz.idsc.owl.gui.ren.TransitionRender;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.io.Serialization;

public class OwlyFrame extends BaseFrame {
  private boolean replay = false;
  private int replayIndex = 0;
  private final List<TrajectoryPlanner> backup = new ArrayList<>();
  private final JSlider jSlider = new JSlider();

  public OwlyFrame() {
    {
      JToggleButton jToggleButton = new JToggleButton("Replay");
      jToggleButton.setToolTipText("stops LiveFeed and goes to Replaymode");
      jToggleButton.addActionListener(actionEvent -> replay = jToggleButton.isSelected());
      jToolBar.add(jToggleButton);
    }
    {
      JButton jButton = new JButton("<<");
      jButton.setToolTipText("Replay: 1 Step back");
      jButton.addActionListener(actionEvent -> {
        if (replayIndex > 0) {
          replayIndex = replayIndex - 1;
        } else {
          replayIndex = 0;
          System.err.println("GUI: Already displaying first Planningstep");
        }
        jSlider.setValue(replayIndex);
      });
      jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton(">>");
      jButton.setToolTipText("Replay: 1 Step forward");
      jButton.addActionListener(actionEvent -> {
        if (replayIndex < backup.size() - 1) {
          replayIndex = replayIndex + 1;
        } else {
          replayIndex = backup.size() - 1;
          System.err.println("GUI: Already displaying latest Planningstep");
        }
        jSlider.setValue(replayIndex);
      });
      jToolBar.add(jButton);
    }
    {
      jSlider.setOpaque(false);
      jSlider.addChangeListener(changeEvent -> {
        replayIndex = jSlider.getValue();
        repaint(replayIndex);
      });
      jToolBar.add(jSlider);
    }
  }

  public void setGlc(TrajectoryPlanner trajectoryPlanner) {
    try {
      backup.add(Serialization.copy(trajectoryPlanner));
      jSlider.setMaximum(backup.size() - 1);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    if (!replay) { // live feed
      replayIndex = backup.size() - 1;
      jSlider.setValue(replayIndex);
    }
  }

  private void repaint(int index) {
    if (0 <= index && index < backup.size())
      try {
        geometricComponent.setRenderInterfaces( //
            RenderElements.create(backup.get(index)));
        // jStatusLabel.setText(backup.get(index).infoString());
        geometricComponent.jComponent.repaint();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  public void setRrts(TransitionSpace transitionSpace, RrtsNode root, TransitionRegionQuery transitionRegionQuery) {
    try {
      Collection<RrtsNode> nodes = Nodes.ofSubtree(root);
      Collection<RrtsNode> collection = Serialization.copy(nodes);
      geometricComponent.setRenderInterfaces( //
          RenderElements.create(collection, Serialization.copy(transitionRegionQuery)));
      if (!transitionSpace.equals(RnTransitionSpace.INSTANCE))
        geometricComponent.addRenderInterface(new TransitionRender(transitionSpace).setCollection(collection));
      geometricComponent.jComponent.repaint();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void addBackground(RenderInterface renderInterface) {
    if (Objects.nonNull(renderInterface))
      geometricComponent.addRenderInterfaceBackground(renderInterface);
  }
}
