package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired //생성자를 이용한 DI
    public JdbcMemoRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo) {
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        //insert 문에는 update 사용
        return memo;
    }

    public List<Memo> findAll(){
        //모든 내용을 조회하기 위해 List 로 반환
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper());
        //select 문에는 query 사용
    }

    public Optional<Memo> findById(int id){
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
        //Optional : 찾는 id가 없을 경우(null 값인 경우)를 처리하기 위한 함수
    }

    private RowMapper<Memo> memoRowMapper() {
        //ResultSet = rs
        //DB에서 ResultSet 형식으로 데이터를 가져옴
        //RowMapper : ResultSet 을 Memo 라는 형식으로 맵핑하기 위한 함수
        //{id = 1, text = 'this is memo~'}
        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }
}
