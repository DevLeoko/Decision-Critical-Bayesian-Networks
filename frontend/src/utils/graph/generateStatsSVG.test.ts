import { generateStatsSVG } from "./generateStatsSVG";

test("Empty SVG stats", () => {
  const svg = generateStatsSVG(undefined);
  expect(svg).toMatchSnapshot();
});

test("Computed SVG stats", () => {
  const svg = generateStatsSVG([0, 0.5, 1, 0.1], "computed");
  expect(svg).toMatchSnapshot();
});

test("Virt. evidence SVG stats", () => {
  const svg = generateStatsSVG(undefined, "empty", 0.5);
  expect(svg).toMatchSnapshot();
});

test("Evidence SVG stats", () => {
  const svg = generateStatsSVG([1, 0.9, 0.8], "evidence", 0.5);
  expect(svg).toMatchSnapshot();
});
