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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Application;

/**
 * This is the JAX-RS application which provides the rest services.
 */
public class ChatApplication extends Application {
  private Set<Object> singletons = new HashSet<>();

  /**
   * This is my first sentence.
   */
  public ChatApplication() {
    List<AsyncResponse> listeners = new ArrayList<>();
    singletons.add(new ChatListener(listeners));
    singletons.add(new ChatSpeaker(listeners));
  }

  /**
   * This is my first sentence.
   * 
   * @see javax.ws.rs.core.Application#getSingletons()
   */
  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }
}
