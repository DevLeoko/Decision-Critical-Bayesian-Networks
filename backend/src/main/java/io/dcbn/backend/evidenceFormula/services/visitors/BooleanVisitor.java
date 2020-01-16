package io.dcbn.backend.evidenceFormula.services.visitors;

import io.dcbn.backend.evidenceFormulas.FormulaBaseVisitor;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanAmbiguousLiteralExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanBinaryExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanComparisonExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanLiteralExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanNotExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanParenthesisExpressionContext;

public class BooleanVisitor extends FormulaBaseVisitor<Boolean> {

  @Override
  public Boolean visitBooleanAmbiguousLiteralExpression(
      BooleanAmbiguousLiteralExpressionContext ctx) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean visitBooleanParenthesisExpression(BooleanParenthesisExpressionContext ctx) {
    return visit(ctx.booleanExpression());
  }

  @Override
  public Boolean visitBooleanLiteralExpression(BooleanLiteralExpressionContext ctx) {
    String text = ctx.booleanLiteral().getText();
    return Boolean.parseBoolean(text);
  }

  @Override
  public Boolean visitBooleanBinaryExpression(BooleanBinaryExpressionContext ctx) {
    boolean left = visit(ctx.left);
    boolean right = visit(ctx.right);

    switch (ctx.operator.getText()) {
      case "&": return left && right;
      case "|": return left || right;
      default: throw new IllegalArgumentException("Unknown operator: " + ctx.operator.getText());
    }
  }

  @Override
  public Boolean visitBooleanComparisonExpression(BooleanComparisonExpressionContext ctx) {
    NumberVisitor visitor = new NumberVisitor();
    double left = visitor.visit(ctx.comparisonExpression().left);
    double right = visitor.visit(ctx.comparisonExpression().right);

    String operator = ctx.comparisonExpression().COMPARISON_OPERATOR().getText();
    switch (operator) {
      case "=": return left == right;
      case "!=": return left != right;
      case "<": return left < right;
      case "<=": return left <= right;
      case ">": return left > right;
      case ">=": return left >= right;
      default: throw new IllegalArgumentException("Unknown operator: " + operator);
    }
  }

  @Override
  public Boolean visitBooleanNotExpression(BooleanNotExpressionContext ctx) {
    return !visit(ctx.booleanExpression());
  }

}
