package learn.lambda;

import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 对lambda表达式、方法引用的学习
 */

public class LearnLambda {
    /**
     * 使用自己编写的函数式接口，实现：输入两个数，返回两数之和
     */
    @Test
    public void testMyFunctionalInterface(){
        //1-匿名内部类
        System.out.println(operate(1, 2, new MyFunctional() {
            @Override
            public int operate(int x, int y) {
                return x+y;
            }
        }));
        //2-lambda表达式
        System.out.println(operate(1,2,(x,y)->x+y));
        //3-方法引用（使用现成的Integer类的静态方法）
        System.out.println(operate(1,2,Integer::sum));
    }
    public int operate(int x,int y,MyFunctional operation){
        return operation.operate(x, y);
    }

    /**
     * 使用内置的Consumer接口，实现：给定一个List，先将其逆序，再将最后一个数删除，最后输出这个list
     */
    @Test
    public void testConsumer(){
        List<Integer> list=new ArrayList<>();
        list.add(2);
        list.add(7);
        list.add(4);
        list.add(8);
        Consumer<List<Integer>> consumer = Collections::reverse;
        handleList(list, consumer.andThen(l->l.remove(l.size()-1)).andThen(System.out::println));
    }

    public void handleList(List<Integer> list, Consumer<List<Integer>> consumer){
        consumer.accept(list);
    }

    /**
     * 使用内置的Supplier接口，实现：产生一个由10个随机数组成的List
     */
    @Test
    public void testSupplier(){
        List<Integer> list1 = supplyList(() -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                list.add(new Random().nextInt(100));
            }
            return list;
        });
        System.out.println(list1);
    }

    public List<Integer> supplyList(Supplier<List<Integer>> supplier){
        return supplier.get();
    }

    /**
     * 使用内置的Function接口，实现：给定一个List，返回它的最大值
     */
    @Test
    public void testFunction(){
        List<Integer> list=new ArrayList<>();
        list.add(2);
        list.add(7);
        list.add(4);
        list.add(8);
        int result=operateNumber(list, Collections::max);
        System.out.println(result);
    }

    public Integer operateNumber(List<Integer> list, Function<List<Integer>,Integer> function){
        return function.apply(list);
    }

    /**
     * 使用内置的Predicate接口，实现：给定一个学生列表，筛选出分数大于60的学生
     */
    @Test
    public void testPredicate(){
        List<Student> studentList=new ArrayList<>();
        studentList.add(new Student("张三",65));
        studentList.add(new Student("李四",34));
        studentList.add(new Student("王五",77));
        studentList.add(new Student("徐七",99));
        studentList.add(new Student("黄八",15));
        System.out.println(filterStudentByAge(studentList,s->s.getScore()>60));
    }

    public List<Student> filterStudentByAge(List<Student> list, Predicate<Student> predicate){
        return list.stream().filter(predicate).collect(Collectors.toList());
    }


}
