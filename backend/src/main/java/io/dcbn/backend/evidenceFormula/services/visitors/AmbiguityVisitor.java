package io.dcbn.backend.evidenceFormula.services.visitors;

import io.dcbn.backend.evidenceFormula.services.FunctionProvider;
import io.dcbn.backend.evidenceFormula.services.exceptions.EvaluationException;
import io.dcbn.backend.evidenceFormula.services.exceptions.ParameterSizeMismatchException;
import io.dcbn.backend.evidenceFormula.services.exceptions.SymbolNotFoundException;
import io.dcbn.backend.evidenceFormula.services.exceptions.TypeMismatchException;
import io.dcbn.backend.evidenceFormulas.FormulaBaseVisitor;
import io.dcbn.backend.evidenceFormulas.FormulaParser.AmbiguousFunctionCallLiteralContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.AmbiguousIdentifierLiteralContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Visitor responsible for evaluating any expressions whose type cannot be determined while parsing (i.e. function calls and variables).
 * The methods in this class are responsible for visiting the corresponding rules in the grammar.
 * They will not be documented in more detail.
 */
public class AmbiguityVisitor extends FormulaBaseVisitor<Object> {

  private Map<String, Object> variables;
  private FunctionProvider functions;

  public AmbiguityVisitor(Map<String, Object> variables,
      FunctionProvider functions) {
    this.variables = variables;
    this.functions = functions;
  }

  @Override
  public Object visitAmbiguousIdentifierLiteral(AmbiguousIdentifierLiteralContext ctx) {
    String name = ctx.IDENTIFIER().getText();
    return variables.getOrDefault(name, name);
  }

  @Override
  public Object visitAmbiguousFunctionCallLiteral(AmbiguousFunctionCallLiteralContext ctx) {
    String name = ctx.functionCall().IDENTIFIER().getText();

    ExpressionVisitor visitor = new ExpressionVisitor(variables, functions);
    List<Object> parameters = ctx.functionCall().expression().stream().map(visitor::visit).collect(
        Collectors.toList());

    try {
      return functions.call(name, parameters);
    } catch (EvaluationException ex) {
      ex.setLine(ctx.start.getLine());
      ex.setCol(ctx.start.getCharPositionInLine());
      throw ex;
    }
  }

}
