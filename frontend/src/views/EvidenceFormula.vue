<template>
  <v-layout row wrap>
    <v-flex xs3>
      <formula-list
        :loading.sync="loading"
        :formulas="formulas"
        @update-list="updateList()"
      />
    </v-flex>
    <v-flex xs9 pa-3>
      <formula-view
        v-if="currentFormula()"
        :formula="currentFormula()"
        :loading.sync="loading"
        @update-list="updateList()"
      />
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

export interface Formula {
  id: number;
  name: string;
  formula: string;
}

export default Vue.extend({
  data() {
    return {
      formulas: [] as Formula[],
      loading: false
    };
  },
  components: {
    FormulaList,
    FormulaView
  },

  methods: {
    updateList() {
      this.axios
        .get("/evidence-formulas")
        .then(res => {
          this.formulas = res.data;
        })
        .then(() => (this.loading = false));
    },
    currentFormula(): any {
      for (let formula of this.formulas) {
        if (formula.id == +this.$route.params.id) {
          return Object.assign({}, formula);
        }
      }
      return null;
    }
  },

  created() {
    this.updateList();
  }
});
</script>
