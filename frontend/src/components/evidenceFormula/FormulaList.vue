<template>
  <v-navigation-drawer permanent width="100%">
    <v-list style=" max-height: 80vh; overflow: auto">
      <v-list-item-group color="primary">
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
      </v-list-item-group>
    </v-list>

    <template v-slot:append>
      <v-flex class="pa-4" style="text-align: center">
        <v-btn small color="primary" @click="addFormula">
          <v-icon class="mr-1" color="white">add_box</v-icon>
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
      console.log(id);
      this.$router.push({
        name: "EvidenceFormula",
        params: {
          id
        }
      });
    },
    addFormula() {
      var name = "lol5"; //TODO make unique default name
      this.axios
        .post("/evidence-formulas", {
          name: name,
          formula: "true"
        })
        .then(res => {
          this.axios.get("/evidence-formulas/" + name).then(res => {
            this.formulas.push({
              id: res.data.id,
              name: res.data.name,
              formula: res.data.formula
            });
          });
        });
    },
    deleteFormula(id: number) {
      for (var i = 0; i < this.formulas.length; i++) {
        if (id === this.formulas[i].id) {
          this.formulas.splice(i, 1);
        }
      }
    }
  },
  created() {
    //clearing the formulas array to avoid doubled formulas
    var length = this.formulas.length;
    console.log(length);
    for (var i = 0; i < length; i++) {
      this.formulas.pop();
    }
    //Requesting and pushing the formulas to the list
    this.axios.get("/evidence-formulas").then(res => {
      for (var i = 0; i < res.data.length; i++) {
        this.formulas.push({
          id: res.data[i].id,
          name: res.data[i].name,
          formula: res.data[i].formula
        });
      }
    });
  }
});
</script>
