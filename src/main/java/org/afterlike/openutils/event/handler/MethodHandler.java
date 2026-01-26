package org.afterlike.openutils.event.handler;

import java.lang.reflect.Method;
import org.afterlike.openutils.event.api.Event;
/*
 * Derived from AzuraClient’s EventBus
 * https://github.com/AzuraClient/Azura-Event-Bus
 *
 * MIT License
 *
 * Copyright (c) 2025 Azura
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public final class MethodHandler {
	private final Object parent;
	private Method method;
	public MethodHandler(final Method method, final Object parent) {
		this.method = method;
		this.parent = parent;
		if (method.getParameterCount() != 1
				|| !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
			this.method = null;
			return;
		}
		this.method.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	public Class<? extends Event> getEventClass() {
		return (Class<? extends Event>) this.method.getParameterTypes()[0];
	}

	public void call(final Event event) {
		if (this.method == null)
			return;
		try {
			this.method.invoke(parent, event);
		} catch (final Exception ignored) {
		}
	}
}
