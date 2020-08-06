// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.lev.LogWeightingDemo;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

/* package */ abstract class ScatteredSetCoordinateDemo extends LogWeightingDemo {
  final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerMagnif = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  final JToggleButton jToggleHeatmap = new JToggleButton("heatmap");
  final JToggleButton jToggleArrows = new JToggleButton("arrows");

  public ScatteredSetCoordinateDemo( //
      boolean addRemoveControlPoints, //
      List<GeodesicDisplay> list, //
      List<LogWeighting> array) {
    super(addRemoveControlPoints, list, array);
    setMidpointIndicated(false);
    spinnerLogWeighting.addSpinnerListener(v -> recompute());
    {
      spinnerRefine.setList(Arrays.asList(3, 5, 10, 15, 20, 25, 30, 35, 40, 50, 60, 70, 80));
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

  final int refinement() {
    return spinnerRefine.getValue();
  }

  final int magnification() {
    return spinnerMagnif.getValue();
  }

  final ColorDataGradient colorDataGradient() {
    return spinnerColorData.getValue();
  }
}
