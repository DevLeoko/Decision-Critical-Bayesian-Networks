<template>
  <div style="max-height: 100%; width: 100% ">
    <test-toolbar
      @test="displayResults"
      @export="exportState()"
      @import="importState()"
      @clear="clear()"
      :nodeIndecies="this.nodeIndecies"
      :presentValues="this.presentValues"
    />
    <div id="mynetwork" ref="network"></div>
    <v-menu
      v-model="showNodeAction"
      :position-x="x"
      :position-y="y"
      :close-on-click="false"
      absolute
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
    <input ref="stateImport" type="file" display="none" />
    <v-snackbar v-model="hasErrorBar" color="error" :timeout="5000">
      {{ errorMessage }}
      <v-btn icon @click="hasErrorBar = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
  </div>
</template>

<style lang="scss" scoped>
#mynetwork {
  width: 100%;
  height: 100%;
  max-height: 100vh;
  border: 1px solid lightgray;
}
</style>

<script lang="ts">
import TestToolbar from "@/components/graph/TestToolbar.vue";
import Vue from "vue";
import vis, { network } from "vis-network";

// TODO fetch actual graph from backend
import graph from "@/../tests/resources/graph1.json";

import { generateGraphImage, createVisGraph, dcbn } from "../../utils/graph";

import FileDownload from "js-file-download";

export default Vue.extend({
  components: {
    TestToolbar
  },

  data() {
    return {
      hasErrorBar: false,
      errorMessage: "",
      timeSlices: 0,
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
        const id = this.nodeIndecies.indexOf(key);
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
        image: generateGraphImage(values, type, entry.virtualEvidence)
      });
    },

    rerenderAll() {
      for (let i = 0; i < this.nodeIndecies.length; ++i) {
        this.rerenderNode(i);
      }
    },

    clear() {
      this.presentValues = [];
      this.nodeIndecies.forEach(() =>
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
        nodeIndecies: this.nodeIndecies,
        presentValues
      };
      FileDownload(JSON.stringify(obj), `${this.graph.name}.json`);
    },

    importState() {
      (this.$refs.stateImport as any).click();
    },

    handleFileSelect(file: File) {
      const reader = new FileReader();
      reader.onload = this.setFile;
      reader.readAsText(file);
    },

    setFile(event: any) {
      const text = event.target.result;
      let obj = JSON.parse(text);

      for (let node of this.graph.nodes) {
        const name = node.name;
        if (!obj.nodeIndecies.includes(name)) {
          this.error = true;
          this.errorMessage = `No node with name ${name} found!`;
          return;
        }
      }

      this.presentValues = obj.presentValues;
      this.rerenderAll();
    }
  },

  // TODO check whether thats the right lifecycle hook
  mounted() {
    (this.$refs.stateImport as any).addEventListener("change", (evt: any) =>
      this.handleFileSelect(evt.target.files[0])
    );
    this.axios
      .get(`/graphs/${this.$route.params.id}`)
      .then(res => {
        this.timeSlices = res.data.timeSlices;
        const { nodeData, nodeIndecies, network } = createVisGraph(
          document.getElementById("mynetwork")!,
          res.data,
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

    this.clear();
      })
      .catch(error => {
        this.errorMessage = error.response.data.message;
        this.hasErrorBar = true;
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
