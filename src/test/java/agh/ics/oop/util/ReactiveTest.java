package agh.ics.oop.util;

import agh.ics.oop.reactive.Reactive;
import agh.ics.oop.reactive.ReactivePropagate;
import org.junit.Assert;
import org.junit.Test;

public class ReactiveTest {
    @Test
    public void testToString() {
        Reactive<String> reactive = new Reactive<>("hello");
        Assert.assertEquals("hello", reactive.toString());
    }

    @Test
    public void testLoop() {
        Reactive<Integer> reactive1 = new Reactive<>(0);
        Reactive<Integer> reactive2 = new Reactive<>(0);
        reactive1.bindTo(reactive2, n -> n + 1, ReactivePropagate.ALL);
        reactive2.bindTo(reactive1, n -> n + 1);

        reactive1.setValue(1);


        Assert.assertEquals(1, reactive1.getValue().intValue());
        Assert.assertEquals(2, reactive2.getValue().intValue());
    }
}
