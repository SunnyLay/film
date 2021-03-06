package demo.spring.boot.demospringboot.jpa.vo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import demo.spring.boot.demospringboot.jpa.vo.other.Shows;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "t_cinemas_movie")
public class CinemasMoviesVo {
    private int dur;
    private String sc;
    private long wish;
    private int preferential;
    private String img;
    private int showCount;
    @Transient
    private List<Shows> shows;
    private boolean globalReleased;
    @Id
    //movieId
    private long id;
    @Column(name = "des")
    private String desc;
    private String nm;

    //补充
    private Integer cinemasId;

}