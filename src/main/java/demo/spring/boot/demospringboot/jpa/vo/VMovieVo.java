package demo.spring.boot.demospringboot.jpa.vo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * 2018/4/7    Created by   chao
 */
@Entity
@Table(name = "t_vmovie")
@Data
@ToString
public class VMovieVo {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer id;
    private String title;
    @Column(unique = true)
    private String href;
    private String imgUrl;
    private String tab;
    @Column(columnDefinition = "varchar(32) default '-2'")
    private String videoUrl;

    private int flag;

    public VMovieVo(String title, String href, String imgUrl, String tab) {
        this.title = title;
        this.href = href;
        this.imgUrl = imgUrl;
        this.tab = tab;
    }

    public VMovieVo() {
    }
}
