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

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/listener")
public class ChatListener {

  List<AsyncResponse> listeners;
  final Logger logger = LoggerFactory.getLogger(ChatListener.class);

  /**
   * Takes a list of listeners which is shared with the ChatSpeaker REST service.
   * 
   * @param listeners the injected list of chat listeners
   */
  public ChatListener(List<AsyncResponse> listeners) {
    this.listeners = listeners;
  }

  /**
   * This method acts as a simple check to verify the path of deployment and that basic things are
   * working.
   *
   * @return the text "pong"
   */
  @GET
  @Path("ping")
  @Produces("text/plain")
  public String ping() {
    return "PONG";
  }

  /**
   * The listen REST service adds the caller to a list to receive the next message that is sent by
   * the speaker. This REST service will have to be recalled to receive additional messages after
   * each message is delivered.
   *
   * @param res the async response to provide the output
   */
  @GET
  public void listen(@Suspended AsyncResponse res) {
    logger.debug("******* LISTENING *************");
    res.setTimeout(100, TimeUnit.SECONDS);
    synchronized (listeners) {
      listeners.add(res);
    }
  }
}
