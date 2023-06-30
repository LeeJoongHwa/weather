package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.DateWeather;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {
    //JpaRepository<domain에 있는 entity, @Id로 지정된 것>

    List<DateWeather> findAllByDate(LocalDate localDate);
}
