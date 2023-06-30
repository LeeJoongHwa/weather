package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.invalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
//클래스에 @Transactional을 붙이게 되면 클래스 내에 있는
//모든 메서드들이 Transactional의 동작을 하게 됨
public class DiaryService {

    @Value("${openweathermap.key}")
    //application.properties 에 적어둔 key 를 가져오는 것임.
    private String apiKey;
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate(){
        logger.info("오늘도 날씨 데이터 잘 가져옴");
        dateWeatherRepository.save(getWeatherFromApi());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");
        //날씨 데이터 가져오기 (API 에서 가져오기 or DB 에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);

        //DB 에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
        logger.info("end to create diary");
    }

    private DateWeather getWeatherFromApi(){
        // open weather map 에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        // 받아온 날씨 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double)parsedWeather.get("temp"));
        return dateWeather;
    }

    private DateWeather getDateWeather(LocalDate date){
        List<DateWeather> dateWeatherListFromDB =
                dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size() == 0){
            //새로 api 에서 날씨 정보를 가져와야한다.
            //정책상 현재 날씨를 가져오도록 하거나 날씨없이 일기를 쓰기
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }
    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
//        if(date.isAfter(LocalDate.ofYearDay(3050, 1))){
//            throw new invalidDate();
//        }
        //diaryRepository 인터페이스에서 특정 날짜를 가져올
        // list 메서드를 만들고 난 후 파라미터를 넣어준다.
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    private String getWeatherString() {
        String apiUrl =
                "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid="
                        + apiKey;
        //OpenAPI 에서 가져온 데이터들을 response 에 담아주는 작업.
        //위에 createDiary에서 getWeatherString() 메서드를  호출해 json 형태의 데이터를
        //weatherData에 담아준다.
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //HttpURL을 열어주고
            connection.setRequestMethod("GET");
            //GET으로 데이터들을 조회한다.
            int responseCode = connection.getResponseCode();
            //Code는 숫자를 return 하므로 int 변수를 만들어 준다. 200,404,505 등등
            BufferedReader br; //실행 속도와 성능 향상을 위해 BufferedReader를 사용.
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                //200은 정상 작동하는 Code
            } else {
                br = new BufferedReader(new InputStreamReader(
                        connection.getErrorStream()));
                //200 외의 ErrorCode를 받아옴.
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }

    }

    private Map<String, Object> parseWeather(String jsonString) {
        //파싱의 기능을 하는 메서드
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
            //json-simple 라이브러리에 있는 jsonParser를 이용하여 파싱을 하여
            //jsonObject에 파싱한 결과값을 넣을 것임.
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }
}
