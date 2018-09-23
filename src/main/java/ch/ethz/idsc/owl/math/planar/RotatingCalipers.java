// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.ArcTan;

// TODO JAN
class RotatingCalipers {
  int minX;
  int minY;
  int maxX;
  int maxY;
  Scalar area;

  RotatingCalipers(Tensor tensor) {
    Tensor pX = tensor.get(Tensor.ALL, 0);
    Tensor pY = tensor.get(Tensor.ALL, 1);
    int minX = ArgMin.of(pX);
    int minY = ArgMin.of(pY);
    int maxX = ArgMax.of(pX);
    int maxY = ArgMax.of(pY);
    Scalar dx = pX.Get(maxX).subtract(pX.Get(minX));
    Scalar dy = pY.Get(maxY).subtract(pY.Get(minY));
    area = dx.multiply(dy);
    // System.out.println(minY + " " + maxY);
    // System.out.println(minX + " " + maxX);
    // MinMax minMax = MinMax.of(tensor);
    // System.out.println(minMax.min());
    // System.out.println(minMax.max());
    // return Times.of( //
    // Differences.of(minMax.min()).Get(0), //
    // Differences.of(minMax.max()).Get(0));
  }

  public static void main(String[] args) {
    Distribution distribution = UniformDistribution.unit();
    Tensor points = RandomVariate.of(distribution, 100, 2);
    // points = CirclePoints.of(5);
    Tensor hull = ConvexHull.of(points);
    // Tensor pX = hull.get(Tensor.ALL, 0);
    // Tensor pY = hull.get(Tensor.ALL, 1);
    // int minX = ArgMin.of(pX);
    // int minY = ArgMin.of(pY);
    // int maxX = ArgMax.of(pX);
    // int maxY = ArgMax.of(pY);
    // System.out.println(minY + " " + maxY);
    // System.out.println(minX + " " + maxX);
    Tensor dHull = Differences.of(hull.copy().append(hull.get(0)));
    Scalar min = DoubleScalar.POSITIVE_INFINITY;
    // int min_index = -1;
    for (int index = 0; index < hull.length(); ++index) {
      Scalar angle = ArcTan.of(dHull.Get(index, 0), dHull.Get(index, 1)).negate();
      // GeometricLayer.of(model2pixel);
      Tensor matrix = RotationMatrix.of(angle);
      Tensor rotated = Tensor.of(hull.stream().map(row -> matrix.dot(row)));
      // System.out.println(Pretty.of(Differences.of(rotated)));
      RotatingCalipers rotatingCalipers = new RotatingCalipers(rotated);
      if (Scalars.lessEquals(rotatingCalipers.area, min)) {
        min = rotatingCalipers.area;
        // min_index = index;
        System.out.println(min);
      }
      // System.out.println(eval(rotated));
    }
  }
}
