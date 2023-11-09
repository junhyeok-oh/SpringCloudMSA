package com.example.catalogservice.service;

import com.example.catalogservice.jpa.CatalogEntity;

public interface CatalogService {
    Iterable<CatalogEntity> getAllCatalogs();
    //getAllCatalogs 를 상속 받아서 실제 구현체를 만들어놓는 impl 클래스 등록
}
