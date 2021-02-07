// code by jph
package ch.ethz.idsc.sophus.app.analysis;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.MeanDefect;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringManifold;
import ch.ethz.idsc.sophus.ref.d1.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.Dual3PointCurveSubdivision;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum BSpline4SplitComparisonDemo {
  ;
  public static void main(String[] args) {
    Dual3PointCurveSubdivision d0 = //
        (Dual3PointCurveSubdivision) BSpline4CurveSubdivision.split3(RnGeodesic.INSTANCE);
    Tensor weights_lo = Join.of( //
        d0.lo(Tensors.vector(1), Tensors.vector(0), Tensors.vector(0)), //
        d0.lo(Tensors.vector(0), Tensors.vector(1), Tensors.vector(0)), //
        d0.lo(Tensors.vector(0), Tensors.vector(0), Tensors.vector(1)));
    Tensor weights_hi = Join.of( //
        d0.hi(Tensors.vector(1), Tensors.vector(0), Tensors.vector(0)), //
        d0.hi(Tensors.vector(0), Tensors.vector(1), Tensors.vector(0)), //
        d0.hi(Tensors.vector(0), Tensors.vector(0), Tensors.vector(1)));
    // System.out.println(weights_lo);
    // System.out.println(hi);
    Dual3PointCurveSubdivision d1 = //
        (Dual3PointCurveSubdivision) BSpline4CurveSubdivision.split2lo(Se2CoveringGeodesic.INSTANCE);
    Dual3PointCurveSubdivision d2 = //
        (Dual3PointCurveSubdivision) BSpline4CurveSubdivision.split3(Se2CoveringGeodesic.INSTANCE);
    Dual3PointCurveSubdivision d3 = //
        (Dual3PointCurveSubdivision) BSpline4CurveSubdivision.split2hi(Se2CoveringGeodesic.INSTANCE);
    BiinvariantMean biinvariantMean = Se2CoveringBiinvariantMean.INSTANCE;
    Distribution distribution = UniformDistribution.of(-Math.PI / 2, Math.PI / 2);
    int[] wint = new int[3];
    Tensor ert = Array.zeros(3);
    int[] winm = new int[3];
    Tensor erm = Array.zeros(3);
    for (int count = 0; count < 10000; ++count) {
      final Tensor p = RandomVariate.of(distribution, 3);
      final Tensor q = RandomVariate.of(distribution, 3);
      final Tensor r = RandomVariate.of(distribution, 3);
      final Tensor sequence = Unprotect.byRef(p, q, r);
      {
        Tensor m = biinvariantMean.mean(sequence, weights_lo);
        Tensor m1 = d1.lo(p, q, r);
        Tensor m2 = d2.lo(p, q, r);
        Tensor m3 = d3.lo(p, q, r);
        // ---
        {
          Tensor v1 = new MeanDefect(sequence, weights_lo, Se2CoveringManifold.INSTANCE.exponential(m1)).tangent();
          Tensor v2 = new MeanDefect(sequence, weights_lo, Se2CoveringManifold.INSTANCE.exponential(m2)).tangent();
          Tensor v3 = new MeanDefect(sequence, weights_lo, Se2CoveringManifold.INSTANCE.exponential(m3)).tangent();
          Tensor err = Tensors.of( //
              Norm._2.ofVector(v1), //
              Norm._2.ofVector(v2), //
              Norm._2.ofVector(v3)); //
          ++winm[ArgMin.of(err)];
          erm = erm.add(err);
        }
        Tensor err = Tensors.of( //
            Norm._2.between(m1, m), //
            Norm._2.between(m2, m), //
            Norm._2.between(m3, m));
        ++wint[ArgMin.of(err)];
        ert = ert.add(err);
      }
      {
        Tensor m = biinvariantMean.mean(sequence, weights_hi);
        Tensor m1 = d1.hi(p, q, r);
        Tensor m2 = d2.hi(p, q, r);
        Tensor m3 = d3.hi(p, q, r);
        // ---
        Tensor err = Tensors.of( //
            Norm._2.between(m1, m), //
            Norm._2.between(m2, m), //
            Norm._2.between(m3, m));
        ert = ert.add(err);
        ++wint[ArgMin.of(err)];
      }
    }
    System.out.println("---");
    System.out.println("wins             =" + Tensors.vectorInt(wint));
    System.out.println("translation error=" + ert.map(Round._3));
    System.out.println("wins             =" + Tensors.vectorInt(winm));
    System.out.println("mean defect error=" + erm.map(Round._3));
  }
}
