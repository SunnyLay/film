package demo.spring.boot.demospringboot.jpa.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import demo.spring.boot.demospringboot.jpa.vo.CinemasDealVo;
import demo.spring.boot.demospringboot.jpa.vo.CinemasMoviePlistVo;

/**
 * 2018/4/12    Created by   chao
 */
@Service
public interface CinemasMoviePlistJpa extends JpaRepository<CinemasMoviePlistVo,Integer>{
}
