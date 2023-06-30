package zerobase.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional //@Transactional 없이 test 를 하게 되면 그 값이 실제로 DB에
                //반영이 되기 때문에 test 시에는 @Transactional 을 사용해야한다.
class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest () {
        //given ~~가 주어지고
        Memo newMemo = new Memo(2, "insertMemoTest");

        //when ~~을 했을 때
        jdbcMemoRepository.save(newMemo);

        //then ~~할 것이다 보통 assert 문이 들어감
        Optional<Memo> result = jdbcMemoRepository.findById(2);
        assertEquals(result.get().getText() , "insertMemoTest");
    }

    @Test
    void findAllMemoTest () {
        //given
        //when
        List<Memo> memoList = jdbcMemoRepository.findAll();
        System.out.println(memoList);
        //then
        assertNotNull(memoList);
    }
}