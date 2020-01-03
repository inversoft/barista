/*
 * Copyright (c) 2020, Inversoft Inc., All Rights Reserved
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

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Chef Cookbook
 *
 * @author Daniel DeGroff
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cookbook {
  /**
   * Any other properties on the incoming JSON response from Chef not accounted for by name will end up here.
   */
  @JsonIgnore
  public Map<String, Object> other = new LinkedHashMap<>();

  public URI url;

  public List<Version> versions = new ArrayList<>();

  @JsonAnySetter
  public void setOtherAttribute(String key, Object value) {
    if (value != null) {
      other.put(key, value);
    }
  }

  public static class Version {
    /**
     * Any other properties on the incoming JSON response from Chef not accounted for by name will end up here.
     */
    @JsonIgnore
    public Map<String, Object> other = new LinkedHashMap<>();

    public URI url;

    public String version;

    @JsonAnySetter
    public void setOtherAttribute(String key, Object value) {
      if (value != null) {
        other.put(key, value);
      }
    }
  }
}
