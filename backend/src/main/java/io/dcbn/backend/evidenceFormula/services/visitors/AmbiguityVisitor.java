package io.dcbn.backend.evidenceFormula.services.visitors;

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
  private Map<String, FunctionWrapper> functions;

  public AmbiguityVisitor(Map<String, Object> variables,
      Map<String, FunctionWrapper> functions) {
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
    if (!functions.containsKey(name)) {
      throw new SymbolNotFoundException(name, ctx.start.getLine(), ctx.start.getCharPositionInLine());
    }

    FunctionWrapper wrapper = functions.get(name);
    ExpressionVisitor visitor = new ExpressionVisitor(variables, functions);
    List<Class<?>> expectedTypes = wrapper.getExpectedParameterTypes();
    List<Object> parameters = ctx.functionCall().expression().stream().map(visitor::visit).collect(
        Collectors.toList());

    if (parameters.size() != expectedTypes.size()) {
      throw new ParameterSizeMismatchException(name, expectedTypes.size(), parameters.size(), ctx.start.getLine(), ctx.start.getCharPositionInLine());
    }

    for (int i = 0; i < parameters.size(); i++) {
      Class<?> type = expectedTypes.get(i);
      Object parameter = parameters.get(i);

      if (!type.isInstance(parameter)) {
        throw new TypeMismatchException(type, parameter.getClass(), ctx.start.getLine(), ctx.start.getCharPositionInLine());
      }
    }

    return wrapper.getFunction().apply(parameters);
  }

}
