<template>
  <div>
    <div class="list-of-formulas" id="container">
      <div :key="formula.id" v-for="formula in formulas">
        <v-list-item>
          <v-list-item-content @click="$emit('open-formula', formula.id)">
            <v-list-item-title>{{ formula.name }}</v-list-item-title>
          </v-list-item-content>
          <v-list-item-action>
            <v-btn icon>
              <v-icon color="black">delete</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </div>
    </div>
    <div class="text-center" style="margin-top: 1vh">
      <!-- TODO make vertically centered -->
      <div style="border-top-width: 1vh">
        <v-btn small color="primary" @click="addFormula">
          <v-icon color="white">add_box</v-icon>
          New Expression
        </v-btn>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: ["formulas"],
  methods: {
    addFormula() {
      var newFormula = {
        name: "newFormula",
        id: this.formulas.length,
        formula: ""
      };
      this.formulas.push(newFormula);
    }
  }
});
</script>

<style scoped>
.list-of-formulas {
  max-height: calc(95vh - 70px);
  overflow-y: auto;
}
</style>
