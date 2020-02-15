<template>
  <v-layout row justify-center>
    <v-dialog v-model="open" width="600">
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
          <cpt-container :time0="time0" :node="node" />
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
import CptContainer from "@/components/graph/CptContainer.vue";
import { dcbn } from "../../utils/graph";

export default Vue.extend({
  data() {
    return {
      properties: true,
      time0: true
    };
  },
  components: { CptContainer },
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
