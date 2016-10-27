package com.mal.a7walek.bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by OmarAli on 25/10/2016.
 */

public class BusProvider {

    public static Bus mBUS;

    /**
     * Returns an instance of Bus class
     */
    public static Bus getInstance(){

        if (mBUS == null) {
            mBUS = new Bus(ThreadEnforcer.ANY);
        }
        return mBUS;

    }
    private BusProvider(){

    }
}
