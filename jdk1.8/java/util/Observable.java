/*
 * Copyright (c) 1994, 2012, Oracle and/or its affiliates. All rights reserved.
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
 * 基本掌握
 */

/**
 * 观察者模式的被观察者接口
 * This class represents an observable object, or "data"
 * in the model-view paradigm. It can be subclassed to represent an
 * object that the application wants to have observed.
 * <p>
 * 一个被观察者可以有多个实现了Observer接口的观察者，当被观察者发生变化时就会调用notifyObservers()来通知他们进行update()方法
 * An observable object can have one or more observers. An observer
 * may be any object that implements interface <tt>Observer</tt>. After an
 * observable instance changes, an application calling the
 * <code>Observable</code>'s <code>notifyObservers</code> method
 * causes all of its observers to be notified of the change by a call
 * to their <code>update</code> method.
 * <p>
 * 通知观察者的顺序取决于子类的实现，不过本类中默认是按照观察者的添加顺序来通知的
 * The order in which notifications will be delivered is unspecified.
 * The default implementation provided in the Observable class will
 * notify Observers in the order in which they registered interest, but
 * subclasses may change this order, use no guaranteed order, deliver
 * notifications on separate threads, or may guarantee that their
 * subclass follows this order, as they choose.
 * <p>
 * 观察者模式的通知机制和线程中的wait()和notify()没有任何关系
 * Note that this notification mechanism has nothing to do with threads
 * and is completely separate from the <tt>wait</tt> and <tt>notify</tt>
 * mechanism of class <tt>Object</tt>.
 * <p>
 * 如果两个观察者对象的equals()返回true，那么就认为两个观察者是同一个
 * When an observable object is newly created, its set of observers is
 * empty. Two observers are considered the same if and only if the
 * <tt>equals</tt> method returns true for them.
 *
 * @author  Chris Warth
 * @see     java.util.Observable#notifyObservers()
 * @see     java.util.Observable#notifyObservers(java.lang.Object)
 * @see     java.util.Observer
 * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
 * @since   JDK1.0
 */
public class Observable {
    /**
     * 一个标志变量，标识被观察者对象，也就是本类对象，是否进行了改变，
     * 至于发生了什么改变，不同的子类有不同的实现，只要在改变的时候让changed变成true就行了
     */
    private boolean changed = false;
    /**
     * 存储所有观察者对象的容器，由于是vector，所以添加和删除操作都是线程安全的
     */
    private Vector<Observer> obs;

    /**
     * 初始化一个新的vector容器，用于存储观察者对象
     * Construct an Observable with zero Observers.
     */

    public Observable() {
        obs = new Vector<>();
    }

    /**
     * 添加一个新的观察者对象到容器中，注意，仅当容器中没有相同的观察者对象才会添加进去
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param   o   an observer to be added.
     * @throws NullPointerException   if the parameter o is null.
     * 虽然vector的addElement()是加锁的，但是obs.contains(o)这句话会存在线程安全问题，
     * 有可能两个线程同时向列表中添加相同的观察者，此时有可能出现同时判断为没有重复元素，导致被观察者列表出现重复的观察者
     */
    public synchronized void addObserver(Observer o) {
        //不能添加null
        if (o == null)
            throw new NullPointerException();
        //保证观察者列表中没有重复的观察者
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    /**
     * 从观察者列表中移除一个观察者，由于观察者是不重复的，所以直接移除就行
     * 为什么vector的removeElement()是加锁的，本方法还要加锁呢？
     * 这是因为在后续的通知方法中获取观察者列表时，不希望同时出现观察者增加或删除的动作，是为了保证只通知改变时那一瞬间的所有观察者们
     * Deletes an observer from the set of observers of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     * @param   o   the observer to be deleted.
     */
    public synchronized void deleteObserver(Observer o) {
        obs.removeElement(o);
    }

    /**
     * 默认不传参的通知方法
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyObservers(null)</tt></blockquote>
     *
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void notifyObservers() {
        notifyObservers(null);
    }

    /**
     * 核心方法，通知观察者
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to indicate
     * that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param   arg   any object.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void notifyObservers(Object arg) {
        /*
         * 当前观察者列表的一个副本
         * a temporary array buffer, used as a snapshot of the state of
         * current Observers.
         */
        Object[] arrLocal;
        /*
         * 加锁加在这里是一个折中的办法：
         * 好处是在通知过程中，可以添加删除观察者，也可以再次触发改变
         * 坏处就是有可能刚刚加入观察者列表的观察者收不到这一次变化，或者刚刚移除的观察者收到了这次本不该收到的变化
         */
        synchronized (this) {
            /*
             * 敲黑板！！！！！这里加锁的目的是为了保证只通知被观察者改变时的那一瞬间的观察者们！！！！
             * We don't want the Observer doing callbacks into
             * arbitrary code while holding its own Monitor.
             * The code where we extract each Observable from
             * the Vector and store the state of the Observer
             * needs synchronization, but notifying observers
             * does not (should not).  The worst result of any
             * potential race-condition here is that:
             * 1) a newly-added Observer will miss a
             *   notification in progress
             * 2) a recently unregistered Observer will be
             *   wrongly notified when it doesn't care
             */
            //仅当changed==true时，才说明被观察者发生了变化，才需要通知
            if (!changed)
                return;
            /*
             * 为了防止出现通知的时候观察者列表发生变化，会抛出ConcurrentModificationException
             * 所以保存当前观察者列表的副本，用于通知
             * 这样的做法有可能会造成，刚刚加入观察者列表的观察者收不到这一次变化，或者刚刚移除的观察者收到了这次本不该收到的变化
             */
            arrLocal = obs.toArray();
            //将changed置为false，以接收下次新的变化
            clearChanged();
        }
        /*
         * 遍历缓存的观察者列表，然后依次发送
         * 为什么这里不加锁呢？因为之前已经把观察者列表取出来了，我们只需要考虑通知取出来的那个列表中的观察者就可以
         * 并且，如果通知的过程也加锁，那么一旦观察者非常多的时候，被观察者就无法进行添加或删除操作了
         */
        for (int i = arrLocal.length-1; i>=0; i--)
            ((Observer)arrLocal[i]).update(this, arg);
    }

    /**
     * 清除所有观察者
     * Clears the observer list so that this object no longer has any observers.
     */
    public synchronized void deleteObservers() {
        obs.removeAllElements();
    }

    /**
     * 被观察者发生了改变
     * Marks this <tt>Observable</tt> object as having been changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
    protected synchronized void setChanged() {
        changed = true;
    }

    /**
     * 重置changed变量，表示被观察者没有发生变化
     * Indicates that this object has no longer changed, or that it has
     * already notified all of its observers of its most recent change,
     * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
     * This method is called automatically by the
     * <code>notifyObservers</code> methods.
     *
     * @see     java.util.Observable#notifyObservers()
     * @see     java.util.Observable#notifyObservers(java.lang.Object)
     */
    protected synchronized void clearChanged() {
        changed = false;
    }

    /**
     * 返回当前被观察者是否发生了改变
     * Tests if this object has changed.
     *
     * @return  <code>true</code> if and only if the <code>setChanged</code>
     *          method has been called more recently than the
     *          <code>clearChanged</code> method on this object;
     *          <code>false</code> otherwise.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#setChanged()
     */
    public synchronized boolean hasChanged() {
        return changed;
    }

    /**
     * 返回当前观察者的数量
     * Returns the number of observers of this <tt>Observable</tt> object.
     *
     * @return  the number of observers of this object.
     */
    public synchronized int countObservers() {
        return obs.size();
    }
}
