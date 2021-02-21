// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.math.var.Variograms;
import ch.ethz.idsc.sophus.opt.LogWeighting;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public abstract class LogWeightingDemo extends LogWeightingBase {
  private static final Tensor BETAS = Tensors.fromString("{0, 1/2, 1, 3/2, 7/4, 2, 5/2, 3}");
  // ---
  private final SpinnerLabel<Bitype> spinnerBiinvariant = new SpinnerLabel<>();
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
      logWeighting.equals(LogWeightings.COORDINATE) || //
      logWeighting.equals(LogWeightings.LAGRAINATE)) {
        spinnerVariogram.setValue(Variograms.INVERSE_POWER);
        spinnerBeta.setValueSafe(RealScalar.of(2));
      }
      if ( //
      logWeighting.equals(LogWeightings.KRIGING) || //
      logWeighting.equals(LogWeightings.KRIGING_COORDINATE)) {
        spinnerVariogram.setValue(Variograms.POWER);
        setBitype(Bitype.HARBOR);
        spinnerBeta.setValueSafe(RationalScalar.of(3, 2));
      }
    }
  };

  public LogWeightingDemo(boolean addRemoveControlPoints, List<ManifoldDisplay> list, List<LogWeighting> array) {
    super(addRemoveControlPoints, list, array);
    spinnerLogWeighting.addSpinnerListener(spinnerListener);
    {
      spinnerBiinvariant.setArray(Bitype.values());
      spinnerBiinvariant.setValue(Bitype.LEVERAGES1);
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
  }

  protected final void setBitype(Bitype biinvariant) {
    spinnerBiinvariant.setValue(biinvariant);
  }

  protected final Biinvariant biinvariant() {
    return bitype().from(manifoldDisplay());
  }

  protected final Bitype bitype() {
    return spinnerBiinvariant.getValue();
  }

  @Override
  protected final TensorUnaryOperator operator(VectorLogManifold vectorLogManifold, Tensor sequence) {
    return logWeighting().operator( //
        biinvariant(), //
        vectorLogManifold, //
        variogram(), //
        sequence);
  }

  protected final ScalarUnaryOperator variogram() {
    return spinnerVariogram.getValue().of(spinnerBeta.getValue());
  }

  @Override
  protected final TensorScalarFunction function(Tensor sequence, Tensor values) {
    return logWeighting().function( //
        biinvariant(), //
        manifoldDisplay().hsManifold(), //
        variogram(), //
        sequence, values);
  }

  @Override
  protected void recompute() {
    // ---
  }
}
