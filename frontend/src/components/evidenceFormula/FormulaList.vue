<template>
  <v-navigation-drawer permanent width="100%">
    <v-dialog v-model="deleteWarning" persistent max-width="400">
      <v-card>
        <v-card-title>
          <v-icon class="mr-2">delete_sweep</v-icon>
          {{ $t("formula.list.confirmDeletion") }}
        </v-card-title>
        <v-card-text>
          {{
            $t("formula.list.sureToDelete", { formula: deletedFormula.name })
          }}
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn color="gray" text @click="deleteWarning = false">
            {{ $t("formula.list.cancel") }}
          </v-btn>

          <v-btn
            color="red"
            outlined
            @click="
              deleteWarning = false;
              deleteFormula(deletedFormula);
            "
          >
            {{ $t("formula.list.delete") }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-list style=" max-height: 80vh; overflow: auto">
      <v-list-item-group color="primary" v-model="selectedFormula">
        <v-list-item
          @click="changeSelection(formula)"
          :key="formula.id"
          :value="formula.id"
          v-for="formula in sortedFormulas"
        >
          <v-list-item-content class="pl-3">
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
          {{ $t("formula.list.newExpression") }}
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
      deleteWarning: false,

      selectedFormula: null as null | number
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
      if (this.selectedFormula === formula.id) {
        this.selectedFormula = null;

        this.$router.push({
          name: "EvidenceFormulaBase"
        });
      } else {
        this.selectedFormula = formula.id;

        this.$router.push({
          name: "EvidenceFormula",
          params: {
            id: `${formula.id}`
          }
        });
      }
    }
  },

  mounted() {
    const id = this.$route.params.id;
    this.selectedFormula = id ? Number.parseInt(id) : null;
  },

  computed: {
    sortedFormulas() {
      return this.formulas.slice().sort((a, b) => a.name.localeCompare(b.name));
    }
  }
});
</script>
