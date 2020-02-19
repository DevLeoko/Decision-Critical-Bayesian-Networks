<template>
  <v-navigation-drawer permanent width="100%">
    <v-dialog v-model="deleteWarning" persistent max-width="400">
      <v-card>
        <v-card-title>
          <v-icon class="mr-2">delete_sweep</v-icon>
          {{ $t("formulaList.confirmDeletion") }}
        </v-card-title>
        <v-card-text>
          {{ $t("formulaList.sureToDelete1") }}
          {{ deletedFormula.name }}
          {{ $t("formulaList.sureToDelete2") }}
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn color="gray" text @click="deleteWarning = false">
            {{ $t("formulaList.cancel") }}
          </v-btn>

          <v-btn
            color="red"
            outlined
            @click="
              deleteWarning = false;
              deleteFormula(deletedFormula);
            "
          >
            {{ $t("formulaList.delete") }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-list style=" max-height: 80vh; overflow: auto">
      <v-list-item-group color="primary">
        <v-list-item
          @click="changeSelection(formula)"
          :key="formula.id"
          v-for="formula in sortedFormulas"
        >
          <v-list-item-content>
            <v-list-item-title>{{ formula.name }}</v-list-item-title>
          </v-list-item-content>

          <v-list-item-action
            @click.stop="
              deleteWarning = true;
              deletedFormula = formula;
            "
          >
            <v-btn icon>
              <v-icon color="black">delete</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </v-list-item-group>
    </v-list>

    <template v-slot:append>
      <v-divider />
      <v-progress-linear indeterminate v-if="loading" />
      <v-flex class="pa-4" style="text-align: center">
        <v-btn small color="primary" @click="addFormula()">
          <v-icon class="mr-1" color="white">add_box</v-icon>
          {{ $t("formulaList.newExpression") }}
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
    },
    loading: {
      type: Boolean
    }
  },

  data() {
    return {
      deletedFormula: {} as Formula,
      deleteWarning: false
    };
  },

  methods: {
    generateNewName(): string {
      const defaultFormulaName = "newFormula";
      for (let i = 0; ; i++) {
        let testName = `${defaultFormulaName}${i === 0 ? "" : i}`;
        if (!this.formulas.filter(formula => formula.name == testName).length) {
          return testName;
        }
      }
    },

    addFormula() {
      this.$emit("update:loading", true);
      let name = this.generateNewName();
      this.axios
        .post("/evidence-formulas", {
          name: name,
          formula: "true"
        })
        .then(() => this.$emit("update-list"))
        .then(() => this.$emit("update:loading", false));
    },

    deleteFormula(formula: Formula) {
      this.$emit("update:loading", true);
      this.axios
        .delete(`/evidence-formulas/${formula.id}`)
        .then(resp => {
          this.$emit("update-list");
          if (resp.data.length > 0) {
            this.$emit("graphs-changed", resp.data);
          }
        })
        .then(() => this.$emit("update:loading", false));
    },

    changeSelection(formula: Formula) {
      this.$router.push({
        name: "EvidenceFormula",
        params: {
          lang: this.$i18n.locale,
          id: `${formula.id}`
        }
      });
    }
  },

  computed: {
    sortedFormulas() {
      return this.formulas.slice().sort((a, b) => a.name.localeCompare(b.name));
    }
  }
});
</script>
