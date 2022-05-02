package com.tvd12.ezyfoxserver.client.testing.util;

import com.tvd12.ezyfoxserver.client.util.EzyQueue;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertEquals;

public class EzyQueueTest {

    @Test
    public void addTest() {
        // given
        Integer value = new Random().nextInt();
        EzyQueue<Integer> queue = new EzyQueue<>();

        // when
        boolean actual = queue.add(value);

        // then
        //noinspection PointlessBooleanExpression
        assert actual == true;
    }

    @Test
    public void addFullTest() {
        // given
        Integer value1 = new Random().nextInt();
        Integer value2 = new Random().nextInt();
        EzyQueue<Integer> queue = new EzyQueue<>(1);

        // when
        boolean add1 = queue.add(value1);
        boolean add2 = queue.add(value2);

        // then
        assert queue.getCapacity() == 1;
        //noinspection PointlessBooleanExpression
        assert add1 == true;
        //noinspection PointlessBooleanExpression
        assert add2 == false;
    }

    @Test
    public void offerTest() {
        // given
        Integer value = new Random().nextInt();
        EzyQueue<Integer> queue = new EzyQueue<>();

        // when
        boolean actual = queue.offer(value);

        // then
        //noinspection PointlessBooleanExpression
        assert actual == true;
    }

    @Test
    public void offerFullTest() {
        // given
        Integer value1 = new Random().nextInt();
        Integer value2 = new Random().nextInt();
        EzyQueue<Integer> queue = new EzyQueue<>(1);

        // when
        boolean add1 = queue.offer(value1);
        boolean add2 = queue.offer(value2);

        // then
        //noinspection PointlessBooleanExpression
        assert add1 == true;
        //noinspection PointlessBooleanExpression
        assert add2 == false;
    }

    @Test
    public void peekTest() {
        // given
        Integer value = new Random().nextInt();
        EzyQueue<Integer> queue = new EzyQueue<>();
        queue.offer(value);

        // when
        Integer actual = queue.peek();

        // then
        assert actual.equals(value);
        assert !queue.isEmpty();
    }

    @Test
    public void pollTest() {
        // given
        Integer value = new Random().nextInt();
        EzyQueue<Integer> queue = new EzyQueue<>();
        queue.offer(value);

        // when
        Integer actual = queue.poll();

        // then
        assert actual.equals(value);
        assert queue.isEmpty();
    }

    @Test
    public void pollAllTest() {
        // given
        Integer value1 = new Random().nextInt();
        Integer value2 = new Random().nextInt();
        EzyQueue<Integer> queue = new EzyQueue<>();
        queue.offer(value1);
        queue.offer(value2);

        // when
        List<Integer> actual = new ArrayList<>();
        queue.pollAll(actual);

        // then
        List<Integer> expected = Arrays.asList(value1, value2);
        assertEquals(actual, expected);
        assert queue.isEmpty();
    }

    @Test
    public void clearTest() {
        // given
        Integer value = new Random().nextInt();
        EzyQueue<Integer> queue = new EzyQueue<>();
        queue.offer(value);

        // when
        queue.clear();

        // then
        assert queue.isEmpty();
    }

}
