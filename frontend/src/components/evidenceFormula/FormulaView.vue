<template>
  <v-container>
    <v-layout row wrap pa-3>
      <v-flex xs4>
        <v-text-field
          v-model="formula.name"
          counter="25"
          label="Name"
          outlined
        ></v-text-field>
      </v-flex>
      <v-flex xs2 offset-xs1 pa-2>
        <v-btn color="primary" @click="dialogOpen = true">
          <v-icon class="mr-1">info</v-icon>Info</v-btn
        >
        <info-dialog :open.sync="dialogOpen" />
      </v-flex>
    </v-layout>
    <v-layout row wrap pa-3>
      <v-flex xs7>
        <v-textarea
          outlined
          label="Formula"
          counter="500"
          :value="formula.formula"
          rows="15"
          auto-grow
        ></v-textarea>
      </v-flex>
      <v-flex xs5 px-5>
        <v-card outlined color="grey lighten-3">
          <v-card-title primary-title>
            Test
          </v-card-title>
          <v-card-text>
            <p v-for="variable in variables" :key="variable.name" class="my-0">
              {{ variable.name }} =
              <v-text-field
                v-if="variable.type == 'text'"
                dense
                class="d-inline-flex"
                solo
                outlined
                flat
                v-model="variable.value"
              ></v-text-field>
              <v-text-field
                v-else-if="variable.type == 'number'"
                dense
                class="d-inline-flex"
                solo
                outlined
                flat
                type="number"
                v-model.number="variable.value"
              ></v-text-field>
              <v-switch
                v-else-if="variable.type == 'boolean'"
                class="d-inline-flex mt-0"
                v-model="variable.value"
              ></v-switch>
              <v-select
                v-else-if="variable.type.length"
                class="d-inline-flex"
                :items="variable.type"
                v-model="variable.value"
                solo
                outlined
                flat
                dense
              ></v-select>
            </p>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary"
              ><v-icon class="mr-1">colorize</v-icon>test</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-flex>
      <v-flex xs12 mt-2>
        <v-btn color="success"><v-icon class="mr-1">save</v-icon>Save</v-btn>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script lang="ts">
import InfoDialog from "@/components/evidenceFormula/InfoDialog.vue";
import Vue from "vue";
export default Vue.extend({
  props: ["formula"],
  components: {
    InfoDialog
  },
  data() {
    return {
      variables: [
        { name: "uuid", type: "text", value: "-----" },
        { name: "speed", type: "number", value: 10 },
        { name: "x", type: "number", value: 10 },
        { name: "y", type: "number", value: 10 },
        { name: "heading", type: "number", value: 10 },
        { name: "filler", type: "boolean", value: false },
        { name: "type", type: ["Type1", "Type2"], value: "Type2" }
      ],
      dialogOpen: false
    };
  }
});
</script>
