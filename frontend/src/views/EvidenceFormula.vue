<template>
  <v-layout row wrap>
    <v-flex xs3>
      <formula-list
        :loading.sync="loading"
        :formulas="formulas"
        @update-list="updateList()"
        @graphs-changed="updateChangedGraphs($event)"
      />
    </v-flex>
    <v-flex xs9 pa-3>
      <formula-view
        v-if="currentFormula()"
        :formula="currentFormula()"
        @update:loading="loading = $event"
        @update-list="updateList()"
      />
      <v-layout v-else row wrap>
        <v-flex xs6 offset-xs3>
          <v-alert type="info" :value="true">
            {{ $t("evidenceFormula.selectOrCreate") }}
          </v-alert>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-snackbar color="orange darker-2" v-model="graphsChanged" timeout="7500">
      The graphs {{ changedGraphs }} that where using this evidence formula have
      been changed!
    </v-snackbar>
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
      loading: false,
      graphsChanged: false,
      changedGraphs: ""
    };
  },
  components: {
    FormulaList,
    FormulaView
  },

  methods: {
    updateList() {
      this.axios.get("/evidence-formulas").then(res => {
        this.formulas = res.data;
      });
    },
    currentFormula(): any {
      for (let formula of this.formulas) {
        if (formula.id == +this.$route.params.id) {
          return Object.assign({}, formula);
        }
      }
      return null;
    },

    updateChangedGraphs(graphs: string[]) {
      this.graphsChanged = true;
      this.changedGraphs = graphs.join(", ");
    }
  },

  created() {
    this.updateList();
  }
});
</script>
