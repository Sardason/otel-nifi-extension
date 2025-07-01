package io.opentelemetry.javaagent.instrumentation.nifi.v1_22_0;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.instrumentation.api.util.VirtualField;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SpanLinkTracker {
    private static final VirtualField<Span, Set<SpanContext>> spanLinkMap =
            VirtualField.find(Span.class, Set.class);

    private static final Logger logger = Logger.getLogger(SpanLinkTracker.class.getName());

    private SpanLinkTracker() {}

    public static void addLink(Span span, SpanContext linkContext) {
        if (span == null || linkContext == null) {
            logger.warning("Span or Link is null");
            return;
        }

        Set<SpanContext> links = spanLinkMap.get(span);
        if (links == null) {
            links = ConcurrentHashMap.newKeySet();
            spanLinkMap.set(span, links);
        }

        if (links.add(linkContext)) {
            span.addLink(linkContext);
            logger.fine("Link added to span");
        } else {
            logger.fine("Duplicate link ignored");
        }
    }
}
