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
import com.inversoft.rest.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Daniel DeGroff
 */
public class ChefClientTest {
  private static String organization;

  private static String pemPath;

  private static String url;

  private ChefClient client;

  @BeforeSuite
  public static void loadConfig() throws IOException {
    try (Reader reader = Files.newBufferedReader(Paths.get("config.properties"))) {
      Properties properties = new Properties();
      properties.load(reader);
      url = properties.getProperty("url");
      organization = properties.getProperty("organization");

      assertNotNull(url);
      assertNotNull(organization);
    }

    pemPath = System.getProperty("user.home") + "/.chef/" + System.getProperty("user.name") + ".pem";

    // Java keystore (trustStore) for SSL certs that need a chain to be validated
    if (Files.isRegularFile(Paths.get("cacerts"))) {
      System.setProperty("javax.net.ssl.trustStore", "cacerts");
    }
  }

  @BeforeMethod
  public void createClient() {
    client = new ChefClient(System.getProperty("user.name"), url, organization, pemPath, this::handleSuccess, this::handleError);
  }

  @Test(enabled = false)
  public void retrieve() throws Exception {
    ClientResponse<Nodes, Void> response = client.retrieveNodes();
    assertEquals(response.status, 200);
    assertTrue(response.successResponse.size() > 0);
  }

  @Test(enabled = false)
  public void updateNode() throws Exception {
    Node node = client.retrieveNode$("Passport-1-passport");
    node.normal.put("chef-client-test", System.currentTimeMillis());

    ClientResponse<Node, Void> response = client.updateNode("Passport-1-passport", node);
    assertEquals(response.status, 200);
    assertNotNull(response.successResponse);
  }

  private void handleError(ClientResponse<?, Void> response) {
    fail("Got an error! Status code [" + response.status + "]. Exception [" + response.exception + "]. Error body [" + response.errorResponse + "]");
  }

  private Object handleSuccess(ClientResponse<?, Void> response) {
    return response.successResponse;
  }
}
