import {Artifact} from "./artifact";
import {Relation} from "./relation";

export class ADR {
  id: number;
  title: string;
  context: string;
  decision: string;
  status: string;
  consequences: string;
  artifacts: Artifact[];
  relations: Relation[];
  date: string;
  commit: string;
}
