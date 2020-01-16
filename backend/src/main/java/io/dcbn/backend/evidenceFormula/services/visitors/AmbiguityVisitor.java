package io.dcbn.backend.evidenceFormula.services.visitors;

import io.dcbn.backend.evidenceFormulas.FormulaBaseVisitor;
import io.dcbn.backend.evidenceFormulas.FormulaParser.AmbiguousFunctionCallLiteralContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.AmbiguousIdentifierLiteralContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
      throw new IllegalArgumentException("Function " + name + " doesn't exist.");
    }

    FunctionWrapper wrapper = functions.get(name);
    ExpressionVisitor visitor = new ExpressionVisitor(variables, functions);
    List<Class<?>> expectedTypes = wrapper.getExpectedParameterTypes();
    List<Object> parameters = ctx.functionCall().expression().stream().map(visitor::visit).collect(
        Collectors.toList());

    if (parameters.size() != expectedTypes.size()) {
      throw new IllegalArgumentException("Parameters do not match function signature!");
    }

    for (int i = 0; i < parameters.size(); i++) {
      Class<?> type = expectedTypes.get(i);
      Object parameter = parameters.get(i);

      if (!type.isInstance(parameter)) {
        String message = (i + 1) + ". parameter type mismatch in function " + name + ". Expected: " + type + ", but got: " + parameter.getClass();
        throw new IllegalArgumentException(message);
      }
    }

    return wrapper.getFunction().apply(parameters);
  }

}
