package com.courseregistration.gui;

import java.util.function.Consumer;

public interface Refreshable {
    
    void refresh();
    
    
    default void forceDashboardRefresh() {
        refresh();
    }
    
    
    default void setOnRefreshListener(Consumer<Refreshable> listener) {
       
    }
    
    
    default boolean isRefreshing() {
        return false;
    }
}