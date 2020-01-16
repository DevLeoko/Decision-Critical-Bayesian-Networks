package io.dcbn.backend.evidenceFormula.services.visitors;

import io.dcbn.backend.evidenceFormulas.FormulaBaseVisitor;
import io.dcbn.backend.evidenceFormulas.FormulaParser.ExpressionContext;
import java.util.Map;

public class ExpressionVisitor extends FormulaBaseVisitor<Object> {

  private Map<String, Object> variables;
  private Map<String, FunctionWrapper> functions;

  public ExpressionVisitor(Map<String, Object> variables,
      Map<String, FunctionWrapper> functions) {
    this.variables = variables;
    this.functions = functions;
  }

  @Override
  public Object visitExpression(ExpressionContext ctx) {
    if (ctx.booleanExpression() != null) {
      return new BooleanVisitor(variables, functions).visit(ctx.booleanExpression());
    } else if (ctx.numberExpression() != null) {
      return new NumberVisitor(variables, functions).visit(ctx.numberExpression());
    } else {
      return new AmbiguityVisitor(variables, functions).visit(ctx.ambiguousLiteral());
    }
  }

}
