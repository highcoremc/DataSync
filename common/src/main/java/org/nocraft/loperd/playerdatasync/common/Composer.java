package org.nocraft.loperd.playerdatasync.common;

import java.util.ArrayList;

public class Composer<T extends Registerable & Shutdownable> implements Registerable, Shutdownable {

    private final ArrayList<T> objects = new ArrayList<>();

    public Composer() {}

    @SafeVarargs
    public Composer(T... objects) {
        for (T object : objects) {
            this.add(object);
        }
    }

    public void add(T object) {
        this.objects.add(object);
    }

    @Override
    public void register() {
        for (T object : this.objects) {
            object.register();
        }
    }

    @Override
    public void unregister() {
        for (T object : this.objects) {
            object.unregister();
        }
    }

    @Override
    public void shutdown() {
        for (T object : this.objects) {
            object.shutdown();
        }
    }
}
