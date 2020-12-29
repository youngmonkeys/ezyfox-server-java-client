package com.tvd12.ezyfoxserver.client.testing.util;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.util.EzyValueStack;

public class EzyValueStackTest {

	@Test
	public void newValueStack() {
		// given
		Integer defaultValue = new Random().nextInt();
		
		// when
		EzyValueStack<Integer> stack = new EzyValueStack<>(defaultValue);
		
		// then
		assert stack.top().equals(defaultValue);
		assert stack.last().equals(defaultValue);
	}
	
	@Test
	public void top() {
		// given
		Integer defaultValue = new Random().nextInt();
		Integer value1 = new Random().nextInt();
		Integer value2 = new Random().nextInt();
		EzyValueStack<Integer> stack = new EzyValueStack<>(defaultValue);
		stack.push(value1);
		stack.push(value2);
		
		// when
		Integer actual = stack.top();
		
		// then
		assert actual.equals(value2);
	}
	
	@Test
	public void last() {
		// given
		Integer defaultValue = new Random().nextInt();
		Integer value1 = new Random().nextInt();
		Integer value2 = new Random().nextInt();
		EzyValueStack<Integer> stack = new EzyValueStack<>(defaultValue);
		stack.push(value1);
		stack.push(value2);
		
		// when
		Integer actual = stack.last();
		
		// then
		assert actual.equals(value2);
	}
	
	@Test
	public void popNormal() {
		// given
		Integer defaultValue = new Random().nextInt();
		Integer value1 = new Random().nextInt();
		Integer value2 = new Random().nextInt();
		EzyValueStack<Integer> stack = new EzyValueStack<>(defaultValue);
		stack.push(value1);
		stack.push(value2);
		
		// when
		Integer actual = stack.pop();
		
		// then
		assert actual.equals(value2);
		assert stack.size() == 1;
	}
	
	@Test
	public void popWithDefault() {
		// given
		Integer defaultValue = new Random().nextInt();
		EzyValueStack<Integer> stack = new EzyValueStack<>(defaultValue);
		
		// when
		Integer actual = stack.pop();
		
		// then
		assert actual.equals(defaultValue);
		assert stack.size() == 0;
	}
	
	@Test
	public void clear() {
		// given
		Integer defaultValue = new Random().nextInt();
		Integer value1 = new Random().nextInt();
		Integer value2 = new Random().nextInt();
		EzyValueStack<Integer> stack = new EzyValueStack<>(defaultValue);
		stack.push(value1);
		stack.push(value2);
		
		// when
		stack.clear();
		
		// then
		assert stack.size() == 0;
	}
	
	@Test
	public void popAll() {
		// given
		Integer defaultValue = new Random().nextInt();
		Integer value1 = new Random().nextInt();
		Integer value2 = new Random().nextInt();
		EzyValueStack<Integer> stack = new EzyValueStack<>(defaultValue);
		stack.push(value1);
		stack.push(value2);
		
		// when
		List<Integer> actual = new ArrayList<>();
		stack.popAll(actual);
		
		// then
		List<Integer> expected = Arrays.asList(value2, value1);
		assertEquals(actual, expected);
		assert stack.size() == 0;
	}
	
}
