package demo.spring.boot.demospringboot.controller.thrid.party.data.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import demo.spring.boot.demospringboot.jpa.service.CinemasDealJpa;
import demo.spring.boot.demospringboot.jpa.service.CinemasDetailJpa;
import demo.spring.boot.demospringboot.jpa.service.CinemasJpa;
import demo.spring.boot.demospringboot.jpa.service.CinemasMoviePlistJpa;
import demo.spring.boot.demospringboot.jpa.service.CinemasMoviesJpa;
import demo.spring.boot.demospringboot.jpa.service.CinemasVipInfoJpa;
import demo.spring.boot.demospringboot.jpa.service.CityJpa;
import demo.spring.boot.demospringboot.jpa.service.HotMovieDetailCommentJpa;
import demo.spring.boot.demospringboot.jpa.service.HotMoviesJpa;
import demo.spring.boot.demospringboot.jpa.service.HotMovieDetailJpa;
import demo.spring.boot.demospringboot.jpa.vo.HotMovieDetailCommentVo;
import demo.spring.boot.demospringboot.jpa.vo.HotMovieVo;
import demo.spring.boot.demospringboot.jpa.vo.HotMovieDetailVo;
import demo.spring.boot.demospringboot.jpa.vo.other.CinemasWithMovie;
import demo.spring.boot.demospringboot.jpa.vo.other.City;
import demo.spring.boot.demospringboot.jpa.vo.CityVo;
import demo.spring.boot.demospringboot.thrid.party.api.maoyan.MaoyanCinemasFactory;
import demo.spring.boot.demospringboot.util.IP;

@Service
public class DataFactoryService {
    private static Logger LOGGER =
            LoggerFactory.getLogger(DataFactoryService.class);
    @Autowired
    private HotMoviesJpa hotMoviesJpa;
    @Autowired
    private MaoyanCinemasFactory maoyanCinemasFactory;
    @Autowired
    private HotMovieDetailJpa hotMovieDetailJpa;
    @Autowired
    private HotMovieDetailCommentJpa hotMovieDetailCommentJpa;
    @Autowired
    private CinemasJpa cinemasJpa;
    @Autowired
    private CityJpa cityJpa;

    @Autowired
    private CinemasDealJpa cinemasDealJpa;

    @Autowired
    private CinemasMoviesJpa cinemasMoviesJpa;

    @Autowired
    private CinemasMoviePlistJpa cinemasMoviePlistJpa;

    @Autowired
    private CinemasVipInfoJpa cinemasVipInfoJpa;

    public void loadInHotMovie() {
        hotMoviesJpa.deleteAll();
        maoyanCinemasFactory.loadInMovies(IP.getNextRandow())
                .stream()
                .forEach(vo -> {
                    LOGGER.info("保存{}", vo);
                    hotMoviesJpa.save(vo);
                });
    }

    public void makeUpHotMovie() {
        if (hotMoviesJpa.findAll().size() == 0) {
            LOGGER.info("检查hotMovie数据异常 - 补充数据start");
            maoyanCinemasFactory.loadInMovies(IP.getNextRandow())
                    .stream()
                    .forEach(vo -> {
                        LOGGER.info("保存{}", vo);
                        hotMoviesJpa.save(vo);
                    });
            LOGGER.info("检查hotMovie数据异常 - 补充数据end");
        } else {
            LOGGER.info("检查hotMovie数据正常");
        }
    }

    public void loadInHotMoviesDetail() {
        List<Integer> ids = new ArrayList<>();
        hotMovieDetailJpa.findAll().stream().forEach(vo -> {
            ids.add(vo.getId());
        });
        List<HotMovieVo> byIdNotIn =
                hotMoviesJpa.findByIdNotIn(ids);
        if (byIdNotIn.size() == 0) {
            LOGGER.info("检查hotMovieDetail数据正常");
        } else {
            LOGGER.info("检查hotMovieDetail数据异常 - 抓取数据start");
            byIdNotIn.forEach(vo -> {
                HotMovieDetailVo hotMovieDetailVo =
                        maoyanCinemasFactory.loadInMoviesDetail(IP.getNextRandow(),
                                String.valueOf(vo.getId()));
                LOGGER.info("保存{}", hotMovieDetailVo);
                hotMovieDetailJpa.save(hotMovieDetailVo);
            });
            LOGGER.info("检查hotMovieDetail数据异常 - 抓取数据end");
        }
    }

    public void loadInHotMoviesDetailComment() throws InterruptedException {
        List<Integer> ids = new ArrayList<>();
        hotMovieDetailCommentJpa.findAll().stream().forEach(vo -> {
            ids.add(vo.getMovieId());
        });
        List<HotMovieVo> byIdNotIn =
                hotMoviesJpa.findByIdNotIn(ids);
        if (byIdNotIn.size() == 0) {
            LOGGER.info("检查loadInHotMoviesDetailComment数据正常");
        } else {
            LOGGER.info("检查loadInHotMoviesDetailComment数据异常 - 补充数据 start");
            byIdNotIn.stream().forEach(vo -> {
                Integer movieId = vo.getId();
                Integer limit = 14;
                Integer offset = 0;
                for (int i = 0; i < 20; i++) {
                    offset = i * limit;
                    try {
                        Thread.sleep(1000 * 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    List<HotMovieDetailCommentVo> HotMovieDetailCommentVos
                            = maoyanCinemasFactory.loadInComments(IP.getNextRandow(), movieId, limit, offset);
                    for (HotMovieDetailCommentVo vo2 : HotMovieDetailCommentVos) {
                        try {
                            hotMovieDetailCommentJpa.save(vo2);
                        } catch (Exception e) {
                            LOGGER.error("数据插入异常：{}", vo, e);
                        }
                    }
                    if (HotMovieDetailCommentVos.size() == 0) {
                        break;
                    }
                }
            });
            LOGGER.info("检查loadInHotMoviesDetailComment数据异常 - 补充数据 end");
        }
    }

    /**
     * 用于处理热门区域的 影院下面的电影
     */
    public void loadInCinemasWithFilm() {
        List<String> citys = new ArrayList<>();
        citys.add("北京市");
        citys.add("杭州市");
        citys.add("溧阳市");
        citys.add("南京市");
        citys.add("上海市");
        citys.add("苏州市");
        citys.add("无锡市");
        citys.add("盐城市");
        cinemasJpa.findCinemasVoByCityIsIn(citys).forEach(cinemasVo -> {
            try {
                CinemasWithMovie cinemasWithMovie = maoyanCinemasFactory.
                        geCinemasWithMovie(IP.getNextRandow(), cinemasVo.getId());
                cinemasWithMovie.getDealList().getDealList().forEach(cinemasDealVo -> {
                    //用于处理 商品
                    cinemasDealVo.setCinemasId(Integer.valueOf(cinemasWithMovie.getCinemaId()));
                    cinemasDealJpa.save(cinemasDealVo);
                });
                cinemasWithMovie.getShowData().getMovies().forEach(moviesVo -> {
                    moviesVo.setCinemasId(Integer.valueOf(cinemasWithMovie.getCinemaId()));
                    cinemasMoviesJpa.save(moviesVo);
                    //用于处理电影院播放的电影
                    moviesVo.getShows().forEach(shows -> {
                        shows.getPlist().forEach(cinemasMoviePlistVo -> {
                            //用于处理 场次
                            cinemasMoviePlistVo.setCinemasId(Integer.valueOf(cinemasWithMovie.getCinemaId()));
                            cinemasMoviePlistVo.setMovieId(moviesVo.getId());
                            cinemasMoviePlistJpa.save(cinemasMoviePlistVo);
                        });
                    });
                });
                cinemasWithMovie.getShowData().getVipInfo().forEach(cinemasVipInfoVo -> {
                    //处理会员 数据
                    cinemasVipInfoVo.setCinemasId(Integer.valueOf(cinemasWithMovie.getCinemaId()));
                    cinemasVipInfoJpa.save(cinemasVipInfoVo);
                });

            } catch (InterruptedException e) {
                LOGGER.error("loadInCinemasWithFilm 异常：{}", cinemasVo, e);
            }

        });

    }


    /**
     * 用于初始化城市数据
     */
    public void InitCityJson() throws IOException {
        LOGGER.info("执行城市init开始");
        File file = ResourceUtils.getFile("classpath:city.json");
        String path = file.getPath();
        String string = FileUtil.readAsString(file);

        JSONArray jsonArray = JSON.parseArray(string);

        List<City> cities =
                jsonArray.toJavaList(City.class);

        cities.stream().forEach(vo -> {
            LOGGER.info("vo: {}", vo);
            vo.getChildren().stream().forEach(child -> {
                CityVo cityVo = new CityVo();
                cityVo.setBelongCity(vo.getName());
                cityVo.setCity(child.getName());
                cityVo.setLat(child.getLat());
                cityVo.setLog(child.getLog());
                try {
                    Thread.sleep(100);
                    cityJpa.save(cityVo);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        });
        LOGGER.info("执行城市init end");
    }




}
