package learn.lambda;

/**
 * @Author koliday
 * @Date 2020/3/7
 * @Desc 自定义的函数式接口
*/
@FunctionalInterface
public interface MyFunctional {

    /**
     * 这个函数式接口里只能有一个抽象方法，本方法接收两个int参数，并对他们进行运算，返回一个int结果
     * 运算的方式由实现类决定
     */
    public abstract int operate(int x,int y);

    /**
     * 函数式接口和普通接口一样可以允许有静态不可变变量
     */
    public static final int NUMBER=1;

    /**
     * 函数式接口和普通接口一样可以允许有静态方法
     */
    public static void staticPrint(String name){
        System.out.println(name);
    }

    /**
     * 函数式接口和普通接口一样可以允许有默认方法
     */
    default void print(String name){
        System.out.println(name);
    }
}
