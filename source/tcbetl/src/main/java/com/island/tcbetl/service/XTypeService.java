package com.island.tcbetl.service;

import com.island.tcbetl.repository.XTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.Map;

@Slf4j
@Service(value = "XTypeService")
public class XTypeService {

    @Autowired
    @Qualifier("XTypeRepository")
    private XTypeRepository xTypeRepository;

    /**
     * Load XType mapping data from database.
     * @return
     */
    public Map<String, String> loadFiledInfoBySourceName() {
        log.info("Start to load XType mapping data from database.");
        return xTypeRepository.findAllMap();
    }
}
