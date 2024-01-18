import { Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {ADR} from "../../../interfaces/adr";
import {Relation} from "../../../interfaces/relation";
import * as vis from 'vis';

@Component({
  selector: 'app-relation-graph',
  templateUrl: './relation-graph.component.html',
  styleUrls: ['./relation-graph.component.css']
})
export class RelationGraphComponent implements OnInit {
  @Input() adrs: ADR[] = [];
  @ViewChild('networkContainer') networkContainer: ElementRef | undefined;

  private network: vis.Network | undefined;
  private nodes: { id: string; label: string; color?: string; title?: string }[] = [];
  private edges: { from: string; to: string; arrows: string; label: string}[] = [];
  private graphNodes: vis.DataSet<any>;
  private graphEdges: vis.DataSet<any>;

  selectedRelation: 'enables' | 'is enabled by' | 'extends' | 'deprecates' | 'is related to' = 'enables';
  adrRelationMap = new Map<string, Relation[]>(); // Map to store relations for each ADR
  adrRelationMapByType = new Map<string, Relation[]>(); // Map to store relations for a specific relation type

  constructor() { }

  ngOnInit(): void {
    this.getRelations();
    console.log("Relation Map", this.adrRelationMap);
  }

  getRelations() {
    for (const adr of this.adrs) {
      const adrName = adr.title.split(':')[0];
      const adrRelations = adr.relations;
      if (adrName && adrRelations) {
        if (!this.adrRelationMap.has(adrName)) {
          this.adrRelationMap.set(adrName, adrRelations);
        } else {
          const existingRelations = this.adrRelationMap.get(adrName);
          if (existingRelations) {
            existingRelations.push(...adrRelations);
            this.adrRelationMap.set(adrName, existingRelations);
          }
        }
      }
    }
  }

  getRelationsByTypeAndCreateGraph() {
    this.getRelationsByType(this.selectedRelation);
    this.createGraph();
  }

  getRelationsByType(relationType: string) {
    this.adrRelationMapByType.clear();

    for (const [adrName, relations] of this.adrRelationMap.entries()) {
      const filteredRelations = relations.filter(relation => relation.type === relationType);
      if (filteredRelations.length > 0) {
        if (!this.adrRelationMapByType.has(adrName)) {
          this.adrRelationMapByType.set(adrName, filteredRelations);
        } else {
          const existingRelations = this.adrRelationMapByType.get(adrName);
          if (existingRelations) {
            existingRelations.push(...filteredRelations);
            this.adrRelationMapByType.set(adrName, existingRelations);
          }
        }
      }
    }
  }

  createGraph() {
    this.nodes = [];
    this.edges = [];

    const addedNodes = new Set<string>();
    // Add nodes and edges to build the Graph
    for (const [key, value] of this.adrRelationMapByType.entries()) {
      // Add the key node
      if (!addedNodes.has(key)) {
        const matchedAdr = this.adrs.find(adr => adr.title.includes(key));
        const adrTitle = matchedAdr?.title;
        this.nodes.push({ id: key, label: key, color: 'rgb(255,255,102)', title: "Test"});
        addedNodes.add(key);
      }

      //add relation nodes
      for (const relation of value) {
        const { affectedAdr } = relation;

        if (!addedNodes.has(affectedAdr)) {
          const matchedAdr = this.adrs.find(adr => adr.title.includes(key));
          const adrTitle = matchedAdr?.title;
          this.nodes.push({ id: affectedAdr, label: "ADR "+affectedAdr, color: 'rgb(204,255,204)', title: "ADR Title" });
          addedNodes.add(affectedAdr);
        }

        // Add edges between key and affected ADR nodes
        this.edges.push({ from: key, to: affectedAdr, arrows: "to", label: this.selectedRelation });
      }
    }

    //build the graph
    this.graphNodes = new vis.DataSet(this.nodes);
    this.graphEdges = new vis.DataSet(this.edges);

    if (this.networkContainer && this.networkContainer.nativeElement) {
      const container = this.networkContainer.nativeElement;

      var data = {
        nodes: this.graphNodes,
        edges: this.graphEdges,
      };

      // var options = {}
      var options = {
        interaction: {hover: true},
        manipulation: {
          enabled: true,
        },
        nodes: {
          scaling: {
            label: {
              enabled: true,
            },
          },
        },
      };
      this.network = new vis.Network(container, data, options);
      this.network.on("hoverNode", function (params) {
        // @ts-ignore
        document.getElementById("eventSpanHeading").innerText = "showPopup event: ";
        // @ts-ignore
        document.getElementById("eventSpanContent").innerText = JSON.stringify(
          params,
          params,
          4
        );
      });
    }
  }
}

/* data structure of relation map */
/* 0:
    key: 'ADR 001'
    value
      0: {id: 161, type;'enables', affectedAdr: '003'}
      1: {id: 162, type;'enables', affectedAdr: '005'}
      2: {id: 163, type;'enables', affectedAdr: '008'}
  1:
    key : 'ADR 003'
    value: ....
 */
