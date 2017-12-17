//// code by jph
// package ch.ethz.idsc.owl.gui.ren;
//
// import java.awt.Color;
// import java.awt.Graphics2D;
// import java.awt.geom.Path2D;
//
// import ch.ethz.idsc.owl.gui.RenderInterface;
// import ch.ethz.idsc.owl.gui.win.GeometricLayer;
// import ch.ethz.idsc.owl.math.map.Se2Utils;
// import ch.ethz.idsc.tensor.Tensor;
// import ch.ethz.idsc.tensor.Tensors;
// import ch.ethz.idsc.tensor.alg.Subdivide;
// import ch.ethz.idsc.tensor.lie.CirclePoints;
// import ch.ethz.idsc.tensor.opt.BSplineFunction;
//
// public class CurveRender implements RenderInterface {
// public static final Tensor TEST1 = Tensors.fromString("{{0,1},{1,0},{2,0},{3,0},{4,0}}");
// public static final Tensor TEST2 = Tensors.fromString("{{0,0},{1,0},{2,0},{3,0},{4,1}}");
// BSplineFunction bSplineFunction1 = BSplineFunction.of(1, CirclePoints.of(5));
// BSplineFunction bSplineFunction2 = BSplineFunction.of(2, CirclePoints.of(5));
// BSplineFunction bSplineFunction3 = BSplineFunction.of(3, CirclePoints.of(5));
// BSplineFunction bSplineT1 = BSplineFunction.of(2, CirclePoints.of(4));
// BSplineFunction bSplineT2 = BSplineFunction.of(2, CirclePoints.of(4));
// BSplineFunction bSplineL1 = BSplineFunction.of(1, CirclePoints.of(4));
// BSplineFunction bSplineL2 = BSplineFunction.of(1, CirclePoints.of(4));
//
// @Override
// public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
// geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(Tensors.vector(0, -1, 0)));
// {
// Tensor polygon = Subdivide.of(0, 4, 100).map(bSplineFunction1);
// Path2D path = geometricLayer.toPath2D(polygon);
// graphics.setColor(Color.BLUE);
// graphics.draw(path);
// }
// {
// Tensor polygon = Subdivide.of(0, 4, 100).map(bSplineFunction2);
// Path2D path = geometricLayer.toPath2D(polygon);
// graphics.setColor(Color.RED);
// graphics.draw(path);
// }
// {
// Tensor polygon = Subdivide.of(0, 4, 100).map(bSplineFunction3);
// Path2D path = geometricLayer.toPath2D(polygon);
// graphics.setColor(Color.GREEN);
// graphics.draw(path);
// }
// geometricLayer.popMatrix();
// geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(Tensors.vector(5, -5, 0)));
// {
// Tensor polygon = Subdivide.of(0, 3, 100).map(bSplineT1);
// Path2D path = geometricLayer.toPath2D(polygon);
// graphics.setColor(Color.BLACK);
// graphics.draw(path);
// }
// {
// Tensor polygon = Subdivide.of(0, 3, 100).map(bSplineT2);
// Path2D path = geometricLayer.toPath2D(polygon);
// graphics.setColor(Color.BLACK);
// graphics.draw(path);
// }
// {
// Tensor polygon = Subdivide.of(0, 3, 100).map(bSplineL1);
// Path2D path = geometricLayer.toPath2D(polygon);
// graphics.setColor(Color.BLUE);
// graphics.draw(path);
// }
// {
// Tensor polygon = Subdivide.of(0, 3, 100).map(bSplineL2);
// Path2D path = geometricLayer.toPath2D(polygon);
// graphics.setColor(Color.BLUE);
// graphics.draw(path);
// }
// geometricLayer.popMatrix();
// }
// }
