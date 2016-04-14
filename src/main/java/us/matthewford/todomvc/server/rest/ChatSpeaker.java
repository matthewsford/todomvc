/**
 * Copyright (C) 2016 Matthew Ford (matthew@matthewford.us)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.matthewford.todomvc.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

/**
 * This REST service handles the speaking or sending side of the chat application.
 * 
 * @version $Revision: 1 $
 */
@Path("/speaker")
public class ChatSpeaker {

  List<AsyncResponse> listeners;
  final Logger logger = LoggerFactory.getLogger(ChatSpeaker.class);

  /**
   * Takes a list of listeners which is shared with the ChatListner REST service.
   * 
   * @param listeners the list of chat listeners to send messages to
   */
  public ChatSpeaker(List<AsyncResponse> listeners) {
    this.listeners = listeners;
  }

  /**
   * The speaker REST service delivers the message to each registered listener.
   * 
   * @param speech the text to send to the chat listeners
   */
  @POST
  @Consumes("text/plain")
  public void speak(String speech) {
    logger.debug("******* SPEAKING *************");

    synchronized (listeners) {
      Iterator<AsyncResponse> it = listeners.iterator();
      while (it.hasNext()) {
        it.next().resume(Response.ok(speech, "text/plain").build());
        it.remove();
      }
    }
  }
}
