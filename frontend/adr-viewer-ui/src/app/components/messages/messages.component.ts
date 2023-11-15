import { Component, OnInit } from '@angular/core';
import {MessageService} from "../../services/message.service";

/**
 * Used to display the toast messages that are added to the MessageService
 */
@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.css']
})
export class MessagesComponent implements OnInit {

  constructor(
    public messageService: MessageService
  ) { }

  ngOnInit(): void {
  }

}
