// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;

abstract class BaseCurvatureDemo extends CurvatureDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  // ---
  final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  final JToggleButton jToggleSymi = new JToggleButton("graph");
  final JSlider jSlider = new JSlider(0, 1000, 500);

  public BaseCurvatureDemo() {
    this(GeodesicDisplays.ALL);
  }

  public BaseCurvatureDemo(List<GeodesicDisplay> list) {
    super(list);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(3);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(5);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    jToggleSymi.setSelected(true);
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
  }
}
