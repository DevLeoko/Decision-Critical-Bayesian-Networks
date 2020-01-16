package io.dcbn.backend.evidenceFormula.services.visitors;

import io.dcbn.backend.evidenceFormulas.FormulaBaseVisitor;
import io.dcbn.backend.evidenceFormulas.FormulaParser.NumberAmbiguousExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.NumberBinaryExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.NumberLiteralExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.NumberParenthesisExpressionContext;

public class NumberVisitor extends FormulaBaseVisitor<Double> {

  @Override
  public Double visitNumberAmbiguousExpression(NumberAmbiguousExpressionContext ctx) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Double visitNumberBinaryExpression(NumberBinaryExpressionContext ctx) {
    double left = visit(ctx.left);
    double right = visit(ctx.right);

    switch (ctx.operator.getText()) {
      case "+": return left + right;
      case "-": return left - right;
      case "*": return left * right;
      case "/": return left / right;
      default: throw new IllegalArgumentException("Unknown operator: " + ctx.operator.getText());
    }
  }

  @Override
  public Double visitNumberLiteralExpression(NumberLiteralExpressionContext ctx) {
    return Double.parseDouble(ctx.numberLiteral().NUMBER().getText());
  }

  @Override
  public Double visitNumberParenthesisExpression(NumberParenthesisExpressionContext ctx) {
    return visit(ctx.numberExpression());
  }
}
