import { dcbn } from "./graph";

export function generateStatsSVG(
  graphValue: number[] | undefined,
  type: dcbn.NodeValueType = "empty",
  virtualEvidence: number | null = null
): string {
  const svgContainer = document.createElementNS(
    "http://www.w3.org/2000/svg",
    "svg"
  );
  svgContainer.setAttribute("xmlns", "http://www.w3.org/2000/svg");
  svgContainer.setAttribute("viewBox", "0 0 300 100");
  svgContainer.setAttribute("width", "300");
  svgContainer.setAttribute("height", "100");
  const background = document.createElementNS(
    "http://www.w3.org/2000/svg",
    "rect"
  );
  background.setAttribute("width", "100%");
  background.setAttribute("height", "100%");
  background.setAttribute(
    "fill",
    type == "evidence"
      ? "#e47833"
      : type == "empty"
      ? "#6c7a89"
      : type == "computed"
      ? "#3498db"
      : ""
  );
  background.setAttribute("rx", "4");
  background.setAttribute("ry", "4");
  svgContainer.appendChild(background);
  const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
  line.setAttribute("x1", "0%");
  line.setAttribute("x2", "100%");
  line.setAttribute("y1", "50%");
  line.setAttribute("y2", "50%");
  line.setAttribute("opacity", "0.3");
  line.setAttribute("stroke", "white");
  line.setAttribute("stroke-dasharray", "17");
  svgContainer.appendChild(line);
  if (virtualEvidence !== null) {
    const virtualLine = document.createElementNS(
      "http://www.w3.org/2000/svg",
      "line"
    );
    virtualLine.setAttribute("x1", "0%");
    virtualLine.setAttribute("x2", "100%");
    virtualLine.setAttribute("y1", `${80 * (1 - virtualEvidence) + 10}%`);
    virtualLine.setAttribute("y2", `${80 * (1 - virtualEvidence) + 10}%`);
    virtualLine.setAttribute("opacity", "0.4");
    virtualLine.setAttribute("stroke", "white");
    virtualLine.setAttribute("stroke-width", "3");
    svgContainer.appendChild(virtualLine);
  }
  if (graphValue) {
    if (graphValue.length === 1) graphValue.push(graphValue[0]);

    const graphString =
      "M" +
      graphValue
        .map(
          (val, i) =>
            `${(300 / (graphValue.length - 1)) * i} ${80 * (1 - val) + 10} ${
              graphValue.length == i + 1 ? "" : "L"
            }`
        )
        .join("");
    const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path.setAttribute("d", graphString);
    path.setAttribute("fill", "none");
    path.setAttribute("stroke", "white");
    path.setAttribute("opacity", "1");
    path.setAttribute("stroke-width", "5");
    svgContainer.appendChild(path);
  }
  const svg = svgContainer.outerHTML;
  return "data:image/svg+xml;charset=utf-8," + encodeURIComponent(svg);
}
