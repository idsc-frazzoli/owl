// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.api.Variograms;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.Biinvariants;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public abstract class LogWeightingDemo extends AbstractPlaceDemo {
  private static final Tensor BETAS = Tensors.fromString("{0, 1/2, 1, 3/2, 7/4, 2, 5/2, 3}");
  // ---
  protected final SpinnerLabel<LogWeighting> spinnerLogWeighting = new SpinnerLabel<>();
  private final SpinnerLabel<Biinvariant> spinnerBiinvariant = new SpinnerLabel<>();
  private final SpinnerLabel<Variograms> spinnerVariogram = SpinnerLabel.of(Variograms.values());
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final SpinnerListener<LogWeighting> spinnerListener = new SpinnerListener<LogWeighting>() {
    @Override
    public void actionPerformed(LogWeighting logWeighting) {
      {
        boolean enabled = !logWeighting.equals(LogWeightings.DISTANCES);
        spinnerVariogram.setEnabled(enabled);
        spinnerBeta.setEnabled(enabled);
      }
      if (logWeighting.equals(LogWeightings.DISTANCES)) {
        spinnerVariogram.setValue(Variograms.POWER);
        spinnerBeta.setValueSafe(RealScalar.of(1));
      }
      if ( //
      logWeighting.equals(LogWeightings.WEIGHTING) || //
      logWeighting.equals(LogWeightings.COORDINATE)) {
        spinnerVariogram.setValue(Variograms.INVERSE_POWER);
        spinnerBeta.setValueSafe(RealScalar.of(2));
      }
      if ( //
      logWeighting.equals(LogWeightings.KRIGING) || //
      logWeighting.equals(LogWeightings.KRIGING_COORDINATE)) {
        spinnerVariogram.setValue(Variograms.POWER);
        setBiinvariant(Biinvariants.HARBOR);
        spinnerBeta.setValueSafe(RationalScalar.of(3, 2));
      }
    }
  };

  public LogWeightingDemo(boolean addRemoveControlPoints, List<GeodesicDisplay> list, List<LogWeighting> array) {
    super(addRemoveControlPoints, list);
    {
      spinnerLogWeighting.setList(array);
      if (array.contains(LogWeightings.COORDINATE))
        spinnerLogWeighting.setValue(LogWeightings.COORDINATE);
      else
        spinnerLogWeighting.setIndex(0);
      spinnerLogWeighting.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "weights");
      spinnerLogWeighting.addSpinnerListener(spinnerListener);
    }
    {
      spinnerBiinvariant.setArray(Biinvariants.values());
      spinnerBiinvariant.setValue(Biinvariants.TARGET);
      spinnerBiinvariant.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "distance");
      spinnerBiinvariant.addSpinnerListener(v -> recompute());
    }
    spinnerVariogram.addToComponentReduced(timerFrame.jToolBar, new Dimension(230, 28), "variograms");
    spinnerVariogram.addSpinnerListener(v -> recompute());
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setValue(RealScalar.of(2));
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
      spinnerBeta.addSpinnerListener(v -> recompute());
    }
    timerFrame.jToolBar.addSeparator();
    // ---
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()) {
        case MouseEvent.BUTTON1: // insert point
          if (!isPositioningOngoing())
            recompute();
          break;
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        if (isPositioningOngoing())
          recompute();
      }
    };
    // ---
    timerFrame.geometricComponent.jComponent.addMouseListener(mouseAdapter);
    timerFrame.geometricComponent.jComponent.addMouseMotionListener(mouseAdapter);
  }

  protected final LogWeighting logWeighting() {
    return spinnerLogWeighting.getValue();
  }

  protected final void setLogWeighting(LogWeighting logWeighting) {
    spinnerLogWeighting.setValue(logWeighting);
    spinnerListener.actionPerformed(logWeighting);
  }

  protected final void setBiinvariant(Biinvariant biinvariant) {
    spinnerBiinvariant.setValue(biinvariant);
  }

  protected final Biinvariant biinvariant() {
    return spinnerBiinvariant.getValue();
  }

  protected final TensorUnaryOperator operator(VectorLogManifold vectorLogManifold, Tensor sequence) {
    return logWeighting().operator( //
        biinvariant(), //
        vectorLogManifold, //
        variogram(), //
        sequence);
  }

  protected final TensorUnaryOperator operator(Tensor sequence) {
    return operator(geodesicDisplay().vectorLogManifold(), sequence);
  }

  protected final ScalarUnaryOperator variogram() {
    return spinnerVariogram.getValue().of(spinnerBeta.getValue());
  }

  protected final TensorScalarFunction function(Tensor sequence, Tensor values) {
    return logWeighting().function( //
        biinvariant(), //
        geodesicDisplay().vectorLogManifold(), //
        variogram(), //
        sequence, values);
  }

  protected void recompute() {
    // ---
  }
}
