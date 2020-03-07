/*
 * Copyright (c) 1994, 1998, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package java.util;


/**
 * 观察者模式中的观察者接口
 * A class can implement the <code>Observer</code> interface when it
 * wants to be informed of changes in observable objects.
 *
 * @author  Chris Warth
 * @see     java.util.Observable
 * @since   JDK1.0
 */
public interface Observer {

    /**
     * 当被观察者发生变化时就会调用这个update()方法来触发观察者的一些行为
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param   o     the observable object. 被观察者对象，也就是能知道是谁发生了变化
     * @param   arg   an argument passed to the <code>notifyObservers</code>
     *                 method. 观察者对象的传参，也就是能知道被观察者发生了哪些变化
     */
    void update(Observable o, Object arg);
}
