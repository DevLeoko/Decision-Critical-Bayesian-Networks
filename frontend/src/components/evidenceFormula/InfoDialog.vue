<template>
  <v-dialog v-model="open" max-width="600">
    <v-card>
      <v-card-title class="headline">
        {{ $t("formula.infoDialog.title") }}
      </v-card-title>

      <v-card-text>
        <p>
          {{ $t("formula.infoDialog.introduction") }}
        </p>
        <div>
          {{ $t("formula.infoDialog.availableFunctions") }}
          <ul>
            <li>inArea(&lt;area name&gt;)</li>
            <li>distanceToNearest()</li>
            <li>distanceToNearestByType(&lt;vessel type&gt;)</li>
          </ul>
        </div>
        <p>
          {{ $t("formula.infoDialog.variable") }}
        </p>
        <p>
          {{ $t("formula.infoDialog.testing") }}
        </p>
        <p>
          {{ $t("formula.infoDialog.example") }}
        </p>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="open = false">
          {{ $t("formula.infoDialog.gotIt") }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: ["open"],
  watch: {
    open(value) {
      this.$emit("update:open", value);
    }
  }
});
</script>
