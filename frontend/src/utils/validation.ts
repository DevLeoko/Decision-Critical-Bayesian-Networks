// eslint-disable-next-line no-useless-escape
export const mailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
export const passwordRegex = /^.*(?=.{8,})(?=.*\d)((?=.*[a-zA-Z]){1}).*$/;

export const mailValidation = [
  (v: string) => !!v || "An email is required",
  (v: string) => mailRegex.test(v) || "This is not a valid email"
];

export const passwordValidation = [
  (v: string) => !!v || "Please enter a password",
  (v: string) =>
    passwordRegex.test(v) ||
    "The password requires a min. length of 8 and has to contain at least one letter and one number"
];

export const nameValidation = [
  (v: string) => !!v || "Name is required",
  (v: string) => /^.{1,16}$/.test(v) || "Invalid name",
  (v: string) => /^.*[a-zA-Z].*$/.test(v) || "Invalid name"
];

export const required = (message: string) => [(v: any) => !!v || message];

export const matchValidation = (message: string, match: any) => [
  (v: any) => v == match || message
];
