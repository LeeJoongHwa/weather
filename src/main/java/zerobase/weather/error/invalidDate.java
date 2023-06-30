package zerobase.weather.error;

public class invalidDate extends RuntimeException {
    private static final String MESSAGE = "너무 과거 혹은 미래의 날짜 입니다.";
    public invalidDate(){
        super(MESSAGE);

    }
}
