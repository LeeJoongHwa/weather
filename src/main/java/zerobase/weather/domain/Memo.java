package zerobase.weather.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "memo")
public class Memo {
    @Id //primary Key가 무엇인지 명시 해주는 어노테이션
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Auto_increase
    private int id;
    private String text;
}
