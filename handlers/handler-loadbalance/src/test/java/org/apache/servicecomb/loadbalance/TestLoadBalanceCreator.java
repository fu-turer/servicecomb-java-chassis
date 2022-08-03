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

import java.util.ArrayList;
import java.util.List;

import org.apache.servicecomb.core.Endpoint;
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.core.Transport;
import org.apache.servicecomb.foundation.test.scaffolding.config.ArchaiusUtils;
import org.apache.servicecomb.registry.api.registry.MicroserviceInstance;
import org.junit.Test;

import com.netflix.loadbalancer.Server;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

public class TestLoadBalanceCreator {

  Invocation invocation = Mockito.mock(Invocation.class);
  Transport transport = Mockito.mock(Transport.class);

  @Test
  public void testLoadBalanceWithRoundRobinRuleAndFilter() {
    // Robin components implementations require getReachableServers & getServerList have the same size, we add a test case for this.
    RoundRobinRuleExt rule = new RoundRobinRuleExt();
    List<ServiceCombServer> servers = new ArrayList<>();
    Endpoint host1 = new Endpoint(transport, "host1");
    MicroserviceInstance instance1 = new MicroserviceInstance();
    instance1.setInstanceId("instance1");
    ServiceCombServer server = new ServiceCombServer(null, host1, instance1);

    Endpoint host2 = new Endpoint(transport, "host2");
    MicroserviceInstance instance2 = new MicroserviceInstance();
    instance2.setInstanceId("instance2");
    ServiceCombServer server2 = new ServiceCombServer(null, host2, instance2);

    servers.add(server);
    servers.add(server2);
    LoadBalancer lb = new LoadBalancer(rule, "test");

    List<ServerListFilterExt> filters = new ArrayList<>();

    filters.add((serverList, invocation1) -> {
      List<ServiceCombServer> filteredServers = new ArrayList<>();
      for (ServiceCombServer server1 : servers) {
        if (server1.getHost().equals("host1")) {
          continue;
        }
        filteredServers.add(server1);
      }
      return filteredServers;
    });
    lb.setFilters(filters);

    Mockito.when(invocation.getLocalContext(LoadbalanceHandler.CONTEXT_KEY_SERVER_LIST)).thenReturn(servers);
    Server s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);
    s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);
    s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);
  }

  @Test
  public void testLoadBalanceWithRandomRuleAndFilter() {
    // Robin components implementations require getReachableServers & getServerList have the same size, we add a test case for this.
    RandomRuleExt rule = new RandomRuleExt();
    LoadBalancer lb = new LoadBalancer(rule, "service");

    List<ServiceCombServer> servers = new ArrayList<>();
    Endpoint host1 = new Endpoint(transport, "host1");
    MicroserviceInstance instance1 = new MicroserviceInstance();
    instance1.setInstanceId("instance1");
    ServiceCombServer server = new ServiceCombServer(null, host1, instance1);

    Endpoint host2 = new Endpoint(transport, "host2");
    MicroserviceInstance instance2 = new MicroserviceInstance();
    instance2.setInstanceId("instance2");
    ServiceCombServer server2 = new ServiceCombServer(null, host2, instance2);

    servers.add(server);
    servers.add(server2);

    List<ServerListFilterExt> filters = new ArrayList<>();
    filters.add((serverList, invocation1) -> {
      List<ServiceCombServer> filteredServers = new ArrayList<>();
      for (ServiceCombServer server1 : servers) {
        if (server1.getHost().equals("host1")) {
          continue;
        }
        filteredServers.add(server1);
      }
      return filteredServers;
    });
    lb.setFilters(filters);
    Mockito.when(invocation.getLocalContext(LoadbalanceHandler.CONTEXT_KEY_SERVER_LIST)).thenReturn(servers);
    Server s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);
    s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);
    s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);
  }

  @Test
  public void testLoadBalanceWithWeightedResponseTimeRuleAndFilter() {
    Endpoint endpoint1 = Mockito.mock(Endpoint.class);
    Endpoint endpoint2 = Mockito.mock(Endpoint.class);
    // Robin components implementations require getReachableServers & getServerList have the same size, we add a test case for this.
    WeightedResponseTimeRuleExt rule = new WeightedResponseTimeRuleExt();
    LoadBalancer lb = new LoadBalancer(rule, "service");

    List<ServiceCombServer> servers = new ArrayList<>();
    MicroserviceInstance instance1 = new MicroserviceInstance();
    instance1.setInstanceId("ii01");
    MicroserviceInstance instance2 = new MicroserviceInstance();
    instance2.setInstanceId("ii02");
    ServiceCombServer server = new ServiceCombServer(null, endpoint1, instance1);
    ServiceCombServer server2 = new ServiceCombServer(null, endpoint2, instance2);

    Mockito.when(endpoint1.getEndpoint()).thenReturn("host1");
    Mockito.when(endpoint2.getEndpoint()).thenReturn("host2");

    servers.add(server);
    servers.add(server2);
    List<ServerListFilterExt> filters = new ArrayList<>();
    filters.add((serverList, invocation1) -> {
      List<ServiceCombServer> filteredServers = new ArrayList<>();
      for (ServiceCombServer server1 : servers) {
        if (server1.getHost().equals("host1")) {
          continue;
        }
        filteredServers.add(server1);
      }
      return filteredServers;
    });
    lb.setFilters(filters);
    Mockito.when(invocation.getLocalContext(LoadbalanceHandler.CONTEXT_KEY_SERVER_LIST)).thenReturn(servers);
    Server s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);
    s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);
    s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);
  }

  @Test
  public void testLoadBalanceWithSessionSticknessRule() {
    SessionStickinessRule rule = new SessionStickinessRule();
    LoadBalancer lb = new LoadBalancer(rule, "service");

    List<ServiceCombServer> servers = new ArrayList<>();
    Endpoint host1 = new Endpoint(transport, "host1");
    MicroserviceInstance instance1 = new MicroserviceInstance();
    ServiceCombServer server = new ServiceCombServer(null, host1, instance1);
    instance1.setInstanceId("instance1");

    Endpoint host2 = new Endpoint(transport, "host2");
    MicroserviceInstance instance2 = new MicroserviceInstance();
    ServiceCombServer server2 = new ServiceCombServer(null, host2, instance2);
    instance2.setInstanceId("instance2");

    servers.add(server);
    servers.add(server2);

    lb.setFilters(new ArrayList<>());
    Mockito.when(invocation.getLocalContext(LoadbalanceHandler.CONTEXT_KEY_SERVER_LIST)).thenReturn(servers);

    Server s = lb.chooseServer(invocation);
    Assertions.assertEquals(server, s);
    s = lb.chooseServer(invocation);
    Assertions.assertEquals(server, s);

    long time = rule.getLastAccessedTime();
    rule.setLastAccessedTime(time - 1000 * 300);
    ArchaiusUtils.setProperty("cse.loadbalance.service.SessionStickinessRule.sessionTimeoutInSeconds", 9);
    s = lb.chooseServer(invocation);
    Assertions.assertEquals(server2, s);

    ArchaiusUtils.setProperty("cse.loadbalance.service.SessionStickinessRule.successiveFailedTimes", 5);
    lb.getLoadBalancerStats().incrementSuccessiveConnectionFailureCount(s);
    lb.getLoadBalancerStats().incrementSuccessiveConnectionFailureCount(s);
    lb.getLoadBalancerStats().incrementSuccessiveConnectionFailureCount(s);
    lb.getLoadBalancerStats().incrementSuccessiveConnectionFailureCount(s);
    lb.getLoadBalancerStats().incrementSuccessiveConnectionFailureCount(s);
    s = lb.chooseServer(invocation);
    Assertions.assertEquals(server, s);
  }
}
