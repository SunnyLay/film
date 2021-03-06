package demo.spring.boot.demospringboot.controller.film;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import demo.spring.boot.demospringboot.framework.Code;
import demo.spring.boot.demospringboot.framework.Response;
import demo.spring.boot.demospringboot.jpa.service.CinemasDetailJpa;
import demo.spring.boot.demospringboot.jpa.service.CinemasJpa;
import demo.spring.boot.demospringboot.jpa.service.HotMovieDetailCommentJpa;
import demo.spring.boot.demospringboot.jpa.service.HotMoviesJpa;
import demo.spring.boot.demospringboot.jpa.service.HotMovieDetailJpa;
import demo.spring.boot.demospringboot.jpa.service.VMovieJpa;
import demo.spring.boot.demospringboot.jpa.vo.other.CinemasDetailJosnParse;
import demo.spring.boot.demospringboot.jpa.vo.CinemasDetailVo;
import demo.spring.boot.demospringboot.jpa.vo.CinemasVo;
import demo.spring.boot.demospringboot.jpa.vo.HotMovieDetailCommentVo;
import demo.spring.boot.demospringboot.jpa.vo.HotMovieVo;
import demo.spring.boot.demospringboot.jpa.vo.HotMovieDetailVo;
import demo.spring.boot.demospringboot.jpa.vo.other.SeatJson;
import demo.spring.boot.demospringboot.jpa.vo.VMovieVo;
import demo.spring.boot.demospringboot.jpa.vo.other.DateShow;
import demo.spring.boot.demospringboot.jpa.vo.other.DateShowIndex;
import demo.spring.boot.demospringboot.jpa.vo.other.Sections;
import demo.spring.boot.demospringboot.mybatis.service.CinemasService;
import demo.spring.boot.demospringboot.mybatis.vo.CinemasJsonBo;
import demo.spring.boot.demospringboot.thrid.party.api.maoyan.MaoyanCinemasFactory;
import demo.spring.boot.demospringboot.util.IP;

@RestController
@RequestMapping(value = "/film")
public class FilmController {

    private static Logger LOGGER = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    private HotMoviesJpa hotMoviesJpa;

    @Autowired
    private HotMovieDetailJpa hotMovieDetailJpa;

    @Autowired
    private HotMovieDetailCommentJpa hotMovieDetailCommentJpa;


    @Autowired
    private CinemasJpa cinemasJpa;

    @Autowired
    private CinemasDetailJpa cinemasDetailJpa;

    @Autowired
    private MaoyanCinemasFactory maoyanCinemasFactory;

    @Autowired
    private CinemasService cinemasService;

    @Autowired
    private VMovieJpa vMovieJpa;

    @GetMapping(value = "/getCinems/{page}/{limit}")
    public List<CinemasVo> getCinems(
            @PathVariable(value = "page") Integer page,
            @PathVariable(value = "limit") Integer limit) {

        return null;


    }

    @GetMapping(value = "/getHotMovies/{page}/{size}")
    public Response<List<HotMovieVo>> getHotMovies(
            @PathVariable(value = "page") Integer page,
            @PathVariable(value = "size") Integer size) {
        Response<List<HotMovieVo>> response
                = new Response<>();
        try {
            Pageable pageable = new PageRequest(page, size);
            Page<HotMovieVo> result = hotMoviesJpa.findAll(pageable);
            response.setCode(Code.System.OK);
            response.setMsg(Code.System.SERVER_SUCCESS_MSG);
            response.setContent(result.getContent());
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(Code.SystemError.SERVER_INTERNAL_ERROR_MSG);
            response.addException(e);
        }
        return response;
    }

    @GetMapping(value = "/getHotMovieDetail/{movieId}")
    public Response<HotMovieDetailVo> getHotMovies(
            @PathVariable(value = "movieId") Integer movieId) {
        Response<HotMovieDetailVo> response
                = new Response<>();
        try {
            HotMovieDetailVo hotMovieDetailVo
                    = hotMovieDetailJpa.findOne(movieId);
            response.setCode(Code.System.OK);
            response.setMsg(Code.System.SERVER_SUCCESS_MSG);
            response.setContent(hotMovieDetailVo);
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(Code.SystemError.SERVER_INTERNAL_ERROR_MSG);
            response.addException(e);
        }
        return response;
    }

    /**
     * 获取 评论
     */
    @GetMapping(value = "/getComments/{movieId}/{page}/{size}")
    public Response<List<HotMovieDetailCommentVo>> getComments(
            @PathVariable(value = "movieId") Integer movieId,
            @PathVariable(value = "page") Integer page,
            @PathVariable(value = "size") Integer size) {
        Response<List<HotMovieDetailCommentVo>> response
                = new Response<>();
        try {

            //构造查询条件
            HotMovieDetailCommentVo HotMovieDetailCommentVo = new HotMovieDetailCommentVo();
            HotMovieDetailCommentVo.setMovieId(movieId);

            ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                    //姓名采用“开始匹配”的方式查询
                    .withIgnoreNullValues();
            Example<HotMovieDetailCommentVo> example = Example.of(HotMovieDetailCommentVo);
            //分页查询
            Pageable pageable = new PageRequest(page, size);
            Page<HotMovieDetailCommentVo> result = hotMovieDetailCommentJpa.findAll(example, pageable);
            response.setCode(Code.System.OK);
            response.setMsg(Code.System.SERVER_SUCCESS_MSG);
            response.setContent(result.getContent());
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(Code.SystemError.SERVER_INTERNAL_ERROR_MSG);
            response.addException(e);
        }
        return response;
    }

    /**
     * 获取 电影院
     */
    @GetMapping(value = "/getCinemas/{lat}/{lng}/{page}/{size}")
    public Response<List<CinemasJsonBo>> getCinemas(
            @PathVariable(value = "lat") String lat,
            @PathVariable(value = "lng") String lng,
            @PathVariable(value = "page") Integer page,
            @PathVariable(value = "size") Integer size) {
        Response<List<CinemasJsonBo>> response
                = new Response<>();
        try {
            List<CinemasJsonBo> cinemasJsonBos = cinemasService.queryCinemasByDist(lat, lng, page, size);
            response.setCode(Code.System.OK);
            response.setMsg(Code.System.SERVER_SUCCESS_MSG);
            response.setContent(cinemasJsonBos);
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(Code.SystemError.SERVER_INTERNAL_ERROR_MSG);
            response.addException(e);
        }
        return response;
    }

    /**
     * 搜索 电影院
     */
    @GetMapping(value = "/search/{key}/{page}/{size}")
    public Response<List<CinemasVo>> getCinemas(
            @PathVariable(value = "key") String key,
            @PathVariable(value = "page") Integer page,
            @PathVariable(value = "size") Integer size) {
        Response<List<CinemasVo>> response
                = new Response<>();
        try {

            //分页查询
            Pageable pageable = new PageRequest(page, size);
            List<CinemasVo> result =
                    cinemasJpa.findCinemasJsonVoByNmLike("%" + key + "%", pageable);
            response.setCode(Code.System.OK);
            response.setMsg(Code.System.SERVER_SUCCESS_MSG);
            response.setContent(result);
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(Code.SystemError.SERVER_INTERNAL_ERROR_MSG);
            response.addException(e);
        }
        return response;
    }

    /**
     * 获取 电影院 正在播放的电影 电影院id必填
     */
    @GetMapping(value = "/get-cinema-and-movie/{cinemaId}")
    public Response<CinemasDetailJosnParse> getCinemaAndMovie(
            @PathVariable(value = "cinemaId") Integer cinemaId,
            @RequestParam(value = "movieId", required = false) Integer movieId) {
        Response<CinemasDetailJosnParse> response
                = new Response<>();
        try {
            CinemasDetailVo cinemasDetailVo = null;
            if (null == movieId) {
                //根据电影院id来查询
                cinemasDetailVo = cinemasDetailJpa.getFirstByCinemasId(cinemaId);
            } else {
                //根据电影院id和电影id来查询
                cinemasDetailVo = cinemasDetailJpa.
                        findCinemasDetailVoByCinemasIdAndMovieId(cinemaId, movieId);
            }
            if (null == cinemasDetailVo) {
                //如果数据库中没有，就抓取
                this.getDataFromWeb(cinemaId);
                if (null == movieId) {
                    //根据电影院id来查询
                    cinemasDetailVo = cinemasDetailJpa.getFirstByCinemasId(cinemaId);
                } else {
                    //根据电影院id和电影id来查询
                    cinemasDetailVo = cinemasDetailJpa.
                            findCinemasDetailVoByCinemasIdAndMovieId(cinemaId, movieId);
                }
            }

            response.setCode(Code.System.OK);
            response.setMsg(Code.System.SERVER_SUCCESS_MSG);
            CinemasDetailJosnParse cinemasDetailJosnParse =
                    JSON.parseObject(cinemasDetailVo.getContent(), CinemasDetailJosnParse.class);
            List<DateShowIndex> dateShowIndexList = new ArrayList<>();
            this.getDateShows(cinemasDetailVo, dateShowIndexList);
            Collections.sort(dateShowIndexList);//按时间排序
            cinemasDetailJosnParse.setDateShow(dateShowIndexList);
             response.setContent(cinemasDetailJosnParse);
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(Code.SystemError.SERVER_INTERNAL_ERROR_MSG);
            response.setMsg(e.getMessage());
            response.addException(e);
        }
        return response;
    }

    /**
     * 获取 电影院 正在播放的电影 电影院id必填 //
     */
    @GetMapping(value = "/get-seats")
    public Response<SeatJson> getSeats(
            @RequestParam(value = "showDate") String showDate,
            @RequestParam(value = "showId") Integer showId) {
        Response<SeatJson> response
                = new Response<>();
        try {
            response.setCode(Code.System.OK);
            response.setMsg(Code.System.SERVER_SUCCESS_MSG);
            SeatJson seats = maoyanCinemasFactory.getSeats(showId, showDate, IP.getNextRandow());
            Sections sections = seats.getSections().get(0);
            Integer[][] map = new Integer[sections.getRows()][sections.getColumns()];
            sections.getSeatRows().stream().forEach(vo -> {
                vo.getSeats().stream().forEach(vo2 -> {
                    if ("E".equals(vo2.getType())) {
                        map[vo2.getRowNum() - 1][vo2.getColumnNum()] = 0;
                    } else if ("N".equals(vo2.getType())) {
                        map[vo2.getRowNum() - 1][vo2.getColumnNum()] = 1;
                    } else {
                        map[vo2.getRowNum() - 1][vo2.getColumnNum()] = 3;
                    }

                });
            });
            sections.setMap(map);
            response.setContent(seats);
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(Code.SystemError.SERVER_INTERNAL_ERROR_MSG);
            response.addException(e);
        }
        return response;
    }

    /**
     * 获取 电影院
     */
    @GetMapping(value = "/getVMovie/{page}/{size}")
    public Response<List<VMovieVo>> getVMovie(
            @PathVariable(value = "page") Integer page,
            @PathVariable(value = "size") Integer size) {
        Response<List<VMovieVo>> response
                = new Response<>();
        try {
            Pageable pageable = new PageRequest(page, size);
            List<VMovieVo> vMovieVos = vMovieJpa.findVMovieVosByVideoUrlIsNot("-2", pageable);
//            vMovieVos.stream().filter(vo->{
//                vo.setVideoUrl(vo.getVideoUrl().substring(2));
//                return true;
//            }).collect(Collectors.toList());;
            response.setCode(Code.System.OK);
            response.setMsg(Code.System.SERVER_SUCCESS_MSG);
            response.setContent(vMovieVos);
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(Code.SystemError.SERVER_INTERNAL_ERROR_MSG);
            response.addException(e);
        }
        return response;
    }



    private void getDateShows(CinemasDetailVo cinemasDetailVo, List<DateShowIndex> dateShowIndexList) {
        JSONObject jsonObject = JSON.parseObject(cinemasDetailVo.getContent());
        JSONObject dateShowIndexJSONObject = (JSONObject) jsonObject.getInnerMap().get("DateShow");
        if (null != dateShowIndexJSONObject) {
            dateShowIndexJSONObject.getInnerMap().forEach((k, v) -> {
                //k就是日期  eg. 2018-4-6
                DateShowIndex dateShowIndex = new DateShowIndex();
                //填充日期
                dateShowIndex.setDate(k);
                //获取放映list
                List<DateShow> dateShows = ((JSONArray) v).toJavaList(DateShow.class);
                dateShowIndex.setDateShows(dateShows);
                dateShowIndexList.add(dateShowIndex);
            });

        }
    }

    private void getDataFromWeb(Integer cinemasId) {
        List<String> movieIds = null;
        try {
            movieIds = maoyanCinemasFactory
                    .geCinemasMovieIds(IP.getNextRandow(), cinemasId);
            maoyanCinemasFactory.
                    loadInCinemasDetail(IP.getNextRandow(), movieIds, cinemasId).stream()
                    .forEach(vo -> {
                        cinemasDetailJpa.save(vo);
                    });
        } catch (InterruptedException e) {
            LOGGER.error("爬取数据异常");
            e.printStackTrace();
        }

    }
}
