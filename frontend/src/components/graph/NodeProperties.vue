<template>
  <v-layout row justify-center>
    <v-dialog
      v-model="open"
      fullscreen
      hide-overlay
      transition="dialog-bottom-transition"
    >
      <v-card>
        <v-toolbar dark color="primary">
          <v-btn icon dark @click="updateOpen(false)">
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-toolbar-title>Properties</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-toolbar-items>
            <v-btn dark text @click="updateOpen(false)">Save</v-btn>
          </v-toolbar-items>
        </v-toolbar>
        <v-tabs v-model="propertyTabs">
          <v-tab>General</v-tab>
          <v-tab>Definition</v-tab>
          <v-tab>Format</v-tab>
        </v-tabs>

        <v-tabs-items v-model="propertyTabs">
          <v-tab-item>General</v-tab-item>
          <v-tab-item>Definition</v-tab-item>
          <v-tab-item>Format</v-tab-item>
        </v-tabs-items>
      </v-card>
    </v-dialog>
  </v-layout>
</template>

<script lang="ts">
import Vue from "vue";
// import CptContainer from "@/components/graph/CptContainer.vue";
import { dcbn } from "@/utils/graph/graph";

export default Vue.extend({
  data() {
    return {
      properties: true,
      time0: true,
      propertyTabs: null
    };
  },
  // components: { CptContainer },
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
