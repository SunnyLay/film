package demo.spring.boot.demospringboot.jpa.vo.other;

import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 2018/4/5    Created by   chao
 */
@Data
@ToString
public class DateShowIndex implements Comparable<DateShowIndex> {
    private String date;
    private List<DateShow> dateShows;

    @Override
    public int compareTo(DateShowIndex o) {
        return this.date.compareTo(o.date);
    }
}
