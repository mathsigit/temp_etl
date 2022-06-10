package com.island.tcbetl.repository;


import com.island.tcbetl.model.InputFields;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository(value = "InputFieldsRepository")
public interface InputFieldsRepository extends JpaRepository<InputFields, Long> {

    @Query(value = "SELECT NEWID() AS ID,* FROM InputFields WHERE SourceName = :SourceName ORDER BY FieldSequence ASC "
            , nativeQuery = true)
    List<InputFields> findByNameOrderByFieldSequenceAsc(@Param("SourceName")String sourceName);
}
