<template>
  <v-layout row wrap>
    <v-flex xs3>
      <formula-list :formulas="formulas" />
    </v-flex>
    <v-flex xs9 pa-3>
      <formula-view v-if="currentFormula" :formula="currentFormula" />
      <v-layout v-else row wrap>
        <v-flex xs6 offset-xs3>
          <v-alert type="info" :value="true">
            Please select an evidence formula or create a new one!
          </v-alert>
        </v-flex>
      </v-layout>
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
          formula: "true"
        },
        {
          name: "test2",
          id: 1,
          formula: "false"
        },
        {
          name: "test3",
          id: 2,
          formula: "bruh"
        }
      ],
      activeFormula: -1
    };
  },
  components: {
    FormulaList,
    FormulaView
  },
  computed: {
    currentFormula(): any {
      return this.formulas[Number.parseInt(this.$route.params.id)];
    }
  }
});
</script>
