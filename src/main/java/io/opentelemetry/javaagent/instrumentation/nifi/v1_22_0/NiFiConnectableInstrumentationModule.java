/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.nifi.v1_22_0;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ArrayList;
import java.util.List;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;

@AutoService(InstrumentationModule.class)
public final class NiFiConnectableInstrumentationModule extends InstrumentationModule {
  public NiFiConnectableInstrumentationModule() {
    super("nifi");
  }

  @Override
  public int order() {
    return 1;
  }

  @Override
  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
    return hasClassesNamed("org.apache.nifi.connectable.Connectable");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    ArrayList<TypeInstrumentation> result = new ArrayList<>();
    result.add(new NiFiConnectableInstrumentation());
    return result;
  }
}
