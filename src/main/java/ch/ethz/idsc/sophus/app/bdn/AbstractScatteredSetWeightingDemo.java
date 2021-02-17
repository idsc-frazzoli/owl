// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.lev.LogWeightingDemo;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.opt.LogWeighting;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

public abstract class AbstractScatteredSetWeightingDemo extends LogWeightingDemo {
  protected final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerMagnif = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  protected final JToggleButton jToggleHeatmap = new JToggleButton("heatmap");
  protected final JToggleButton jToggleArrows = new JToggleButton("arrows");

  public AbstractScatteredSetWeightingDemo( //
      boolean addRemoveControlPoints, //
      List<ManifoldDisplay> list, //
      List<LogWeighting> array) {
    super(addRemoveControlPoints, list, array);
    setMidpointIndicated(false);
    spinnerLogWeighting.addSpinnerListener(v -> recompute());
    {
      spinnerRefine.setList(Arrays.asList(3, 5, 10, 15, 20, 25, 30, 35, 40, 50, 60, 70, 80, 120, 160));
      spinnerRefine.setValue(20);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
      spinnerRefine.addSpinnerListener(v -> recompute());
    }
    {
      spinnerMagnif.setList(Arrays.asList(1, 2, 3, 4));
      spinnerMagnif.setValue(2);
      spinnerMagnif.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "magnify");
      spinnerMagnif.addSpinnerListener(v -> recompute());
    }
    spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "color scheme");
    spinnerColorData.addSpinnerListener(v -> recompute());
    {
      jToggleHeatmap.setSelected(true);
      timerFrame.jToolBar.add(jToggleHeatmap);
      jToggleArrows.setSelected(false);
      timerFrame.jToolBar.add(jToggleArrows);
    }
    // ---
    timerFrame.jToolBar.addSeparator();
  }

  protected final int refinement() {
    return spinnerRefine.getValue();
  }

  protected final int magnification() {
    return spinnerMagnif.getValue();
  }

  protected final ColorDataGradient colorDataGradient() {
    return spinnerColorData.getValue();
  }
}
