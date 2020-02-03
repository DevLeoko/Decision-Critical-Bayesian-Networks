<template>
  <div>
    <v-toolbar small bottom style="width: 100%">
      <v-btn color="success" class="ml-3" @click="evaluate($route.params.id)">
        <v-icon class="mr-1">check</v-icon>Test
      </v-btn>
      <v-spacer></v-spacer>
      <v-btn small color="primary" class="ml-3">Clear graph</v-btn>
      <v-btn small color="primary" class="ml-3">Export state</v-btn
      ><v-btn small color="primary" class="ml-3"> Import state</v-btn>
      <v-spacer></v-spacer>
      <v-btn small color="primary lighten-2">Switch to Editor</v-btn>
    </v-toolbar>
    <v-snackbar v-model="hasErrorBar" color="error" :timeout="5000">
      {{ errorMessage }}
      <v-btn icon @click="hasErrorBar = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

// TODO fetch actual response from backend
// import graphResp from "@/../tests/resources/graph1_resp.json";
export default Vue.extend({
  props: {
    nodeIndecies: {
      type: Array as () => string[]
    },
    presentValues: {
      type: Array as () => {
        evidences: boolean[];
        //Assuming its the true value
        virtualEvidence: number | null;
        computed: number[];
      }[]
    }
  },

  data() {
    return {
      // graphResp,
      hasErrorBar: false,
      errorMessage: ""
    };
  },

  methods: {
    evaluate(id: number) {
      let valueMap = {} as { [key: string]: number[][] };
      this.nodeIndecies.forEach(node => {
        var index = this.nodeIndecies.indexOf(node) as number;
        //If evidences are present
        if (this.presentValues[index].evidences.length != 0) {
          console.log(node);
          let valueArray = [] as number[][];
          this.presentValues[index].evidences.forEach(evi => {
            if (evi) {
              valueArray.push([1.0, 0.0]);
            } else {
              valueArray.push([0.0, 1.0]);
            }
          });
          valueMap[node] = valueArray;
          //If virtual evidence is present
        } else if (this.presentValues[index].virtualEvidence != null) {
          let valueArray = [] as number[][];
          let virEvi = this.presentValues[index].virtualEvidence as number;
          valueArray.push([virEvi, 1 - virEvi]);
          valueMap[node] = valueArray;
        }
      });
      this.axios
        .post(`/graphs/${id}/evaluate`, valueMap)
        .then(res => {
          console.log(res);
          this.$emit("test", res.data);
        })
        .catch(error => {
          this.errorMessage = error.response.data.message;
          this.hasErrorBar = true;
        });
    }
  }
});
</script>
