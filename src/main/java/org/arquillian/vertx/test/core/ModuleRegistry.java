package org.arquillian.vertx.test.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arquillian.vertx.test.modulespi.Observer;

public class ModuleRegistry {

    private Map<String, Observer[]> subscriptions;
    private Set<String> publishables;
    
    public ModuleRegistry() {
        this.subscriptions = new HashMap<String, Observer[]>();
        this.publishables = new HashSet<String>();
    }
    
    public void addObserver(String name, Observer[] events) {
        this.subscriptions.put(name, events);
    }
    
    public void addEvent(String event) {
        this.publishables.add(event);
    }

    public Observer[] listensTo(String event) {
        List<Observer> modules = new ArrayList<Observer>();
        for(Map.Entry<String, Observer[]> entry : subscriptions.entrySet()) {
            for(Observer observer : entry.getValue()) {
                if(observer.getEvent().equals(event)) {
                    modules.add(observer);
                    continue;
                }
            }
        }
        return modules.toArray(new Observer[0]);
    }
    
    public String[] events() {
        Set<String> obervable = new HashSet<String>();
        for(Map.Entry<String, Observer[]> subscription : subscriptions.entrySet()) {
            for(Observer sub : subscription.getValue()) {
                obervable.add(sub.getEvent());
            }
        }
        return obervable.toArray(new String[0]);
    }
}
