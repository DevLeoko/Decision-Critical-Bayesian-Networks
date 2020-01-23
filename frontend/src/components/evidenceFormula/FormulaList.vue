<template>
  <v-navigation-drawer permanent width="100%">
    <v-list style=" max-height: 80vh; overflow: auto">
      <v-list-item
        :key="formula.id"
        v-for="formula in formulas"
        link
        @click="selectFormula(formula.id)"
      >
        <v-list-item-content>
          <v-list-item-title>{{ formula.name }}</v-list-item-title>
        </v-list-item-content>
        <v-list-item-action @click="deleteFormula(formula.id)">
          <v-btn icon>
            <v-icon color="black">delete</v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>
    </v-list>

    <template v-slot:append>
      <v-flex class="pa-4" style="text-align: center">
        <v-btn small color="primary" @click="addFormula">
          <v-icon color="white">add_box</v-icon>
          New Expression
        </v-btn>
      </v-flex>
    </template>
  </v-navigation-drawer>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: ["formulas"],
  methods: {
    selectFormula(id: string) {
      this.$router.push({
        name: "EvidenceFormula",
        params: {
          id
        }
      });
    },
    addFormula() {
      var newFormula = {
        name: "newFormula",
        id: this.formulas.length,
        formula: ""
      };
      this.formulas.push(newFormula);
    },
    deleteFormula(id: number) {
      for (var i = 0; i < this.formulas.length; i++) {
        if (id === this.formulas[i].id) {
          this.formulas.splice(i, 1);
        }
      }
    }
  }
});
</script>
