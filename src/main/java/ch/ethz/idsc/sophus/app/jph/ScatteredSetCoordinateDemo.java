// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

/* package */ abstract class ScatteredSetCoordinateDemo extends ControlPointsDemo {
  final SpinnerLabel<Supplier<BarycentricCoordinate>> spinnerBarycentric = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = new SpinnerLabel<>();
  final JToggleButton jToggleHeatmap = new JToggleButton("heatmap");
  final JToggleButton jToggleArrows = new JToggleButton("arrows");

  public ScatteredSetCoordinateDemo(boolean addRemoveControlPoints, List<GeodesicDisplay> list, Supplier<BarycentricCoordinate>[] array) {
    super(addRemoveControlPoints, list);
    {
      spinnerBarycentric.setArray(array);
      spinnerBarycentric.setIndex(0);
      spinnerBarycentric.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "barycentric");
    }
    {
      spinnerRefine.setList(Arrays.asList(3, 5, 10, 15, 20, 25, 30, 35, 40, 50));
      spinnerRefine.setValue(20);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
    }
    {
      spinnerColorData.setArray(ColorDataGradients.values());
      spinnerColorData.setIndex(0);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "color scheme");
    }
    {
      jToggleHeatmap.setSelected(true);
      timerFrame.jToolBar.add(jToggleHeatmap);
      jToggleArrows.setSelected(false);
      timerFrame.jToolBar.add(jToggleArrows);
    }
    timerFrame.jToolBar.addSeparator();
  }

  int refinement() {
    return spinnerRefine.getValue();
  }

  ColorDataGradient colorDataGradient() {
    return spinnerColorData.getValue();
  }

  BarycentricCoordinate barycentricCoordinate() {
    return spinnerBarycentric.getValue().get();
  }
}
