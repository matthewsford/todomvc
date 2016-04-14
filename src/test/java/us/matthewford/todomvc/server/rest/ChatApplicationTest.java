/**
 * Copyright (C) 2016 Matthew Ford (matthew@matthewford.us)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package us.matthewford.todomvc.server.rest;

import static javax.ws.rs.core.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Test the ChatApplication service.
 * 
 * @version 0.0.3
 * @since 0.0.3
 */
public class ChatApplicationTest {
  private ChatApplication chat = null;
  private ChatListener listener = null;
  private ChatSpeaker speaker = null;

  /**
   * Before each test create a ChatAppllication and extract references for ChatListener and
   * ChatSpeaker.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @BeforeTest
  public void testSetup() {
    chat = new ChatApplication();
    Iterator<Object> it = chat.getSingletons().iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      if (obj instanceof ChatListener) {
        listener = (ChatListener) obj;
      } else if (obj instanceof ChatSpeaker) {
        speaker = (ChatSpeaker) obj;
      }
    }
  }

  /**
   * ChatApplication should not be null.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @Test
  public void testShouldNotBeNull() {
    assertThat(chat).isNotNull();
  }

  /**
   * ChatApplication should return a non-null singletons list.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @Test
  public void testShouldReturnNonNullSingletonsList() {
    assertThat(chat.getSingletons()).isNotNull();
  }

  /**
   * ChatApplication should respond with a PONG when PING is called.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @Test
  public void testShouldContaincontainListenerSpeakerAndNothingElse() {
    assertThat(chat.getSingletons().isEmpty()).isFalse();
    assertThat(chat.getSingletons().size()).isEqualTo(2);
    assertThat(listener).isNotNull();
    assertThat(listener).isInstanceOf(ChatListener.class);
    assertThat(speaker).isNotNull();
    assertThat(speaker).isInstanceOf(ChatSpeaker.class);
  }

  /**
   * ChatApplication should respond with a PONG when PING is called.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @Test
  public void testShouldSendPongInResponseToPing() {
    String pong = listener.ping();
    assertThat(pong).isEqualTo("PONG");
  }

  private void assertResponse(Response response, String expectedMessage) {
    assertThat(response).hasStatus(200);
    assertThat(response).hasMediaType(MediaType.TEXT_PLAIN_TYPE);
    assertThat(response).hasEntity();
    assertThat(response.getEntity().toString()).isEqualTo(expectedMessage);
  }

  /**
   * ChatApplication should not error with no listeners.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @Test
  public void testShouldNotErrorWithNoListeners() {
    /* ******** GIVEN ******** */
    String message = "Hello, world!";

    /* ******** WHEN ******** */
    speaker.speak(message);

    /* ******** THEN ******** */
  }

  /**
   * ChatApplication should send message to one listeners.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @Test
  public void testShouldSendMessageToSingleListener() {
    /* ******** GIVEN ******** */
    AsyncResponse resp = mock(AsyncResponse.class);
    ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
    listener.listen(resp);
    String message = "Hello, world!";

    /* ******** WHEN ******** */
    speaker.speak(message);

    /* ******** THEN ******** */
    verify(resp).setTimeout(100, TimeUnit.SECONDS);
    verify(resp).resume(responseCaptor.capture());

    Response response = responseCaptor.getValue();
    assertResponse(response, message);
  }

  /**
   * ChatApplication should remove listener after message is delivered and not deliver any more
   * messages.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @Test
  public void testShouldRemoveListenerAfterMessage() {
    /* ******** GIVEN ******** */
    AsyncResponse resp = mock(AsyncResponse.class);
    ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
    listener.listen(resp);
    String message = "Hello, world!";

    /* ******** WHEN ******** */
    speaker.speak(message);
    speaker.speak("abc");

    /* ******** THEN ******** */
    verify(resp).setTimeout(100, TimeUnit.SECONDS);
    verify(resp).resume(responseCaptor.capture());

    List<Response> allResponses = responseCaptor.getAllValues();
    assertThat(allResponses).hasSize(1);
    verify(resp).resume(allResponses.get(0));

    assertThat(allResponses.get(0)).hasStatus(200);
    assertThat(allResponses.get(0)).hasMediaType(MediaType.TEXT_PLAIN_TYPE);
    assertThat(allResponses.get(0)).hasEntity();
    assertThat(allResponses.get(0).getEntity().toString()).isEqualTo(message);
  }

  /**
   * ChatApplication should send message to more than one, two, listeners.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @Test
  public void testShouldSendMessageToTwoListeners() {
    /* ******** GIVEN ******** */
    AsyncResponse resp1 = mock(AsyncResponse.class);
    AsyncResponse resp2 = mock(AsyncResponse.class);
    ArgumentCaptor<Response> responseCaptor1 = ArgumentCaptor.forClass(Response.class);
    ArgumentCaptor<Response> responseCaptor2 = ArgumentCaptor.forClass(Response.class);
    listener.listen(resp1);
    listener.listen(resp2);
    String message = "Hello, world!";

    /* ******** WHEN ******** */
    speaker.speak(message);

    /* ******** THEN ******** */
    verify(resp1).setTimeout(100, TimeUnit.SECONDS);
    verify(resp1).resume(responseCaptor1.capture());
    verify(resp2).setTimeout(100, TimeUnit.SECONDS);
    verify(resp2).resume(responseCaptor2.capture());

    assertResponse(responseCaptor1.getValue(), message);
    assertResponse(responseCaptor2.getValue(), message);
  }

  /**
   * ChatApplication should send message to many listeners.
   * 
   * @version 0.0.3
   * @since 0.0.3
   */
  @Test
  public void testShouldSendMessageToManyListeners() {
    /* ******** GIVEN ******** */
    int many = 1000;
    AsyncResponse[] resps = new AsyncResponse[many];
    for (int i = 0; i < many; i++) {
      resps[i] = mock(AsyncResponse.class);
    }

    for (int i = 0; i < many; i++) {
      listener.listen(resps[i]);
    }
    String message = "Hello, world!";

    /* ******** WHEN ******** */
    speaker.speak(message);

    /* ******** THEN ******** */
    for (int i = 0; i < many; i++) {
      verify(resps[i]).setTimeout(100, TimeUnit.SECONDS);
      ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
      verify(resps[i]).resume(responseCaptor.capture());
      assertResponse(responseCaptor.getValue(), message);
    }
  }
}
