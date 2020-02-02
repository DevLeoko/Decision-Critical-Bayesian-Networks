import { i18n } from "@/internationalization/translation";

// eslint-disable-next-line no-useless-escape
export const mailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
export const passwordRegex = /^.*(?=.{8,})(?=.*\d)((?=.*[a-zA-Z]){1}).*$/;

export const mailValidation = [
  (v: string) => !!v || i18n.t("messages.emailRequired"),
  (v: string) => mailRegex.test(v) || i18n.t("messages.emailInvalid")
];

export const passwordValidation = [
  (v: string) => !!v || i18n.t("messages.pwRequired"),
  (v: string) => passwordRegex.test(v) || i18n.t("messages.pwRequirements")
];

export const nameValidation = [
  (v: string) => !!v || i18n.t("message.nameRequired"),
  (v: string) => /^.{1,16}$/.test(v) || i18n.t("messages.nameInvalid"),
  (v: string) => /^.*[a-zA-Z].*$/.test(v) || i18n.t("messages.nameInvalid")
];

export const required = (message: string) => [
  (v: any) => !!v || i18n.t(message)
];

export const matchValidation = (message: string, match: any) => [
  (v: any) => v == match || i18n.t(message)
];
