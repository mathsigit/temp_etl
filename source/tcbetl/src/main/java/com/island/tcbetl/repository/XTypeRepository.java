package com.island.tcbetl.repository;

import com.island.tcbetl.model.XType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository(value = "XTypeRepository")
public interface XTypeRepository extends JpaRepository<XType, Long> {

    @Query(value = "SELECT NEWID() AS ID,* FROM NEC_Xtype ", nativeQuery = true)
    List<XType> findAll();

    default Map<String, String> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(XType::getHex2, XType::getLiteral));
    }
}
