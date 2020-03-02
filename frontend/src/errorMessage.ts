import { AxiosError } from "axios";
import { i18n } from "@/internationalization/translation";

function baseMessage(line: number, col: number): string {
  return i18n.t("formulaView.errors.base", { line, col }).toString();
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

  return i18n
    .t("formulaView.errors.parameterSize", {
      base: baseMessage(line, col),
      functionName,
      expected,
      actual
    })
    .toString();
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

  return i18n
    .t("formulaView.errors.typeMismatch", {
      base: baseMessage(line, col),
      expected,
      actual
    })
    .toString();
}

export function parse(error: AxiosError): string {
  if (!error.response) {
    return "";
  }
  let data = error.response.data;
  let line = data.parse.line;
  let col = data.parse.col;
  let offending = data.parse.offendingText;

  return i18n
    .t("formulaView.errors.parse", { base: baseMessage(line, col), offending })
    .toString();
}

export function symbolNotFound(error: AxiosError): string {
  if (!error.response) {
    return "";
  }
  let data = error.response.data;
  let line = data.symbolNotFound.line;
  let col = data.symbolNotFound.col;
  let symbol = data.symbolNotFound.symbolName;

  return i18n
    .t("formulaView.errors.symbolNotFound", {
      base: baseMessage(line, col),
      symbol
    })
    .toString();
}
