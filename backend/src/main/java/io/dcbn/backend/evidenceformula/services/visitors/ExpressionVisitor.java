package io.dcbn.backend.evidenceformula.services.visitors;

import io.dcbn.backend.evidenceformula.services.FunctionProvider;
import io.dcbn.backend.evidenceFormulas.FormulaBaseVisitor;
import io.dcbn.backend.evidenceFormulas.FormulaParser.ExpressionContext;

import java.util.Map;

/**
 * Visitor responsible for evaluating generic expressions (i.e. as function parameters).
 * The methods in this class are responsible for visiting the corresponding rules in the grammar.
 * They will not be documented in more detail.
 */
public class ExpressionVisitor extends FormulaBaseVisitor<Object> {

    private Map<String, Object> variables;
    private FunctionProvider functions;

    public ExpressionVisitor(Map<String, Object> variables,
                             FunctionProvider functions) {
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
