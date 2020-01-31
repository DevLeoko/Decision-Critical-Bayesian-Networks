<template>
  <div style="max-height: 100%; width: 100%">
    <test-toolbar />
    <div id="mynetwork" ref="network"></div>
    <v-sparkline
      :value="graphValue"
      color="white"
      auto-draw
      height="100"
      line-width="3"
      padding="5"
      stroke-linecap="round"
      ref="graphSvg"
    >
    </v-sparkline>
  </div>
</template>

<style lang="scss" scoped>
#mynetwork {
  width: 100%;
  height: 100%;
  uff: #9c4c46;
  border: 1px solid lightgray;
}
</style>

<script lang="ts">
interface Node {
  id: number;
  label: string;
  shape?: string;
  image?: string;
}

interface Link {
  id?: number;
  from: number;
  to: number;
}

function generateGraphImage(svgContainer: HTMLElement): string {
  svgContainer.setAttribute("xmlns", "http://www.w3.org/2000/svg");

  const line = document.createElementNS("http://www.w3.org/2000/svg", "line");

  line.setAttribute("x1", "0%");
  line.setAttribute("x2", "100%");
  line.setAttribute("y1", "50%");
  line.setAttribute("y2", "50%");
  line.setAttribute("opacity", "0.3");
  line.setAttribute("stroke", "white");
  line.setAttribute("stroke-dasharray", "17");

  svgContainer.prepend(line);

  const background = document.createElementNS(
    "http://www.w3.org/2000/svg",
    "rect"
  );

  background.setAttribute("width", "100%");
  background.setAttribute("height", "100%");
  background.setAttribute("fill", "#3498db");
  background.setAttribute("rx", "4");
  background.setAttribute("ry", "4");

  svgContainer.prepend(background);
  const svg = svgContainer.outerHTML;

  return "data:image/svg+xml;charset=utf-8," + encodeURIComponent(svg);
}

import TestToolbar from "@/components/graph/TestToolbar.vue";
import Vue from "vue";
import vis from "vis-network";
import graph from "@/../tests/resources/graph1.json";

export default Vue.extend({
  components: {
    TestToolbar
  },

  data() {
    return {
      graph,
      graphValue: [12, 4, 23, 65, 43, 2],

      nodes: [
        { id: 1, label: "Node 1", shape: "image" },
        { id: 2, label: "Node 2", shape: "image" },
        { id: 3, label: "Node 3" },
        { id: 4, label: "Node 4" },
        { id: 5, label: "Node 5" }
      ] as Array<Node>
    };
  },

  methods: {
    buildGraph(): string {
      return generateGraphImage((this.$refs.graphSvg as any).$el);
    }
  },

  mounted() {
    // create an array with edges
    var edges = new vis.DataSet<Link>([
      { from: 1, to: 3 },
      { from: 1, to: 2 },
      { from: 2, to: 4 },
      { from: 2, to: 5 },
      { from: 3, to: 3 }
    ]);

    this.nodes[0].image = this.buildGraph();
    this.graphValue = [34, 52, 234, 100];
    this.nodes[1].image = this.buildGraph();

    console.log(this.nodes[0].image);

    // create a network
    var container = document.getElementById("mynetwork");

    var data = {
      nodes: this.nodes,
      edges: edges
    };

    var options = {};

    var network = new vis.Network(container!, data, options);
  }

  // watch: {
  //   graph() {

  //   }
  // }
});
</script>
