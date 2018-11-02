package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.util.EzyLoggable;

/**
 * Created by tavandung12 on 9/20/18.
 */

public abstract class EzyAbstractSocketClient
		extends EzyLoggable
		implements EzySocketClient {

    protected abstract boolean connect0();

    protected abstract void resetComponents();

}
