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

import com.inversoft.chef.domain.Nodes;
import com.inversoft.rest.ClientResponse;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Daniel DeGroff
 */
public class ChefClientTest {
  @Test(enabled = false)
  public void test() throws Exception {
    ChefClient client = new ChefClient("userId", "https://chef.example.com", "organization", "/usr/local/path/to/pem/userId.pem", null, null);
    ClientResponse<Nodes, Void> response = client.retrieveNodes();
    assertEquals(response.status, 200);
  }
}
