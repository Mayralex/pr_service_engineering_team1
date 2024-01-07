import {Artifact} from "./artifact";

export class ADR {
  id: number;
  title: string;
  context: string;
  decision: string;
  status: string;
  consequences: string;
  artifacts: Artifact[];
  relations: string;
  date: string;
  commit: string;
}
