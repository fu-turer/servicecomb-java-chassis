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

package org.apache.servicecomb.loadbalance;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.core.Transport;
import org.apache.servicecomb.registry.api.registry.MicroserviceInstance;
import org.apache.servicecomb.registry.cache.CacheEndpoint;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.Server;

public class TestSessionSticknessRule {

  @Test
  public void testRuleFullOperation() {
    SessionStickinessRule rule = new SessionStickinessRule();

    LoadBalancer mockedLb = mock(LoadBalancer.class);
    Transport transport = mock(Transport.class);
    MicroserviceInstance instance1 = new MicroserviceInstance();
    instance1.setInstanceId("1234");
    ServiceCombServer mockedServer =
        new ServiceCombServer(null, transport, new CacheEndpoint("rest:127.0.0.1:8889", instance1));
    Invocation invocation = mock(Invocation.class);
    LoadBalancerStats stats = mock(LoadBalancerStats.class);
    Mockito.when(mockedLb.getLoadBalancerStats()).thenReturn(stats);
    rule.chooseServerWhenTimeout(Arrays.asList(mockedServer), invocation);
    mockedServer.setAlive(true);
    mockedServer.setReadyToServe(true);
    List<ServiceCombServer> allServers = Arrays.asList(mockedServer);
    rule.setLoadBalancer(mockedLb);


    Server s = rule.choose(allServers, invocation);
    Assertions.assertEquals(s, mockedServer);

    s = rule.choose(allServers, invocation);
    Assertions.assertEquals(s, mockedServer);
  }

  @Test
  public void testServerWithoutTimeoutAndWithThreshold() {

    boolean status = true;

    SessionStickinessRule ss = Mockito.spy(new SessionStickinessRule());

    Invocation invocation = mock(Invocation.class);
    ServiceCombServer server = mock(ServiceCombServer.class);
    List<ServiceCombServer> servers = new ArrayList<>();
    servers.add(server);

    ss.setLastServer(server);

    LoadBalancer mockedLb = mock(LoadBalancer.class);
    ss.setLoadBalancer(mockedLb);
    LoadBalancerStats stats = mock(LoadBalancerStats.class);
    Mockito.when(mockedLb.getLoadBalancerStats()).thenReturn(stats);
    Mockito.when(ss.isTimeOut()).thenReturn(false);
    Mockito.doReturn(true).when(ss).isErrorThresholdMet();

    try {
      ss.choose(servers, invocation);
    } catch (Exception e) {
      status = false;
    }
    Assertions.assertTrue(status);
  }

  @Test
  public void testServerWithTimeout() {

    boolean status = true;

    SessionStickinessRule ss = Mockito.spy(new SessionStickinessRule());

    Invocation invocation = mock(Invocation.class);
    ServiceCombServer server = mock(ServiceCombServer.class);
    List<ServiceCombServer> servers = new ArrayList<>();
    servers.add(server);

    ss.setLastServer(server);

    Mockito.doReturn(true).when(ss).isTimeOut();

    try {
      ss.choose(servers, invocation);
    } catch (Exception e) {
      status = false;
    }

    Assertions.assertTrue(status);
  }

  @Test
  public void testServerWithoutTimeoutException() {

    boolean status = true;

    SessionStickinessRule ss = Mockito.spy(new SessionStickinessRule());

    Invocation invocation = mock(Invocation.class);
    ServiceCombServer server = mock(ServiceCombServer.class);
    List<ServiceCombServer> servers = new ArrayList<>();
    servers.add(server);

    ss.setLastServer(server);

    Mockito.doReturn(false).when(ss).isTimeOut();

    try {
      ss.choose(servers, invocation);
    } catch (Exception e) {
      status = false;
    }
    Assertions.assertFalse(status);
  }

  @Test
  public void testServerWithoutTimeoutAndThreshold() {

    boolean status = true;

    SessionStickinessRule ss = Mockito.spy(new SessionStickinessRule());

    Invocation invocation = mock(Invocation.class);
    ServiceCombServer server = mock(ServiceCombServer.class);
    List<ServiceCombServer> servers = new ArrayList<>();
    servers.add(server);

    ss.setLastServer(server);

    Mockito.doReturn(false).when(ss).isTimeOut();

    Mockito.doReturn(false).when(ss).isErrorThresholdMet();
    try {
      ss.choose(servers, invocation);
    } catch (Exception e) {
      status = false;
    }
    Assertions.assertTrue(status);
  }

  @Test
  public void testServerWithActualServerObj() {

    boolean status = true;
    SessionStickinessRule ss = new SessionStickinessRule();

    Invocation invocation = mock(Invocation.class);
    ServiceCombServer server = mock(ServiceCombServer.class);
    List<ServiceCombServer> servers = new ArrayList<>();
    servers.add(server);

    ss.setLastServer(server);
    try {
      ss.choose(servers, invocation);
    } catch (Exception e) {
      status = false;
    }
    Assertions.assertTrue(status);
  }

  @Test
  public void testLastServerNotExist() {
    SessionStickinessRule rule = Mockito.spy(new SessionStickinessRule());

    Transport transport = mock(Transport.class);
    Invocation invocation = mock(Invocation.class);
    MicroserviceInstance instance1 = new MicroserviceInstance();
    instance1.setInstanceId("1234");
    ServiceCombServer mockedServer =
        new ServiceCombServer(null, transport, new CacheEndpoint("rest:127.0.0.1:8890", instance1));
    mockedServer.setAlive(true);
    mockedServer.setReadyToServe(true);
    mockedServer.setId("mockedServer");
    List<ServiceCombServer> allServers = Arrays.asList(mockedServer);
    LoadBalancer lb = new LoadBalancer(rule, "mockedServer");
    when(invocation.getLocalContext(LoadbalanceHandler.CONTEXT_KEY_SERVER_LIST)).thenReturn(allServers);
    rule.setLoadBalancer(lb);
    ServiceCombServer server = new ServiceCombServer(null, transport, new CacheEndpoint("rest:127.0.0.1:8890", instance1));
    rule.setLastServer(server);

    Mockito.doReturn(false).when(rule).isTimeOut();

    Mockito.doReturn(false).when(rule).isErrorThresholdMet();
    Server s = rule.choose(allServers, invocation);
    Assertions.assertEquals(mockedServer, s);
  }
}
