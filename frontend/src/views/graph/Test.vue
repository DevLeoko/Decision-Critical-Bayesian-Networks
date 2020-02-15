<template>
  <div style="max-height: 100%; width: 100% ">
    <test-toolbar
      @test="displayResults"
      @export="exportState()"
      @import="importState()"
      @clear="clear()"
      :nodeIndices="this.nodeIndices"
      :presentValues="this.presentValues"
    />
    <div id="mynetwork" ref="network"></div>
    <v-menu
      v-model="showNodeAction"
      :position-x="x"
      :position-y="y"
      :close-on-click="false"
      absolute
      top
    >
      <div class="white">
        <v-btn
          tile
          @click="
            if (presentValues[activeId].virtualEvidence === null) {
              virtualSync = 0; // force watcher to pick up update
              virtualSync = 0.5;
            }
            virtualEvidenceOpen = true;
          "
          >Virtual Evidence</v-btn
        >
        <v-btn
          tile
          @click="
            if (!presentValues[activeId].evidences.length)
              presentValues[activeId].evidences = new Array(timeSlices).fill(
                false
              );
            binaryEvidenceOpen = true;
          "
        >
          Binary Evidences
        </v-btn>
        <v-btn
          tile
          @click="valuesOpen = true"
          v-if="activeId !== -1 && presentValues[activeId].computed.length"
          >Values</v-btn
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
            v-model="virtualSync"
            thumb-color="primary"
            color=" green lighten-2"
            track-color="red lighten-2"
            thumb-label
            max="1"
            step="0.05"
            hide-details
          ></v-slider>
          <v-row justify="space-between" align-content="center" no-gutters>
            <v-col class="flex-grow-0">false</v-col>
            <v-col cols="3">
              <v-text-field
                type="number"
                v-model.number="virtualSync"
                max="1"
                min="0"
                step="0.01"
                hide-details
                outlined
                dense
              ></v-text-field>
            </v-col>
            <v-col class="flex-grow-0">true</v-col>
          </v-row>
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
            v-for="i in timeSlices"
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
    <v-dialog v-model="valuesOpen" width="500" v-if="activeId !== -1">
      <v-card>
        <v-card-title> Values of {{ nodeIndices[activeId] }} </v-card-title>

        <v-card-text>
          <v-row
            v-for="(value, index) in presentValues[activeId].computed"
            :key="index"
          >
            <v-col cols="1">{{ index + 1 }}</v-col>
            <v-col cols="11">
              <v-progress-linear height="100%" :value="value * 100">
                <template v-slot="{ value }">
                  <strong :style="`color: ${value >= 40 ? 'white' : 'black'}`">
                    {{ value.toFixed(2) }}%
                  </strong>
                </template>
              </v-progress-linear>
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn color="primary" text @click="valuesOpen = false">
            Close
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <input
      ref="stateImport"
      type="file"
      @change="evt => handleFileSelect(evt.target.files[0])"
      display="none"
    />
    <v-snackbar v-model="error" color="error" :timeout="5000">
      {{ errorMessage }}
      <v-btn icon @click="error = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
  </div>
</template>

<script lang="ts">
import TestToolbar from "@/components/graph/TestToolbar.vue";
import Vue from "vue";
import vis, { network } from "vis-network";

import FileDownload from "js-file-download";
import { dcbn } from "@/utils/graph/graph";
import { generateStatsSVG } from "@/utils/graph/generateStatsSVG";
import { createTestGraph } from "@/utils/graph/graphGenerator";

export default Vue.extend({
  components: {
    TestToolbar
  },

  data() {
    return {
      timeSlices: 0,
      graphName: "",
      nodes: null as vis.data.DataSet<vis.Node, "id"> | null,
      nodeIndices: [] as string[],
      showNodeAction: false,
      x: 0,
      y: 0,

      virtualSync: 0,

      virtualEvidenceOpen: false,
      binaryEvidenceOpen: false,
      valuesOpen: false,

      activeId: -1,

      presentValues: [] as {
        evidences: boolean[];
        //Assuming its the true value
        virtualEvidence: number | null;
        computed: number[];
      }[],

      error: false,
      errorMessage: ""
    };
  },

  methods: {
    displayResults(results: dcbn.GraphResult) {
      Object.keys(results).forEach(key => {
        const values = results[key];
        const id = this.nodeIndices.indexOf(key);
        this.presentValues[id].computed = values.map(val => val[0]);
        this.rerenderNode(id);
      });
    },

    quickSetValues(nodeId: number, upper: boolean) {
      this.showNodeAction = false;

      const desiredValue = new Array(this.timeSlices).fill(upper);

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
        image: generateStatsSVG(values, type, entry.virtualEvidence)
      });
    },

    rerenderAll() {
      for (let i = 0; i < this.nodeIndices.length; ++i) {
        this.rerenderNode(i);
      }
    },

    clear() {
      this.presentValues = [];
      this.nodeIndices.forEach(() =>
        this.presentValues.push({
          evidences: [],
          virtualEvidence: null,
          computed: []
        })
      );
      this.rerenderAll();
    },

    exportState() {
      const presentValues = Object.assign([], this.presentValues);
      for (let presentValue of presentValues) {
        presentValue.computed = [];
      }

      let obj = {
        nodeIndices: this.nodeIndices,
        presentValues
      };
      FileDownload(JSON.stringify(obj), `${this.graphName}.json`);
    },

    importState() {
      (this.$refs.stateImport as HTMLInputElement).value = "";
      (this.$refs.stateImport as any).click();
    },

    handleFileSelect(file: File) {
      if (file) {
        const reader = new FileReader();
        reader.onload = this.setFile;
        reader.readAsText(file);
      }
    },

    setFile(event: any) {
      const text = event.target.result;
      const fileContent = JSON.parse(text);
      const fileNodeIndices = fileContent.nodeIndices as string[];

      for (let name of fileNodeIndices) {
        if (!this.nodeIndices.includes(name)) {
          this.error = true;
          this.errorMessage = `No node with name ${name} found!`;
          return;
        }
      }

      this.presentValues = fileContent.presentValues;
      this.rerenderAll();
    }
  },

  mounted() {
    const container = document.getElementById("mynetwork")!;

    this.axios
      .get(`/graphs/${this.$route.params.id}`)
      .then(res => {
        this.timeSlices = res.data.timeSlices;
        this.graphName = res.data.name;
        const { nodeData, nodeIndices, network } = createTestGraph(
          container,
          res.data,
          this.quickSetValues
        );

        network.on("click", param => {
          const nodeId = param.nodes[0];

          if (nodeId !== undefined) {
            const boundingBox = network.getBoundingBox(nodeId);
            const position = network.canvasToDOM({
              x: boundingBox.left,
              y: boundingBox.top
            });
            const containerPos = container.getBoundingClientRect() as DOMRect;
            this.x = containerPos.x + position.x;
            this.y = containerPos.y + position.y;
            this.activeId = nodeId;
            this.showNodeAction = true;
          } else {
            this.showNodeAction = false;
          }
        });

        network.on("dragStart", () => {
          this.showNodeAction = false;
        });
        network.on("zoom", () => {
          this.showNodeAction = false;
        });

        this.nodes = nodeData;
        this.nodeIndices = nodeIndices;

        this.clear();
      })
      .catch(error => {
        console.log(error);
        this.errorMessage = error.response.data.message;
        this.error = true;
      });
  },

  watch: {
    virtualSync(val) {
      if (this.activeId != -1)
        this.presentValues[this.activeId].virtualEvidence = val;
    },
    presentValues: {
      handler() {
        if (this.activeId != -1) this.rerenderNode(this.activeId);
      },
      deep: true
    }
  }
});
</script>
