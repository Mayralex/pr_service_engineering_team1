import {ADR} from "./adr";

export class AdrPage {
  data: ADR[];
  paginationInfo: {
    pageOffset: number;
    pageCount: number;
    limit: number;
  }
}
