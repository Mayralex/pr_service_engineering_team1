import { Pipe, PipeTransform } from '@angular/core';
import {ADR} from "../interfaces/adr";

@Pipe({
  name: 'search'
})
export class SearchPipe implements PipeTransform {

  transform(adrs: ADR[], searchText: string): ADR[] {

    if (!adrs) {
      return [];
    }
    if (!searchText) {
      return adrs;
    }

    searchText = searchText.toLowerCase();
    return adrs.filter(adr => {
      // Customize this condition as per filtering requirements
      return adr.title.toLowerCase().includes(searchText);
    });
  }

}
