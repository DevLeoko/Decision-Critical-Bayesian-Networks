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
        <v-btn tile @click="virtualEvidenceOpen = true">Virtual Evidence</v-btn>
        <v-btn
          tile
          @click="
            if (!presentValues[activeId].evidences.length)
              presentValues[activeId].evidences = new Array(
                graph.timeSlices
              ).fill(false);
            binaryEvidenceOpen = true;
          "
          >Binary Evidences</v-btn
        >
        <v-btn icon color="red"><v-icon>close</v-icon></v-btn>
      </div>
    </v-menu>

    <v-dialog v-model="virtualEvidenceOpen" width="500" v-if="activeId !== -1">
      <v-card>
        <v-card-title>
          Set virtual evidence
        </v-card-title>

        <v-card-text class="py-3">
          <v-slider
            :value="presentValues[activeId].virtualEvidence"
            v-model="virtualSync"
            :color="
              presentValues[activeId].virtualEvidence === null
                ? 'grey'
                : 'primary'
            "
            thumb-label
            max="1"
            step="0.05"
            hide-details
          ></v-slider>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="red"
            text
            @click="
              virtualEvidenceOpen = false;
              presentValues[activeId].virtualEvidence = null;
            "
          >
            Reset
          </v-btn>
          <v-btn
            color="grey darken-2"
            text
            @click="virtualEvidenceOpen = false"
          >
            Done
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="binaryEvidenceOpen" width="500" v-if="activeId !== -1">
      <v-card>
        <v-card-title>
          Set evidences
        </v-card-title>

        <v-card-text>
          <v-switch
            v-for="i in graph.timeSlices"
            :key="i"
            :label="`: Timestep ${i}`"
            v-model="presentValues[activeId].evidences[i - 1]"
            color="primary"
            hide-details
          ></v-switch>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="red"
            text
            @click="
              binaryEvidenceOpen = false;
              presentValues[activeId].evidences = [];
            "
          >
            RESET
          </v-btn>
          <v-btn color="grey darken-2" text @click="binaryEvidenceOpen = false">
            Done
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

      virtualSync: 0,

      virtualEvidenceOpen: false,
      binaryEvidenceOpen: false,

      activeId: -1,

      presentValues: [] as {
        evidences: boolean[];
        virtualEvidence: number | null;
        computed: number[];
      }[]
    };
  },

  methods: {
    displayResults(results: dcbn.GraphResult) {
      Object.keys(results).forEach(key => {
        const values = results[key];
        const id = this.nodeIndecies.indexOf(key);
        this.presentValues[id].computed = values.map(val => val[0]);

        this.rerenderNode(id);
      });
    },

    quickSetValues(nodeId: number, upper: boolean) {
      this.showNodeAction = false;

      const desiredValue = new Array(graph.timeSlices).fill(upper);

      const resetAction =
        this.presentValues[nodeId].evidences.length &&
        this.presentValues[nodeId].evidences.every(val => val === upper);

      if (resetAction) {
        this.presentValues[nodeId].evidences = [];
      } else {
        this.presentValues[nodeId].virtualEvidence = null;
        this.presentValues[nodeId].evidences = desiredValue;
      }

      this.rerenderNode(nodeId);
    },

    rerenderNode(id: number) {
      const entry = this.presentValues[id];

      if (entry.evidences.length && entry.virtualEvidence !== null) {
        entry.virtualEvidence = null;
        return;
      }

      let values: number[] | undefined;
      let type: dcbn.NodeValueType;

      if (entry.evidences.length) {
        values = entry.evidences.map(val => (val ? 1 : 0));
        type = "evidence";
      } else if (entry.computed.length) {
        values = entry.computed;
        type = "computed";
      } else {
        values = undefined;
        type = "empty";
      }

      this.nodes!.update({
        id,
        image: generateGraphImage(values, type, entry.virtualEvidence)
      });
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
        this.activeId = nodeId;
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
      this.presentValues.push({
        evidences: [],
        virtualEvidence: null,
        computed: []
      })
    );
  },

  watch: {
    virtualSync(val) {
      if (this.activeId != -1)
        this.presentValues[this.activeId].virtualEvidence = val;
    },
    presentValues: {
      handler() {
        this.rerenderNode(this.activeId);
      },
      deep: true
    }
  }
});
</script>
