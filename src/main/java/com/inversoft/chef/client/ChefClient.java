/*
 * Copyright (c) 2016, Inversoft Inc., All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.inversoft.chef.client;

import com.inversoft.chef.domain.Node;
import com.inversoft.chef.domain.Nodes;
import com.inversoft.net.ssl.SSLTools;
import com.inversoft.rest.ClientResponse;
import com.inversoft.rest.JSONBodyHandler;
import com.inversoft.rest.JSONResponseHandler;
import com.inversoft.rest.RESTClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Client that connects to a Chef server to provide native bindings to the API.
 */
public class ChefClient {
  private final String baseURL;

  private final String organization;

  private final String privateKey;

  private final String userId;

  public String chefVersion = "12.10.24";

  public int connectTimeout = 2000;

  public int readTimeout = 2000;

  /**
   * Construct a new ChefClient.
   *
   * @param userId          User name correspond to the PEM key.
   * @param baseURL         Chef API server address.
   * @param organization    Chef organization.
   * @param pemPath         Path of the PEM key.
   */
  public ChefClient(String userId, String baseURL, String organization, String pemPath) {
    this.userId = userId;
    this.baseURL = baseURL;
    this.organization = organization;
    this.privateKey = getPrivateKey(pemPath);
  }

  /**
   * Construct a new ChefClient.
   *
   * @param userId          User name correspond to the PEM key.
   * @param baseURL         Chef API server address.
   * @param organization    Chef organization.
   * @param chefVersion     Chef API version.
   * @param pemPath         Path of the PEM key.
   */
  public ChefClient(String userId, String baseURL, String organization, String chefVersion, String pemPath) {
    this.userId = userId;
    this.baseURL = baseURL;
    this.organization = organization;
    this.chefVersion = chefVersion;
    this.privateKey = getPrivateKey(pemPath);
  }

  /**
   * Delete a Chef Client.
   *
   * @param name The name of the chef client to delete.
   * @return The client response that contains the status code, the response body and/or any exceptions that occurred.
   */
  public ClientResponse<Void, Void> deleteClient(String name) {
    return start(client ->
        client.urlSegment("organizations")
              .urlSegment(organization)
              .urlSegment("nodes")
              .urlSegment(name)
              .delete());
  }

  /**
   * Delete a Chef Node.
   *
   * @param name The name of the chef node to delete.
   * @return The client response that contains the status code, the response body and/or any exceptions that occurred.
   */
  public ClientResponse<Void, Void> deleteNode(String name) {
    return start(client ->
        client.urlSegment("organizations")
              .urlSegment(organization)
              .urlSegment("clients")
              .urlSegment(name)
              .delete());
  }

  /**
   * Retrieve a Chef Node.
   *
   * @param name The name of the chef node to retrieve.
   * @return The client response that contains the status code, the response body and/or any exceptions that occurred.
   */
  public ClientResponse<Node, Void> retrieveNode(String name) {
    return start(Node.class, client ->
        client.urlSegment("organizations")
              .urlSegment(organization)
              .urlSegment("nodes")
              .urlSegment(name)
              .successResponseHandler(new JSONResponseHandler<>(Node.class))
              .get());
  }

  /**
   * Retrieve all Chef Nodes in the organization.
   *
   * @return The client response that contains the status code, the response body and/or any exceptions that occurred.
   */
  public ClientResponse<Nodes, Void> retrieveNodes() {
    return start(Nodes.class, client ->
        client.urlSegment("organizations")
              .urlSegment(organization)
              .urlSegment("nodes")
              .successResponseHandler(new JSONResponseHandler<>(Nodes.class))
              .get());
  }

  /**
   * Update a Chef Node.
   *
   * @param name    The name of the chef node to updated.
   * @param node TThe new node.
   * @return The client response that contains the status code, the response body and/or any exceptions that occurred.
   */
  public ClientResponse<Node, Void> updateNode(String name, Node node) {
    return start(Node.class, client ->
        client.urlSegment("organizations")
              .urlSegment(organization)
              .urlSegment("nodes")
              .urlSegment(name)
              .bodyHandler(new JSONBodyHandler(node))
              .successResponseHandler(new JSONResponseHandler<>(Node.class))
              .put());
  }

  private String getPrivateKey(String pemPath) {
    try {
      return new String(Files.readAllBytes(Paths.get(pemPath)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * RSA sign the provided string with the configured private RSA key.
   *
   * @param string The string to sign.
   * @return The signed string.
   */
  private String rsaSignature(String string) {
    try {
      return SSLTools.signWithRSA(string, privateKey);
    } catch (GeneralSecurityException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * SHA-1 encode the byte array and return a Base64 encoded string.
   *
   * @param bytes the byte array to encode.
   * @return a Base64 encoded string.
   */
  private String sha1Base64Encode(byte[] bytes) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-1").digest(bytes);
      return new String(Base64.getEncoder().encode(digest));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Split the provided string so that each string does not exceed the provided line length.
   *
   * @param string     The string to split.
   * @param lineLength The max length of each line.
   * @return a list of strings.
   */
  private List<String> splitAtLength(String string, int lineLength) {
    List<String> strings = new ArrayList<>((string.length() / lineLength) + 1);
    int index = 0;
    while (index < string.length()) {
      strings.add(string.substring(index, Math.min(index + lineLength, string.length())));
      index += lineLength;
    }

    return strings;
  }

  private <T> ClientResponse<T, Void> start(Class<T> type, Consumer<RESTClient<T, Void>> consumer) {
    RESTClient<T, Void> client = new RESTClient<>(type, Void.TYPE)
        .url(this.baseURL)
        .connectTimeout(connectTimeout)
        .readTimeout(readTimeout);

    consumer.accept(client);

    String contentHash = sha1Base64Encode(client.bodyHandler != null ? client.bodyHandler.getBody() : new byte[]{});
    String timeStamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

    client.header("Accept", "application/json");
    client.header("X-Ops-Content-Hash", contentHash);
    client.header("X-Ops-Sign", "version=1.0");
    client.header("X-Ops-Timestamp", timeStamp);
    client.header("X-Ops-UserId", userId);
    client.header("X-Chef-Version", chefVersion);

    String uriPath = client.getURI().getPath();
    String hashedPath = sha1Base64Encode(uriPath.getBytes());

    // Signed Headers
    String sb = "Method:" + client.method.name() + "\n" +
        "Hashed Path:" + hashedPath + "\n" +
        "X-Ops-Content-Hash:" + contentHash + "\n" +
        "X-Ops-Timestamp:" + timeStamp + "\n" +
        "X-Ops-UserId:" + userId;

    String signedString = rsaSignature(sb);
    List<String> authorizationHeaders = splitAtLength(signedString, 60);
    IntStream.range(0, authorizationHeaders.size()).forEach(index ->
        client.header("X-Ops-Authorization-" + (index + 1), authorizationHeaders.get(index)));

    return client.go();
  }

  private ClientResponse<Void, Void> start(Consumer<RESTClient<Void, Void>> consumer) {
    return start(Void.TYPE, consumer);
  }
}
