import {
  mailRegex,
  passwordRegex,
  mailValidation,
  passwordValidation,
  nameValidation,
  required,
  matchValidation
} from "./validation";
import { i18n } from "../internationalization/translation";

jest.mock("@/internationalization/translation");

function expectRuleEvaluation(
  rules: ((value: any) => {})[],
  input: any
): ReturnType<typeof expect> {
  for (let rule of rules) {
    if (rule(input) !== true) return expect(false);
  }
  return expect(true);
}

describe("Validation RegEx", () => {
  test("Mail RegEx", () => {
    expect(mailRegex.test("tom@tommy.com")).toBe(true);
    expect(mailRegex.test("tom@fake")).toBe(false);
  });

  test("Password RegEx", () => {
    expect(passwordRegex.test("SuperSecure123!_uff")).toBe(true);
    expect(passwordRegex.test("insecure")).toBe(false);
  });
});

describe("Rules", () => {
  const i18nMock: jest.MockedFunction<typeof i18n.t> = i18n.t as any;

  afterEach(i18nMock.mockClear);

  describe("Mail rule", () => {
    test("Mail rule with valid mail", () => {
      expectRuleEvaluation(mailValidation, "me@validmail.com").toBe(true);
      expect(i18nMock).not.toBeCalled();
    });

    test("Mail rule with invaild mail", () => {
      expectRuleEvaluation(mailValidation, "not.a@mail").toBe(false);
      expect(i18nMock).toBeCalled();
    });

    test("Mail rule with null mail", () => {
      expectRuleEvaluation(mailValidation, null).toBe(false);
      expect(i18nMock).toBeCalled();
    });
  });

  describe("Password rule", () => {
    test("Password rule with valid password", () => {
      expectRuleEvaluation(passwordValidation, "ThisIs_REA11Y*Secure!").toBe(
        true
      );
      expect(i18nMock).not.toBeCalled();
    });

    test("Password rule with invaild password", () => {
      expectRuleEvaluation(passwordValidation, "123").toBe(false);
      expect(i18nMock).toBeCalled();
    });

    test("Password rule with null password", () => {
      expectRuleEvaluation(passwordValidation, null).toBe(false);
      expect(i18nMock).toBeCalled();
    });
  });

  describe("Name rule", () => {
    test("Name rule with valid name", () => {
      expectRuleEvaluation(nameValidation, "Tom").toBe(true);
      expect(i18nMock).not.toBeCalled();
    });

    test("Name rule with invaild name", () => {
      expectRuleEvaluation(nameValidation, "^-^").toBe(false);
      expect(i18nMock).toBeCalled();
    });

    test("Name rule with too long name", () => {
      expectRuleEvaluation(
        nameValidation,
        "Thisnameisreallyridiculouslylongname"
      ).toBe(false);
      expect(i18nMock).toBeCalled();
    });

    test("Name rule with null name", () => {
      expectRuleEvaluation(nameValidation, null).toBe(false);
      expect(i18nMock).toBeCalled();
    });
  });

  describe("Special rules", () => {
    test("Required rule with valid value", () => {
      expectRuleEvaluation(required("messagePath"), "Tom").toBe(true);
      expect(i18nMock).not.toBeCalled();
    });

    test("Required rule with invalid value", () => {
      expectRuleEvaluation(required("messagePath"), null).toBe(false);
      expect(i18nMock).toBeCalled();
    });

    test("Match rule with successful match", () => {
      expectRuleEvaluation(
        matchValidation("messagePath", "matchMe"),
        "matchMe"
      ).toBe(true);
      expect(i18nMock).not.toBeCalled();
    });

    test("Match rule with unsuccessful match", () => {
      expectRuleEvaluation(
        matchValidation("messagePath", "matchMe"),
        "matchHim"
      ).toBe(false);
      expect(i18nMock).toBeCalled();
    });
  });
});
