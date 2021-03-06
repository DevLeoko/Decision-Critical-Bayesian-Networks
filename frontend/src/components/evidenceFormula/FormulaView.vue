<template>
  <v-container>
    <v-row wrap pa-3>
      <v-col cols="7">
        <v-text-field
          v-model="internalFormula.name"
          hide-details
          :error="hasError && nameError"
          :label="$t('formula.view.name')"
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
          class="formula-area"
          height="60vh"
          outlined
          :label="$t('formula.view.formula')"
          counter="500"
          v-model="internalFormula.formula"
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
            {{ $t("formula.view.testData") }}
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
        <v-btn color="success" @click="save(internalFormula)"
          ><v-icon class="mr-1">save</v-icon
          >{{ $t("formula.view.save") }}</v-btn
        >
        <v-btn class="ml-2" color="primary" @click="test()">
          <v-icon class="mr-1">colorize</v-icon>{{ $t("formula.view.test") }}
        </v-btn>
      </v-col>
    </v-row>
    <v-snackbar v-model="hasError" color="error" :timeout="5000">
      {{ errorMessage }}
      <v-btn icon @click="hasError = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
    <v-snackbar v-model="success" color="success" :timeout="3000">
      {{ successMessage }}
      <v-btn icon @click="success = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>

    <v-snackbar
      v-model="successfulEvaluation"
      :color="evaluationResult ? 'success' : 'error'"
      :timeout="3000"
    >
      {{ evaluationResult ? "true" : "false" }}
      <v-btn icon @click="successfulEvaluation = false"
        ><v-icon>clear</v-icon></v-btn
      >
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
      errorMessage: "",
      success: false,
      successMessage: "",
      successfulEvaluation: false,
      evaluationResult: false,
      internalFormula: this.formula,
      savedName: ""
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

        if (data.parameterSize) {
          this.formulaError = true;
          this.errorMessage = parameterSize(error);
        } else if (data.symbolNotFound) {
          this.formulaError = true;
          this.errorMessage = symbolNotFound(error);
        } else if (data.parse) {
          this.formulaError = true;
          this.errorMessage = parse(error);
        } else if (data.typeMismatch) {
          this.formulaError = true;
          this.errorMessage = typeMismatch(error);
        } else {
          this.nameError = true;
          this.errorMessage = this.$t("formula.list.responses.duplicateName", {
            name: this.savedName
          }).toString();
        }
      }
    },

    setSuccess() {
      this.$emit("update-list");
      this.success = true;
      this.successMessage = this.$t(
        "formula.list.responses.formulaSaved"
      ).toString();
    },

    showResponse(data: boolean) {
      this.successfulEvaluation = true;
      this.evaluationResult = data;
    },

    test() {
      this.$emit("update:loading", true);
      let result = {} as { [key: string]: any };
      for (let variable of this.variables) {
        result[variable.name] = variable.value;
      }
      this.axios
        .post(`/evidence-formulas/evaluate`, {
          formula: this.internalFormula,
          parameters: result
        })
        .then(resp => {
          this.successfulEvaluation = false;
          this.showResponse(resp.data);
        })
        .catch(this.setError)
        .then(() => this.$emit("update:loading", false));
    },

    save(formula: Formula) {
      this.savedName = formula.name;
      this.$emit("update:loading", true);
      this.axios
        .put(`/evidence-formulas/${formula.id}`, formula)
        .then(this.setSuccess)
        .catch(this.setError)
        .then(() => this.$emit("update:loading", false));
    }
  },
  watch: {
    $route(value, old) {
      this.internalFormula = this.formula;
    }
  }
});
</script>

<style>
.formula-area textarea {
  height: 97%;
}
</style>
