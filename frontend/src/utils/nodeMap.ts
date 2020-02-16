import { dcbn } from "./graph/graph";

export default class NodeMap {
  private map: {
    [uuid: string]: dcbn.Node;
  };

  constructor() {
    this.map = {};
  }

  put(uuid: string, node: dcbn.Node) {
    this.map[uuid] = node;
  }

  get(uuid: string): dcbn.Node | undefined {
    return this.map[uuid];
  }

  remove(uuid: string) {
    delete this.map[uuid];
  }

  getUuidFromName(name: string): string | undefined {
    return Object.keys(this.map).find(uuid => this.map[uuid].name == name);
  }

  uuids(): string[] {
    return Object.keys(this.map);
  }

  nodes(): dcbn.Node[] {
    return Object.values(this.map);
  }
}
