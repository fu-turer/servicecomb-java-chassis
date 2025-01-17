/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicecomb.it.testcase;

import org.apache.servicecomb.core.Const;
import org.apache.servicecomb.it.extend.engine.GateRestTemplate;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class TestTraceEdge {
  static GateRestTemplate client = GateRestTemplate.createEdgeRestTemplate("trace");

  @Test
  public void echo() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(Const.TRACE_ID_NAME, "testId");

    HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

    String traceId = client.exchange("/echo-proxy", HttpMethod.GET, requestEntity, String.class).getBody();
    Assertions.assertEquals("testId", traceId);
    traceId = client.exchange("/echo-proxy", HttpMethod.GET, requestEntity, String.class).getBody();
    Assertions.assertEquals("testId", traceId);
  }
}
