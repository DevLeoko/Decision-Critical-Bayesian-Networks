<template>
  <div style="max-height: 100%; width: 100% ">
    <test-toolbar @test="displayResults" />
    <div id="mynetwork" ref="network"></div>
    <v-menu
      v-model="showNodeAction"
      :position-x="x"
      :position-y="y"
      :close-on-click="false"
      absolute
    >
      <div class="white">
        <v-btn tile>Virtual Evidence</v-btn>
        <v-btn tile>Binary Evidences</v-btn>
        <v-btn icon color="red"><v-icon>close</v-icon></v-btn>
      </div>
    </v-menu>

    <v-dialog v-model="virtualEvidenceOpen" width="500">
      <v-card>
        <v-card-title>
          Set virtual evidence
        </v-card-title>

        <v-card-text class="py-3">
          <v-slider
            v-model="slider"
            thumb-label
            max="1"
            step="0.05"
            hide-details
          ></v-slider>
        </v-card-text>

        <!-- <v-divider></v-divider> -->

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="virtualEvidenceOpen = false">
            Apply
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<style lang="scss" scoped>
#mynetwork {
  width: 100%;
  height: 100%;
  border: 1px solid lightgray;
}
</style>

<script lang="ts">
import TestToolbar from "@/components/graph/TestToolbar.vue";
import Vue from "vue";
import vis, { network } from "vis-network";

import graph from "@/../tests/resources/graph1.json";

import { generateGraphImage, createVisGraph, dcbn } from "../../utils/graph";

export default Vue.extend({
  components: {
    TestToolbar
  },

  data() {
    return {
      graph,
      nodes: null as vis.data.DataSet<vis.Node, "id"> | null,
      nodeIndecies: [] as string[],
      showNodeAction: false,
      x: 0,
      y: 0,

      virtualEvidenceOpen: false,

      presentValues: [] as {
        evidences: number[][];
        virtualEvidence: number[];
      }[]
    };
  },

  methods: {
    displayResults(results: dcbn.GraphResult) {
      Object.keys(results).forEach(key => {
        const values = results[key];
        const id = this.nodeIndecies.indexOf(key);

        const presentValue = this.presentValues[id];

        if (!presentValue.evidences.length) {
          this.nodes!.update({
            id,
            image: generateGraphImage(
              values.map(val => val[0]),
              presentValue.virtualEvidence.length ? "virtEvidence" : "computed"
            )
          });
        }
      });
    },

    quickSetValues(nodeId: number, upper: boolean) {
      this.showNodeAction = false;

      const desiredValue = new Array(graph.timeSlices).fill(
        upper ? [1, 0] : [0, 1]
      );

      const resetAction =
        this.presentValues[nodeId].evidences.length &&
        this.presentValues[nodeId].evidences.every(
          val => !!val[0] == upper && !!val[1] != upper
        );

      if (resetAction) {
        this.presentValues[nodeId].evidences = [];

        this.nodes!.update({
          id: nodeId,
          image: generateGraphImage(undefined)
        });
      } else {
        this.presentValues[nodeId].evidences = desiredValue;

        this.presentValues[nodeId].virtualEvidence = [];

        this.nodes!.update({
          id: nodeId,
          image: generateGraphImage(
            new Array(graph.timeSlices).fill(upper ? 1 : 0),
            "evidence"
          )
        });
      }
    }
  },

  mounted() {
    const { nodeData, nodeIndecies, network } = createVisGraph(
      document.getElementById("mynetwork")!,
      this.graph,
      this.quickSetValues,
      (nodeId, position) => {
        this.x = position.x + 10;
        this.y = position.y - 50;
        this.showNodeAction = true;
      }
    );

    network.on("deselectNode", () => {
      this.showNodeAction = false;
    });
    network.on("dragStart", () => {
      this.showNodeAction = false;
    });
    network.on("zoom", () => {
      this.showNodeAction = false;
    });

    this.nodes = nodeData;
    this.nodeIndecies = nodeIndecies;

    nodeIndecies.forEach(() =>
      this.presentValues.push({ evidences: [], virtualEvidence: [] })
    );
  }
});
</script>
