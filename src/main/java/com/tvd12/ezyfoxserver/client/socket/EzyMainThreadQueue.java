package com.tvd12.ezyfoxserver.client.socket;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandler;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EzyMainThreadQueue extends EzyLoggable {
	private final Queue<EzyEventHandlerExecutor> eventExecutors;
	private final Queue<EzyDataHandlerExecutor> dataExecutors;

	public EzyMainThreadQueue() {
		this.eventExecutors = new LinkedList<>();
		this.dataExecutors = new LinkedList<>();
	}

	public void add(EzyEvent event, EzyEventHandler handler) {
		synchronized (eventExecutors) {
			eventExecutors.add(new EzyEventHandlerExecutor(event, handler));
		}
	}

	public void add(EzyArray data, EzyDataHandler handler) {
		synchronized (dataExecutors) {
			dataExecutors.add(new EzyDataHandlerExecutor(data, handler));
		}
	}

	public void polls() {
		List<EzyEventHandlerExecutor> eventExecutors = dequeueEventHandlers();
		for (EzyEventHandlerExecutor executor : eventExecutors)
			executor.execute();
		List<EzyDataHandlerExecutor> dataExecutors = dequeueDataHandlers();
		for (EzyDataHandlerExecutor executor : dataExecutors)
			executor.execute();
	}

	private List<EzyEventHandlerExecutor> dequeueEventHandlers() {
		List<EzyEventHandlerExecutor> list = new ArrayList<EzyEventHandlerExecutor>();
		synchronized (eventExecutors) {
			while (!eventExecutors.isEmpty()) {
				list.add(eventExecutors.poll());
			}
		}
		return list;
	}

	private List<EzyDataHandlerExecutor> dequeueDataHandlers() {
		List<EzyDataHandlerExecutor> list = new ArrayList<EzyDataHandlerExecutor>();
		synchronized (dataExecutors) {
			while (!dataExecutors.isEmpty()) {
				list.add(dataExecutors.poll());
			}
		}
		return list;
	}

	public static class EzyEventHandlerExecutor extends EzyLoggable {
		private final EzyEvent event;
		private final EzyEventHandler handler;

		public EzyEventHandlerExecutor(EzyEvent event, EzyEventHandler handler) {
			this.event = event;
			this.handler = handler;
		}

		public void execute() {
			try {
				handler.handle(event);
			} catch (Exception ex) {
				logger.error("handle event: {} error", event, ex);
			}
		}
	}

	public class EzyDataHandlerExecutor extends EzyLoggable {
		private final EzyArray data;
		private final EzyDataHandler handler;

		public EzyDataHandlerExecutor(EzyArray data, EzyDataHandler handler) {
			this.data = data;
			this.handler = handler;
		}

		public void execute() {
			try {
				handler.handle(data);
			} catch (Exception ex) {
				logger.error("handle data: {} error", data, ex);
			}
		}
	}
}
