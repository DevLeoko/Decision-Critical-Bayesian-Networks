<template>
  <v-layout row justify-center>
    <v-dialog
      v-model="open"
      fullscreen
      hide-overlay
      transition="dialog-bottom-transition"
      @keypress.esc="updateOpen(false)"
    >
      <v-card>
        <v-toolbar dark color="primary">
          <v-btn icon dark @click="updateOpen(false)">
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-toolbar-title>{{
            $t("nodeProperties.properties")
          }}</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-toolbar-items>
            <v-btn dark text @click="save()">{{
              $t("nodeProperties.save")
            }}</v-btn>
          </v-toolbar-items>
        </v-toolbar>
        <v-tabs v-model="propertyTabs">
          <v-tab>{{ $t("nodeProperties.general") }}</v-tab>
          <v-tab>{{ $t("nodeProperties.definition") }}</v-tab>
        </v-tabs>

        <v-tabs-items v-model="propertyTabs">
          <v-tab-item><general-tab :node="node"/></v-tab-item>
          <v-tab-item>
            <cpt-container :node="node" />
            <v-select
              :items="evidenceFormulas"
              style="max-width: 300px; margin: auto"
              :label="this.$t('nodeProperties.evidenceFormula')"
              v-model="node.evidenceFormulaName"
              clearable
            />
          </v-tab-item>
        </v-tabs-items>
      </v-card>
    </v-dialog>
  </v-layout>
</template>

<script lang="ts">
import Vue from "vue";
import GeneralTab from "@/components/graph/properties/GeneralTab.vue";
import CptContainer from "@/components/graph/properties/CptContainer.vue";
import { dcbn } from "@/utils/graph/graph";

export default Vue.extend({
  data() {
    return {
      properties: true,
      time0: true,
      propertyTabs: null,
      oldName: "",
      evidenceFormulas: [] as string[]
    };
  },
  components: {
    GeneralTab,
    CptContainer
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
    },

    save() {
      this.$emit("save", { oldName: this.oldName, node: this.node });
      this.updateOpen(false);
    }
  },

  watch: {
    open(val) {
      if (val) {
        this.oldName = this.node.name;
      }
    }
  },

  mounted() {
    this.axios
      .get("/evidence-formulas")
      .then(resp => {
        this.evidenceFormulas = resp.data.map((formula: any) => formula.name);
      })
      .catch(err => {
        //TODO: DISPLAY ERROR.
        console.log(err);
      });
  }
});
</script>
