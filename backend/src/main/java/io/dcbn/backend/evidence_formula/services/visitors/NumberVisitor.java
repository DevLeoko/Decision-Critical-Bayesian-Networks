package io.dcbn.backend.evidence_formula.services.visitors;

import io.dcbn.backend.evidence_formula.services.FunctionProvider;
import io.dcbn.backend.evidence_formula.services.exceptions.TypeMismatchException;
import io.dcbn.backend.evidenceFormulas.FormulaBaseVisitor;
import io.dcbn.backend.evidenceFormulas.FormulaParser.NumberAmbiguousExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.NumberBinaryExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.NumberLiteralExpressionContext;
import io.dcbn.backend.evidenceFormulas.FormulaParser.NumberParenthesisExpressionContext;

import java.util.Map;

/**
 * Visitor responsible for evaluating number expressions.
 * The methods in this class are responsible for visiting the corresponding rules in the grammar.
 * They will not be documented in more detail.
 */
public class NumberVisitor extends FormulaBaseVisitor<Double> {

    private Map<String, Object> variables;
    private FunctionProvider functions;

    public NumberVisitor(Map<String, Object> variables,
                         FunctionProvider functions) {
        this.variables = variables;
        this.functions = functions;
    }

    @Override
    public Double visitNumberAmbiguousExpression(NumberAmbiguousExpressionContext ctx) {
        AmbiguityVisitor visitor = new AmbiguityVisitor(variables, functions);
        Object result = visitor.visit(ctx.ambiguousLiteral());

        if (result instanceof Double) {
            if (ctx.PLUS_MINUS() != null) {
                return "-".equals(ctx.PLUS_MINUS().getText()) ? -(double) result : (double) result;
            } else {
                return (double) result;
            }
        } else {
            throw new TypeMismatchException(Double.class, result.getClass(), ctx.start.getLine(), ctx.start.getCharPositionInLine());
        }
    }

    @Override
    public Double visitNumberBinaryExpression(NumberBinaryExpressionContext ctx) {
        Double left = visit(ctx.left);
        Double right = visit(ctx.right);

        switch (ctx.operator.getText()) {
            case "+":
                return left + right;
            case "-":
                return left - right;
            case "*":
                return left * right;
            case "/":
                return left / right;
            default:
                throw new IllegalArgumentException("Unknown operator: " + ctx.operator.getText()); // Should never happen.
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
