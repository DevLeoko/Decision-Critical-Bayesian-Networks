<template>
  <v-container>
    <v-row wrap pa-3>
      <v-col cols="7">
        <v-text-field
          v-model="formula.name"
          hide-details
          :error="hasError && nameError"
          label="Name"
          outlined
        ></v-text-field>
      </v-col>
      <v-spacer />
      <v-col cols="5" style="justify-content: end; display: flex;">
        <v-btn
          color="primary"
          @click="dialogOpen = true"
          style="align-self: center"
        >
          <v-icon class="mr-1">info</v-icon>Info
        </v-btn>
        <info-dialog :open.sync="dialogOpen" />
      </v-col>
    </v-row>
    <v-row row pa-3>
      <v-col cols="7">
        <v-textarea
          class="sheoi"
          height="60vh"
          outlined
          label="Formula"
          counter="500"
          v-model="formula.formula"
          :error="hasError && formulaError"
        />
      </v-col>
      <v-col cols="5" pl-5>
        <v-card
          outlined
          color="grey lighten-3"
          style="overflow-y: auto; max-height: 60vh"
        >
          <v-card-title primary-title>
            Test
          </v-card-title>
          <v-card-text>
            <v-container>
              <v-row v-for="variable in variables" :key="variable.name">
                <v-col align-self="center" cols="4">
                  {{ variable.name }} =
                </v-col>
                <v-col cols="8">
                  <v-text-field
                    hide-details
                    v-if="variable.type == 'text'"
                    dense
                    solo
                    outlined
                    flat
                    v-model="variable.value"
                  />
                  <v-text-field
                    hide-details
                    v-else-if="variable.type == 'number'"
                    dense
                    solo
                    outlined
                    flat
                    type="number"
                    v-model.number="variable.value"
                  />
                  <v-switch
                    hide-details
                    v-else-if="variable.type == 'boolean'"
                    class="mt-0"
                    v-model="variable.value"
                  />
                  <v-select
                    hide-details
                    v-else-if="variable.type.length"
                    :items="variable.type"
                    v-model="variable.value"
                    solo
                    outlined
                    flat
                    dense
                  />
                </v-col>
              </v-row>
            </v-container>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-row mt-2>
      <v-col>
        <v-btn color="success" @click="save(formula)"
          ><v-icon class="mr-1">save</v-icon>Save</v-btn
        >
        <v-btn class="ml-2" color="primary" @click="test()">
          <v-icon class="mr-1">colorize</v-icon>test
        </v-btn>
      </v-col>
      <v-col>
        <div
          v-if="successfulEvaluation && evaluationResult"
          class="success--text headline"
          style="text-align: end"
        >
          true
        </div>

        <div
          v-if="successfulEvaluation && !evaluationResult"
          class="error--text headline"
          style="text-align: end"
        >
          false
        </div>
      </v-col>
    </v-row>
    <v-snackbar v-model="hasError" color="error" timeout="5000">
      {{ error }}
      <v-btn icon @click="hasError = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
    <v-snackbar v-model="success" color="success" timeout="3000">
      {{ successMessage }}
      <v-btn icon @click="success = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
  </v-container>
</template>

<script lang="ts">
import InfoDialog from "@/components/evidenceFormula/InfoDialog.vue";
import Vue from "vue";
import { Formula } from "@/views/EvidenceFormula.vue";
import { AxiosError } from "axios";
import {
  parameterSize,
  typeMismatch,
  parse,
  symbolNotFound
} from "@/errorMessage";

export default Vue.extend({
  props: {
    formula: {
      type: Object as () => Formula
    }
  },
  components: {
    InfoDialog
  },
  data() {
    return {
      variables: [
        { name: "uuid", type: "text", value: "-----" },
        { name: "speed", type: "number", value: 10 },
        { name: "cog", type: "number", value: 10 },
        { name: "width", type: "number", value: 10 },
        { name: "length", type: "number", value: 10 },
        { name: "draught", type: "number", value: 10 },
        { name: "cog", type: "number", value: 10 },
        { name: "longitude", type: "number", value: 10 },
        { name: "latitude", type: "number", value: 10 },
        { name: "heading", type: "number", value: 10 },
        { name: "filler", type: "boolean", value: false },
        {
          name: "vesselType",
          type: [
            "FISHING",
            "TOWING",
            "DIVING",
            "MILITARY",
            "SAILING",
            "PLEASURE",
            "PILOT",
            "SEARCH_AND_RESCUE",
            "TUG",
            "PORT_TENDER",
            "ANTI_POLLUTION",
            "LAW",
            "MEDICAL",
            "RR_18",
            "WIG",
            "HIGH_SPEED",
            "PASSENGER",
            "CARGO",
            "TANKER",
            "NON_SPECIFIED",
            "FISHING_VESSEL",
            "PASSENGER_SHIP",
            "VESSEL",
            "SPECIAL_PURPOSE_SHIP",
            "GENERAL_CARGO_SHIP",
            "OTHER",
            "BULK_CARRIER",
            "HIGH_SPEED_CRAFT",
            "SPARE",
            "MOBILE_OFF_SHORE_DRILLING_UNIT",
            "OIL_TANKER",
            "UNDERWATERNUCLEAR_SHIP",
            "NUCLEAR_SHIP"
          ],
          value: "FISHING"
        }
      ] as { name: string; type: string | string[]; value: string }[],
      dialogOpen: false,
      hasError: false,
      nameError: false,
      formulaError: false,
      error: "",
      success: false,
      successMessage: "",
      successfulEvaluation: false,
      evaluationResult: false
    };
  },

  methods: {
    setError(error: AxiosError) {
      this.hasError = true;
      this.formulaError = this.nameError = false;
      this.generateErrorMessage(error);
    },

    generateErrorMessage(error: AxiosError) {
      if (error.response) {
        let data = error.response.data;
        let errorBase = `Error in line: ${data.line}, column: ${data.col}: `;

        if (data.parameterSize) {
          this.formulaError = true;
          this.error = parameterSize(error);
        } else if (data.symbolNotFound) {
          this.formulaError = true;
          this.error = symbolNotFound(error);
        } else if (data.parse) {
          this.formulaError = true;
          this.error = parse(error);
        } else if (data.typeMismatch) {
          this.formulaError = true;
          this.error = typeMismatch(error);
        } else {
          this.nameError = true;
          this.error = `Formula with name ${this.formula.name} exists already!`;
        }
      }
    },

    setSuccess() {
      this.$emit("update-list");
      this.success = true;
      this.successMessage = "Formula successfully saved!";
    },

    showResponse(data: boolean) {
      this.successfulEvaluation = true;
      this.evaluationResult = data;
    },

    test() {
      let result = {} as { [key: string]: any };
      for (let variable of this.variables) {
        result[variable.name] = variable.value;
      }
      this.axios
        .post(`/evidence-formulas/evaluate`, {
          formula: this.formula,
          parameters: result
        })
        .then(resp => {
          this.successfulEvaluation = false;
          this.showResponse(resp.data);
        })
        .catch(this.setError);
    },

    save(formula: Formula) {
      if (formula.id === -1) {
        this.axios
          .post("/evidence-formulas", formula)
          .then(this.setSuccess)
          .catch(this.setError);
      } else {
        this.axios
          .put(`/evidence-formulas/${formula.id}`, formula)
          .then(this.setSuccess)
          .catch(this.setError);
      }
    }
  },

  watch: {
    "$route.params.id": function() {
      this.successfulEvaluation = false;
    }
  }
});
</script>

<style>
.sheoi textarea {
  height: 97%;
}
</style>
