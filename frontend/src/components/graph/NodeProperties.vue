<template>
  <v-layout row justify-center>
    <v-dialog v-model="open" width="unset">
      <v-card>
        <v-card-title class="headline primary lighten-3" primary-title>
          <div v-if="properties">
            <v-btn color="primary" @click="properties = false">CPT</v-btn>
          </div>
          <div v-else>
            <v-btn color="primary" @click="properties = true">Properties</v-btn>
          </div>
        </v-card-title>
        <div v-if="properties">
          <v-col cols="12" sm="6" md="8">
            <v-text-field
              label="Node Name"
              :value="node.name"
              @change="changeName"
            ></v-text-field>
          </v-col>

          <v-card-title primary-title>
            Color
          </v-card-title>
          <v-color-picker
            class="ma-2"
            :value="node.color"
            :mode="'hexa'"
            v-on:update:color="changeColor"
          ></v-color-picker>
        </div>

        <div v-else>
          <v-row>
            <v-card-title class="headline">
              <div v-if="Time0">Conditional Probability Table Time 0</div>
              <div v-else>Conditional Probability Table Time T</div>
            </v-card-title>
            <v-btn v-if="Time0" class="ma-3" @click="Time0 = false"
              >Time T</v-btn
            >
            <v-btn v-else class="ma-3" @click="Time0 = true">Time 0</v-btn>
          </v-row>
          <prob-table :node="node" :time0="Time0" />
        </div>

        <v-spacer></v-spacer>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="red" text @click="updateOpen(false)">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-layout>
</template>

<script lang="ts">
import Vue from "vue";
import ProbTable from "@/components/graph/ProbTable.vue";
import { dcbn } from "../../utils/graph";

export default Vue.extend({
  data() {
    return {
      properties: true,
      Time0: true
    };
  },
  components: { ProbTable },
  props: { open: Boolean, node: Object as () => dcbn.Node },
  methods: {
    updateOpen(value: boolean) {
      this.$emit("update:open", value);
    },
    changeColor(color: any) {
      this.node.color = color.hex;
    },
    changeName(name: any) {
      this.node.name = name;
    }
  }
});
</script>
