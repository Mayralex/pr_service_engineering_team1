import { Injectable } from '@angular/core';

/**
 * Service to display toast messages on the user interface
 */
@Injectable({
  providedIn: 'root',
})
export class MessageService {
  messages: string[] = [];

  /**
   * Add a toast message that should be displayed. The message will be displayed right after being added.
   *
   * @param message - the message that should be displayed
   */
  add(message: string) {
    this.messages.push(message);
  }

  /**
   * Deletes all messages that were previously added to the service
   */
  clear() {
    this.messages = [];
  }
}
