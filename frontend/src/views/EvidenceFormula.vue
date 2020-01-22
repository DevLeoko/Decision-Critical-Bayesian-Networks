<template>
  <v-layout row wrap>
    <v-flex xs3>
      <v-navigation-drawer permanent width="100%">
        <formula-list :formulas="formulas" v-on:open-formula="openFormula" />
      </v-navigation-drawer>
    </v-flex>
    <v-flex xs9 pa-3>
      <h1>Evidence formula {{ $route.params.id }}</h1>
      <formula-view :formula="formulas[this.activeFormula]" />
    </v-flex>
  </v-layout>
</template>

<script lang="ts">
import FormulaList from "@/components/evidenceFormula/FormulaList.vue";
import FormulaView from "@/components/evidenceFormula/FormulaView.vue";
import Vue from "vue";
export default Vue.extend({
  data() {
    return {
      formulas: [
        {
          name: "test1",
          id: 0,
          formula: ""
        },
        {
          name: "test2",
          id: 1,
          formula: ""
        },
        {
          name: "test3",
          id: 2,
          formula: ""
        }
      ],
      activeFormula: -1
    };
  },
  components: {
    FormulaList,
    FormulaView
  },
  methods: {
    openFormula(id: number) {
      for (var i = 0; i < this.formulas.length; i++) {
        if (this.formulas[i].id === id) {
          this.activeFormula = i;
        }
      }
    }
  }
});
</script>
