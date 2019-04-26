package org.infinity.semanticbrain.service;

public interface Cacheable {

    void refresh();

    void broadcastRefresh();

}