<template>
  <div>
    <v-toolbar small bottom style="width: 100%">
      <v-btn
        color="success"
        class="ml-3"
        @click="evaluate($route.params.id)"
        :loading="loading"
      >
        <v-icon class="mr-1">check</v-icon> {{ $t("graph.test.toolbar.test") }}
      </v-btn>
      <v-spacer></v-spacer>
      <v-btn small color="primary" class="ml-3" @click="$emit('clear')">{{
        $t("graph.test.toolbar.clearGraph")
      }}</v-btn>
      <v-btn small color="primary" class="ml-3" @click="$emit('export')">{{
        $t("graph.test.toolbar.exportState")
      }}</v-btn
      ><v-btn small color="primary" class="ml-3" @click="$emit('import')">
        {{ $t("graph.test.toolbar.importState") }}</v-btn
      >
      <v-spacer></v-spacer>
      <v-btn
        v-if="$store.state.user.role == 'ADMIN'"
        small
        color="primary lighten-2"
        @click="$router.push({ name: 'Edit Graph' })"
        >{{ $t("graph.test.toolbar.switchToEditor") }}</v-btn
      >
    </v-toolbar>
    <v-snackbar v-model="hasErrorBar" color="error" :timeout="5000">
      {{ errorMessage }}
      <v-btn icon @click="hasErrorBar = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  props: {
    nodeIndices: {
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
      hasErrorBar: false,
      errorMessage: "",
      loading: false
    };
  },

  methods: {
    evaluate(id: number) {
      let valueMap = {} as { [key: string]: number[][] };
      this.nodeIndices.forEach(node => {
        const index = this.nodeIndices.indexOf(node);
        //If evidences are present
        if (this.presentValues[index].evidences.length) {
          valueMap[node] = this.presentValues[index].evidences.map(evid =>
            evid ? [1, 0] : [0, 1]
          );
          //If virtual evidence is present
        } else if (this.presentValues[index].virtualEvidence != null) {
          let valueArray = [] as number[][];
          let virEvi = this.presentValues[index].virtualEvidence!;
          valueArray.push([virEvi, 1 - virEvi]);
          valueMap[node] = valueArray;
        }
      });
      this.loading = true;
      this.axios
        .post(`/graphs/${id}/evaluate`, valueMap)
        .then(res => {
          this.$emit("test", res.data);
        })
        .catch(error => {
          this.errorMessage = error.response.data.message;
          this.hasErrorBar = true;
        })
        .then(() => (this.loading = false));
    }
  }
});
</script>
