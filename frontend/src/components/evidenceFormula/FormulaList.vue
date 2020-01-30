<template>
  <v-navigation-drawer permanent width="100%">
    <v-list style=" max-height: 80vh; overflow: auto">
      <v-list-item-group color="primary">
        <v-list-item
          :key="formula.id"
          v-for="formula in sortedFormulas"
          link
          @click="selectFormula(formula.id)"
        >
          <v-list-item-content>
            <v-list-item-title>{{ formula.name }}</v-list-item-title>
          </v-list-item-content>
          <v-list-item-action @click.stop="deleteFormula(formula)">
            <v-btn icon>
              <v-icon color="black">delete</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </v-list-item-group>
    </v-list>

    <template v-slot:append>
      <v-flex class="pa-4" style="text-align: center">
        <v-btn small color="primary" @click="addFormula()">
          <v-icon class="mr-1" color="white">add_box</v-icon>
          New Expression
        </v-btn>
      </v-flex>
    </template>
  </v-navigation-drawer>
</template>

<script lang="ts">
import Vue from "vue";
import { Formula } from "@/views/EvidenceFormula.vue";

export default Vue.extend({
  props: {
    formulas: {
      type: Array as () => Formula[]
    }
  },
  methods: {
    selectFormula(id: string) {
      console.log(id);
      this.$router.push({
        name: "EvidenceFormula",
        params: {
          id
        }
      });
    },

    generateNewName(): string {
      const defaultFormulaName = "newFormula";
      if (this.formulas.length == 0) {
        return defaultFormulaName;
      }

      for (let i = 0; ; i++) {
        let testName = `${defaultFormulaName}${i === 0 ? "" : i}`;
        if (!this.formulas.filter(formula => formula.name == testName).length) {
          return testName;
        }
      }
    },

    addFormula() {
      let name = this.generateNewName();
      this.axios
        .post("/evidence-formulas", {
          name: name,
          formula: "true"
        })
        .then(res => this.$emit("update-list"));
    },

    deleteFormula(formula: Formula) {
      this.axios
        .delete(`/evidence-formulas/${formula.id}`)
        .then(_ => this.$emit("update-list"));
    }
  },

  computed: {
    sortedFormulas() {
      return this.formulas.slice().sort((a, b) => a.name.localeCompare(b.name));
    }
  }
});
</script>
