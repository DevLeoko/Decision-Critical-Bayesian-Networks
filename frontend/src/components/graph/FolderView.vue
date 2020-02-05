<template>
  <div>
    <!-- useless div as both componentes are fixed -->
    <v-slide-y-transition>
      <v-btn
        v-if="!menu"
        fixed
        bottom
        icon
        large
        elevation="4"
        class="ml-3"
        style="z-index: 10"
        @click="menu = true"
        ><v-icon>chevron_right</v-icon>
      </v-btn>
    </v-slide-y-transition>

    <v-navigation-drawer app fixed clipped width="20vw" v-model="menu">
      <template v-slot:prepend>
        <v-row align="center" no-gutters>
          <v-col class="flex-grow-0 ml-3">
            <v-btn icon elevation="4" large @click="menu = false">
              <v-icon>chevron_left</v-icon>
            </v-btn>
          </v-col>
          <v-col>
            <v-text-field
              v-model="search"
              label="Search for graph"
              solo
              class="ma-3"
              hide-details
              clearable
              clear-icon="clear"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-divider></v-divider>
      </template>
      <v-treeview
        :items="treeItems"
        :search="search"
        @update:active="arr => selectGraph(arr[0])"
        activatable
        open-on-click
      >
        <template v-slot:prepend="{ item, open }">
          <v-icon v-if="item.children.length > 0">
            {{ open ? "folder_open" : "folder" }}
          </v-icon>
          <v-icon v-else>device_hub</v-icon>
        </template>
        <template v-slot:append="{ item }">
          <v-menu offset-y v-if="!item.children.length">
            <template v-slot:activator="{ on }">
              <v-btn v-on.stop="on" @click.stop icon>
                <v-icon>
                  more_vert
                </v-icon>
              </v-btn>
            </template>
            <v-list>
              <v-list-item @click="duplicateGraph(item.graph)">
                <v-list-item-title>
                  <v-icon class="mr-2">file_copy</v-icon> Duplicate
                </v-list-item-title>
              </v-list-item>

              <v-list-item @click="() => $refs.actions.renameGraph(item)">
                <v-list-item-title>
                  <v-icon class="mr-2">text_fields</v-icon> Rename
                </v-list-item-title>
              </v-list-item>

              <v-list-item @click="() => $refs.actions.moveGraph(item)">
                <v-list-item-title>
                  <v-icon class="mr-2">double_arrow</v-icon> Move
                </v-list-item-title>
              </v-list-item>

              <v-list-item @click="() => $refs.actions.deleteGraph(item)">
                <v-list-item-title>
                  <v-icon class="mr-2">delete</v-icon> Delete
                </v-list-item-title>
              </v-list-item>
              <v-list-item @click="exportGraph(item.graph)">
                <v-list-item-title>
                  <v-icon class="mr-2">import_export</v-icon>
                  Export
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </template>
      </v-treeview>
      <template v-slot:append>
        <v-divider></v-divider>
        <v-progress-linear indeterminate v-if="loading" />
        <v-row justify="center">
          <v-col class="flex-grow-0 my-4">
            <v-btn color="primary" @click="createGraph()">
              <v-icon>add</v-icon> Add new graph
            </v-btn>
            <v-file-input
              hide-details
              label="Import"
              outlined
              dense
              class="ma-1"
              @change="importGraph($event)"
            ></v-file-input>
          </v-col>
        </v-row>
      </template>
    </v-navigation-drawer>
    <folder-actions
      ref="actions"
      :folders="
        graphs
          .map(graph =>
            graph.name.substring(0, graph.name.lastIndexOf('/') + 1)
          )
          .filter((graph, index, self) => self.indexOf(graph) == index)
          .sort()
      "
      @rename="renameGraph"
      @delete="deleteGraph"
    ></folder-actions>
    <v-snackbar v-model="hasErrorBar" color="error" :timeout="5000">
      {{ error }}
      <v-btn icon @click="hasErrorBar = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
    <v-snackbar v-model="successBar" color="success" :timeout="3000">
      {{ successMessage }}
      <v-btn icon @click="successBar = false"><v-icon>clear</v-icon></v-btn>
    </v-snackbar>
  </div>
</template>

<script lang="ts">
export interface TreeItem {
  name: string;
  id: string | number;
  graph?: dcbn.DenseGraph;
  children: TreeItem[];
}

import Vue from "vue";
import FolderActions from "@/components/graph/FolderViewActions.vue";
import { dcbn } from "../../utils/graph";
export default Vue.extend({
  props: {
    graphs: Array as () => Array<dcbn.DenseGraph>
  },

  components: { FolderActions },
  data() {
    return {
      search: null,
      menu: true,
      i: 100,
      hasErrorBar: false,
      error: "",
      successBar: false,
      successMessage: "",
      loading: false
    };
  },

  methods: {
    async createGraph() {
      this.loading = true;
      try {
        const newName = this.generateNewUString("newGraph");
        const res = await this.axios.post("/graphs", {
          id: 0,
          name: newName,
          timeSlices: 5,
          nodes: []
        });
        this.graphs.push({
          name: newName,
          id: res.data
        });
        this.throwSuccess("The graph has been generated");
      } catch (err) {
        this.throwError(err.response.data.message);
      } finally {
        this.loading = false;
      }
    },

    duplicateGraph(graph: dcbn.DenseGraph) {
      this.loading = true;
      this.axios
        .get(`/graphs/${graph.id}`)
        .then(res => {
          const copy = res.data as dcbn.Graph;
          copy.id = 0;
          copy.name = this.generateNewUString(`${graph.name}_COPY`);
          copy.nodes.forEach(node => {
            node.id = 0;
            node.timeZeroDependency.id = 0;
            node.timeTDependency.id = 0;
          });
          this.axios
            .post("/graphs", copy)
            .then(resp => {
              copy.id = resp.data;
              this.graphs.push({ name: copy.name, id: copy.id });
              this.throwSuccess(
                `Graph ${graph.name} duplicated to ${copy.name}`
              );
            })
            .catch(error => this.throwError(error.response.data.message));
        })
        .catch(error => this.throwError(error.response.data.message))
        .then(() => (this.loading = false));
    },

    renameGraph({ graph, name }: { graph: dcbn.DenseGraph; name: string }) {
      this.loading = true;
      this.axios
        .post(`/graphs/${graph.id}/name`, name, {
          headers: {
            "Content-Type": "text/plain"
          }
        })
        .then(() => {
          graph.name = name;
          this.throwSuccess(`Graph renamed to ${name}`);
        })
        .catch(error => this.throwError(error.response.data.message))
        .then(() => (this.loading = false));
    },

    deleteGraph({ graph }: { graph: dcbn.DenseGraph }) {
      this.loading = true;
      this.axios
        .delete(`/graphs/${graph.id}`)
        .then(() => {
          this.graphs.splice(this.graphs.indexOf(graph), 1);
          this.throwSuccess("Graph deleted");
        })
        .catch(error => this.throwError(error.response.data.message))
        .then(() => (this.loading = false));
    },

    // graphId is actually an int ._.
    selectGraph(id: string) {
      let targetRoute = this.$route.name;

      if (id) {
        if (targetRoute == "GraphBase") targetRoute = "Test Graph";
      } else {
        targetRoute = "GraphBase";
      }

      this.$router.push({
        name: targetRoute,
        params: { id }
      });
    },

    exportGraph(graph: dcbn.DenseGraph) {
      this.loading = true;
      const FileDownload = require("js-file-download");
      this.axios
        .get(`graphs/${graph.id}/export`)
        .then(res => {
          FileDownload(res.data, graph.name + ".xdsl");
        })
        .catch(error => {
          this.throwError(error.response.data.message);
        })
        .then(() => (this.loading = false));
    },

    importGraph(file: any) {
      this.loading = true;
      if (file != null) {
        const formData = new FormData();
        formData.append("graph", file);
        this.axios
          .post("graphs/import", formData, {
            headers: {
              "Content-Type": "multipart/form-data"
            }
          })
          .then(res => {
            this.graphs.push({ name: res.data.name, id: res.data.id });
            this.throwSuccess("Graph imported");
          })
          .catch(error => {
            this.throwError(error.response.data.message);
          })
          .then(() => (this.loading = false));
      } else {
        this.loading = false;
      }
    },

    throwError(message: string) {
      this.error = message;
      this.hasErrorBar = true;
    },
    throwSuccess(message: string) {
      this.successMessage = message;
      this.successBar = true;
    },

    generateNewUString(defaultString: string): string {
      const gefaultGraphCopyName = defaultString;
      for (let i = 0; ; i++) {
        let testName = `${gefaultGraphCopyName}${i === 0 ? "" : i}`;
        if (!this.graphs.filter(graph => graph.name == testName).length) {
          return testName;
        }
      }
    }
  },

  computed: {
    treeItems() {
      let result: TreeItem = {
        name: "Root",
        id: -1,
        children: []
      };

      this.graphs
        .slice(0)
        .sort((a, b) => {
          const nameA = a.name.split("/");
          const nameB = b.name.split("/");

          const diff = nameA.length - nameB.length;

          if (diff != 0) return diff;

          return nameA[nameA.length - 1].localeCompare(nameB[nameA.length - 1]);
        })
        .forEach(entry => {
          const splitName = entry.name.split("/");
          let currentFolder = result.children;

          // resolve folders step by step
          for (let i = 0; i < splitName.length - 1; i++) {
            const element = splitName[i];

            // check whether the next subfolder alrady exists
            const targetFolder: TreeItem = currentFolder.find(
              subFolder => subFolder.name == element
            ) || { name: element, id: -1, children: [] };

            // if not, add it to the current folder
            if (targetFolder.id == -1) {
              currentFolder.push(targetFolder);
              targetFolder.id = `${entry.id} - ${element}`;
            }

            currentFolder = targetFolder.children;
          }

          const graphName = splitName[splitName.length - 1];
          // add actual graph to the "lowest" subfolder
          currentFolder.push({
            name: graphName,
            id: entry.id,
            children: [],
            graph: entry
          });
        });

      return result.children;
    }
  }
});
</script>
