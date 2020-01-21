package io.dcbn.backend.evidenceFormula.services.visitors;

import io.dcbn.backend.evidenceFormulas.FormulaBaseVisitor;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanAmbiguousLiteralExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanBinaryExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanComparisonExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanLiteralExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanNotExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.BooleanParenthesisExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.FormulaContext;
import java.util.Map;

/**
 * Visitor responsible for evaluating boolean expressions.
 * The methods in this class are responsible for visiting the corresponding rules in the grammar.
 * They will not be documented in more detail.
 */
public class BooleanVisitor extends FormulaBaseVisitor<Boolean> {

  private Map<String, Object> variables;
  private Map<String, FunctionWrapper> functions;

  public BooleanVisitor(Map<String, Object> variables,
      Map<String, FunctionWrapper> functions) {
    this.variables = variables;
    this.functions = functions;
  }

  @Override
  public Boolean visitFormula(FormulaContext ctx) {
    return visit(ctx.booleanExpression());
  }

  @Override
  public Boolean visitBooleanAmbiguousLiteralExpression(
      BooleanAmbiguousLiteralExpressionContext ctx) {
    AmbiguityVisitor visitor = new AmbiguityVisitor(variables, functions);
    Object result = visitor.visit(ctx.ambiguousLiteral());

    if (result instanceof Boolean) {
      return (Boolean) result;
    } else {
      throw new IllegalArgumentException(ctx.ambiguousLiteral().getText() + " did't evaluate to boolean!");
    }
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
    Boolean left = visit(ctx.left);
    Boolean right = visit(ctx.right);

    switch (ctx.operator.getText()) {
      case "&": return left && right;
      case "|": return left || right;
      default: throw new IllegalArgumentException("Unknown operator: " + ctx.operator.getText()); // Should never happen.
    }
  }

  private boolean fuzzyEquals(double a, double b) {
    double epsilon = 1e-6;
    return Math.abs(a - b) <= epsilon;
  }

  @Override
  public Boolean visitBooleanComparisonExpression(BooleanComparisonExpressionContext ctx) {
    NumberVisitor visitor = new NumberVisitor(variables, functions);
    Double left = visitor.visit(ctx.comparisonExpression().left);
    Double right = visitor.visit(ctx.comparisonExpression().right);

    String operator = ctx.comparisonExpression().COMPARISON_OPERATOR().getText();
    switch (operator) {
      case "=": return fuzzyEquals(left, right);
      case "!=": return !fuzzyEquals(left, right);
      case "<": return left < right;
      case "<=": return left <= right;
      case ">": return left > right;
      case ">=": return left >= right;
      default: throw new IllegalArgumentException("Unknown operator: " + operator); // Should never happen.
    }
  }

  @Override
  public Boolean visitBooleanNotExpression(BooleanNotExpressionContext ctx) {
    return !visit(ctx.booleanExpression());
  }

}