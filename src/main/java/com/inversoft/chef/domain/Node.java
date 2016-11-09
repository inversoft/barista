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

package com.inversoft.chef.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Chef Node
 *
 * @author Daniel DeGroff
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {
  public Map<String, Object> attributes = new LinkedHashMap<>();

  @JsonProperty("chef_environment")
  public String chefEnvironment;

  @JsonProperty("chef_type")
  public ChefType chefType;

  public Map<String, Object> defaults = new LinkedHashMap<>();

  @JsonProperty("json_class")
  public String jsonClass = "Chef::Node";

  public String name;

  public Map<String, Object> normal = new LinkedHashMap<>();

  public Map<String, Object> overrides = new LinkedHashMap<>();

  @JsonProperty("policy_group")
  public String policyGroup;

  @JsonProperty("policy_name")
  public String policyName;

  @JsonProperty("run_list")
  public List<String> runList = new ArrayList<>();
}
