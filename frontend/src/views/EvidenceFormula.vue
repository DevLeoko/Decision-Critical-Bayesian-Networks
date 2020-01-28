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
      formulas: []
    };
  },
  components: {
    FormulaList,
    FormulaView
  },
  computed: {
    currentFormula(): any {
      for (var i = 0; i < this.formulas.length; i++) {
        if (this.formulas[i].id == this.$route.params.id) {
          return this.formulas[i];
        }
      }
      return null;
    }
  }
});
</script>
