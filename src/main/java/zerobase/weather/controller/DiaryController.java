package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.coyote.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.invalidDate;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {
    //Client - Controller - Service - Repository - DB 순이기 때문에
    //Controller 에서 Service 로 데이터를 넘기기 위해 객체를 만들고 생성자를 만들어준다.
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    //Controller 를 먼저 만들어준다.
    //경로(Path)를 지정해줌.
    //Post 는 저장할 때 사용. GET 은 조회할 때 사용
    //diaryService.createDiary() 메서드는 DiaryService에서 만든다.
    //date 는 날씨를 나타내 줄 파라미터
    //text 는 일기 부분이 들어갈 파라미터
    @ApiOperation(value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장", notes = "이것은 노트")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        //@RequestParam 을 해줌으로써 /create/diary?date=20230601 으로 파라미터를 넘겨줌
        diaryService.createDiary(date, text);
        //위에서 만들었던 Service 생성자를 통해 date와 text를 넘겨준다.
    }

    //일기 조회를 위해서 날짜를 이용할 것이기 때문에 LocalDate 를 파라미터로 넘겨준다.
    //diaryService.readDiary() 메서드는 DiaryService에서 만든다.
    @ApiOperation("선택한 날짜의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        if(date.isAfter(LocalDate.ofYearDay(2023, 1))){
//            throw new invalidDate();
//        }
        return diaryService.readDiary(date);
    }

    @ApiOperation("선택한 기간중의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 첫번째날 : yyyy-MM-dd", example = "2023-06-26")
                            LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 마지막날 : yyyy-MM-dd", example = "2023-06-26")
                            LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     LocalDate date, @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     LocalDate date){
        diaryService.deleteDiary(date);
    }
}
