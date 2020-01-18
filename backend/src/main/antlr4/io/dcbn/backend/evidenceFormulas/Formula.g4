grammar Formula;

formula
  : booleanExpression EOF;

booleanExpression
  : booleanLiteral #booleanLiteralExpression
  | ambiguousLiteral #booleanAmbiguousLiteralExpression
  | comparisonExpression #booleanComparisonExpression
  | left = booleanExpression operator = '&' right = booleanExpression #booleanBinaryExpression
  | left = booleanExpression operator = '|' right = booleanExpression #booleanBinaryExpression
  | '!' booleanExpression #booleanNotExpression
  | '(' booleanExpression ')' #booleanParenthesisExpression;

booleanLiteral
  : 'true' | 'false';

ambiguousLiteral
  : IDENTIFIER #ambiguousIdentifierLiteral
  | functionCall #ambiguousFunctionCallLiteral;

comparisonExpression
  : left = numberExpression COMPARISON_OPERATOR right = numberExpression;

numberExpression
  : numberLiteral #numberLiteralExpression
  | PLUS_MINUS? ambiguousLiteral #numberAmbiguousExpression
  | left = numberExpression operator = MUL_DIV right = numberExpression #numberBinaryExpression
  | left = numberExpression operator = PLUS_MINUS right = numberExpression #numberBinaryExpression
  | '(' numberExpression ')' #numberParenthesisExpression;

numberLiteral
  : NUMBER;

functionCall:
  IDENTIFIER '(' (expression (',' expression)*)? ')';

expression
  : ambiguousLiteral
  | booleanExpression
  | numberExpression;

IDENTIFIER: [a-zA-Z][a-zA-Z_]*;
NUMBER: PLUS_MINUS? [0-9]+ ('.' [0-9]+)? ([eE] PLUS_MINUS? [0-9]+)?;

COMPARISON_OPERATOR: '=' | '!=' | '<' | '<=' | '>' | '>=';
MUL_DIV: '*' | '/';
PLUS_MINUS: '+' | '-';

WS: [ \t\r\n]+ -> skip;