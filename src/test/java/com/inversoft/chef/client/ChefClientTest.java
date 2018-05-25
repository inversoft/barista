/*
 * Copyright (c) 2016-2018, Inversoft Inc., All Rights Reserved
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
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
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

  @AfterSuite
  public static void afterSuite() {
    SSLTools.enableSSLValidation();
  }

  @BeforeSuite
  public static void loadConfig() throws IOException {
    try (Reader reader = Files.newBufferedReader(Paths.get("src/test/resources/config.properties"))) {
      Properties properties = new Properties();
      properties.load(reader);
      url = properties.getProperty("url");
      organization = properties.getProperty("organization");

      assertNotNull(url);
      assertNotNull(organization);
    }

    pemPath = System.getProperty("user.home") + "/.chef/" + System.getProperty("user.name") + ".pem";

    // If you want to use a trust store - copy your cacerts file into /src/test/resources
    Path trustStore = Paths.get("src/test/resources/cacerts");
    if (Files.isRegularFile(trustStore)) {
      System.setProperty("javax.net.ssl.trustStore", trustStore.toAbsolutePath().toString());
    } else {
      SSLTools.disableSSLValidation();
    }
  }

  @BeforeMethod
  public void createClient() {
    client = new ChefClient(System.getProperty("user.name"), url, organization, pemPath);
  }

  @Test(enabled = false)
  public void retrieve() {
    ClientResponse<Nodes, Void> response = client.retrieveNodes();
    assertEquals(response.status, 200);
    assertTrue(response.successResponse.size() > 0);

    String nodeName = response.successResponse.keySet().iterator().next();
    ClientResponse<Node, Void> nodeResponse = client.retrieveNode(nodeName);
    assertEquals(nodeResponse.status, 200);
    assertNotNull(nodeResponse.successResponse);
  }

  @Test(enabled = false)
  public void updateNode() {
    Node node = client.retrieveNode("Passport-1-passport").successResponse;
    node.normal.put("chef-client-test", System.currentTimeMillis());

    ClientResponse<Node, Void> response = client.updateNode("Passport-1-passport", node);
    assertEquals(response.status, 200);
    assertNotNull(response.successResponse);
  }
}
