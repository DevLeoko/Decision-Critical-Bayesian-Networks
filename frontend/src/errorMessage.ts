import { AxiosError } from "axios";

function baseMessage(line: number, col: number): string {
  return `Error at line: ${line}, column: ${col}:`;
}

export function parameterSize(error: AxiosError): string {
  if (!error.response) {
    return "";
  }
  let data = error.response.data;
  let line = data.parameterSize.line;
  let col = data.parameterSize.col;
  let functionName = data.parameterSize.functionName;
  let expected = data.parameterSize.expectedParameterSize;
  let actual = data.parameterSize.actualParameterSize;

  return `${baseMessage(
    line,
    col
  )} function "${functionName}" expected ${expected} parameters but got ${actual}.`;
}

export function typeMismatch(error: AxiosError): string {
  if (!error.response) {
    return "";
  }
  let data = error.response.data;
  let line = data.typeMismatch.line;
  let col = data.typeMismatch.col;
  let expected = data.typeMismatch.expectedType;
  let actual = data.typeMismatch.actualType;

  expected = expected.substr(expected.lastIndexOf(".") + 1);
  actual = actual.substr(actual.lastIndexOf(".") + 1);

  return `${baseMessage(
    line,
    col
  )} expected type ${expected} but got ${actual}.`;
}

export function parse(error: AxiosError): string {
  if (!error.response) {
    return "";
  }
  let data = error.response.data;
  let line = data.parse.line;
  let col = data.parse.col;
  let offending = data.parse.offendingText;

  return `${baseMessage(line, col)} failed to parse "${offending}".`;
}

export function symbolNotFound(error: AxiosError): string {
  if (!error.response) {
    return "";
  }
  let data = error.response.data;
  let line = data.symbolNotFound.line;
  let col = data.symbolNotFound.col;
  let symbol = data.symbolNotFound.symbolName;

  return `${baseMessage(line, col)} could not find symbol "${symbol}".`;
}
