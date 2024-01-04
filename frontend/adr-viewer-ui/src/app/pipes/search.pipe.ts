import {Pipe, PipeTransform} from '@angular/core';
import {ADR} from "../interfaces/adr";

@Pipe({
  name: 'search'
})
export class SearchPipe implements PipeTransform {

  /** Takes the incoming ADR[] and search string and returns the ADR[] filtered by the ADR title or ADR status, depending on the search string.
   *
   * @param adrs
   * @param searchText
   */
  transform(adrs: ADR[], searchText: string): ADR[] {

    // check if adrs and searchText exist
    if (!adrs) {
      return [];
    }
    if (!searchText) {
      return adrs;
    }

    // set everything to lower case and collected present status values
    searchText = searchText.toLowerCase();
    const statusArray = adrs.map(adr => adr.status.toLowerCase());
    const uniqueStatusArray = Array.from(new Set(statusArray));

    // if search is equal to a status value --> return ADRs by status value; else return ADRs filtered by title
    if (uniqueStatusArray.includes(searchText)) {
      return adrs.filter(adr => {
        return adr.status.toLowerCase() === searchText;
      });
    } else {
      return adrs.filter(adr => {
        return adr.title.toLowerCase().includes(searchText);
      });
    }
  }

}
