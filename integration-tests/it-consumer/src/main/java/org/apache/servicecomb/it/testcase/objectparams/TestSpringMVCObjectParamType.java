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

package org.apache.servicecomb.it.testcase.objectparams;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.servicecomb.it.Consumers;
import org.apache.servicecomb.it.schema.objectparams.Color;
import org.apache.servicecomb.it.schema.objectparams.FlattenObjectRequest;
import org.apache.servicecomb.it.schema.objectparams.FlattenObjectResponse;
import org.apache.servicecomb.it.schema.objectparams.FluentSetterFlattenObjectRequest;
import org.apache.servicecomb.it.schema.objectparams.FluentSetterFlattenObjectResponse;
import org.apache.servicecomb.it.schema.objectparams.GenericObjectParam;
import org.apache.servicecomb.it.schema.objectparams.InnerRecursiveObjectParam;
import org.apache.servicecomb.it.schema.objectparams.MultiLayerObjectParam;
import org.apache.servicecomb.it.schema.objectparams.MultiLayerObjectParam2;
import org.apache.servicecomb.it.schema.objectparams.ObjectParamTypeSchema;
import org.apache.servicecomb.it.schema.objectparams.QueryObjectModel;
import org.apache.servicecomb.it.schema.objectparams.RecursiveObjectParam;
import org.apache.servicecomb.it.schema.objectparams.TestNullFieldAndDefaultValueParam;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import io.vertx.core.json.Json;

public class TestSpringMVCObjectParamType {
  interface SpringMVCObjectParamTypeSchemaIntf extends ObjectParamTypeSchema {
    TestNullFieldAndDefaultValueParam testNullFieldAndDefaultValue(Object request);

    FlattenObjectRequest testQueryObjectParam(byte anByte, short anShort, int anInt, long anLong, float anFloat,
        double anDouble, boolean anBoolean, char anChar, Byte anWrappedByte, Short anWrappedShort,
        Integer anWrappedInteger,
        Long anWrappedLong, Float anWrappedFloat, Double anWrappedDouble, Boolean anWrappedBoolean,
        Character anWrappedCharacter, String string, Color color);

    FluentSetterFlattenObjectRequest testFluentSetterQueryObjectParam(byte anByte, short anShort, int anInt,
        long anLong, float anFloat,
        double anDouble, boolean anBoolean, char anChar, Byte anWrappedByte, Short anWrappedShort,
        Integer anWrappedInteger,
        Long anWrappedLong, Float anWrappedFloat, Double anWrappedDouble, Boolean anWrappedBoolean,
        Character anWrappedCharacter, String string, Color color);

    String testQueryObjectWithHeader(String prefix, int index, String name);

    String testQueryObjectWithHeaderName(String prefix, int index, String name);

    String testQueryObjectWithHeaderValue(String prefix, int index, String name);

    String testQueryObjectWithHeaderValueAndName(String prefix, String suffix, int index, String name);

    String testQueryObjectWithParam(String prefix, int index, String name);

    String testQueryObjectWithParamName(String prefix, int index, String name);

    String testQueryObjectWithParamValue(String prefix, int index, String name);
  }

  static Consumers<SpringMVCObjectParamTypeSchemaIntf> consumers =
      new Consumers<>("SpringMVCObjectParamTypeSchema", SpringMVCObjectParamTypeSchemaIntf.class);

  private final String prefix = "prefix-";

  private final String suffix = "-suffix";

  private final QueryObjectModel queryModel = new QueryObjectModel(23, "demo");

  private final String queryParam = "index=23&name=demo";

  @Test
  public void testFlattenObjectParam_rpc() {
    FlattenObjectRequest request = FlattenObjectRequest.createFlattenObjectRequest();
    FlattenObjectResponse response = consumers.getIntf().testFlattenObjectParam(request);
    Assertions.assertEquals(Json.encode(request), Json.encode(response));

    request = new FlattenObjectRequest();
    response = consumers.getIntf().testFlattenObjectParam(request);
    Assertions.assertEquals(Json.encode(request), Json.encode(response));
  }

  @Test
  public void testFlattenObjectParam_rt() {
    FlattenObjectRequest request = FlattenObjectRequest.createFlattenObjectRequest();
    FlattenObjectResponse response = consumers.getSCBRestTemplate()
        .postForObject("/testFlattenObjectParam", request, FlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(response));

    request = new FlattenObjectRequest();
    response = consumers.getSCBRestTemplate()
        .postForObject("/testFlattenObjectParam", request, FlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(response));
  }

  @Test
  public void testFlattenObjectParam_edge() {
    FlattenObjectRequest request = FlattenObjectRequest.createFlattenObjectRequest();
    FlattenObjectResponse response = consumers.getEdgeRestTemplate()
        .postForObject("/testFlattenObjectParam", request, FlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(response));
    response = consumers.getEdgeRestTemplate()
        .postForObject("/testFlattenObjectParam", request, FlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(response));

    request = new FlattenObjectRequest();
    ResponseEntity<FlattenObjectResponse> responseEntity = consumers.getEdgeRestTemplate()
        .postForEntity("/testFlattenObjectParam", request, FlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(responseEntity.getBody()));
    Assertions.assertEquals(FlattenObjectResponse.class, responseEntity.getBody().getClass());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    responseEntity = consumers.getEdgeRestTemplate()
        .postForEntity("/testFlattenObjectParam", request, FlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(responseEntity.getBody()));
    Assertions.assertEquals(FlattenObjectResponse.class, responseEntity.getBody().getClass());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
  }

  @Test
  public void testFluentSetterFlattenObjectParam_rpc() {
    FluentSetterFlattenObjectRequest fluentRequest = FluentSetterFlattenObjectRequest.createFlattenObjectRequest();
    FluentSetterFlattenObjectResponse fluentResponse = consumers.getIntf()
        .testFluentSetterFlattenObjectParam(fluentRequest);
    Assertions.assertEquals(Json.encode(fluentRequest), Json.encode(fluentResponse));

    fluentRequest = new FluentSetterFlattenObjectRequest();
    fluentResponse = consumers.getIntf().testFluentSetterFlattenObjectParam(fluentRequest);
    Assertions.assertEquals(Json.encode(fluentRequest), Json.encode(fluentResponse));
  }

  @Test
  public void testFluentSetterFlattenObjectParam_rt() {
    FluentSetterFlattenObjectRequest fluentRequest = FluentSetterFlattenObjectRequest.createFlattenObjectRequest();
    FluentSetterFlattenObjectResponse fluentResponse = consumers.getSCBRestTemplate()
        .postForObject("/testFluentSetterFlattenObjectParam", fluentRequest, FluentSetterFlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(fluentRequest), Json.encode(fluentResponse));

    fluentRequest = new FluentSetterFlattenObjectRequest();
    fluentResponse = consumers.getSCBRestTemplate()
        .postForObject("/testFluentSetterFlattenObjectParam", fluentRequest, FluentSetterFlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(fluentRequest), Json.encode(fluentResponse));
  }

  @Test
  public void testFluentSetterFlattenObjectParam_edge() {
    FluentSetterFlattenObjectRequest fluentRequest = FluentSetterFlattenObjectRequest.createFlattenObjectRequest();
    FluentSetterFlattenObjectResponse response = consumers.getEdgeRestTemplate()
        .postForObject("/testFluentSetterFlattenObjectParam", fluentRequest, FluentSetterFlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(fluentRequest), Json.encode(response));
    response = consumers.getEdgeRestTemplate()
        .postForObject("/testFluentSetterFlattenObjectParam", fluentRequest, FluentSetterFlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(fluentRequest), Json.encode(response));

    fluentRequest = new FluentSetterFlattenObjectRequest();
    ResponseEntity<FluentSetterFlattenObjectResponse> responseEntity = consumers.getEdgeRestTemplate()
        .postForEntity("/testFluentSetterFlattenObjectParam", fluentRequest, FluentSetterFlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(fluentRequest), Json.encode(responseEntity.getBody()));
    Assertions.assertEquals(FluentSetterFlattenObjectResponse.class, responseEntity.getBody().getClass());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    responseEntity = consumers.getEdgeRestTemplate()
        .postForEntity("/testFluentSetterFlattenObjectParam", fluentRequest, FluentSetterFlattenObjectResponse.class);
    Assertions.assertEquals(Json.encode(fluentRequest), Json.encode(responseEntity.getBody()));
    Assertions.assertEquals(FluentSetterFlattenObjectResponse.class, responseEntity.getBody().getClass());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
  }

  @Test
  public void testMultiLayerObjectParam_rpc() {
    MultiLayerObjectParam request = new MultiLayerObjectParam("sss-1", new Date(),
        new MultiLayerObjectParam2("sss-2", 12.12, FlattenObjectRequest.createFlattenObjectRequest()));
    MultiLayerObjectParam response = consumers.getIntf().testMultiLayerObjectParam(request);
    Assertions.assertEquals(request, response);
//  Highway will not give null return value
    response = consumers.getIntf().testMultiLayerObjectParam(null);
    Assertions.assertTrue(response == null || response.getString() == null);
  }

  @Test
  public void testMultiLayerObjectParam_rt() {
    MultiLayerObjectParam request = new MultiLayerObjectParam("sss-1", new Date(),
        new MultiLayerObjectParam2("sss-2", 12.12, FlattenObjectRequest.createFlattenObjectRequest()));
    ResponseEntity<MultiLayerObjectParam> responseEntity = consumers.getSCBRestTemplate()
        .exchange("/testMultiLayerObjectParam", HttpMethod.PUT,
            new HttpEntity<>(request), MultiLayerObjectParam.class);
    Assertions.assertEquals(request, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());

    responseEntity = consumers.getSCBRestTemplate()
        .exchange("/testMultiLayerObjectParam", HttpMethod.PUT,
            new HttpEntity<>(null), MultiLayerObjectParam.class);
    //  Highway will not give null return value
    Assertions.assertTrue(responseEntity.getBody() == null || responseEntity.getBody().getString() == null);
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
  }

  @Test
  public void testMultiLayerObjectParam_edge() {
    MultiLayerObjectParam request = new MultiLayerObjectParam("sss-1", new Date(),
        new MultiLayerObjectParam2("sss-2", 12.12, FlattenObjectRequest.createFlattenObjectRequest()));
    ResponseEntity<MultiLayerObjectParam> responseEntity = consumers.getEdgeRestTemplate()
        .exchange("/testMultiLayerObjectParam", HttpMethod.PUT,
            new HttpEntity<>(request), MultiLayerObjectParam.class);
    Assertions.assertEquals(request, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    responseEntity = consumers.getEdgeRestTemplate()
        .exchange("/testMultiLayerObjectParam", HttpMethod.PUT,
            new HttpEntity<>(request), MultiLayerObjectParam.class);
    Assertions.assertEquals(request, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());

    responseEntity = consumers.getEdgeRestTemplate()
        .exchange("/testMultiLayerObjectParam", HttpMethod.PUT,
            new HttpEntity<>(null), MultiLayerObjectParam.class);
    // Highway will not return null
    Assertions.assertTrue(responseEntity.getBody() == null || responseEntity.getBody().getString() == null);
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    responseEntity = consumers.getEdgeRestTemplate()
        .exchange("/testMultiLayerObjectParam", HttpMethod.PUT,
            new HttpEntity<>(null), MultiLayerObjectParam.class);
    // Highway will not return null
    Assertions.assertTrue(responseEntity.getBody() == null || responseEntity.getBody().getString() == null);
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
  }

  @Test
  public void testRecursiveObjectParam_rpc() {
    RecursiveObjectParam request = createRecursiveObjectParam();
    RecursiveObjectParam response = consumers.getIntf().testRecursiveObjectParam(request);
    Assertions.assertEquals(request, response);
  }

  @Test
  public void testRecursiveObjectParam_rt() {
    RecursiveObjectParam request = createRecursiveObjectParam();
    ResponseEntity<RecursiveObjectParam> responseEntity = consumers.getSCBRestTemplate()
        .postForEntity("/testRecursiveObjectParam", request, RecursiveObjectParam.class);
    Assertions.assertEquals(request, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
  }

  @Test
  public void testRecursiveObjectParam_edge() {
    RecursiveObjectParam request = createRecursiveObjectParam();
    ResponseEntity<RecursiveObjectParam> responseEntity = consumers.getEdgeRestTemplate()
        .postForEntity("/testRecursiveObjectParam", request, RecursiveObjectParam.class);
    Assertions.assertEquals(request, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    responseEntity = consumers.getEdgeRestTemplate()
        .postForEntity("/testRecursiveObjectParam", request, RecursiveObjectParam.class);
    Assertions.assertEquals(request, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
  }

  @Test
  public void testListObjectParam_rpc() {
    List<GenericObjectParam<List<RecursiveObjectParam>>> request = Arrays.asList(
        new GenericObjectParam<>("s1", 1,
            Arrays.asList(
                createRecursiveObjectParam(),
                createRecursiveObjectParam()
            )),
        new GenericObjectParam<>("s2", 2, null)
    );
    List<GenericObjectParam<List<RecursiveObjectParam>>> response = consumers.getIntf()
        .testListObjectParam(request);
    Assertions.assertEquals(request, response);
  }

  @Test
  public void testListObjectParam_rt() {
    List<GenericObjectParam<List<RecursiveObjectParam>>> request = Arrays.asList(
        new GenericObjectParam<>("s1", 1,
            Arrays.asList(
                createRecursiveObjectParam(),
                createRecursiveObjectParam()
            )),
        new GenericObjectParam<>("s2", 2, null)
    );
    @SuppressWarnings("unchecked")
    List<GenericObjectParam<List<RecursiveObjectParam>>> response = consumers.getSCBRestTemplate()
        .postForObject("/testListObjectParam", request, List.class);
    Assertions.assertEquals(request, response);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testListObjectParam_edge() {
    List<GenericObjectParam<List<RecursiveObjectParam>>> request = Arrays.asList(
        new GenericObjectParam<>("s1", 1,
            Arrays.asList(
                createRecursiveObjectParam(),
                createRecursiveObjectParam()
            )),
        new GenericObjectParam<>("s2", 2, null)
    );

    List<GenericObjectParam<List<RecursiveObjectParam>>> responseRest = consumers.getEdgeRestTemplate()
        .postForObject("/testListObjectParam", request, List.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(responseRest));
    List<GenericObjectParam<List<RecursiveObjectParam>>> responseHighway = consumers.getEdgeRestTemplate()
        .postForObject("/testListObjectParam", request, List.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(responseHighway));
  }

  @Test
  public void testMapObjectParam() {
    Map<String, GenericObjectParam<Map<String, GenericObjectParam<RecursiveObjectParam>>>> request = new LinkedHashMap<>();
    LinkedHashMap<String, GenericObjectParam<RecursiveObjectParam>> innerMap = new LinkedHashMap<>();
    innerMap.put("k1-1", new GenericObjectParam<>("k1-1", 11, createRecursiveObjectParam()));
    innerMap.put("k1-2", new GenericObjectParam<>("k1-2", 12, createRecursiveObjectParam()));
    request.put("k1", new GenericObjectParam<>("k1", 1, innerMap));
    innerMap = new LinkedHashMap<>();
    innerMap.put("k2-1", new GenericObjectParam<>("k2-1", 21, createRecursiveObjectParam()));
    request.put("k2", new GenericObjectParam<>("k2", 2, innerMap));
    Map<String, GenericObjectParam<Map<String, GenericObjectParam<RecursiveObjectParam>>>> response =
        consumers.getIntf().testMapObjectParam(request);
    Assertions.assertEquals(Json.encode(request), Json.encode(response));

    @SuppressWarnings("unchecked")
    Map<String, GenericObjectParam<Map<String, GenericObjectParam<RecursiveObjectParam>>>> responseRT
        = consumers.getSCBRestTemplate().postForObject("/testMapObjectParam", request, Map.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(responseRT));

    @SuppressWarnings("unchecked")
    Map<String, GenericObjectParam<Map<String, GenericObjectParam<RecursiveObjectParam>>>> responseEdge
        = consumers.getEdgeRestTemplate().postForObject("/testMapObjectParam", request, Map.class);
    Assertions.assertEquals(Json.encode(request), Json.encode(responseEdge));
  }

  @Test
  public void testQueryObjectParam() {
    FlattenObjectRequest expected = FlattenObjectRequest.createFlattenObjectRequest();
    FlattenObjectRequest response = consumers.getIntf().testQueryObjectParam(
        expected.getAnByte(), expected.getAnShort(), expected.getAnInt(), expected.getAnLong(), expected.getAnFloat(),
        expected.getAnDouble(), expected.isAnBoolean(), expected.getAnChar(),
        expected.getAnWrappedByte(), expected.getAnWrappedShort(), expected.getAnWrappedInteger(),
        expected.getAnWrappedLong(), expected.getAnWrappedFloat(), expected.getAnWrappedDouble(),
        expected.getAnWrappedBoolean(), expected.getAnWrappedCharacter(),
        expected.getString(), expected.getColor()
    );
    Assertions.assertEquals(expected, response);

    StringBuilder requestUriBuilder = new StringBuilder();
    requestUriBuilder.append("/testQueryObjectParam?")
        .append("anByte=" + expected.getAnByte()).append("&")
        .append("anShort=" + expected.getAnShort()).append("&")
        .append("anInt=" + expected.getAnInt()).append("&")
        .append("anLong=" + expected.getAnLong()).append("&")
        .append("anFloat=" + expected.getAnFloat()).append("&")
        .append("anDouble=" + expected.getAnDouble()).append("&")
        .append("anBoolean=" + expected.isAnBoolean()).append("&")
        .append("anChar=" + expected.getAnChar()).append("&")
        .append("anWrappedByte=" + expected.getAnWrappedByte()).append("&")
        .append("anWrappedShort=" + expected.getAnWrappedShort()).append("&")
        .append("anWrappedInteger=" + expected.getAnWrappedInteger()).append("&")
        .append("anWrappedLong=" + expected.getAnWrappedLong()).append("&")
        .append("anWrappedFloat=" + expected.getAnWrappedFloat()).append("&")
        .append("anWrappedDouble=" + expected.getAnWrappedDouble()).append("&")
        .append("anWrappedBoolean=" + expected.getAnWrappedBoolean()).append("&")
        .append("anWrappedCharacter=" + expected.getAnWrappedCharacter()).append("&")
        .append("string=" + expected.getString()).append("&")
        .append("color=" + expected.getColor());
    ResponseEntity<FlattenObjectRequest> responseEntity = consumers.getSCBRestTemplate()
        .getForEntity(requestUriBuilder.toString(), FlattenObjectRequest.class);
    Assertions.assertEquals(expected, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());

    responseEntity = consumers.getEdgeRestTemplate()
        .getForEntity(requestUriBuilder.toString(), FlattenObjectRequest.class);
    Assertions.assertEquals(expected, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());

    expected.setAnWrappedBoolean(null);
    expected.setString(null);
    expected.setAnInt(0);
    expected.setAnWrappedInteger(null);
    response = consumers.getIntf().testQueryObjectParam(
        expected.getAnByte(), expected.getAnShort(), expected.getAnInt(), expected.getAnLong(), expected.getAnFloat(),
        expected.getAnDouble(), expected.isAnBoolean(), expected.getAnChar(),
        expected.getAnWrappedByte(), expected.getAnWrappedShort(), expected.getAnWrappedInteger(),
        expected.getAnWrappedLong(), expected.getAnWrappedFloat(), expected.getAnWrappedDouble(),
        expected.getAnWrappedBoolean(), expected.getAnWrappedCharacter(),
        expected.getString(), expected.getColor()
    );
    Assertions.assertEquals(expected, response);
  }

  @Test
  public void testFluentSetterQueryObjectParam() {
    FluentSetterFlattenObjectRequest expected = FluentSetterFlattenObjectRequest.createFlattenObjectRequest();
    FluentSetterFlattenObjectRequest response = consumers.getIntf().testFluentSetterQueryObjectParam(
        expected.getAnByte(), expected.getAnShort(), expected.getAnInt(), expected.getAnLong(), expected.getAnFloat(),
        expected.getAnDouble(), expected.isAnBoolean(), expected.getAnChar(),
        expected.getAnWrappedByte(), expected.getAnWrappedShort(), expected.getAnWrappedInteger(),
        expected.getAnWrappedLong(), expected.getAnWrappedFloat(), expected.getAnWrappedDouble(),
        expected.getAnWrappedBoolean(), expected.getAnWrappedCharacter(),
        expected.getString(), expected.getColor()
    );
    Assertions.assertEquals(expected, response);

    StringBuilder requestUriBuilder = new StringBuilder();
    requestUriBuilder.append("/testFluentSetterQueryObjectParam?")
        .append("anByte=" + expected.getAnByte()).append("&")
        .append("anShort=" + expected.getAnShort()).append("&")
        .append("anInt=" + expected.getAnInt()).append("&")
        .append("anLong=" + expected.getAnLong()).append("&")
        .append("anFloat=" + expected.getAnFloat()).append("&")
        .append("anDouble=" + expected.getAnDouble()).append("&")
        .append("anBoolean=" + expected.isAnBoolean()).append("&")
        .append("anChar=" + expected.getAnChar()).append("&")
        .append("anWrappedByte=" + expected.getAnWrappedByte()).append("&")
        .append("anWrappedShort=" + expected.getAnWrappedShort()).append("&")
        .append("anWrappedInteger=" + expected.getAnWrappedInteger()).append("&")
        .append("anWrappedLong=" + expected.getAnWrappedLong()).append("&")
        .append("anWrappedFloat=" + expected.getAnWrappedFloat()).append("&")
        .append("anWrappedDouble=" + expected.getAnWrappedDouble()).append("&")
        .append("anWrappedBoolean=" + expected.getAnWrappedBoolean()).append("&")
        .append("anWrappedCharacter=" + expected.getAnWrappedCharacter()).append("&")
        .append("string=" + expected.getString()).append("&")
        .append("color=" + expected.getColor());
    ResponseEntity<FluentSetterFlattenObjectRequest> responseEntity = consumers.getSCBRestTemplate()
        .getForEntity(requestUriBuilder.toString(), FluentSetterFlattenObjectRequest.class);
    Assertions.assertEquals(expected, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());

    responseEntity = consumers.getEdgeRestTemplate()
        .getForEntity(requestUriBuilder.toString(), FluentSetterFlattenObjectRequest.class);
    Assertions.assertEquals(expected, responseEntity.getBody());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());

    expected.setAnWrappedBoolean(null);
    expected.setString(null);
    expected.setAnInt(0);
    expected.setAnWrappedInteger(null);
    response = consumers.getIntf().testFluentSetterQueryObjectParam(
        expected.getAnByte(), expected.getAnShort(), expected.getAnInt(), expected.getAnLong(), expected.getAnFloat(),
        expected.getAnDouble(), expected.isAnBoolean(), expected.getAnChar(),
        expected.getAnWrappedByte(), expected.getAnWrappedShort(), expected.getAnWrappedInteger(),
        expected.getAnWrappedLong(), expected.getAnWrappedFloat(), expected.getAnWrappedDouble(),
        expected.getAnWrappedBoolean(), expected.getAnWrappedCharacter(),
        expected.getString(), expected.getColor()
    );
    Assertions.assertEquals(expected, response);
  }

  @Test
  public void testQueryObjectWithHeader_rt() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("prefix", prefix);
    Assertions.assertEquals(prefix + queryModel,
        queryObjectHeader(consumers, headers, "/testQueryObjectWithHeader?" + queryParam));
  }

  @Test
  public void testQueryObjectWithHeader_pojo() {
    Assertions.assertEquals(prefix + queryModel,
        consumers.getIntf().testQueryObjectWithHeader(prefix, 23, "demo"));
  }

  @Test
  public void testQueryObjectWithHeaderName_rt() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("prefix", prefix);
    Assertions.assertEquals(prefix + queryModel,
        queryObjectHeader(consumers, headers, "/testQueryObjectWithHeaderName?" + queryParam));
  }

  @Test
  public void testQueryObjectWithHeaderName_pojo() {
    Assertions.assertEquals(prefix + queryModel,
        consumers.getIntf().testQueryObjectWithHeaderName(prefix, 23, "demo"));
  }

  @Test
  public void testQueryObjectWithHeaderValue_rt() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("prefix", prefix);
    Assertions.assertEquals(prefix + queryModel,
        queryObjectHeader(consumers, headers, "/testQueryObjectWithHeaderValue?" + queryParam));
  }

  @Test
  public void testQueryObjectWithHeaderValue_pojo() {
    Assertions.assertEquals(prefix + queryModel,
        consumers.getIntf().testQueryObjectWithHeaderValue(prefix, 23, "demo"));
  }

  @Test
  public void testQueryObjectWithHeaderValueAndName_rt() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("prefix", prefix);
    headers.add("suffix", suffix);
    Assertions.assertEquals(prefix + queryModel + suffix,
        queryObjectHeader(consumers, headers, "/testQueryObjectWithHeaderValueAndName?" + queryParam));
  }

  @Test
  public void testQueryObjectWithHeaderValueAndName_pojo() {
    Assertions.assertEquals(prefix + queryModel + suffix,
        consumers.getIntf().testQueryObjectWithHeaderValueAndName(prefix, suffix, 23, "demo"));
  }

  @Test
  public void testQueryObjectWithParam_rt() {
    Assertions.assertEquals(prefix + queryModel, consumers.getSCBRestTemplate()
        .getForObject("/testQueryObjectWithParam?prefix=" + prefix + "&" + queryParam, String.class));
  }

  @Test
  public void testQueryObjectWithParam_pojo() {
    Assertions.assertEquals(prefix + queryModel,
        consumers.getIntf().testQueryObjectWithParam(prefix, 23, "demo"));
  }

  @Test
  public void testQueryObjectWithParamName_rt() {
    Assertions.assertEquals(prefix + queryModel, consumers.getSCBRestTemplate()
        .getForObject("/testQueryObjectWithParamName?prefix=" + prefix + "&" + queryParam, String.class));
  }

  @Test
  public void testQueryObjectWithParamName_pojo() {
    Assertions.assertEquals(prefix + queryModel,
        consumers.getIntf().testQueryObjectWithParamName(prefix, 23, "demo"));
  }

  @Test
  public void testQueryObjectWithParamValue_rt() {
    Assertions.assertEquals(prefix + queryModel, consumers.getSCBRestTemplate()
        .getForObject("/testQueryObjectWithParamValue?prefix=" + prefix + "&" + queryParam, String.class));
  }

  @Test
  public void testQueryObjectWithParamValue_pojo() {
    Assertions.assertEquals(prefix + queryModel,
        consumers.getIntf().testQueryObjectWithParamValue(prefix, 23, "demo"));
  }

  protected String queryObjectHeader(Consumers<SpringMVCObjectParamTypeSchemaIntf> consumers, HttpHeaders headers,
      String url) {
    HttpEntity<?> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response = consumers.getSCBRestTemplate()
        .exchange(url,
            HttpMethod.GET,
            entity,
            String.class);
    return response.getBody();
  }

  private RecursiveObjectParam createRecursiveObjectParam() {
    return new RecursiveObjectParam(new InnerRecursiveObjectParam(1, "sss1",
        new RecursiveObjectParam(new InnerRecursiveObjectParam(2, "sss2", new RecursiveObjectParam()),
            new RecursiveObjectParam(new InnerRecursiveObjectParam(3, "sss3", new RecursiveObjectParam()),
                null,
                4L,
                "sss4",
                Color.GREEN),
            5L,
            "sss5",
            Color.RED
        )),
        new RecursiveObjectParam(new InnerRecursiveObjectParam(6, "sss6",
            new RecursiveObjectParam(new InnerRecursiveObjectParam(7, "sss7",
                new RecursiveObjectParam(new InnerRecursiveObjectParam(),
                    null, 8, "sss8", Color.BLUE)),
                new RecursiveObjectParam(),
                9L,
                "sss9",
                Color.RED)),
            new RecursiveObjectParam(),
            10,
            "sss10",
            Color.GREEN
        ),
        11,
        "sss11",
        Color.BLUE
    );
  }
}
