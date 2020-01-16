grammar Formula;

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
  : IDENTIFIER
  | functionCall;

comparisonExpression
  : left = numberExpression COMPARISON_OPERATOR right = numberExpression;

numberExpression
  : numberLiteral #numberLiteralExpression
  | ambiguousLiteral #numberAmbiguousExpression
  | left = numberExpression operator = MUL_DIV right = numberExpression #numberBinaryExpression
  | left = numberExpression operator = PLUS_MINUS right = numberExpression #numberBinaryExpression
  | '(' numberExpression ')' #numberParenthesisExpression;

numberLiteral
  : NUMBER;

functionCall:
  IDENTIFIER '(' (expression (',' expression)*)? ')';

expression
  : booleanExpression
  | numberExpression;

IDENTIFIER: [a-zA-Z][a-zA-Z_]*;
NUMBER: [0-9]+ ('.' [0-9]+)? ([eE] PLUS_MINUS [0-9]+)?;

COMPARISON_OPERATOR: '=' | '!=' | '<' | '<=' | '>' | '>=';
MUL_DIV: '*' | '/';
PLUS_MINUS: '+' | '-';

WS: [ \t\r\n]+ -> channel(HIDDEN);