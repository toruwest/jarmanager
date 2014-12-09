/**
 * Copyright (C) 2009 Future Invent Informationsmanagement GmbH. All rights
 * reserved. <http://www.fuin.org/>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fuin.aspects4swing;

import javax.swing.SwingUtilities;

/**
 * Intercepts calls annotated with <code>@InvokeAndWait</code>,
 * <code>@InvokeLater</code> or <code>@StartNewThread</code>.
 */
public aspect InvokeUIPolicy {

	pointcut invokeAndWaitCall() : 
        execution(@InvokeAndWait void *(..));

	pointcut invokeLaterCall() : 
        execution(@InvokeLater void *(..));

	pointcut startNewThreadCall() : 
        execution(@StartNewThread void *(..));

	pointcut invokeAndWaitWithReturnValue() :
        execution(@InvokeAndWait !void *(..));

	pointcut invokeLaterWithReturnValue() :
        execution(@InvokeLater !void *(..));

	pointcut startNewThreadWithReturnValue() :
        execution (@StartNewThread !void *(..));

/*
	void around(): invokeAndWaitCall() {
		if (SwingUtilities.isEventDispatchThread()) {
			proceed();
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						proceed();
					}
				});
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
*/

	void around(): invokeLaterCall() {
		if (SwingUtilities.isEventDispatchThread()) {
			proceed();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					proceed();
				}
			});
		}
	}

	void around(): startNewThreadCall() {
		final Runnable runnable = new Runnable() {
			public void run() {
				proceed();
			}
		};
		new Thread(runnable).start();
	}

	declare error: invokeLaterWithReturnValue() : 
        "The annotation '@InvokeLater' is only allowed for methods without a return value!";

	declare error: invokeAndWaitWithReturnValue() : 
        "The annotation '@InvokeAndWait' is only allowed for methods without a return value!";

	declare error: startNewThreadWithReturnValue() : 
        "The annotation '@StartNewThread' is only allowed for methods without a return value!";

}
