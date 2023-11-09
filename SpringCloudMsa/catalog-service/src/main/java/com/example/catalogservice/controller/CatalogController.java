package com.example.catalogservice.controller;

import com.example.catalogservice.vo.ResponseCatalog;
import com.example.catalogservice.jpa.CatalogEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.catalogservice.service.CatalogService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/catalog-service/")
public class CatalogController {

    //yml 파일에서 특정한 데이터 가지고 오기 위해 Environment 사용

    Environment env;
    CatalogService catalogService;

    @Autowired
    public CatalogController(Environment env, CatalogService catalogService) {
        this.env = env;
        this.catalogService = catalogService;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Catalog Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    //user 서비스 /users 코드 수정
    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getCatalogs() {
        Iterable<CatalogEntity> catalogList = catalogService.getAllCatalogs();

        List<ResponseCatalog> result = new ArrayList<>();
        catalogList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseCatalog.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
