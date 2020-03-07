package learn.lambda;

/**
 * @Author koliday
 * @Date 2020/3/7
 * @Desc 学生类，用于Predicate测试
*/

public class Student {

    private String name;

    private Integer score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }

    public Student(String name, Integer score) {
        this.name = name;
        this.score = score;
    }
}
