package me.jiangew.dodekatheon.minerva.features.stream;

public class DefaultMethodInterfaceExample {

  interface Mathoperation {
    double calculate(int a);

    default double sqrt(int a) {
      return Math.sqrt(a);
    }
  }

  public static void main(String[] args) {
    Mathoperation operation =
        new Mathoperation() {
          @Override
          public double calculate(int a) {
            return sqrt(a);
          }
        };

    double a = operation.calculate(100);
    double b = operation.sqrt(16);

    System.out.println(String.format("calculate: %s, sqrt: %s", a, b));
  }
}
