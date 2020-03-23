export declare module dcbn {
  export interface TimeZeroDependency {
    id: number;
    parents: string[];
    parentsTm1: string[];
    probabilities: number[][];
  }

  export interface TimeTDependency {
    id: number;
    parents: string[];
    parentsTm1: string[];
    probabilities: number[][];
  }

  export interface StateType {
    states: string[];
  }

  export interface Position {
    x: number;
    y: number;
  }

  export interface Node {
    type: string;
    name: string;
    id: number;
    timeZeroDependency: TimeZeroDependency;
    timeTDependency: TimeTDependency;
    color: string;
    evidenceFormulaName?: any;
    stateType: StateType;
    position: Position;
    value?: number[][];
  }

  export interface Graph {
    id: number;
    name: string;
    timeSlices: number;
    nodes: Node[];
  }

  export interface DenseGraph {
    name: string;
    id: number;
    locked: boolean;
  }

  export interface GraphResult {
    [key: string]: number[][];
  }

  export type NodeValueType = "evidence" | "empty" | "computed";
}

export const defaultColor = "#67809f";
