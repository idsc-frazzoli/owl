// code by jph
package ch.ethz.idsc.owl.bot.kl;

/* package */ enum Block {
  _b22(2, 2), // 0
  _b21(2, 1), // 1
  _b12(1, 2), // 2
  _b11(1, 1), // 3
  _b31(3, 1), // 4
  _b55(5, 5), // 5
  _cd2(2, 2), // 6
  _cu2(2, 2), // 7
  _x55(5, 5), // 8
  ;

  public final int wx;
  public final int wy;

  Block(int wx, int wy) {
    this.wx = wx;
    this.wy = wy;
  }
}
